package t.n.map;

import java.io.File;

public class FolderConfig {
	private static final File cacheFolder;

	static {
		String userDir = System.getProperty("user.home");
		cacheFolder = new File(userDir + "/Documents/map/map-cache");

		if(!cacheFolder.exists()) {
			cacheFolder.mkdirs();
		}
	}

	public static File getCacheFolder() {
		return cacheFolder;
	}

	public static File getImageCacheFolder() {
		return new File(cacheFolder + File.separator + "img");
	}

	public static File getDefaultImageFolder() {
		return new File("./img");
	}

	public static final String[] fixedFiles = {
		"NO_DATA.PNG",
		"error.png",
		"fetching.png"
	};

	private static final String[] dirCandidates = {
			"/Users/toru/Documents/map/gdem",
			"/Volumes/SP UFD U2/gdem"
		};

	public static File getGdemFolder() {
		File gdemDataDir = null;
		for(int i = 0; i < dirCandidates.length; i++) {
			gdemDataDir = new File(dirCandidates[i]);
			if(gdemDataDir.exists()) {
				break;
			} else {
				gdemDataDir = null;
			}
		}
//		if(gdemDataDir == null) throw new RuntimeException("Heightデータを格納したディレクトリが見つかりません。USBメモリかも?");
		return gdemDataDir;
	}
}
