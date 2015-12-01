package t.n.plainmap.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class OldTiledImageRemoveService {

	public static void main(String[] args) throws IOException, InterruptedException {
		ExecutorService executor = Executors.newSingleThreadExecutor();

	    CompletionService<List<Path>> completion = new ExecutorCompletionService<>(executor);
	    completion.submit(new OldFileRemoveCallable());
	    System.out.println("started");
	}

	public static void start() {
		// TODO Auto-generated method stub

	}
}

