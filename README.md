# 远程文件传输

### 部署
```
cd file-transport
mvn clean package
scp file-transport-manager/target/file-transport-manager-0.0.1-jar-with-dependencies.jar root@192.168.209.128:/root
启动监听程序
nohup java -jar file-transport-manager-0.0.1-jar-with-dependencies.jar -p1228 /data > file-transport.log &
比如IP为192.168.209.128，监听端口为1228，上传目录为/data
```

### 测试
```java
import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import file.transport.StorageEngineFactory;
import file.transport.execption.TransportException;
import file.transport.model.StorageMessage;
import file.transport.model.TransportPiece.WriteMode;

public class TransportTest {

    @Test
	public void transport() {
		StorageMessage message = new StorageMessage();
		message.setWriteMode(WriteMode.OVERWRITE);
		Collection<File> files = FileUtils.listFiles(new File("src"), new String[] { "java" }, true);
		for (File file : files) {
			message.append(file.getName(), file);
		}
		try {
			StorageEngineFactory.getInstance().getRemoteStorageEngine().transport(message, "192.168.209.128:1228");
		} catch (TransportException e) {
			e.printStackTrace();
		}
	}
}
```