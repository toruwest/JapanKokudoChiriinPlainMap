package t.n.gps;

import t.n.map.common.LocationInfo;

public class GpsLocationInfoDetector {

	public static LocationInfo detect(String gpsSentence) {
		LocationInfo info = null;
		if(gpsSentence.startsWith("$GPRMC")) {
			int asteriskPos = gpsSentence.indexOf('*');
			if(asteriskPos > 0) {
				String[] split = gpsSentence.substring(0, asteriskPos).split(",");
				if(split.length > 1) {
					info = new LocationInfo(split[3], split[4], split[5], split[6]);
				}
			}
		} else if(gpsSentence.startsWith("$GPGGA")) {
			int asteriskPos = gpsSentence.indexOf('*');
			if(asteriskPos > 0) {
				String[] split = gpsSentence.substring(0, asteriskPos).split(",");
				if(split.length > 1) {
					info = new LocationInfo(split[2], split[3], split[4], split[5]);
				}
			}
		}
		return info;
	}

	public static boolean isContainsLocationInfo(String gpsSentence) {
		return (gpsSentence.startsWith("$GPRMC") || gpsSentence.startsWith("$GPGGA"));
	}
}
