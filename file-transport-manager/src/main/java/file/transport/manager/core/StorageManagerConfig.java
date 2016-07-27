package file.transport.manager.core;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;

public class StorageManagerConfig {

	public static String TMP_DIR = FilenameUtils
			.normalizeNoEndSeparator(SystemUtils.getJavaIoTmpDir() + File.separator + "storagemanager", true);
	public static String ROOT_DIR = FilenameUtils
			.normalizeNoEndSeparator(SystemUtils.getUserHome().getAbsolutePath() + File.separator + "beauty-storage");

	public static int port = 1228;
}