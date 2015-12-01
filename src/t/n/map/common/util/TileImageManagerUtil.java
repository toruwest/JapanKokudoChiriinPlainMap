package t.n.map.common.util;

import t.n.map.common.LightWeightTile;
import t.n.map.common.LonLat;

public class TileImageManagerUtil {

	/**
	 *  パネルの中央に対応する緯度・経度を得る。
	 * @param zoomLevel
	 * @param tileNoAtOriginX
	 * @param tileNoAtOriginY
	 * @param tilePositionNearOrginX
	 * @param tilePositionNearOrginY
	 * @param panelWidth
	 * @param panelHeight
	 * @return LonLat  パネルの中央の緯度・経度
	 */
	public static LonLat getLonTatFromTileInfo(int zoomLevel, int tileNoAtOriginX, int tileNoAtOriginY, int tilePositionNearOrginX, int tilePositionNearOrginY, int panelWidth, int panelHeight) {
		//世界座標については、http://www.gammasoft.jp/blogs/グーグルマップのしくみを探る/how-google-map-works1/ を参照。
		// 引用：
		//	緯度経度の値に対応する世界座標を求めます。第３回目で導いた式を用います。
		//	次に、世界座標からピクセル座標を求めます。これは世界座標 x 2^zoomLevelという式になる。
		//	あとは、ピクセル座標を256で割れば、商の整数部分がタイル番号となります。
		//ということなので、タイル番号から緯度経度を求めるのは、以下のステップとなる。
		//(1)タイル番号に256を掛けてピクセル座標を得る。
		//(2)ピクセル座標を2^zoomLevelで割って世界座標にする。
		//(3)世界座標を,逆変換の式によって緯度経度に変換する。
		//
		// このステップを、原点にあるタイルの左上の角の、パネルの原点からのオフセットを考慮して修正する。
		//(1)タイル番号に256を掛けてピクセル座標を得て、これから上記のオフセットを引いて、パネル原点のピクセル座標を得る（オフセットの符号はマイナスであることに留意）
		//(2)パネル中央のピクセル座標を、(1)にパネルの縦横のサイズ / 2を足して求める。
		//(3)ピクセル座標を2^zoomLevelで割って世界座標にする。
		//(4)世界座標を,逆変換の式によって緯度経度に変換する。
		// 以下にこれを実装する。
		double zoomLevelPow = Math.pow(2, zoomLevel);
		int pixelXatOrigin = tileNoAtOriginX * 256 - tilePositionNearOrginX;
		int pixelYatOrigin = tileNoAtOriginY * 256 - tilePositionNearOrginY;
		int pixelXatCenter = pixelXatOrigin + panelWidth/2;
		int pixelYatCenter = pixelYatOrigin + panelHeight/2;
		double worldXatCenter = (pixelXatCenter / zoomLevelPow);
		double worldYatCenter = (pixelYatCenter/ zoomLevelPow);

		double longitude = TileUtil.getLongitudeFromWorldX(worldXatCenter);
		double latitude  = TileUtil.getLatitudeFromWorldY(worldYatCenter);

		return new LonLat(longitude, latitude);
	}

	public static LonLat getLonTatFromScreenCoord(int tileNoAtOriginX, int tileNoAtOriginY, int originX, int originY, int mouseX, int mouseY, int zoomLevel) {
		double zoomLevelPow = Math.pow(2, zoomLevel);
		int pixelXatOrigin = tileNoAtOriginX * 256 - originX;
		int pixelYatOrigin = tileNoAtOriginY * 256 - originY;
		double worldX = ((pixelXatOrigin + mouseX) / zoomLevelPow);
		double worldY = ((pixelYatOrigin + mouseY) / zoomLevelPow);

		double longitude = TileUtil.getLongitudeFromWorldX(worldX);
		double latitude  = TileUtil.getLatitudeFromWorldY(worldY);
		return new LonLat(longitude, latitude);
	}

	/**
	 * getLonTatFromScreenCoord()の逆変換を行う。
	 * @param lonlat
	 * @param originTile
	 * @param originX
	 * @param originY
	 * @param zoomLevel
	 * @return java.awt.Point Ｘ，ｙ座標。
	 */
	public static java.awt.Point getScreenCoordFromLonTat(final LonLat lonlat, final int tileNoX, final int tileNoY, final int originX, int originY, int zoomLevel) {
		double zoomLevelPow = Math.pow(2, zoomLevel);

		int pixelXatOrigin = tileNoX * 256 - originX;
		int pixelYatOrigin = tileNoY * 256 - originY;

		double worldX = TileUtil.toWorldX(lonlat.getLongitude());
		double worldY = TileUtil.toWorldY(lonlat.getLatitude());

		int x = (int) (worldX * zoomLevelPow - pixelXatOrigin);
		int y = (int) (worldY * zoomLevelPow - pixelYatOrigin);

		return new java.awt.Point(x, y);
	}
}
