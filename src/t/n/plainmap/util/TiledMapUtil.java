package t.n.plainmap.util;

import java.io.File;

import t.n.map.common.LightWeightTile;

/**
 * 電子国土地図のディレクトリ構成が変わったので、使えなくなった。TiledMapUtil2.javaを使うこと。
 * @author toru
 */
public class TiledMapUtil {
	private static final String baseURI = "http://cyberjapandata.gsi.go.jp/sqras/all/";

	//zoomLevelが異なるファイルが重複しないように、zoomLevelによって格納するフォルダを分ける。
	public static String generateLocalFilename(File savingdir, LightWeightTile tile) {
		int zoomLevel = tile.getZoomLevel();
		int tileNoX = tile.getTileNoX();
		int tileNoY = tile.getTileNoY();
		return generateLocalFilename(savingdir, zoomLevel, tileNoX, tileNoY);
	}

	public static String generateLocalFilename(File savingdir, int zoomLevel, int tileNoX, int tileNoY) {
		StringBuilder sb = new StringBuilder();
		sb.append(savingdir.getPath());
		sb.append(File.separator);
		sb.append(zoomLevel);
		sb.append(File.separator);
		sb.append(generateFilename(tileNoX, tileNoY));
		return sb.toString();
	}

	private static StringBuilder generateFilename(int tileNoX, int tileNoY) {
		StringBuilder sb = format(tileNoX);
		sb.append(format(tileNoY));
		sb.append(".png");
		return sb;
	}

	private static StringBuilder format(int tileNo) {
		StringBuilder sb = new StringBuilder();
		String num = String.valueOf(tileNo);
		for(int i = 0; i < 7 - num.length(); i++) {
			sb.append("0");
		}
		sb.append(num);
		return sb;
	}

	//http://portal.cyberjapan.jp/portalsite/version/v4/directoryindex.html
	private static String generateDirectoryIndex(String filename) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 6; i++) {
			sb.append(filename.substring(i, i+1));
			sb.append(filename.substring(i+7, i+8));
			sb.append("/");
		}
		return sb.toString();
	}

	public static String generateImageURI(LightWeightTile tile) {
		return generateImageURI(tile.getZoomLevel(), tile.getTileNoX(), tile.getTileNoY());
	}

	public static String generateImageURI(int zoomLevel, int tileNoX, int tileNoY) {
		StringBuilder sb = new StringBuilder();
		String dataID = getDataIDForZoomLevel(zoomLevel);
		String filename = generateFilename(tileNoX, tileNoY).toString();
		sb.append(baseURI);
		sb.append(dataID);
		sb.append("/latest/");
		sb.append("/");
		sb.append(zoomLevel);
		sb.append("/");
		sb.append(generateDirectoryIndex(filename));
		sb.append(filename);
		return sb.toString();
	}

	/**
	 * @See http://portal.cyberjapan.jp/portalsite/version/v4/haishin.html
	 */
	private static String getDataIDForZoomLevel(int zoomLevel) {
		switch(zoomLevel) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			return "GLMD";
		case 5:
		case 6:
		case 7:
		case 8:
			return "JAIS";
		case 9:
		case 10:
		case 11:
			return "BAFD1000K";
		case 12:
		case 13:
		case 14:
			return "BAFD200K";
		case 15:
		case 16:
		case 17:
			return "DJBMM";
		case 18:
			return "FGD";
		default:
			throw new IllegalArgumentException("範囲外:" + zoomLevel);
		}
	}
}
