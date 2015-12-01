package t.n.map;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import t.n.map.common.LightWeightTile;
import t.n.mapdata.http.HttpGetter;
import t.n.plainmap.ITiledImageReceiver;
import t.n.plainmap.util.TiledMapUtil2;

public class ImageGetter extends HttpGetter {
	private final ExecutorService pool = Executors.newFixedThreadPool(5);

	public ImageGetter(IFetchingStatusObserver observer, File heightSavingDir, ITiledImageReceiver receiver) {
		super(observer, heightSavingDir, receiver);
	}

	public void getImageAt(LightWeightTile tile) {
		final File imageFile = new File(TiledMapUtil2.generateLocalFilename(imageSavingDir, tile));
		String uri = TiledMapUtil2.generateImageURI(tile);
		startFetching(uri, imageFile, tile);
	}

}
