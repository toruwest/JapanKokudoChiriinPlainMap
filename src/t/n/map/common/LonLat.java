package t.n.map.common;

public class LonLat {

	private final double longitude;
	private final double latitude;

	public LonLat(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	/**
	 * @return longitude 経度
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @return latitude 緯度
	 */
	public double getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return "緯度:" + latitude + ", 経度:" + longitude;
	}
}
