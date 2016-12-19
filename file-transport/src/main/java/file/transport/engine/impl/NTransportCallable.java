package file.transport.engine.impl;

import file.transport.StorageEngineFactory;
import file.transport.engine.SerializeEngine;
import file.transport.execption.TransportException;
import file.transport.model.StorageMessage;
import file.transport.utils.SocketUtils;
import file.transport.utils.StorageConfigUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.Callable;

public class NTransportCallable implements Callable<String> {

    private static transient final Log log = LogFactory.getLog(NTransportCallable.class);

    private StorageMessage message;
    private String[] nodes;
    private File serFile;

    public NTransportCallable(StorageMessage message, String[] nodes) {
        this.message = message;
        this.nodes = nodes;
        this.serFile = new File(
                StorageConfigUtils.getStorageDir() + File.separator + UUID.randomUUID().toString() + ".ser");
    }

    public String call() throws Exception {
        this.serilize();
        Thread[] threads = new Thread[this.nodes.length];
        ProcessResult[] results = new ProcessResult[this.nodes.length];

        for (int i = 0, len = this.nodes.length; i < len; i++) {
            ProcessResult foo = new ProcessResult();
            results[i] = foo;
            foo.setHost(this.nodes[i]);
            threads[i] = new Thread(new FileSendRunnable(this.nodes[i], this.serFile, foo));
        }
        for (Thread th : threads) {
            th.start();
        }
        for (Thread th : threads) {
            th.join();
        }
        FileUtils.deleteQuietly(this.serFile);
        for (ProcessResult foo : results) {
            if (!foo.isSuccess()) {
                return "failed";
            }
        }
        return "success";
    }

    private void serilize() throws TransportException {
        SerializeEngine engine = StorageEngineFactory.getInstance().getSerializeEngine();
        OutputStream output;
        try {
            if (!this.serFile.getParentFile().exists()) {
                this.serFile.getParentFile().mkdirs();
            }
            this.serFile.createNewFile();
            output = new BufferedOutputStream(new FileOutputStream(this.serFile));
            engine.ser(output, message);
            if (StorageConfigUtils.getConfiguration().getBoolean("storage.autoclean", false)) {
                this.message.destroy();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new TransportException("no such file: " + this.serFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new TransportException("create outputstream error.", e);
        }
    }

    private static class ProcessResult {

        private long costTime;
        private boolean success;
        private String host;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public long getCostTime() {
            return costTime;
        }

        public void setCostTime(long costTime) {
            this.costTime = costTime;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

    }

    private static class FileSendRunnable implements Runnable {

        private String dest;
        private int port;
        private File sourceFile;
        private ProcessResult result;

        public FileSendRunnable(String host, File sourceFile, ProcessResult result) {
            String[] str = host.split(":");
            this.dest = str[0];
            this.port = Integer.valueOf(str[1]);
            this.sourceFile = sourceFile;
            this.result = result;
        }

        @Override
        public void run() {
            InputStream in = null;
            long mills = System.currentTimeMillis();
            try {
                in = new BufferedInputStream(new FileInputStream(this.sourceFile));
                SocketUtils.send(this.dest, this.port, in);
                this.result.setSuccess(true);
                this.result.setCostTime(System.currentTimeMillis() - mills);
            } catch (Throwable e) {
                this.result.setSuccess(false);
                e.printStackTrace();
                if (log.isErrorEnabled()) {
                    log.error("send file to " + this.dest + ":" + this.port + " failed: ", e);
                }
            } finally {
                IOUtils.closeQuietly(in);
            }

        }

    }
}