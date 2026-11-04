package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

 // one line of a sale
 // It copies the product name and price at time of sale, so later price changes or renamed products never 
 // rewrite history, and it remembers which batches the units were taken from so a void can put them back

public class SaleItem {

    private final String productId;
    private final String productName;
    private final double unitPrice;
    private final int quantity;
    private final Map<String, Integer> allocations;

    public SaleItem(String productId, String productName, double unitPrice, int quantity,
                    Map<String, Integer> allocations) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.allocations = new LinkedHashMap<>(allocations);
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return Math.round(unitPrice * quantity * 100.0) / 100.0;
    }

    public Map<String, Integer> getAllocations() {
        return Collections.unmodifiableMap(allocations);
    }
}
