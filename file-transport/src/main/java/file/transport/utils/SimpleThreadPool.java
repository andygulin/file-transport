package file.transport.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class SimpleThreadPool {
    private static SimpleThreadPool INSTANCE = null;

    static {
        synchronized (SimpleThreadPool.class) {
            INSTANCE = new SimpleThreadPool();
        }
    }

    private ExecutorService pool = null;

    private SimpleThreadPool() {
        int cpuNums = Runtime.getRuntime().availableProcessors();
        pool = Executors.newFixedThreadPool(cpuNums * 8);
    }

    public static final SimpleThreadPool getInstance() {
        return INSTANCE;
    }

    public <T> List<T> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        List<T> tt = new ArrayList<T>();
        List<Future<T>> result = this.pool.invokeAll(tasks);
        for (Future<T> foo : result) {
            tt.add(foo.get());
        }
        return tt;
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.pool.invokeAny(tasks);
    }

    public void shutdown() {
        this.pool.shutdown();
    }
}