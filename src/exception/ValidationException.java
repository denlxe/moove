package exception;

// thrown when the user typed something that is missing, malformed or out of range.
public class ValidationException extends AgriException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
