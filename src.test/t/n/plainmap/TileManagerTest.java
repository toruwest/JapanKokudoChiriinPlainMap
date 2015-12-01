package t.n.plainmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.util.List;

import org.junit.Test;

import t.n.map.IFetchingStatusObserver;
import t.n.map.common.KokudoTile;
import t.n.map.common.TilePosition;
import t.n.map.common.LightWeightTile;
import t.n.map.common.LonLat;

public class TileManagerTest implements IFetchingStatusObserver {

	private static final int INITIAL_ZOOM_LEVEL = 13;
	//以下は東京駅付近の座標
	private static final double initLongitude = 139.768553d;
	private static final double initLatitude = 35.682286d;
//	private LonLat initLonLat = new LonLat(139.768553d, 35.682286d);

	@Test
	public void test1() {
		ITileImageManager imageManager = new TileImageManagerImpl(this, INITIAL_ZOOM_LEVEL, new LonLat(initLongitude, initLatitude));
		int w = 600;
		int h = 350;

		imageManager.init(w, h, KokudoTile.HEIGHT_MAP_ZOOM_LEVEL);
		List<TilePosition> posList = imageManager.getTilePositionList();
		assertEquals(9, posList.size());

		Rectangle rect = rangeCheck(posList, w, h);
		assertTrue(rect.x <= 256);
		assertTrue(-256 < rect.x);
		assertTrue(w <= rect.width + 256);
		assertTrue(rect.y < h);
		assertTrue(h <= rect.height + 256);
	}

	@Test
	public void test2() {
		ITileImageManager imageManager = new TileImageManagerImpl(this, INITIAL_ZOOM_LEVEL, new LonLat(initLongitude, initLatitude));
		int w = 1024;
		int h = 768;

		imageManager.init(w, h, KokudoTile.HEIGHT_MAP_ZOOM_LEVEL);
		List<TilePosition> posList = imageManager.getTilePositionList();
		assertEquals(15, posList.size());

		Rectangle rect = rangeCheck(posList, w, h);
		assertTrue(rect.x <= 256);
		assertTrue(-256 < rect.x);
		assertTrue(w <= rect.width + 256);
		assertTrue(rect.y < h);
		assertTrue(h <= rect.height + 256);
	}

	@Test
	public void test3() {
		ITileImageManager imageManager = new TileImageManagerImpl(this, INITIAL_ZOOM_LEVEL, new LonLat(initLongitude, initLatitude));
		int w = 1152;
		int h = 800;

		imageManager.init(w, h, KokudoTile.HEIGHT_MAP_ZOOM_LEVEL);
		List<TilePosition> posList = imageManager.getTilePositionList();
		assertEquals(25, posList.size());

		Rectangle rect = rangeCheck(posList, w, h);
		assertTrue(rect.x <= 256);
		assertTrue(-256 < rect.x);
		assertTrue(w <= rect.width + 256);
		assertTrue(rect.y < h);
		assertTrue(h <= rect.height + 256);
	}

	@Test
	public void test4() {
		ITileImageManager imageManager = new TileImageManagerImpl(this, INITIAL_ZOOM_LEVEL, new LonLat(initLongitude, initLatitude));
		int w = 300;
		int h = 223;

		imageManager.init(w, h, KokudoTile.HEIGHT_MAP_ZOOM_LEVEL);
		List<TilePosition> posList = imageManager.getTilePositionList();
		assertEquals(3, posList.size());

		Rectangle rect = rangeCheck(posList, w, h);
		assertTrue(rect.x <= 256);
		assertTrue(-256 < rect.x);
		assertTrue(w <= rect.width + 256);
		assertTrue(rect.y < h);
		assertTrue(h <= rect.height + 256);
	}

	private Rectangle rangeCheck(List<TilePosition> posList, int w, int h) {

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int x = 0, y = 0;

		for(TilePosition p : posList) {
			x = p.getX();
			y = p.getY();
			System.out.println(x + ":" + y);
			if(x > maxX) maxX = x;
			if(x < minX) minX = x;
			if(y > maxY) maxY = y;
			if(y < minY) minY = y;
		}
		//Rectangleの３番目と４番目はwidth, heightだけどDTOとして使いたいだけなのでmaxX,maxYを格納する。
		return new Rectangle(minX, minY, maxX, maxY);
	}

	@Override
	public void notifyStartFetching(String uri) {
	}

	@Override
	public void notifyErrorFetching(String uriCopy) {
	}

	@Override
	public void notifyFetchCompleted(LightWeightTile tile) {
	}

}
