package t.n.plainmap;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import t.n.map.FolderConfig;
import t.n.map.IFetchingStatusObserver;
import t.n.map.IImageReceiver;
import t.n.map.ImageGetter;
import t.n.map.common.LightWeightTile;
import t.n.plainmap.util.TiledMapUtil2;

public class TiledImageReaderImpl implements ITiledImageReceiver {
	private final IFetchingStatusObserver observer;
	private IImageReceiver imageReceiver;
	private final ImageGetter imageGetter;
	private static final File imgSavingDir = FolderConfig.getImageCacheFolder();
	private static final File defaultImageDir = FolderConfig.getDefaultImageFolder();
	private static File NO_DATA_FILE = null;
	private static Image NO_DATA_IMG = null;
	private static Image ERROR_IMG = null;
	private static Image FETCHING_IMG = null;
	private static final boolean noAccess = false;
	private final Set<String> downloadingSet;

	static {
		try {
			NO_DATA_IMG = ImageIO.read(new File(defaultImageDir, "NO_DATA.PNG"));
			ERROR_IMG = ImageIO.read(new File(defaultImageDir, "error.png"));
			FETCHING_IMG = ImageIO.read(new File(defaultImageDir, "fetching.png"));

			if(!imgSavingDir.exists()) {
				if (!imgSavingDir.mkdirs()) {
					//TODO フォルダーを作れなかったことを呼び出し元に通知。
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TiledImageReaderImpl(IFetchingStatusObserver observer) {
		this.observer = observer;
		downloadingSet = Collections.synchronizedSet(new HashSet<String>());
		imageGetter = new ImageGetter(observer, imgSavingDir, this);
	}

	public static File getNoDataImageFile() {
		return NO_DATA_FILE;
	}

	public void shutdown() {
		imageGetter.shutdown();
	}

	//TODO uriとstat,imageの関連付けは、必要ない？
	//TODO queueとthread pool(Executor service)を組み合わせて、同時にアクセスするスレッド数の上限を制限する。
	//非同期読み込み。とりあえず”読み込み中”のようなイメージを返しておき、後で差し替える。読めなかったら"NO DATA"と書かれたイメージを返す。
	//TODO 404になったuriを覚えておき、再度アクセスされたら二度と読みにいかないようにする。

	//以下の順で指定されたタイルに対応するImageインスタンスを作ります。
	//(1)ディスクに保存してあるファイル (2)国土地理院のホームページから得たタイルイメージ。
	//(1)の場合即座に戻ってくる。
	//(2)の場合、このクラスに非同期で通知されるので、(A)ファイルに書き込む。(B)Viewに通知。
	public Image getImageAt(LightWeightTile tile, final IImageReceiver imageReceiver) {
		this.imageReceiver = imageReceiver;
		Image resultImg = null;
//		final String uri;
		if(!tile.isError()) {
//			uri = TiledMapUtil.generateImageURI(tile);
		} else {
			throw new IllegalArgumentException(tile.getCause());
		}

		final File localFile = new File(TiledMapUtil2.generateLocalFilename(imgSavingDir, tile));
		//Imageファイルは以下の状況がある。それぞれ以下のように処理する。すべてHeightデータは後で非同期で受け取る。
		//(1)ファイルがない場合：noAccessがfalseならダウンロード開始、trueならダウンロードしない。
		//(2)ファイルがあるが、ダウンロード中の場合：特に何もしない。ダウンロードが終わるのを待つ。
		//(3)ファイルがある場合(ダウンロードが終わっている): 通知。
		//TODO ネットワークエラーや、ディスクの空き領域の状況に応じてアクセスを制御する。
		// 後者はこのクラス自身で判断できる。前者は
		if(!localFile.exists()) { //(1)
			if(!noAccess) {
				downloadingSet.add(localFile.getAbsolutePath());
				imageGetter.getImageAt(tile);
				resultImg = FETCHING_IMG;
			} else {
				resultImg = NO_DATA_IMG;
			}
		} else {
			if(downloadingSet.contains(localFile.getAbsolutePath())) {
				//(2)の場合。何もなし
				resultImg = FETCHING_IMG;
			} else { //(3)
				try {
					resultImg = ImageIO.read(localFile);
				} catch (IOException e) {
					System.err.println("警告：" + localFile.getName() + "の読み込みで" + e.getMessage());
//					e.printStackTrace();
				}
			}
		}
		return resultImg;
	}

	//非同期で国土地理院のサーバーからgetして作成したイメージデータを受け取る。
	@Override
	public void receiveImageData(LightWeightTile tile, File tileImageFile, ImageDataStatus status) {
		if(tileImageFile != null) {
			downloadingSet.remove(tileImageFile.getAbsolutePath());
			switch(status) {
			case success:
				try {
					imageReceiver.setImage(tile, ImageIO.read(tileImageFile), status);
					observer.notifyFetchCompleted(tile);
				} catch (Exception e) {
					//				e.printStackTrace();
				}
				break;
			case noData:
				//イメージが存在しない場合、”NO DATA"と書かれた画像を代わりに表示する。
				imageReceiver.setImage(tile, NO_DATA_IMG, status);
				break;
			case error:
				//サーバーからのイメージ読み取りの際にエラーが起きた場合、”ERROR"と書かれた画像を代わりに表示する。
				imageReceiver.setImage(tile, ERROR_IMG, status);
				break;
			}
		} else {
			imageReceiver.setImage(tile, ERROR_IMG, status);
		}
	}

}
