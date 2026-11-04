package model;

import java.time.LocalDateTime;

/**
 * A record that expired stock was thrown away. It has no cash value and cannot be
 * voided, because the units are gone and cannot be sold again.
 */
public class WriteOff extends Transaction {

    private final String description;
    private final int units;


    public WriteOff(int id, LocalDateTime timestamp, String recordedBy, String description, int units) {
        super(id, timestamp, recordedBy);
        this.description = description;
        this.units = units;
    }

    public String getDescription() {
        return description;
    }

    public int getUnits() {
        return units;
    }

    @Override
    public String getType() {
        return "Write-off";
    }

    @Override
    public String getTypeCode() {
        return "WRITEOFF";
    }

    @Override
    public String getDetails() {
        return "Discarded " + units + " expired units: " + description;
    }

    @Override
    public double getCashAmount() {
        return 0;
    }

    @Override
    public boolean isVoidable() {
        return false;
    }
}
