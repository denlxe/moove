package exception;

// Base class (bad input, missing stock, wrong password,etc).
public class AgriException extends Exception {

    private static final long serialVersionUID = 1L;


    public AgriException(String message) {
        super(message);
    }

    public AgriException(String message, Throwable cause) {
        super(message, cause);
    }
}
