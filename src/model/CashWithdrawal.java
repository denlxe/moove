package model;

import util.Fmt;

import java.time.LocalDateTime;


public class CashWithdrawal extends Transaction {

    private final String purpose;
    private final double amount;

    public CashWithdrawal(int id, LocalDateTime timestamp, String recordedBy, String purpose, double amount) {
        super(id, timestamp, recordedBy);
        this.purpose = purpose;
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String getType() {
        return "Cash out";
    }

    @Override
    public String getTypeCode() {
        return "CASHOUT";
    }

    @Override
    public String getDetails() {
        return purpose + " (" + Fmt.money(amount) + " taken out)";
    }

    // returns the amount as a negative number, because it leaves the wallet 
    @Override
    public double getCashAmount() {
        return -amount;
    }
}
