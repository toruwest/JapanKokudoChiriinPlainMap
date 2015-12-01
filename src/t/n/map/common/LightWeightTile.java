package t.n.map.common;

import java.awt.Image;


public class LightWeightTile {

	private static final int TILE_Y_MAX = (int)Math.pow(2, 19);
	private final int tileNoX;
	private final int tileNoY;
	private boolean isError;
	private final int zoomLevel;
	private Image image;
	private AbstractHeightData heightData;

	public LightWeightTile(int zoomLevel, int tileNoX, int tileNoY) {
		this.zoomLevel = zoomLevel;
		this.tileNoX = tileNoX;
		this.tileNoY = tileNoY;
	}

	public LightWeightTile(int tileNoX, int tileNoY) {
		this(KokudoTile.HEIGHT_MAP_ZOOM_LEVEL, tileNoX, tileNoY);
	}

	public LightWeightTile(KokudoTile tile) {
		this.tileNoX = tile.getTileNoX();
		this.tileNoY = tile.getTileNoY();
		this.zoomLevel = tile.getZoomLevel();
		this.isError = tile.isError();
	}

	public int getTileNoX() {
		return tileNoX;
	}

	public int getTileNoY() {
		return tileNoY;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public boolean isError() {
		return isError;
	}

	public String getCause() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof LightWeightTile) {
			LightWeightTile oponent = (LightWeightTile)obj;
			return (tileNoX == oponent.getTileNoX() && tileNoY == oponent.getTileNoY());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return tileNoX * TILE_Y_MAX + tileNoY;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

	public void setHeightData(AbstractHeightData heightData) {
		this.heightData = heightData;
	}

	public AbstractHeightData getHeightData() {
		return heightData;
	}

}
