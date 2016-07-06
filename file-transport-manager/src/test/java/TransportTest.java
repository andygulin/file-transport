package file.transport;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import file.transport.execption.TransportException;
import file.transport.model.StorageMessage;
import file.transport.model.TransportPiece.WriteMode;

public class TransportTest {

	@Test
	public void transport() {
		StorageMessage message = new StorageMessage();
		message.setWriteMode(WriteMode.OVERWRITE);
		Collection<File> files = FileUtils.listFiles(new File("E:/dl"), new String[] { "jpg" }, false);
		for (File file : files) {
			message.append(file.getName(), file);
		}
		try {
			StorageEngineFactory.getInstance().getRemoteStorageEngine().transport(message, "127.0.0.1:1228");
		} catch (TransportException e) {
			e.printStackTrace();
		}
	}
}
