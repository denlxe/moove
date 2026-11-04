package exception;

// thrown when the data files cannot be read, written or parsed.
public class StorageException extends AgriException {

    private static final long serialVersionUID = 1L;


    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(String message) {
        super(message);
    }
}
