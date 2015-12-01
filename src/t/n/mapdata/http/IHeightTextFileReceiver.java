package t.n.mapdata.http;

import java.io.File;

import t.n.map.common.LightWeightTile;

public interface IHeightTextFileReceiver {

	void receiveHeightTextFile(LightWeightTile tile, File localFile, HeightDataStatus status);

}
