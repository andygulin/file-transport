package file.transport.manager.core;

import com.dyuproject.protostuff.ByteString;
import file.transport.StorageEngineFactory;
import file.transport.execption.TransportException;
import file.transport.execption.TransportRuntimeException;
import file.transport.model.TransportPiece;
import file.transport.model.TransportPiece.WriteMode;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.UUID;

public class ClientSocketRunnable implements Runnable {

    private static final Log log = LogFactory.getLog(ClientSocketRunnable.class);

    private Socket socket;

    public ClientSocketRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        InputStream input = null;
        OutputStream output = null;

        String newPath = FilenameUtils.normalizeNoEndSeparator(StorageManagerConfig.TMP_DIR + File.separator + UUID.randomUUID().toString() + ".storage", true);
        OutputStream out = null;

        try {
            input = this.socket.getInputStream();
            output = this.socket.getOutputStream();

            File newFile = new File(newPath);
            File pFile = newFile.getParentFile();
            if (!pFile.exists() || pFile.isFile()) {
                pFile.mkdirs();
            }

            if (newFile.exists()) {
                newFile.delete();
            }

            newFile.createNewFile();

            out = new BufferedOutputStream(new FileOutputStream(newFile));

            byte[] buffer = new byte[4096];
            int n;
            while ((n = input.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                out.flush();
            }
            out.close();

            this.processMessageFromFile(newPath);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "recieve client message error: ";
            throw new TransportRuntimeException(errorMsg, e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                final String foo = "close client socket error: ";
                if (log.isErrorEnabled()) {
                    log.error(foo, e);
                }
                throw new TransportRuntimeException(foo, e);
            }
        }

    }

    private void processMessageFromFile(String file) throws TransportException {
        if (log.isDebugEnabled()) {
            log.debug("----------------------------------------");
            log.debug("process message from file " + file + " start...");
            log.debug("----------------------------------------");
        }

        InputStream inputStream;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));

            Iterator<TransportPiece> it = StorageEngineFactory.getInstance().getSerializeEngine().dser(inputStream);

            int num = 0;
            while (it.hasNext()) {
                TransportPiece foo = it.next();
                this.processPiece(foo);
                num++;
            }
            if (log.isDebugEnabled()) {
                log.debug("----------------------------------------");
                log.debug(num + " pieces finished!");
                log.debug("process message from file " + file + " SUCCESS!!!");
                log.debug("----------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (log.isErrorEnabled()) {
                log.error("----------------------------------------");
                log.error("process message from file " + file + " ERROR!!!");
                log.error("----------------------------------------");
            }
            throw new TransportException(e);
        } finally {
            new File(file).delete();
        }
    }

    private void processPiece(TransportPiece piece) throws TransportException {
        if (piece == null) {
            return;
        }

        String destFilePath = FilenameUtils
                .normalizeNoEndSeparator(StorageManagerConfig.ROOT_DIR + File.separator + piece.getDest(), true);
        File destFile = new File(destFilePath);
        if (destFile.exists()) {
            if (WriteMode.SKIP.equals(piece.getMode())) {
                return;
            } else if (WriteMode.OVERWRITE.equals(piece.getMode())) {
                destFile.delete();
            } else if (WriteMode.DELETE.equals(piece.getMode())) {
                destFile.delete();
                return;
            } else {
                throw new TransportException("unknow write mode:" + piece.getMode());
            }
        }
        File destFileParent = destFile.getParentFile();
        if (!destFileParent.exists() || destFileParent.isFile()) {
            boolean mkdirSuccess = destFileParent.mkdirs();
            if (!mkdirSuccess) {
                throw new TransportException("mkdirs failed: " + destFileParent.getAbsolutePath());
            }
        }
        try {
            destFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            if (log.isErrorEnabled()) {
                log.error("create dest file failed: " + destFile.getAbsolutePath(), e);
            }
            throw new TransportException("create dest file failed: " + destFile.getAbsolutePath(), e);
        }
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(destFile));
            ByteString.writeTo(output, piece.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            if (log.isErrorEnabled()) {
                log.error("write dest file failed: " + destFile.getAbsolutePath(), e);
            }
            throw new TransportException("write dest file failed: " + destFile.getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}