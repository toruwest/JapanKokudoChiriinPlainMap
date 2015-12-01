package t.n.plainmap;

import java.util.List;

import t.n.map.common.TilePosition;
import t.n.map.common.LightWeightTile;
import t.n.map.common.LonLat;

public interface ITileImageManager {
//	Map<LightweightTile, Image> getTileImageMap();
//	List<LightWeightTile> getTiles();
//	List<TilePosition> getTilePositionList();
//	Map<TilePosition, LightWeightTile> getPositionTileMap();

	void init(int width, int height, int zoomLevel);
	void resize(int width, int height);
	void move(float centerX, float centerY);
	void zoom(int zoomLevel);
	void moveLocationTo(LonLat newLocation);
	LonLat getCurrentLocation();
	LightWeightTile getTileAt(TilePosition pos);
	List<TilePosition> getTilePositionList();
//	LightWeightTile getOriginTile();

	int getTilePositionNearOriginX();
	int getTilePositionNearOriginY();
	int getOriginTileNoX();
	int getOriginTileNoY();

	void close();
}
