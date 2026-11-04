package model;

import util.Fmt;

import java.time.LocalDateTime;

/**
 * when a farmer brings their own milk to be processed and pays a fee
 * per liter. 
 * Fee goes into  wallet and it does not change the shop's stock.
 */
public class ProcessingFee extends Transaction {

    private final String farmerName;
    private final double liters;
    private final double ratePerLiter;

    public ProcessingFee(int id, LocalDateTime timestamp, String recordedBy,
                         String farmerName, double liters, double ratePerLiter) {
        super(id, timestamp, recordedBy);
        this.farmerName = farmerName;
        this.liters = liters;
        this.ratePerLiter = ratePerLiter;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public double getLiters() {
        return liters;
    }

    public double getRatePerLiter() {
        return ratePerLiter;
    }

    @Override
    public String getType() {
        return "Processing fee";
    }

    @Override
    public String getTypeCode() {
        return "FEE";
    }

    @Override
    public String getDetails() {
        return farmerName + ": " + String.format("%.1f", liters) + " L at " + Fmt.money(ratePerLiter) + "/L";
    }

    @Override
    public double getCashAmount() {
        return Math.round(liters * ratePerLiter * 100.0) / 100.0;
    }
}
