package t.n.plainmap.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import t.n.map.FolderConfig;

public class OldFileRemoveCallable implements Callable<List<Path>> {
	private static final File imgSavingDir = FolderConfig.getImageCacheFolder();
	private static final long MAX_CACHE_FILE_SIZE = 50000000; // 50 MB
	private static volatile boolean interruptedFlag;
	private static WatchKey watchKey;
	private static WatchService watchService;
	private static Path imgSavingPath;
	private static List<Path> deletedFiles;

	@Override
	public List<Path> call() throws Exception {
		doMonitor();
		return deletedFiles;
	}

	private static void doMonitor() throws IOException, InterruptedException {
		interruptedFlag = false;
		imgSavingPath = imgSavingDir.toPath();

		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				//注意：以下はEclipseのconsoleで止めると実行されない。
				System.out.println("中断されました。");
				interruptedFlag = true;
				watchKey.cancel();
				try {
					watchService.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

    	System.out.println("別のコマンドプロンプトを起動して、" + imgSavingDir + " の下でファイルを作成/削除し、何が起こるかを見ます。");

		watchService = FileSystems.getDefault().newWatchService();
		//指定したフォルダの直下しか監視されないので、サブフォルダーを列挙・登録する。
//		imgSavingDir.toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
		for(File subFolder : imgSavingDir.listFiles()) {
			if(subFolder.isDirectory()) {
				subFolder.toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
			}
		}

		while(true) {
			watchKey = watchService.take(); //ここで待機する。
			String watchableName = watchKey.watchable().toString();
            System.out.println("Watchable : " + watchableName);

            if (watchKey.isValid()) {
            	for (WatchEvent<?> event : watchKey.pollEvents()) {
            		System.out.println("An event was found after file creation of kind " + event.kind()
            			+ ". The event occurred on file " + event.context() + ".");
            		traverseFolder();
            	}

            	if(! watchKey.reset()) {
            		System.out.println("watch key is invalid by now.");
            		break;
            	}
            }

			if(interruptedFlag) {
				break;
			}
		}
	}

	private static void traverseFolder() throws IOException {
		final Map<FileTime, Path> map = new TreeMap<>();
		long filesSizeInTheFolder = 0;

		FileVisitor<Path> myFileVisitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				System.out.println("I'm about to visit the " + dir + " directory");
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attribs) {
				Path parent = path.getParent();
				if(path.toFile().isFile() && !parent.equals(imgSavingPath)) {
					map.put(attribs.lastAccessTime(), path);
				}
				return FileVisitResult.CONTINUE;
			}
		};

		Files.walkFileTree(imgSavingPath, myFileVisitor);
		for(FileTime ft : map.keySet()) {
			Path p = map.get(ft);
			filesSizeInTheFolder += p.toFile().length();
			System.out.println(p + " : date: " + ft + ", size:" + p.toFile().length());
		}

		long free = imgSavingDir.getFreeSpace();
		long total =imgSavingDir.getTotalSpace();

		System.out.printf("disk free: %,3d / file size under the folder: %,3d / total: %,3d  (%4.1f%% used)%n", free, filesSizeInTheFolder, total, 100 * (total - free)/(float)total );
		if(filesSizeInTheFolder > MAX_CACHE_FILE_SIZE) {
			removeOldFiles(map.values(), filesSizeInTheFolder, filesSizeInTheFolder - MAX_CACHE_FILE_SIZE);
		}
	}

	private static void removeOldFiles(Collection<Path> files, long currentFilesSize, long targetDeleteFileSize) throws IOException {
		System.out.println("current:" + currentFilesSize + ", target: " + targetDeleteFileSize);
		long deletedFileSize = 0;
		for(Path p : files) {
			deletedFileSize += p.toFile().length();
			System.out.println("deleteing file:" + p);
			Files.delete(p);
			deletedFiles.add(p);
			if(deletedFileSize >= targetDeleteFileSize) break;
		}
	}
}
