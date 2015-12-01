package t.n.map;

import java.awt.Image;

import t.n.map.common.LightWeightTile;
import t.n.plainmap.ImageDataStatus;

public interface IImageReceiver {

	void setImage(LightWeightTile tile, Image image, ImageDataStatus status);

}
