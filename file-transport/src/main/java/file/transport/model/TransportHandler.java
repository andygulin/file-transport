package file.transport.model;

public interface TransportHandler {

    void onError(String errorMessage);

    void onSuccess();
}