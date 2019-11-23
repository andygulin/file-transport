package file.transport.engine.impl;

import com.dyuproject.protostuff.ByteString;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import file.transport.engine.SerializeEngine;
import file.transport.execption.TransportException;
import file.transport.model.StorageMessage;
import file.transport.model.TransportPiece;
import file.transport.model.TransportPiece.WriteMode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map.Entry;

public class SerializeEngineImpl implements SerializeEngine {

    private static transient final Log log = LogFactory.getLog(SerializeEngineImpl.class);

    @Override
    public void ser(OutputStream output, StorageMessage message) throws TransportException {
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        TransportPiece piece;
        int i = 0;
        // add send files
        for (Iterator<Entry<String, String>> it = message.iterator(); it.hasNext(); ) {
            Entry<String, String> entry = it.next();
            piece = new TransportPiece();
            if (StringUtils.equals("/", message.getDirectory()) || StringUtils.isBlank(message.getDirectory())) {
                piece.setDest(FilenameUtils.normalizeNoEndSeparator("/" + entry.getKey(), true));
            } else {
                piece.setDest(FilenameUtils.normalizeNoEndSeparator(message.getDirectory() + "/" + entry.getKey(), true));
            }
            final WriteMode wm = WriteMode.SKIP.equals(message.getWriteMode()) ? WriteMode.SKIP : WriteMode.OVERWRITE;
            piece.setMode(wm);
            piece.setPieceNum(i++);
            File tmpFile = new File(message.generateDestFile(entry.getValue()));
            try {
                piece.setContent(ByteString.copyFrom(FileUtils.readFileToByteArray(tmpFile)));
                ProtostuffIOUtil.writeDelimitedTo(output, piece, TransportPiece.getSchema(), linkedBuffer);
                linkedBuffer.clear();
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
                if (log.isErrorEnabled()) {
                    log.error("write storage message error: ", e);
                }
                throw new TransportException("write storage message error: ", e);
            }
        }
        // add delete files
        for (String foo : message.getDeleteFileSet()) {
            piece = new TransportPiece();
            if (StringUtils.equals("/", message.getDirectory()) || StringUtils.isBlank(message.getDirectory())) {
                piece.setDest(FilenameUtils.normalizeNoEndSeparator("/" + foo, true));
            } else {
                piece.setDest(FilenameUtils.normalizeNoEndSeparator(message.getDirectory() + "/" + foo, true));
            }
            piece.setMode(WriteMode.DELETE);
            piece.setPieceNum(i++);
            try {
                ProtostuffIOUtil.writeDelimitedTo(output, piece, TransportPiece.getSchema(), linkedBuffer);
                linkedBuffer.clear();
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
                if (log.isErrorEnabled()) {
                    log.error("write storage message error: ", e);
                }
                throw new TransportException("write storage message error: ", e);
            }
        }
    }

    @Override
    public Iterator<TransportPiece> dser(InputStream input) throws TransportException {
        return new InnerIterator(input);
    }

    private static class InnerIterator implements Iterator<TransportPiece> {

        private InputStream input;

        private TransportPiece message;

        InnerIterator(InputStream input) {
            this.input = input;
        }

        @Override
        public boolean hasNext() {
            TransportPiece tmp = new TransportPiece();
            try {
                ProtostuffIOUtil.mergeDelimitedFrom(input, tmp, TransportPiece.getSchema());
                this.message = tmp;
            } catch (IOException e) {
                final String msg = "dser from stream stop: ";
                if (log.isDebugEnabled()) {
                    log.debug(msg, e);
                }
                this.message = null;
            }
            return this.message != null;
        }

        @Override
        public TransportPiece next() {
            return this.message;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("unsupport remove TransportPiece!");
        }
    }
}