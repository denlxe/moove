package model;

import exception.AgriException;

import java.time.LocalDateTime;

/**
 * any recorded event in the shop's history like sale, donation, processing fee, or write-off
 * subclasses decide what the event contains
 * TODO: create class donation
 */
public abstract class Transaction implements Voidable {

    public enum Status {
        COMPLETED("Completed"),
        VOIDED("Voided");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final int id;
    private final LocalDateTime timestamp;
    private final String recordedBy;
    private Status status = Status.COMPLETED;
    private String voidedBy = "";
    private LocalDateTime voidedAt;
    private String voidReason = "";

    protected Transaction(int id, LocalDateTime timestamp, String recordedBy) {
        this.id = id;
        this.timestamp = timestamp;
        this.recordedBy = recordedBy;
    }

    // returns the name shown in the ui like "Sale" or "Donation"
    public abstract String getType();

    // returns a stable code written to the data file, such as "SALE"
    public abstract String getTypeCode();

    // returns a one-line description of what happened (items sold, donor, ...)
    public abstract String getDetails();

    /*
     * returns cash this transaction puts into the wallet when it is not voided
     * (zero for item donations and write-offs)
     */
    public abstract double getCashAmount();


     // undoes the stock effect of this transaction and is called while voiding
    public void rollbackStock(Inventory inventory) throws AgriException {
        // no stock effect by default
    }

    public double getWalletEffect() {
        return isVoided() ? 0 : getCashAmount();
    }

    @Override
    public boolean isVoidable() {
        return true;
    }

    @Override
    public boolean isVoided() {
        return status == Status.VOIDED;
    }

    @Override
    public void markVoided(String voidedBy, LocalDateTime at, String reason) {
        this.status = Status.VOIDED;
        this.voidedBy = voidedBy == null ? "" : voidedBy;
        this.voidedAt = at;
        this.voidReason = reason == null ? "" : reason;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public Status getStatus() {
        return status;
    }

    public String getVoidedBy() {
        return voidedBy;
    }

    public LocalDateTime getVoidedAt() {
        return voidedAt;
    }

    public String getVoidReason() {
        return voidReason;
    }
}
