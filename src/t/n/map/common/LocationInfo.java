package t.n.map.common;

public class LocationInfo {

	private final float lon;
	private final float lat;

	//以下で得られる値は、ddd.mmmmmmの形式
	public LocationInfo(String latStr, String nsStr, String lonStr, String ewStr) {
		if(lonStr != null && nsStr != null && latStr != null && ewStr!= null &&	!lonStr.isEmpty() && !nsStr.isEmpty() && !latStr.isEmpty() && !ewStr.isEmpty() ) {
			 lon = Float.parseFloat(lonStr) * (ewStr.equals("E")?1:nsStr.equals("W")?-1:0) / 100;
			 lat = Float.parseFloat(latStr) * (nsStr.equals("N")?1:nsStr.equals("S")?-1:0) / 100;
		} else {
			lon = Float.NaN;
			lat = Float.NaN;
		}
	}

	public double getLonInDegMinSec() {
		return toDegMinFromDegree(lon);
	}

	public double getLatInDegMinSec() {
		return toDegMinFromDegree(lat);
	}

	//以下で得られる値は、ddd.mmmmmmの形式
	public Float getLonInDegrees() {
		return lon;
	}

	//以下で得られる値は、ddd.mmmmmmの形式
	public Float getLatInDegrees() {
		return lat;
	}

	/**
	 * GPSデバイスから得られるNMEAフォーマットに含まれる緯度・経度(dddmm.mmmm)
	 * (139°45′39.94は、13945.3994 で表されている)を、
	 * 139.756656のような度分表記の数値に変換する。
	 * 秒以下は分の小数点として表現されている。
	 * @param arg
	 * @return
	 */
	public static double toDegMinFromDegree(double arg) {
		double deg = Math.floor(arg); //度の部分
		double tmp = arg - deg;
		double min = tmp * 100 / 60; //分
		return deg + min;
	}


}
