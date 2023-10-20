package file.transport.utils;

import file.transport.execption.TransportRuntimeException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class StorageConfigUtils {
    private static final Log log = LogFactory.getLog(StorageConfigUtils.class);

    private static final Configuration cfg;

    private static final String STORAGE_TEMP_DIR;
    private static final String STORAGE_DIR;

    static {
        String propFileName = "storage.cfg.properties";
        try {
            cfg = new PropertiesConfiguration(StorageConfigUtils.class.getClassLoader().getResource(propFileName));
            STORAGE_TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "file-transport-storage"
                    + File.separator + "temp";
            STORAGE_DIR = System.getProperty("java.io.tmpdir") + File.separator + "file-transport-storage"
                    + File.separator + "data";
            FolderUtils.mkdirs(STORAGE_DIR, STORAGE_TEMP_DIR);
        } catch (ConfigurationException e) {
            log.error(e.getMessage());
            String errorMsg = "load " + propFileName + " error: ";
            if (log.isErrorEnabled()) {
                log.error(errorMsg, e);
            }
            throw new TransportRuntimeException(errorMsg, e);
        }
    }

    private StorageConfigUtils() {
    }

    public static Configuration getConfiguration() {
        return cfg;
    }

    public static String getStorageTempDir() {
        return STORAGE_TEMP_DIR;
    }

    public static String getStorageDir() {
        return STORAGE_DIR;
    }
}