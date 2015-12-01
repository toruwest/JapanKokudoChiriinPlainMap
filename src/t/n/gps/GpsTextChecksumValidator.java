package t.n.gps;

public class GpsTextChecksumValidator {
	public static String getExpectedSum(String line) {
		String result = null;
		int end   = line.indexOf('*');
		if(end != -1 && end + 2 < line.length()) {
			result = line.substring(end + 1, end + 3);
		}
		return result;
	}

	// '$' と'*'の間(これらを除く)のキャラクターのXORを計算する。
	public static byte computeActualChecksum(String line) {
		byte b = 0;

		if(line != null && !line.isEmpty()) {
			int begin = line.indexOf('$') + 1;
			int end   = line.indexOf('*');
			if(begin != -1 && end != -1) {
				for(int i = begin; i < end; i++) {
					b ^= line.charAt(i);
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
		return b;
	}

}
