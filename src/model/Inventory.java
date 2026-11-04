package model;

import exception.InsufficientStockException;
import exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/*
 * shop's stock includes: 
 * - raw milk waiting to be processed 
 * - batches of finished products
 */

public class Inventory {

    private static final double EPSILON = 1e-9;

    private final List<ProductBatch> batches = new ArrayList<>();
    private final List<MilkIntake> intakes = new ArrayList<>();
    private double rawMilkLiters;
    private int nextBatchNumber = 1;
    private int nextIntakeNumber = 1;

    // =========================  raw milk ========================= 

    // records raw milk arriving at the plant
    public MilkIntake receiveMilk(double liters, String source, LocalDate date, String recordedBy)
            throws ValidationException {
        if (liters <= 0) {
            throw new ValidationException("Liters received must be greater than zero.");
        }
        MilkIntake intake = new MilkIntake(nextIntakeNumber++, date, liters, source, recordedBy);
        intakes.add(intake);
        rawMilkLiters += liters;
        return intake;
    }

    // returns liters of raw milk currently available for production 
    public double getRawMilkLiters() {
        return rawMilkLiters;
    }

    // returns all milk intake records, oldest first
    public List<MilkIntake> getIntakes() {
        return Collections.unmodifiableList(intakes);
    }

    // ========================= batches =========================

    // Adds a production batch (also used to restock an existing product).
    public ProductBatch addBatch(Product product, int units, double litersUsed,
                                 LocalDate producedOn, LocalDate expiresOn)
            throws ValidationException, InsufficientStockException {
        if (units <= 0) {
            throw new ValidationException("Units must be greater than zero.");
        }
        if (litersUsed < 0) {
            throw new ValidationException("Liters used cannot be negative.");
        }
        if (expiresOn.isBefore(producedOn)) {
            throw new ValidationException("The expiry date cannot be before the production date.");
        }
        if (litersUsed > rawMilkLiters + EPSILON) {
            throw new InsufficientStockException(String.format(
                    "Only %.1f L of raw milk is available, but this batch needs %.1f L.",
                    rawMilkLiters, litersUsed), rawMilkLiters);
        }
        rawMilkLiters = Math.max(0, rawMilkLiters - litersUsed);
        String id = String.format("B%04d", nextBatchNumber++);
        ProductBatch batch = new ProductBatch(id, product.getId(), units, units, producedOn, expiresOn, litersUsed);
        batches.add(batch);
        return batch;
    }

    // returns every batch, in the order they were added
    public List<ProductBatch> getBatches() {
        return Collections.unmodifiableList(batches);
    }

    // returns every batch sorted by expiry date, earliest first
    public List<ProductBatch> getBatchesByExpiry() {
        List<ProductBatch> sorted = new ArrayList<>(batches);
        sorted.sort(ProductBatch.BY_EXPIRY);
        return sorted;
    }

    // returns units that can be sold right now (in stock and not expired)
    public int availableUnits(String productId, LocalDate today) {
        int total = 0;
        for (ProductBatch b : batches) {
            if (isSellable(b, productId, today)) {
                total += b.getUnitsRemaining();
            }
        }
        return total;
    }

    // returns the sellable batch that will be sold next (earliest expiry), or null if none
    public ProductBatch nextBatchToSell(String productId, LocalDate today) {
        PriorityQueue<ProductBatch> queue = sellableQueue(productId, today);
        return queue.peek();
    }

    // removes quantity units of a product from stock (earliest expiring batch first)
    // nothing changes if there is not enough stock
    public Map<String, Integer> allocate(String productId, int quantity, LocalDate today)
            throws ValidationException, InsufficientStockException {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero.");
        }
        PriorityQueue<ProductBatch> queue = sellableQueue(productId, today);
        int available = 0;
        for (ProductBatch b : queue) {
            available += b.getUnitsRemaining();
        }
        if (available < quantity) {
            throw new InsufficientStockException(
                    "Only " + available + " in stock (need " + quantity + ").", available);
        }
        Map<String, Integer> taken = new LinkedHashMap<>();
        int needed = quantity;
        while (needed > 0 && !queue.isEmpty()) {
            ProductBatch batch = queue.poll();
            int take = Math.min(needed, batch.getUnitsRemaining());
            batch.deduct(take);
            taken.merge(batch.getId(), take, Integer::sum);
            needed -= take;
        }
        return taken;
    }

    // Puts units back into the batch they were taken from (used when a sale is voided)
    public void restore(String batchId, int units) throws ValidationException {
        for (ProductBatch b : batches) {
            if (b.getId().equals(batchId)) {
                b.restore(units);
                return;
            }
        }
        throw new ValidationException("Batch " + batchId + " no longer exists, so its stock cannot be restored.");
    }

    // ========================= expiry =========================

    // returns the batches that still have units, are not expired, and expire within NEAR_EXPIRY_DAYS
    public List<ProductBatch> getNearExpiryBatches(LocalDate today) {
        return getBatchesByExpiry().stream()
                .filter(b -> b.getUnitsRemaining() > 0
                        && b.getFreshness(today) == ProductBatch.Freshness.NEAR_EXPIRY)
                .collect(Collectors.toList());
    }

    // returns the batches that have expired but still hold units
    public List<ProductBatch> getExpiredBatchesWithStock(LocalDate today) {
        return getBatchesByExpiry().stream()
                .filter(b -> b.getUnitsRemaining() > 0 && b.isExpired(today))
                .collect(Collectors.toList());
    }

    // ========================= loading from disk (used by DataStore) =========================

    // adds an already existing batch while loading saved data
    public void loadBatch(ProductBatch batch) {
        batches.add(batch);
    }

    // adds an already existing intake record while loading saved data
    public void loadIntake(MilkIntake intake) {
        intakes.add(intake);
    }

    // restores the raw milk pool and id counters while loading saved data
    public void loadCounters(double rawMilkLiters, int nextBatchNumber, int nextIntakeNumber) {
        this.rawMilkLiters = rawMilkLiters;
        this.nextBatchNumber = nextBatchNumber;
        this.nextIntakeNumber = nextIntakeNumber;
    }

    // returns the number the next batch id will use (saved to disk)
    public int getNextBatchNumber() {
        return nextBatchNumber;
    }

    // returns the number the next intake record will use (saved to disk)
    public int getNextIntakeNumber() {
        return nextIntakeNumber;
    }

    // ========================= helpers =========================

    private boolean isSellable(ProductBatch b, String productId, LocalDate today) {
        return b.getProductId().equals(productId) && b.getUnitsRemaining() > 0 && !b.isExpired(today);
    }

    private PriorityQueue<ProductBatch> sellableQueue(String productId, LocalDate today) {
        PriorityQueue<ProductBatch> queue = new PriorityQueue<>(ProductBatch.BY_EXPIRY);
        for (ProductBatch b : batches) {
            if (isSellable(b, productId, today)) {
                queue.add(b);
            }
        }
        return queue;
    }
}
