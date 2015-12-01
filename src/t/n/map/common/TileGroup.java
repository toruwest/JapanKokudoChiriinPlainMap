package t.n.map.common;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import t.n.map.common.util.TileUtil;

public class TileGroup {
	protected int tileNoAtOriginX;
	protected int tileNoAtOriginY;
	protected final Set<Integer> tileNoXset;
	protected final Set<Integer> tileNoYset;

	protected Dimension dim;
	protected int tileSize;
	protected boolean useAsterDem;
	protected final Map<TilePosition, LightWeightTile> tileMap;
	protected LonLatRange range;
	protected int tileNoXmin = 0;
	protected int tileNoYmin = 0;
	protected int tileNoXmax = 0;
	protected int tileNoYmax = 0;

	public TileGroup() {
		tileMap = new HashMap<>();
		tileNoXset = new TreeSet<>();
		tileNoYset = new TreeSet<>();
	}

	public TileGroup(LonLat initCenterLonLat, int tileSize, Dimension dim) {
		this();
		this.tileSize = tileSize;
		this.dim = dim;
	}

	public TileGroup(LonLat initCenterLonlat, Dimension dim) {
		this(initCenterLonlat, KokudoTile.TILE_SIZE, dim);
	}

	public TileGroup(boolean useAsterDem, LonLatRange range, int tileSize) {
		this();
		this.useAsterDem = useAsterDem;
		this.tileSize = tileSize;
		this.range = range;

		LonLat leftUpperLonLat = new LonLat(range.getLonMin(), range.getLatMax());

		if(useAsterDem) {
			tileNoAtOriginX = TileUtil.getGdemTileNoX(leftUpperLonLat);
			tileNoAtOriginY = TileUtil.getGdemTileNoY(leftUpperLonLat);
		} else {
			tileNoAtOriginX = TileUtil.getTileNoX(KokudoTile.HEIGHT_MAP_ZOOM_LEVEL, leftUpperLonLat);
			tileNoAtOriginY = TileUtil.getTileNoY(KokudoTile.HEIGHT_MAP_ZOOM_LEVEL, leftUpperLonLat);
		}

		LonLat rightDownLonLat = new LonLat(range.getLonMax(), range.getLatMin());
		int tileCountX, tileCountY;
		if(useAsterDem) {
			tileCountX = TileUtil.getGdemTileNoX(rightDownLonLat) - tileNoAtOriginX;
			tileCountY = TileUtil.getGdemTileNoY(rightDownLonLat) - tileNoAtOriginY;
		} else {
			tileCountX = TileUtil.getTileNoX(KokudoTile.HEIGHT_MAP_ZOOM_LEVEL, rightDownLonLat) - tileNoAtOriginX;
			tileCountY = TileUtil.getTileNoY(KokudoTile.HEIGHT_MAP_ZOOM_LEVEL, rightDownLonLat) - tileNoAtOriginY;
		}

		if(tileCountX==0)tileCountX = 1;
		if(tileCountY==0)tileCountY = 1;
		dim = new Dimension(tileSize * tileCountX, tileSize * tileCountY);
	}

	public void adjust() {

		if(tileNoAtOriginX < 0) {
			System.out.println();
		}
		if(tileNoAtOriginY < 0) {
			System.out.println();
		}
	}

	public List<Integer> getXTilesList() {
		List<Integer> result = new ArrayList<>();
		Iterator<Integer> iterator = tileNoXset.iterator();
		while(iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	public List<Integer> getYTilesList() {
		List<Integer> result = new ArrayList<>();
		Iterator<Integer> iterator = tileNoYset.iterator();
		while(iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	public void reset() {
		tileNoXset.clear();
		tileNoYset.clear();
		tileNoXmin = Integer.MAX_VALUE;
		tileNoXmax = Integer.MIN_VALUE;
		tileNoYmin = Integer.MAX_VALUE;
		tileNoYmax = Integer.MIN_VALUE;
	}

	public int getTileNoAtOriginY() {
		return tileNoAtOriginY;
	}

	public int getTileNoAtOriginX() {
		return tileNoAtOriginX;
	}

	public int getRegionWidth() {
		return dim.width;
	}

	public int getRegionHeight() {
		return dim.height;
	}

	public Dimension getDimension() {
		return dim;
	}

	public int getTileSize() {
		return tileSize;
	}

	public int getTileCountX() {
		return tileNoXset.size();
	}

	public int getTileCountY() {
		return tileNoYset.size();
	}

	public void add(LightWeightTile tile) {
		int x = tile.getTileNoX();
		int y = tile.getTileNoY();
		TilePosition position = new TilePosition(x, y);
		tileMap.put(position, tile);
		tileNoXset.add(x);
		tileNoYset.add(y);
		tileNoXmin = Math.min(x, tileNoXmin);
		tileNoYmin = Math.min(y, tileNoYmin);
		tileNoXmax = Math.max(x, tileNoXmax);
		tileNoYmax = Math.max(y, tileNoYmax);
	}

	public LightWeightTile getTileAt(int tileNoX, int tileNoY) {
		Integer[] xarray = tileNoXset.toArray(new Integer[tileNoXset.size()]);
		int minx = xarray[0];
		int maxx = xarray[xarray.length - 1];
		Integer[] yarray = tileNoYset.toArray(new Integer[tileNoYset.size()]);
		int miny = yarray[0];
		int maxy = yarray[yarray.length - 1];
		if(minx <= tileNoX && tileNoX <= maxx && miny <= tileNoY && tileNoY <= maxy) {
			TilePosition position = new TilePosition(tileNoX, tileNoY);
			return tileMap.get(position);
		} else {
			return null;
		}
	}


	public LonLatRange getLonLatRange() {
		return range;
	}

	public List<TilePosition> getTilePositionList() {
		//FIXME 初回実行時はまだgetX/YTilesList()が空なので、結果も空になる。
		//TileImageManagerImpl.javaにてupdate()を追加したら大丈夫だった。
		List<TilePosition> result = new ArrayList<>();
		for(int tileNoY : getYTilesList()) {
			for(int tileNoX : getXTilesList()) {
				result.add(new TilePosition(tileNoX, tileNoY));
			}
		}

		return result;
	}

}
