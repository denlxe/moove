package model;

import util.Fmt;

import java.time.LocalDateTime;

/** TODO:
 * A donation received by the shop: either cash (which goes into the wallet) or an item (which is only recorded). 
 * - It keeps the donor name, the type, the amount or item name, and the date.
 */

/* Constructor should contain
 * id           transaction number
 * timestamp    when it was recorded (this is the donation date)
 * recordedBy   username of the person who recorded it
 * donorName    who donated
 * kind         cash or item
 * amount       pesos donated (cash donations only, otherwise 0)
 * itemName     what was donated (item donations only, otherwise empty)
 * itemQuantity how many items (item donations only, otherwise 0)
 */

public class Donation extends Transaction {

    public enum Kind {
        CASH("Cash"),
        ITEM("Item");

        private final String label;

        Kind(String label) {
            this.label = label;
        }

        /** @return the text shown in the UI */
        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final String donorName;
    private final Kind kind;
    private final double amount;
    private final String itemName;
    private final int itemQuantity;

    public Donation(int id, LocalDateTime timestamp, String recordedBy, String donorName, Kind kind, 
    		double amount, String itemName, int itemQuantity) {
        super(id, timestamp, recordedBy);
        this.donorName = donorName;
        this.kind = kind;
        this.amount = amount;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
    }

    public String getDonorName() { return donorName; }
    public Kind getKind() { return kind; }
    public double getAmount() { return amount; }
    public String getItemName() { return itemName; }
    public int getItemQuantity() { return itemQuantity; }

    @Override
    public String getType() {
        return "Donation";
    }

    @Override
    public String getTypeCode() {
        return "DONATION";
    }

    @Override
    public String getDetails() {
        if (kind == Kind.CASH) {
            return donorName + " donated " + Fmt.money(amount) + " (cash)";
        }
        return donorName + " donated " + itemQuantity + " x " + itemName + " (item)";
    }

    @Override
    public double getCashAmount() {
        return kind == Kind.CASH ? amount : 0;
    }
}