package t.n.plainmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import t.n.map.FolderConfig;
import t.n.map.common.KokudoTile;

public class NoImageDataManager {

	private static final String NO_IMAGE_AREA_DAT = "noImageArea.dat";

	public static Set<KokudoTile> initOrLoad() {
		File serializedFile = new File(FolderConfig.getCacheFolder(), NO_IMAGE_AREA_DAT);
		if(serializedFile.exists()) {
			HashSet<KokudoTile> obj = null;
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedFile));
				obj = (HashSet<KokudoTile>) ois.readObject();
			} catch (Exception e) {
				return new HashSet<>();
			}
			return obj;
		} else {
			return new HashSet<>();
		}
	}

	public static void saveAndClose(Set<KokudoTile> noImageDataSet) throws IOException {
		File serializedFile = new File(FolderConfig.getCacheFolder(), NO_IMAGE_AREA_DAT);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializedFile));
			oos.writeObject(noImageDataSet);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
