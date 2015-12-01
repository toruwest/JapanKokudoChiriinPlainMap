package t.n.mapdata.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.concurrent.Future;

import org.apache.http.client.ClientProtocolException;
//import org.apache.http.conn.params.ConnRoutePNames;



import t.n.map.IFetchingStatusObserver;
import t.n.map.common.LightWeightTile;
import t.n.plainmap.ITiledImageReceiver;
import t.n.plainmap.ImageDataStatus;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ProxyServer;
import com.ning.http.client.Response;
import com.ning.http.client.providers.jdk.JDKAsyncHttpProvider;

public class HttpGetter {

	private final IFetchingStatusObserver observer;
	private final ITiledImageReceiver imageReceiver;
	protected File heightSavingDir;
	protected File imageSavingDir;
	private ProxyServer proxy;
	private static AsyncHttpClient httpClient;

	static {
		httpClient = getAsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
	}

	private static AsyncHttpClient getAsyncHttpClient(AsyncHttpClientConfig config) {
		if (config == null) {
			config = new AsyncHttpClientConfig.Builder().setAllowPoolingConnections(true).setMaxConnections(5).build();
		}
		return new AsyncHttpClient(new JDKAsyncHttpProvider(config), config);
	}

	public HttpGetter(IFetchingStatusObserver observer, File imageSavingDir, ITiledImageReceiver imageReceiver) {
		this.observer = observer;
		this.imageSavingDir = imageSavingDir;
		this.imageReceiver = imageReceiver;
	}

	public HttpGetter(IFetchingStatusObserver observer, File imageSavingDir, ITiledImageReceiver imageReceiver, String proxyHost, int proxyPort) {
		this.observer = observer;
		this.imageSavingDir = imageSavingDir;
		this.imageReceiver = imageReceiver;
		proxy = new ProxyServer(proxyHost, proxyPort);
	}

	protected void startFetching(final String uri, final File localFile, final LightWeightTile tile) {
		System.out.println("will fetch:" + uri);
		if(observer != null) {
			observer.notifyStartFetching(uri);
		}
		//以下はレスポンスを待たずに直ちにリターンしてくる。
		final Future<Integer> future;
		if(proxy == null) {
			future = httpClient.prepareGet(uri).execute(new MyAsyncCompletionHandler(localFile, tile));
		} else {
			future = httpClient.prepareGet(uri).setProxyServer(proxy).execute(new MyAsyncCompletionHandler(localFile, tile));
		}
	}

	public void shutdown() {
		if(httpClient != null) httpClient.close();
	}

	class MyAsyncCompletionHandler extends AsyncCompletionHandler<Integer> {
		private final File localFile;
		private final LightWeightTile tile;
		private boolean isMkdirFailed = false;

		public MyAsyncCompletionHandler(File localFile, LightWeightTile tile) {
			this.localFile = localFile;
			this.tile = tile;
		}

		@Override
		public Integer onCompleted(Response response) {
			int stat = 0;
			isMkdirFailed = false;

			try {
				// レスポンスの取得が完了したら、URLを表示　TODO Loggerに記録。
				//TODO (ここに書くべきことではないけど）URLの表示が長すぎてプログレッシブバーがちょっとしか表示されない。URLを省略して表示した方がいい。
				//TODO プログレッシブバーに出す内容を考える。リクエストを出した数と、レスポンスを得た数を出す？
				System.out.println("onComplete(): " + response.getUri());
				// レスポンスヘッダーの取得
				stat = response.getStatusCode();
				if(stat == 200) {
					// ファイルへの保存
					InputStream is;
					is = response.getResponseBodyAsStream();
					File parentDir = localFile.getParentFile();
					boolean isMkdirSuccess = false;
					boolean isParentDirExists = parentDir.exists();
					if(parentDir != null && !isParentDirExists) {
						isMkdirSuccess = parentDir.mkdirs();
					}
					if(isParentDirExists || !isParentDirExists && isMkdirSuccess) {
						FileOutputStream out = new FileOutputStream(localFile, false);
						int b;
						while((b = is.read()) != -1){
							out.write(b);
						}
						out.close();
						is.close();
					} else {
						//TODO 格納フォルダが作れなかったので、呼び出し元へ通知。
						//TODO ディスクがいっぱいで書き込めない状況への対処。ディスクの使用を制限する。
						isMkdirFailed  = true;
					}
				}
				if(isMkdirFailed) {
					//TODO heightDataReceiver.receiveHeighTextFile(tile, localFile, HeightDataStatus.success);
				} else if(stat == 200) {
					imageReceiver.receiveImageData(tile, localFile, ImageDataStatus.success);
				} else if(stat == 404){
					//イメージが存在しない場合、”NO DATA"と書かれた画像を代わりに表示する。
					imageReceiver.receiveImageData(tile, localFile, ImageDataStatus.noData);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return stat;
		}

		@Override
		public void onThrowable(Throwable t) {
			//サーバーに接続できなかった場合、タイムアウトになり、ここが実行される。
			//TODO pingできる？
			//TODO 一回でも接続できなかった場合、画面に表示して、以降のアクセスは止める。なんらかの方法で（ボタン押下など）再開する。
			//"Connection Reset"となる場合と,"No response received after 60000"となる場合がある。後者はネットワークが接続できない場合と、
			//リクエストを出し過ぎて待たされている場合がある。どちらなのか区別して、前者なら以降のアクセスを中止したい。
			//なお、後者については、getAsyncHttpClient()で同時アクセス数を制限しているつもりなのにお構いなしにリクエストされているようだ。
			if(t instanceof ConnectException) {
				System.err.println("connection failed:" + t.getMessage());
			} else {
				System.err.println(t.getMessage());
			}
		}
	}
}