package file.transport.manager.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSocketThreadPool {
	private static ExecutorService pool;

	static {
		synchronized (ClientSocketThreadPool.class) {
			pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);
		}
	}

	public static void invoke(ClientSocketRunnable task) {
		pool.execute(task);
	}

	public static void shutdown() {
		pool.shutdown();
	}

}
