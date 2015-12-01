package t.n.map.common;

import java.io.Serializable;

public abstract class AbstractHeightData implements Serializable {
	public enum HeightDataType {normal, noHeight, errortype, fetching};

	public AbstractHeightData() {}

	public abstract int getWidth();
	public abstract int getDepth();

	public abstract float get(int x, int y);

	public abstract HeightDataType getStatus();

}
