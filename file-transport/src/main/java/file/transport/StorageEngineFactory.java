package file.transport;

import file.transport.engine.RemoteStorageEngine;
import file.transport.engine.SerializeEngine;
import file.transport.engine.impl.RemoteStorageEngineImpl;
import file.transport.engine.impl.SerializeEngineImpl;
import file.transport.utils.StorageConfigUtils;

public class StorageEngineFactory {

    private static final StorageEngineFactory SINGLE = new StorageEngineFactory();
    private RemoteStorageEngine remoteStorageEngine;
    private SerializeEngine serializeEngine;

    private StorageEngineFactory() {
    }

    public static StorageEngineFactory getInstance() {
        return SINGLE;
    }

    public synchronized SerializeEngine getSerializeEngine() {
        if (this.serializeEngine == null) {
            this.serializeEngine = new SerializeEngineImpl();
        }
        return serializeEngine;
    }

    public synchronized RemoteStorageEngine getRemoteStorageEngine() {
        if (this.remoteStorageEngine == null) {
            this.remoteStorageEngine = new RemoteStorageEngineImpl(
                    StorageConfigUtils.getConfiguration().getStringArray("storage.nodes"));
        }
        return remoteStorageEngine;
    }
}