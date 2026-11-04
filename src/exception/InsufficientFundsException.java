package exception;

/*
 * Thrown when an action would push the wallet balance below zero,
 * for example voiding a donation after the money has already been spent.
 */
public class InsufficientFundsException extends AgriException {

    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super(message);
    }
}
