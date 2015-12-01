package t.n.map.common.util;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import t.n.map.common.KokudoTile;
import t.n.map.common.LonLat;
import t.n.map.common.LonLatRange;

public class LonLatUtil {
	public static String getLonLatString(LonLat lonlat) {
		if(lonlat != null) {
			return getLatitudeJapaneseString(lonlat.getLatitude()) + ", " + getLongitudeJapaneseString(lonlat.getLongitude());
		} else {
			return "";
		}
	}

	/**
	 * doubleで表された経度を、東経DD度MM分SS.SSS秒という形式の文字列に変換する。
	 * マイナスの場合は、西経になる。
	 * @param longitude
	 * @return
	 */
	public static String getLongitudeJapaneseString(double longitude) {
		String head;
		double tmp;
		if(longitude > 0) {
			head = "東経";
			tmp = longitude;
		} else if(longitude < 0) {
			head = "西経";
			tmp = -longitude;
		} else {
			head = "";
			tmp = 0;
		}
		double lonH = Math.floor(tmp);
		double lonM = (tmp - lonH) * 60;
		double lonM2 = Math.floor(lonM);
		double lonS = (lonM - lonM2)*60;

		return MessageFormat.format("{0}{1}度{2}分{3}秒", head, lonH, lonM2, lonS);
	}

	/**
	 * doubleで表された緯度を、北緯DD度MM分SS.SSS秒という形式の文字列に変換する。
	 * マイナスの場合は、南緯になる。
	 */
	public static String getLatitudeJapaneseString(double latitude) {
		String head;
		double tmp;
		if(latitude > 0) {
			head = "北緯";
			tmp = latitude;
		} else if(latitude < 0) {
			head = "南緯";
			tmp = -latitude;
		} else {
			head = "";
			tmp = 0;
		}
		double latH = Math.floor(tmp);
		double latM = (tmp - latH) * 60;
		double latM2 = Math.floor(latM);
		double latS = (latM - latM2)*60;

		return MessageFormat.format("{0}{1}度{2}分{3}秒", head, latH, latM2, latS);
	}

	/**
	 * doubleで表された経度を、"{E|W}DD:MM:SS.SSS"という形式の文字列に変換する。結果の経度の整数部分は3桁とする。
	 * "{E|W}"については、プラスの場合は"E"、マイナスの場合は"W"で始まる文字列になる。ちょうど0の場合、この文字は含まれない。
	 * @param longitude
	 * @param fractionOnly
	 * @return
	 */
	public static String getLongitudeEnglishString(double longitude, boolean fractionOnly) {
		String head;
		double tmp;
		if(longitude > 0) {
			head = "E";
			tmp = longitude;
		} else if(longitude < 0) {
			head = "W";
			tmp = -longitude;
		} else {
			head = "";
			tmp = 0;
		}
		double lonH = Math.floor(tmp);
		if(fractionOnly) {
			//"012"のように、ゼロパディングしたい。
			String num = String.format("%03d", (int)lonH);
			return MessageFormat.format("{0}{1}", head, num);
		} else {
			double lonM = (tmp - lonH) * 60;
			double lonM2 = Math.floor(lonM);
			double lonS = (lonM - lonM2)*60;
			return MessageFormat.format("{0}{1}:{2}:{3}", head, lonH, lonM2, lonS);
		}
	}

	/**
	 * doubleで表された緯度を、"{N|S}DD:MM:SS.SSS"という形式の文字列に変換する。結果の緯度の整数部分は2桁とする。
	 * "{N|S}"については、プラスの場合は"N"、マイナスの場合は"S"で始まる文字列になる。ちょうど0の場合、この文字は含まれない。
	 */
	public static String getLatitudeEnglishString(double latitude, boolean fractionOnly) {
		String head;
		double tmp;
		if(latitude > 0) {
			head = "N";
			tmp = latitude;
		} else if(latitude < 0) {
			head = "S";
			tmp = -latitude;
		} else {
			head = "";
			tmp = 0;
		}

		double latH = Math.floor(tmp);
		if(fractionOnly) {
			//"02"のように、ゼロパディングしたい。
			String num = String.format("%02d", (int)latH);
			return MessageFormat.format("{0}{1}", head, num);
		} else {
			double latM = (tmp - latH) * 60;
			double latM2 = Math.floor(latM);
			double latS = (latM - latM2)*60;
			return MessageFormat.format("{0}{1}:{2}:{3}", head, latH, latM2, latS);
		}
	}

	private final static Pattern p = Pattern.compile("(\\d+)°(\\d+)′(\\d+)″");

	/**
	 * "153°59′11″"という形式で表現された緯度・経度を、153.98638888のような数値に変換する。
	 * @param arg
	 * @return
	 */
	public static double parseDegMinSec(String arg) {
		double result = Double.NaN;
		Matcher m = p.matcher(arg);
		if(m.matches() && m.groupCount() == 3) {
			double deg = Double.parseDouble(m.group(1));
			double min = Double.parseDouble(m.group(2));
			double sec = Double.parseDouble(m.group(3));
			result = deg + (min / 60) + (sec / 3600);
		}
		return result;
	}

	/**
	 * lonlatが、rangeで囲まれる領域に含まれるかどうかを判定する。
	 * @param LonLatRange range
	 * @param lonlat
	 * @return 領域内に含まれていればtrue
	 */
	public static boolean isInside(LonLatRange range, LonLat lonlat) {
		double lon = lonlat.getLongitude();
		double lat = lonlat.getLatitude();
		return (range.getLonMin() <= lon && range.getLonMax() >= lon
				&& range.getLatMin() <= lat && range.getLatMax() <= lat);
	}

	//以下はhttp://www.gsi.go.jp/KOKUJYOHO/center.htmより抜粋（世界測地系)
	private final static String eastRegionString = "153°59′11″";//最東端 	東京都　南鳥島
	private final static String westRegionString = "122°56′01″";//最西端 	沖縄県　与那国島
	private final static String southRegionString = "20°25′31″";//最南端 	東京都　沖ノ鳥島
	private final static String northRegionString = "45°33′28″";//最北端 	北海道　択捉島

	private final static double northRegion;
	private final static double eastRegion;
	private final static double southRegion;
	private final static double westRegion;

	static {
		eastRegion = LonLatUtil.parseDegMinSec(eastRegionString);
		westRegion = LonLatUtil.parseDegMinSec(westRegionString);
		northRegion = LonLatUtil.parseDegMinSec(northRegionString);
		southRegion = LonLatUtil.parseDegMinSec(southRegionString);
	}

	//タイルの左上の角と、右下の角が両方とも境界の外にあるかを判定する。
	public static boolean isOutOfJapanRegion(KokudoTile tile) {
		return isOutOfJapanRegion(tile.getLeftUpperLon(), tile.getLeftUpperLat()) && isOutOfJapanRegion(tile.getRightDownLon(), tile.getRightDownLat());
	}

	public static boolean isOutOfJapanRegion(LonLat lonlat) {
		double lon = lonlat.getLongitude();
		double lat = lonlat.getLatitude();
		return isOutOfJapanRegion(lon, lat);
	}

	public static boolean isOutOfJapanRegion(double lon, double lat) {
		return (lon < westRegion ||
			lon > eastRegion ||
			lat > northRegion ||
			lat < southRegion);
	}

	//TODO　テストケースからしか呼ばれていない。実質的に使われていない！
	public static LonLat parseLonLatDegMinSec(String arg) {
		String[] lonlatString = arg.split(" ");
		double lon = parseDegMinSec(lonlatString[0]);
		double lat = parseDegMinSec(lonlatString[1]);
		return new LonLat(lon, lat);
	}

}
