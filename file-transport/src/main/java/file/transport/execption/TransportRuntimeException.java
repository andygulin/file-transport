package file.transport.execption;

public class TransportRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -4754362912593003702L;

	protected int errorCode;

	public TransportRuntimeException() {
		super();
	}

	public TransportRuntimeException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public TransportRuntimeException(int errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
	}

	public TransportRuntimeException(int errorCode, Throwable e) {
		super(e);
		this.errorCode = errorCode;
	}

	public TransportRuntimeException(String msg) {
		super(msg);
	}

	public TransportRuntimeException(String msg, Throwable e) {
		super(msg, e);
	}

	public TransportRuntimeException(Throwable e) {
		super(e);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}