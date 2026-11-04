package exception;

// thrown when the signed-in user tries something their role does not allow
public class PermissionException extends AgriException {

    private static final long serialVersionUID = 1L;

    public PermissionException(String message) {
        super(message);
    }
}
