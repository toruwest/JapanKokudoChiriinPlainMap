package t.n.map.common;

import java.io.Serializable;

import t.n.map.common.util.TileUtil;

public final class KokudoTile extends AbstractTile implements Serializable {

	private static final long serialVersionUID = 8909825211533304800L;

	public transient static final double NORTH_BOUND = +85.051128779807f;
	public transient static final double SOUTH_BOUND = -85.051128779807f;
	public transient static final double WEST_BOUND = -180.0f;
	public transient static final double EAST_BOUND = 180.0f;
	public transient static final double R = 128 / Math.PI;
	public transient static final int HEIGHT_MAP_ZOOM_LEVEL = 14;//14に固定。他の値を指定しても404 Not foundになる。
	public transient static final int TILE_SIZE = 256;
	public transient static final double TILE_SIZE_DOUBLE = 256d;

	//タイル番号の上限は、ズームレベルの最大値で決まる。
	private transient static final int MAX = (int)Math.pow(2, 19);

	private final int zoomLevel;
	private final boolean isError;

	private transient String errorCause;
	private transient double scale;
	private double lonDiff = Double.NaN;
	private double latDiff = Double.NaN;

	public KokudoTile() {
		//デシリアライズのために引数なしのコンストラクタが必要
		//以下の項目は以下の適当な値で初期化し、他の項目はこれらから計算して求められる。
		super(-1, -1);
		zoomLevel = -1;
		isError = false;
		computeScale();
		computeLonLatOfCorners();
	}

	public KokudoTile(int zoomLevel, int tileNoX, int tileNoY) {
		super(tileNoX, tileNoY);
		this.zoomLevel = zoomLevel;
		computeScale();
		isError = TileUtil.isRangeError(zoomLevel, tileNoX, tileNoY);
		if(isError) {
			super.tileNoX = 0;
			super.tileNoY = 0;
			errorCause = TileUtil.getErrorCause(zoomLevel, tileNoX, tileNoY);
		} else {
			this.tileNoY = tileNoY;
			this.tileNoX = tileNoX;
			computeLonLatOfCorners();
		}
	}

	/**
	 * 指定されたzoomLevelと緯度経度よりインスタンス化する。
	 * zoomLevel,緯度経度が規定の範囲外であってもインスタンスは生成されることに注意する。
	 * この場合、isError()メソッドがtrueを返す。getCause()メソッドで原因をチェックする。
	 * @param zoomLevel
	 * @param lonlat　緯度経度（タイル内の任意の緯度経度）
	 */
	public KokudoTile(int zoomLevel, LonLat lonlat) {
		super(TileUtil.getTileNoX(zoomLevel, lonlat), TileUtil.getTileNoY(zoomLevel, lonlat));
		this.zoomLevel = zoomLevel;
		computeScale();

		isError = TileUtil.isRangeError(zoomLevel, lonlat);
		if(isError) {
			this.tileNoX = 0;
			this.tileNoY = 0;
			errorCause = TileUtil.getErrorCause(zoomLevel, lonlat);
		} else {
			this.tileNoX = TileUtil.getTileNoX(zoomLevel, lonlat);
			this.tileNoY = TileUtil.getTileNoY(zoomLevel, lonlat);
			computeLonLatOfCorners();
		}
	}

	public boolean isError() {
		return isError;
	}

	public String getCause() {
		return errorCause;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	private void computeScale() {
		scale = Math.pow(2, zoomLevel);
	}

	//FIXME lat(緯度, 北緯は正、南緯は負)は大きいほうをUpper,lon(経度, 東経は正、西経は負で表す)は大きいほうをrightに入れ替える。
	//緯度は北極が最大になる。経度は東経180度が最大で、西経180度が最小になる。
	//lonDiff, latDiffは常に正になるようにする。TileNoは常にゼロ以上
	private void computeLonLatOfCorners() {
		leftUpperLon = TileUtil.getLongitudeFromTileNoX(tileNoX, scale);
		rightDownLon = TileUtil.getLongitudeFromTileNoX(tileNoX + 1, scale);
		leftUpperLat = TileUtil.getLatitudeFromTileNoY(tileNoY, scale);
		rightDownLat = TileUtil.getLatitudeFromTileNoY(tileNoY + 1, scale);
		lonDiff = rightDownLon - leftUpperLon;
		latDiff = leftUpperLat - rightDownLat;
	}

	public String getErrorCause() {
		return errorCause;
	}

	//leftUpperとrightDownの経度を256分割して、引数のlonを比例配分したint値を返す。
	public int getPixelCoordX(double lon) {
		return (int) (Math.round(256 * (lon - leftUpperLon)/(lonDiff)));
	}

	//leftUpperとrightDownの緯度を256分割して、引数のlatを比例配分したint値を返す。
	public int getPixelCoordY(double lat) {
		return (int) (Math.round(256 * (leftUpperLat - lat)/(latDiff)));
	}

	public boolean contains(double lon, double lat) {
		return (leftUpperLon <= lon &&
				lon < rightDownLon &&
				leftUpperLat <= lat &&
				lat < rightDownLat);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof KokudoTile) {
			KokudoTile oponent = (KokudoTile)obj;
			return (tileNoX == oponent.getTileNoX() && tileNoY == oponent.getTileNoY());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return tileNoX * MAX + tileNoY;
	}

	public LonLat getLonLat() {
		return new LonLat(leftUpperLon, leftUpperLat);
	}

}
