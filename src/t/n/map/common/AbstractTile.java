package t.n.map.common;

public abstract class AbstractTile {

	protected int tileNoX;
	protected int tileNoY;
	protected double leftUpperLon;
	protected double leftUpperLat;
	protected double rightDownLon;
	protected double rightDownLat;

	public AbstractTile(int tileNoX, int tileNoY) {
		this.tileNoX = tileNoX;
		this.tileNoY = tileNoY;
	}

	public int getTileNoX() {
		return tileNoX;
	}

	public int getTileNoY() {
		return tileNoY;
	}

	public double getLeftUpperLon() {
		return leftUpperLon;
	}

	public double getLeftUpperLat() {
		return leftUpperLat;
	}

	public double getRightDownLon() {
		return rightDownLon;
	}

	public double getRightDownLat() {
		return rightDownLat;
	}

}
