package t.n.plainmap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import t.n.map.common.LonLat;

public class MapPreference {
	private static final String DEFAULT_PREF_FILENAME ="kokudoTiledMap.pref";
	private static final String INITIAL_LOCATION_LONGITUDE_KEY = "initialLongitude";
	private static final String INITIAL_LOCATION_LATITUDE_KEY = "initialLatitude";

	private String prefFilename = null;
	private final Properties prop = new Properties();
	private final File dataDir;

	public MapPreference(File dataDir){
		this.dataDir = dataDir;
//		System.out.println(dataDir.getAbsolutePath());
		this.prefFilename = DEFAULT_PREF_FILENAME;
	}

//	//JUnit によるテスト用。
//	public Preference(File dataDir, String prefFilename){
//		this(dataDir);
//		if(DEFAULT_PREF_FILENAME.equals(prefFilename)) {
//			throw new IllegalArgumentException("Please do not specify the default property file name. Try another one in order to protect default preference file that is used for application");
//		} else {
//			this.prefFilename = prefFilename;
//		}
//	}

	public LonLat loadInitialLocation() {
		FileReader reader = null;
		LonLat result = null;
		try {
			File prefFile = new File(dataDir, prefFilename);
			if(prefFile.exists()) {
				reader = new FileReader(prefFile);
				prop.load(reader);
				Double lon = Double.valueOf((String)prop.get(INITIAL_LOCATION_LONGITUDE_KEY));
				Double lat = Double.valueOf((String)prop.get(INITIAL_LOCATION_LATITUDE_KEY));
				result = new LonLat(lon, lat);
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public void saveInitialLocation(LonLat location) {
		FileWriter writer = null;
		try {
			if(!dataDir.exists())dataDir.mkdirs();
			File prefFile = new File(dataDir, prefFilename);
			writer = new FileWriter(prefFile);
			prop.setProperty(INITIAL_LOCATION_LONGITUDE_KEY, String.valueOf(location.getLongitude()));
//			prop.setProperty(INITIAL_LOCATION_LONGITUDE_KEY, location.getLongitude());
			prop.setProperty(INITIAL_LOCATION_LATITUDE_KEY, String.valueOf(location.getLatitude()));
//			prop.setProperty(INITIAL_LOCATION_LATITUDE_KEY, location.getLatitude());
			prop.store(writer, "");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean exists() {
		File f = new File(dataDir, prefFilename);
		return f.exists();
	}
}
