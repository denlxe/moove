package model;

import java.time.LocalDate;

// A record of raw milk received, used to track how much milk is available for production.
public class MilkIntake {

    private final int id;
    private final LocalDate date;
    private final double liters;
    private final String source;
    private final String recordedBy;

    public MilkIntake(int id, LocalDate date, double liters, String source, String recordedBy) {
        this.id = id;
        this.date = date;
        this.liters = liters;
        this.source = source;
        this.recordedBy = recordedBy;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getLiters() {
        return liters;
    }

    public String getSource() {
        return source;
    }

    public String getRecordedBy() {
        return recordedBy;
    }
}
