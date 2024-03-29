package file.transport.model;

import com.alibaba.fastjson2.JSON;
import file.transport.execption.TransportRuntimeException;
import file.transport.model.TransportPiece.WriteMode;
import file.transport.utils.StorageConfigUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;

public class StorageMessage {

    private static final Log log = LogFactory.getLog(StorageMessage.class);
    private String directory;

    private WriteMode writeMode;

    private final Map<String, String> tempFileMap = new LinkedHashMap<>();

    private final Set<String> deleteFileSet = new LinkedHashSet<>();

    public StorageMessage() {
        this.directory = "";
    }

    public StorageMessage(String directory) {
        this.directory = FilenameUtils.separatorsToUnix(directory);
    }

    public Iterator<Entry<String, String>> iterator() {
        return tempFileMap.entrySet().iterator();
    }

    public Set<String> getDeleteFileSet() {
        return this.deleteFileSet;
    }

    public WriteMode getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(WriteMode writeMode) {
        this.writeMode = writeMode;
    }

    public void append(String newFileName, URL url) {
        InputStream input;
        try {
            input = url.openStream();
        } catch (IOException e) {
            log.error(e.getMessage());
            if (log.isErrorEnabled()) {
                log.error("append url[" + url + "] error: ", e);
            }
            throw new TransportRuntimeException("append url[" + url + "] error: ", e);
        }
        this.append(newFileName, input, true);
    }

    public void append(String newFileName, File file) {
        InputStream input;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            throw new TransportRuntimeException("file " + file.getAbsolutePath() + " doesn't exist: ", e);
        }
        this.append(newFileName, input, true);
    }

    public void append(String newFileName, InputStream input) {
        this.append(newFileName, input, false);
    }

    public void append(String newFileName, InputStream input, boolean autoCloseInputStream) {
        if (StringUtils.isBlank(newFileName)) {
            throw new TransportRuntimeException("append storage message content error: new file name is blank.");
        }
        if (input == null) {
            throw new TransportRuntimeException("append storage message content error: stream is null.");
        }
        String guid = UUID.randomUUID() + ".ser.tmp";
        File destFile = new File(generateDestFile(guid));
        if (destFile.exists()) {
            throw new TransportRuntimeException("file conflict: " + destFile.getAbsolutePath());
        }
        try {
            destFile.createNewFile();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new TransportRuntimeException("create temp storage file error: ", e);
        }
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(Files.newOutputStream(destFile.toPath()));
            IOUtils.copy(input, output);
            this.tempFileMap.put(newFileName, guid);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TransportRuntimeException("copy stream error: ", e);
        } finally {
            IOUtils.closeQuietly(output);
            if (autoCloseInputStream) {
                IOUtils.closeQuietly(input);
            }
        }
    }

    public String generateDestFile(String guid) {
        return StorageConfigUtils.getStorageTempDir() + "/" + guid;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void appendDrop(String deleteFileName) {
        this.deleteFileSet.add(deleteFileName);
    }

    public String sha() {
        final String json = JSON
                .toJSONString(new Object[]{this.directory, this.writeMode, this.tempFileMap, this.deleteFileSet});
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return DigestUtils.md5Hex(bytes);
    }

    public void destroy() {
        for (String foo : this.tempFileMap.values()) {
            File file = new File(this.generateDestFile(foo));
            FileUtils.deleteQuietly(file);
        }
    }
}