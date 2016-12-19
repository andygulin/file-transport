package file.transport.execption;

public class TransportException extends Exception {

    private static final long serialVersionUID = 8688441214328359249L;

    protected int errorCode;

    public TransportException() {
        super();
    }

    public TransportException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public TransportException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public TransportException(Throwable e) {
        super(e);
    }

    public TransportException(String msg) {
        super(msg);
    }

    public TransportException(String msg, Throwable e) {
        super(msg, e);
    }

    public TransportException(int errorCode, Throwable e) {
        super(e);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}