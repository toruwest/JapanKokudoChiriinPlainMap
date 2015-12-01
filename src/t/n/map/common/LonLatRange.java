package t.n.map.common;


public class LonLatRange {

	private double lonMin = 0;

	private double lonMax = 0;
	private double latMin = 0;
	private double latMax = 0;

	public LonLatRange(double lonMin, double lonMax, double latMin, double latMax) {
		this.lonMin = lonMin;
		this.lonMax = lonMax;
		this.latMin = latMin;
		this.latMax = latMax;
	}

	public double getLonMin() {
		return lonMin;
	}

	public double getLonMax() {
		return lonMax;
	}

	public double getLatMin() {
		return latMin;
	}

	public double getLatMax() {
		return latMax;
	}
}
