package file.transport.engine.impl;

import file.transport.engine.RemoteStorageEngine;
import file.transport.execption.TransportException;
import file.transport.model.StorageMessage;
import file.transport.model.TransportHandler;
import file.transport.utils.SimpleThreadPool;
import file.transport.utils.ValidateUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.Callable;

public class RemoteStorageEngineImpl implements RemoteStorageEngine {

    static final Log log = LogFactory.getLog(RemoteStorageEngineImpl.class);

    private final Set<String> nodes = new LinkedHashSet<>();

    public RemoteStorageEngineImpl(String[] nodes) {
        for (String foo : nodes) {
            this.node(foo);
        }
    }

    public RemoteStorageEngineImpl(Collection<String> nodes) {
        for (String foo : nodes) {
            this.node(foo);
        }
    }

    @Override
    public void transport(StorageMessage message) throws TransportException {
        this.transport(message, this.nodes());
    }

    @Override
    public void transportAsync(StorageMessage message, TransportHandler handler) throws TransportException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public String[] nodes() {
        return this.nodes.toArray(new String[0]);
    }

    @Override
    public void node(String ip, int port) {
        node(ip + ":" + port);
    }

    @Override
    public void node(String... url) {
        Object[] bar;
        for (String foo : url) {
            bar = this.parseHost(foo);
            if (bar != null) {
                this.nodes.add(foo);
            }
        }
    }

    @Override
    public void removeNode(String ip, int port) {
        this.nodes.remove(ip + ":" + port);
    }

    @Override
    public void removeNode(String... url) {
        for (String foo : url) {
            this.nodes.remove(foo);
        }
    }

    @Override
    public void removeAllNode() {
        this.nodes.clear();
    }

    @Override
    public void transport(StorageMessage message, String ip, int port) throws TransportException {
        this.transport(message, ip + ":" + port);
    }

    @Override
    public void transport(StorageMessage message, String... destHost) throws TransportException {
        if (ArrayUtils.isEmpty(destHost) || message == null) {
            return;
        }
        List<Callable<String>> tasks = new ArrayList<>();
        Callable<String> cal = new NTransportCallable(message, destHost);
        tasks.add(cal);
        try {
            List<String> result = SimpleThreadPool.getInstance().invokeAll(tasks);
            for (String foo : result) {
                if (!StringUtils.equalsIgnoreCase("success", foo)) {
                    throw new TransportException("execute transport tasks error.");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            String errMsg = "execute transport tasks error: ";
            if (log.isErrorEnabled()) {
                log.error(errMsg, e);
            }
            throw new TransportException(errMsg, e);
        }
    }

    private Object[] parseHost(final String host) {
        if (StringUtils.isBlank(host)) {
            return null;
        }
        String[] arr = host.split(":");
        for (int i = 0, len = arr.length; i < len; i++) {
            arr[i] = StringUtils.strip(arr[i]);
        }

        if (arr.length != 2 || ValidateUtils.isNotIPv4(arr[0]) || ValidateUtils.isNotNumeric(arr[1])) {
            return null;
        }
        final int foo = Integer.parseInt(arr[1]);
        if (foo < 1 || foo > 65535) {
            return null;
        }
        return new Object[]{arr[0], Integer.valueOf(arr[1])};
    }
}