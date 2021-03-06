package file.transport.manager;

import file.transport.manager.core.ClientSocketRunnable;
import file.transport.manager.core.ClientSocketThreadPool;
import file.transport.manager.core.StorageManagerConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class StorageServer {

    private static final Log log = LogFactory.getLog(StorageServer.class);

    private ServerSocket serverSocket;

    private StorageServer(int port, String storePath) throws IOException {
        File dataDir = new File(FilenameUtils.normalizeNoEndSeparator(storePath, true));
        if (!dataDir.exists()) {
            boolean success = dataDir.mkdirs();
            if (!success) {
                throw new IOException("create folder error: " + dataDir);
            }
        }

        // 初始化参数
        StorageManagerConfig.port = port;
        StorageManagerConfig.ROOT_DIR = FilenameUtils.normalizeNoEndSeparator(dataDir.getAbsolutePath(), true);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("usage: java -jar your.jar -p1228 /home/user/yourdatahome");
        }

        if (!args[0].startsWith("-p") && !StringUtils.isNumeric(args[0].substring(2))) {
            throw new IllegalArgumentException("usage: java -jar your.jar -p1228 /home/user/yourdatahome");
        }

        String storePath = args[1];
        int port = Integer.parseInt(args[0].substring(2));

        StorageServer server = new StorageServer(port, storePath);
        server.start();
    }

    public void stop() throws IOException {
        this.serverSocket.close();
        ClientSocketThreadPool.shutdown();
    }

    private void start() throws IOException {
        serverSocket = new ServerSocket(StorageManagerConfig.port);
        if (log.isInfoEnabled()) {
            log.info("Storage Server start success!");
            log.info("port: " + StorageManagerConfig.port);
            log.info("data folder: " + StorageManagerConfig.ROOT_DIR);
            log.info("temp folder: " + StorageManagerConfig.TMP_DIR);
        }
        for (; ; ) {
            Socket client;
            try {
                client = serverSocket.accept();
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("serversocket is closed.");
                }
                return;
            }

            if (client != null) {
                if (log.isDebugEnabled()) {
                    log.debug("-----------------------------------------");
                    final String dateStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
                    log.debug("accept client message @" + dateStr + ":");
                    log.debug("-----------------------------------------");
                }
                ClientSocketRunnable task = new ClientSocketRunnable(client);
                ClientSocketThreadPool.invoke(task);
            }
        }
    }
}