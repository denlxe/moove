package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

/**
 * how many units were made, when, and when they expire
 * Stock is tracked per batch.
 */
public class ProductBatch {

    public static final int NEAR_EXPIRY_DAYS = 2;

    // orders batches by expiry date, then production date, then id (earliest first)
    public static final Comparator<ProductBatch> BY_EXPIRY = Comparator
            .comparing(ProductBatch::getExpiresOn)
            .thenComparing(ProductBatch::getProducedOn)
            .thenComparing(ProductBatch::getId);

    // how fresh a batch is on a given day
    public enum Freshness {
        FRESH("Fresh"),
        NEAR_EXPIRY("Near expiry"),
        EXPIRED("Expired");

        private final String label;

        Freshness(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final String id;
    private final String productId;
    private final int unitsProduced;
    private int unitsRemaining;
    private final LocalDate producedOn;
    private final LocalDate expiresOn;
    private final double litersUsed;

    public ProductBatch(String id, String productId, int unitsProduced, int unitsRemaining,
                        LocalDate producedOn, LocalDate expiresOn, double litersUsed) {
        this.id = id;
        this.productId = productId;
        this.unitsProduced = unitsProduced;
        this.unitsRemaining = unitsRemaining;
        this.producedOn = producedOn;
        this.expiresOn = expiresOn;
        this.litersUsed = litersUsed;
    }

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public int getUnitsProduced() { return unitsProduced; }
    public int getUnitsRemaining() { return unitsRemaining; }
    public LocalDate getProducedOn() { return producedOn; }
    public LocalDate getExpiresOn() { return expiresOn; }
    public double getLitersUsed() { return litersUsed; }

    // returns true if the sell-by date has passed (a batch is still sellable on its expiry date)
    public boolean isExpired(LocalDate today) {
        return today.isAfter(expiresOn);
    }

    // returns whole days until expiry (negative once expired)
    public long daysUntilExpiry(LocalDate today) {
        return ChronoUnit.DAYS.between(today, expiresOn);
    }

    // returns whether the batch is fresh, near expiry or expired
    public Freshness getFreshness(LocalDate today) {
        if (isExpired(today)) {
            return Freshness.EXPIRED;
        }
        if (daysUntilExpiry(today) <= NEAR_EXPIRY_DAYS) {
            return Freshness.NEAR_EXPIRY;
        }
        return Freshness.FRESH;
    }

    // removes units from stock
    public void deduct(int units) {
        if (units < 0 || units > unitsRemaining) {
            throw new IllegalArgumentException("Cannot deduct " + units + " from " + unitsRemaining);
        }
        unitsRemaining -= units;
    }

    // puts units back (used when a sale is voided)
    public void restore(int units) {
        unitsRemaining = Math.min(unitsProduced, unitsRemaining + Math.max(0, units));
    }

    // empties the batch (used when expired stock is discarded).
    public int discardAll() {
        int removed = unitsRemaining;
        unitsRemaining = 0;
        return removed;
    }
}
