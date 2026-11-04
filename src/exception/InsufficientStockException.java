package exception;

/* Thrown when a sale or production batch needs more than is available
 * (finished units for a sale, raw milk liters for a batch)
 */
public class InsufficientStockException extends AgriException {

    private static final long serialVersionUID = 1L;

    private final double available;


    public InsufficientStockException(String message, double available) {
        super(message);
        this.available = available;
    }

    public double getAvailable() {
        return available;
    }
}
