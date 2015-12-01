package t.n.map.common.util;

import static t.n.map.common.KokudoTile.R;
import t.n.map.common.KokudoTile;
import t.n.map.common.LonLat;

public class TileUtil {
	private static int[] MAX_TILE_NO;

	static {
		MAX_TILE_NO = new int[20];
		for(int i = 0; i <= 19; i++) {
			MAX_TILE_NO[i] = (int)Math.pow(2, i);
		}
	}

	public static boolean isRangeError(int zoomLevel, int tileNoX, int tileNoY) {
		if(zoomLevel < 0 || zoomLevel > 19) {
//			errorCause  = "zoomLevelは0以上18以下である必要があります。zoomLevel: " + zoomLevel;
			return true;
		}
		if(tileNoX < 0 || tileNoY < 0) {
//			errorCause = "どちらかのtileNoが0より小さくなっています。" + tileNoX + "," + tileNoY;
			return true;
		}
		if(tileNoX >= MAX_TILE_NO[zoomLevel] || tileNoY >= MAX_TILE_NO[zoomLevel]) {
//			errorCause = "tileNoはx,y共に" + MAX_TILE_NO[zoomLevel] + "より小さくする必要があります。 zoomLevel:" + zoomLevel + ", x:" + tileNoX + ", y:" + tileNoY;
			return true;
		}
		return false;
	}

	public static String getErrorCause(int zoomLevel, int tileNoX, int tileNoY) {
		String errorCause = "";
		if(zoomLevel < 0 || zoomLevel > 19) {
			errorCause  = "zoomLevelは0以上18以下である必要があります。zoomLevel: " + zoomLevel;
//			return true;
		}
		if(tileNoX < 0 || tileNoY < 0) {
			errorCause = "どちらかのtileNoが0より小さくなっています。" + tileNoX + "," + tileNoY;
//			return true;
		}
		if(tileNoX >= MAX_TILE_NO[zoomLevel] || tileNoY >= MAX_TILE_NO[zoomLevel]) {
			errorCause = "tileNoはx,y共に" + MAX_TILE_NO[zoomLevel] + "より小さくする必要があります。 zoomLevel:" + zoomLevel + ", x:" + tileNoX + ", y:" + tileNoY;
//			return true;
		}
		return errorCause;
	}

	public static boolean isRangeError(int zoomLevel, LonLat lonlat) {
		double longitude = lonlat.getLongitude();
		double latitude  = lonlat.getLatitude();
		if(0 <= zoomLevel && zoomLevel <= 18) {
			if(KokudoTile.WEST_BOUND <= longitude && longitude < KokudoTile.EAST_BOUND) {
				if(KokudoTile.SOUTH_BOUND < latitude && latitude <= KokudoTile.NORTH_BOUND) {
					return false;
				} else {
//					errorCause = "latitudeが範囲外です。許容範囲は" + SOUTH_BOUND + "(これを含まない)から" + NORTH_BOUND + "(これを含む)、引数:" + leftUpperLat;
					return true;
				}
			} else {
//				errorCause = "longitudeが範囲外です。許容範囲は-180(これを含む) から+180(これを含まない)、引数:" + leftUpperLon;
				return true;
			}
		} else {
//			errorCause = "zoomLevelが範囲外です。許容範囲は0以上18以下、引数: " + zoomLevel;
			return true;
		}
	}

	public static String getErrorCause(int zoomLevel, LonLat lonlat) {
		String errorCause = "";
		double longitude = lonlat.getLongitude();
		double latitude  = lonlat.getLatitude();
		if(0 <= zoomLevel && zoomLevel <= 18) {
			if(KokudoTile.WEST_BOUND <= longitude && longitude < KokudoTile.EAST_BOUND) {
				if(KokudoTile.SOUTH_BOUND < latitude && latitude <= KokudoTile.NORTH_BOUND) {
//					return false;
				} else {
					errorCause = "latitudeが範囲外です。許容範囲は" + KokudoTile.SOUTH_BOUND + "(これを含まない)から" + KokudoTile.NORTH_BOUND + "(これを含む)、引数:" + latitude;
//					return true;
				}
			} else {
				errorCause = "longitudeが範囲外です。許容範囲は-180(これを含む) から+180(これを含まない)、引数:" + longitude;
//				return true;
			}
		} else {
			errorCause = "zoomLevelが範囲外です。許容範囲は0以上18以下、引数: " + zoomLevel;
//			return true;
		}
		return errorCause;
	}

	public static int getTileNoX(int zoomLevel, LonLat lonlat) {
		double scale = Math.pow(2, zoomLevel);
		return getTileNoX(scale, toScaledWorldX(scale, lonlat));
	}

	/**
	 * @param lonlat
	 * @return 経度の整数部分。東経はプラス、西経はマイナス。東経１８０度、西経１８０度は？
	 */
	public static int getGdemTileNoX(LonLat lonlat) {
		int tileNoX = (int) ( 10 * Math.floor(lonlat.getLongitude()));
		return tileNoX;
	}

	/**
	 * @param lonlat
	 * @return 緯度の整数部分。北緯はプラス、南緯はマイナス。赤道上は０。
	 */
	public static int getGdemTileNoY(LonLat lonlat) {
		int tileNoY = (int) (10 * Math.floor(lonlat.getLatitude()));
		return tileNoY;
	}

	public static int getTileNoY(int zoomLevel, LonLat lonlat) {
		double scale = Math.pow(2, zoomLevel);
		return getTileNoY(scale, toScaledWorldY(scale, lonlat));
	}

	public static int getTileNoX(double scale, double scaledWorldX) {
		int tileNoX = (int) (scaledWorldX / 256);
		return tileNoX;
	}

	public static int getTileNoY(double scale, double scaledWorldY) {
		int tileNoY = (int) (scaledWorldY / 256);
		return tileNoY;
	}

	public static double toWorldX(double lon) {
		double worldX = R * (Math.toRadians(lon) + Math.PI);
		return worldX;
	}

	public static double toWorldY(double lat) {
		double latRad = Math.toRadians(lat);
		double worldY = - R / 2 * Math.log( (1 + Math.sin(latRad)) / ( 1 - Math.sin(latRad)) ) + 128;
		return worldY;
	}

//	public static double toScaledWorldX(double scale, LonLat lonlat) {
////		double worldX = R * (Math.toRadians() + Math.PI);
////		return worldX * scale;
//		return toScaledWorldX(scale, lonlat.getLongitude());
//	}

	public static double toScaledWorldX(double scale, double lon) {
		double worldX = R * (Math.toRadians(lon) + Math.PI);
		return worldX * scale;
	}

	public static double toScaledWorldX(double scale, LonLat lonlat) {
//		double worldX = R * (Math.toRadians() + Math.PI);
//		return worldX * scale;
		return toScaledWorldX(scale, lonlat.getLongitude());
	}

	public static double toScaledWorldY(double scale, double lat) {
		double latRad = Math.toRadians(lat);
		double worldY = - R / 2 * Math.log( (1 + Math.sin(latRad)) / ( 1 - Math.sin(latRad)) ) + 128;
		return worldY * scale;
	}

	public static double toScaledWorldY(double scale, LonLat lonlat) {
//		double latRad = Math.toRadians(lonlat.getLatitude());
//		double worldY = - R / 2 * Math.log( (1 + Math.sin(latRad)) / ( 1 - Math.sin(latRad)) ) + 128;
//		return worldY * scale;
		return toScaledWorldY(scale, lonlat.getLatitude());
	}

	public static double getLongitudeFromWorldX(double worldX) {
		return Math.toDegrees((worldX / R) - Math.PI);
	}

	public static double getLatitudeFromWorldY(double worldY) {
		return Math.toDegrees(Math.atan(Math.sinh((128 - worldY) / R)));
	}

	public static double getLongitudeFromTileNoX(int tileNoX, double scale) {
		return getLongitudeFromWorldX(tileNoX * 256 / scale);
	}

	public static double getLongitudeFromGdemTileNoX(int tileNoX) {
		return tileNoX * 1f;
	}

	public static double getLatitudeFromGdemTileNoY(int tileNoY) {
		return tileNoY * 1f;
	}

	public static double getLatitudeFromTileNoY(int tileNoY, double scale) {
		return getLatitudeFromWorldY(tileNoY * 256 / scale);
	}
}
