package t.n.map.common;

public class TilePosition {
	private final int x;

	private final int y;

	public TilePosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TilePosition) {
			TilePosition oponent = (TilePosition)obj;
			return (x == oponent.getX() && y == oponent.getY());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return x * Integer.MAX_VALUE / 2 + y;
	}
}
