package file.transport.engine;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import file.transport.execption.TransportException;
import file.transport.model.StorageMessage;
import file.transport.model.TransportPiece;

public interface SerializeEngine {

	void ser(OutputStream output, StorageMessage message) throws TransportException;

	Iterator<TransportPiece> dser(InputStream input) throws TransportException;
}
