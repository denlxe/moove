package model;

import exception.AgriException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// a customer purchase
// one or more items paid for in cash.
public class Sale extends Transaction {

    private final List<SaleItem> items;
    private final double cashReceived;


    public Sale(int id, LocalDateTime timestamp, String recordedBy,
                List<SaleItem> items, double cashReceived) {
        super(id, timestamp, recordedBy);
        this.items = new ArrayList<>(items);
        this.cashReceived = cashReceived;
    }

    public List<SaleItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getTotal() {
        double total = 0;
        for (SaleItem item : items) {
            total += item.getSubtotal();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public double getChange() {
        return Math.round((cashReceived - getTotal()) * 100.0) / 100.0;
    }

    @Override
    public String getType() {
        return "Sale";
    }

    @Override
    public String getTypeCode() {
        return "SALE";
    }

    @Override
    public String getDetails() {
        return items.stream()
                .map(i -> i.getProductName() + " x" + i.getQuantity())
                .collect(Collectors.joining(", "));
    }

    @Override
    public double getCashAmount() {
        return getTotal();
    }

    // puts every unit back into the batch it was taken from
    @Override
    public void rollbackStock(Inventory inventory) throws AgriException {
        for (SaleItem item : items) {
            for (Map.Entry<String, Integer> entry : item.getAllocations().entrySet()) {
                inventory.restore(entry.getKey(), entry.getValue());
            }
        }
    }
}
