package file.transport.utils;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import file.transport.execption.TransportRuntimeException;

public class StorageConfigUtils {
	private static transient final Log log = LogFactory.getLog(StorageConfigUtils.class);

	private static Configuration cfg = null;

	private static String STORAGE_TEMP_DIR = null;
	private static String STORAGE_DIR = null;

	static {
		String propFileName = "storage.cfg.properties";
		try {
			cfg = new PropertiesConfiguration(StorageConfigUtils.class.getClassLoader().getResource(propFileName));
			STORAGE_TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "yicha-commons-storage"
					+ File.separator + "temp";
			STORAGE_DIR = System.getProperty("java.io.tmpdir") + File.separator + "yicha-commons-storage"
					+ File.separator + "data";
			FolderUtils.mkdirs(STORAGE_DIR, STORAGE_TEMP_DIR);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			String errorMsg = "load " + propFileName + " error: ";
			if (log.isErrorEnabled()) {
				log.error(errorMsg, e);
			}
			throw new TransportRuntimeException(errorMsg, e);
		}
	}

	public static final Configuration getConfiguration() {
		return cfg;
	}

	private StorageConfigUtils() {
	}

	public static final String getStorageTempDir() {
		return STORAGE_TEMP_DIR;
	}

	public static final String getStorageDir() {
		return STORAGE_DIR;
	}
}
