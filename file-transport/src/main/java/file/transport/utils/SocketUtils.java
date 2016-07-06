package file.transport.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import file.transport.execption.TransportException;

public class SocketUtils {
	public static int TIMEOUT_SECONDS = -1;

	static {

		String key = "storage.timeout";
		boolean isSysPropSuccess = false;

		if (System.getProperties().containsKey(key)) {
			String foo = System.getProperty(key);
			if (StringUtils.isNotBlank(foo) && StringUtils.isNumeric(foo)) {
				TIMEOUT_SECONDS = Integer.valueOf(foo);
				isSysPropSuccess = true;
			}
		}

		if (!isSysPropSuccess) {
			Configuration cfg = StorageConfigUtils.getConfiguration();
			if (cfg.containsKey(key)) {
				TIMEOUT_SECONDS = cfg.getInt(key);
			}
		}
	}

	public static final void send(String ip, int port, InputStream from) throws TransportException {
		Socket socket = null;
		InputStream input = null;
		OutputStream output = null;
		try {
			socket = new Socket(ip, port);

			if (TIMEOUT_SECONDS > 0) {
				socket.setSoTimeout(TIMEOUT_SECONDS);
			}

			output = socket.getOutputStream();
			input = socket.getInputStream();

			byte[] buffer = new byte[1024];
			int n = 0;
			while ((n = from.read(buffer)) != -1) {
				output.write(buffer, 0, n);
				output.flush();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new TransportException("host unknow error: " + ip + ":" + port, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new TransportException("socket transport to " + ip + ":" + port + " error: ", e);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new TransportException("close socket " + ip + ":" + port + " error: ", e);
			}
		}
	}
}
