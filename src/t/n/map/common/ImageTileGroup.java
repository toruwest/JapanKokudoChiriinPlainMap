package t.n.map.common;

import java.awt.Dimension;

import t.n.map.common.util.TileImageManagerUtil;

public class ImageTileGroup extends TileGroup {
	/** 図1のxに相当。原点にあるタイルの左上の角（図1のo これは画面の原点より外にあり、見えない）と、画面の原点p（左上)とのオフセット。値の範囲は-(タイルの幅)から0まで。 */
	private int tilePositionNearOrginX;

	/** 図1のyに相当。原点にあるタイルの左上の角（図1のo これは画面の原点より外にあり、見えない）と、画面の原点p（左上)とのオフセット。値の範囲は-(タイルの高さ)から0まで。 */
	private int tilePositionNearOrginY;

	private LonLat centerLonLat;
	private int zoomLevel;
	private int panelCenterX;
	private int panelCenterY;
	private int centerTileLeftUpperCornerX;
	private int centerTileLeftUpperCornerY;
	private int numTilesBetweenOriginAndCenterX;
	private int numTilesBetweenOriginAndCenterY;

	public ImageTileGroup() {
		super();
	}

	public ImageTileGroup(LonLat initCenterLonLat, int tileSize, Dimension dim, int zoomLevel) {
		this();
		this.tileSize = tileSize;
		this.dim = dim;
		this.zoomLevel = zoomLevel;
		setCenterLonLat(initCenterLonLat);
	}

	private void hoge() {
		panelCenterX = dim.width / 2;
		panelCenterY = dim.height / 2;
		centerTileLeftUpperCornerX = panelCenterX - tileSize / 2;
		centerTileLeftUpperCornerY = panelCenterY - tileSize / 2;

		numTilesBetweenOriginAndCenterX = (int) Math.ceil(centerTileLeftUpperCornerX / (double) tileSize);
		numTilesBetweenOriginAndCenterY = (int) Math.ceil(centerTileLeftUpperCornerY / (double) tileSize );
	}

	private void setCenterLonLat(LonLat initCenterLonLat) {
		this.centerLonLat = initCenterLonLat;
		hoge();
		AbstractTile centerTile;
//		if(useAsterDem) {
//			centerTile = new GdemTile(initCenterLonLat);
//		} else {
			centerTile = new KokudoTile(zoomLevel, initCenterLonLat);
//		}
		tileNoAtOriginX = centerTile.getTileNoX() - numTilesBetweenOriginAndCenterX;
		tileNoAtOriginY = centerTile.getTileNoY() - numTilesBetweenOriginAndCenterY;

		//以下はadjust()の中でも同じことをやっている。
		if(tilePositionNearOrginX> 0 && tileNoAtOriginX > 0) {
			tilePositionNearOrginX -= tileSize;
			tileNoAtOriginX--;
		}
		if(tilePositionNearOrginY > 0 && tileNoAtOriginY > 0) {
			tilePositionNearOrginY -= tileSize;
			tileNoAtOriginY--;
		}
		//
		AbstractTile leftUpperTile, rightDownTile;
		int tileCountX = dim.width / tileSize;
		int tileCountY = dim.height / tileSize;

//		if(useAsterDem) {
//			leftUpperTile = new GdemTile(tileNoAtOriginX, tileNoAtOriginY);
//			rightDownTile = new GdemTile(tileNoAtOriginX + tileCountX, tileNoAtOriginY + tileCountY);
//		} else {
			leftUpperTile = new KokudoTile(zoomLevel, tileNoAtOriginX, tileNoAtOriginY);
			rightDownTile = new KokudoTile(zoomLevel, tileNoAtOriginX + tileCountX, tileNoAtOriginY + tileCountY);
//		}
		computeLonLatRange(leftUpperTile, rightDownTile);

		//以下はplainMapの場合に必要となる。
		//原点の周りに表示されているタイルの左上の角の、画面原点に対する座標。X,Y共に マイナスTILE_SIZE から0 の範囲にあるはず。
		//たいていの場合、マイナスになる。
		tilePositionNearOrginX = centerTileLeftUpperCornerX - numTilesBetweenOriginAndCenterX * tileSize ;
		tilePositionNearOrginY = centerTileLeftUpperCornerY - numTilesBetweenOriginAndCenterY * tileSize;
	}

	//prepareUpdate()という名前だけど、中でupdate()を呼んでいる。適切な名前が思いつかない...
	//center -> origin
	public void prepareUpdate(int zoomLevel) {
		hoge();
		this.zoomLevel = zoomLevel;
		KokudoTile centerTile;
		if(zoomLevel >= 2) {
			//起動時は指定された緯度経度のタイルを画面の中央に表示するが、これのタイル番号を得る。
			centerTile = new KokudoTile(zoomLevel, centerLonLat);
			//FIXME -179.93408203125003、85.04923290826919のときに以下が異常な値になる。
			tileNoAtOriginX = centerTile.getTileNoX() - numTilesBetweenOriginAndCenterX;
			tileNoAtOriginY = centerTile.getTileNoY() - numTilesBetweenOriginAndCenterY;
//			if(tileNoAtOriginX < 0)tileNoAtOriginX = 0;
//			if(tileNoAtOriginY < 0)tileNoAtOriginY = 0;

			//原点の周りに表示されているタイルの左上の角の、画面原点に対する座標。X,Y共に マイナスtileSize から0 の範囲にあるはず。
			tilePositionNearOrginX = centerTileLeftUpperCornerX - numTilesBetweenOriginAndCenterX * tileSize;
			tilePositionNearOrginY = centerTileLeftUpperCornerY - numTilesBetweenOriginAndCenterY * tileSize;
		} else {
			//zoomLevelが小さいときは地図が画面の左に寄ってしまうので、個別に対応して、センタリングする。
			centerTile = new KokudoTile(zoomLevel, 0, 0);
			tileNoAtOriginX = 0;
			tileNoAtOriginY = 0;
			tilePositionNearOrginX = centerTileLeftUpperCornerX - zoomLevel * tileSize;
			tilePositionNearOrginY = centerTileLeftUpperCornerY - zoomLevel * tileSize;
		}
		adjust();
	}

	@Override
	public void adjust() {

		if(tileNoAtOriginX < 0) {
			System.out.println();
		}
		if(tileNoAtOriginY < 0) {
			System.out.println();
		}
		//タイルの左上の角が画面の原点より右下にあると左や上の本来地図が表示されるはずの領域に何も描画されないので、一つ上や左のタイルが読み込まれるように補正する。
		//ただし、地図の境界の外の、データが無い領域が見えている場合(tileNoAtOriginX,Yが0に近い場合)について考慮すべき。
		if(tilePositionNearOrginX> 0 && tileNoAtOriginX > 0) {
			tilePositionNearOrginX -= tileSize;
			tileNoAtOriginX--;
		}
		if(tilePositionNearOrginY > 0 && tileNoAtOriginY > 0) {
			tilePositionNearOrginY -= tileSize;
			tileNoAtOriginY--;
		}

		if(tileNoAtOriginX < 0) {
			tileNoAtOriginX = 0;
		}
		if(tileNoAtOriginY < 0) {
			tileNoAtOriginY = 0;
		}
	}

	public void move(float moveX, float moveY) {
		//FIXME X,Y共に-TILE_SIZE から+TILE_SIZEの範囲にあるように補正する。
		//TODO 以下は地図を左に滑らせて、画面原点が右隣のタイルに移る場合。右に滑らせて、左隣に移る場合も考慮すべき？
		if(tilePositionNearOrginX + moveX > tileSize) {
			int diffX = (int) (moveX/Math.abs(moveX));//+-1になるように正規化する。
			tileNoAtOriginX -= diffX;
			if(diffX > 0) {
				tilePositionNearOrginX = 0;//or TILE_SIZE
			} else {
				tilePositionNearOrginX = tileSize;
			}
		} else {
			tilePositionNearOrginX += moveX;
		}
		if(tilePositionNearOrginY + moveY > tileSize) {
			int diffY = (int) (moveY/Math.abs(moveY));//+-1になるように正規化する。
			tileNoAtOriginY -= diffY;
			if(diffY>0) {
				tilePositionNearOrginY = 0;
			} else {
				tilePositionNearOrginY = tileSize;
			}
		} else {
			tilePositionNearOrginY += moveY;
		}
		adjust();
	}

	// TODO centerLonLat を更新する。
	public void zoom(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		hoge();
//		int tileNoAtCenterX = tileNoAtOriginX + numTilesBetweenOriginAndCenterX;
//		int tileNoAtCenterY = tileNoAtOriginY + numTilesBetweenOriginAndCenterY;
//		KokudoTile tile = new KokudoTile(zoomLevel, tileNoAtCenterX, tileNoAtCenterX);
		//FIXME 以前の画面原点あるいは中心の緯度・経度の情報がひつようなはず。

		centerLonLat = TileImageManagerUtil.getLonTatFromScreenCoord(tileNoAtOriginX, tileNoAtOriginY, tilePositionNearOrginX, tilePositionNearOrginY, dim.width/2, dim.height/2, zoomLevel);

	}

	public int getTilePositionNearOrginY() {
		return tilePositionNearOrginY;
	}

	public int getTilePositionNearOrginX() {
		return tilePositionNearOrginX;
	}

	private void computeLonLatRange(AbstractTile leftUpperTile, AbstractTile rightDownTile) {
		double lonMin; double lonMax; double latMin; double latMax;
		lonMin = leftUpperTile.getLeftUpperLon();
		latMin = leftUpperTile.getLeftUpperLat();
		lonMax = rightDownTile.getRightDownLon();
		latMax = rightDownTile.getRightDownLat();
		range = new LonLatRange(lonMin, lonMax, latMin, latMax);
	}

	public LonLat getCurrentCenterLonLat() {
		centerLonLat = TileImageManagerUtil.getLonTatFromScreenCoord(tileNoAtOriginX, tileNoAtOriginY, tilePositionNearOrginX, tilePositionNearOrginY, dim.width/2, dim.height/2, zoomLevel);
		return centerLonLat;
	}
}
