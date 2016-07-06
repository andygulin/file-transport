package file.transport.engine;

import file.transport.execption.TransportException;
import file.transport.model.StorageMessage;
import file.transport.model.TransportHandler;

public interface RemoteStorageEngine {

	void transport(StorageMessage message) throws TransportException;

	void transport(StorageMessage message, String ip, int port) throws TransportException;

	void transport(StorageMessage message, String... destHost) throws TransportException;

	void transportAsync(StorageMessage message, TransportHandler handler) throws TransportException;

	String[] nodes();

	void node(String ip, int port);

	void node(String... url);

	void removeNode(String ip, int port);

	void removeNode(String... url);

	void removeAllNode();

}
