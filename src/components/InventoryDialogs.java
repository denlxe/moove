package components;

import model.Product;
import model.ProductBatch;
import model.WriteOff;
import service.AgriSystem;
import util.Fmt;
import util.Input;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

/**
 * manager's stock dialogs: 
 * + receive milk
 * + add a batch (restock)
 * + discard expired stock
 * 
 * they live in one place because both the Inventory screen and the Products screen use them
 * each method returns true if something changed, so the caller knows to refresh
 */
public final class InventoryDialogs {

    private InventoryDialogs() {
    }

    // records raw milk arriving
    public static boolean receiveMilk(AgriSystem system) {
        FormDialog dialog = new FormDialog("Receive milk",
                "Record raw milk delivered to the plant. It becomes available for production batches.",
                "Record milk");
        dialog.addInfo("Raw milk on hand", String.format("%.1f L", system.getInventory().getRawMilkLiters()));
        TextField liters = dialog.addText("Liters received", "", "e.g. 100");
        TextField source = dialog.addText("Source", "", "Farm or cooperative name");
        return dialog.show(() -> system.receiveMilk(
                Input.positiveNumber(liters.getText(), "Liters received"), source.getText()));
    }

    // adds a production batch, which also restocks a product
    public static boolean addBatch(AgriSystem system, Product preselected) {
        List<Product> active = system.getActiveProducts();
        FormDialog dialog = new FormDialog("Add batch / restock",
                "Record a production run. Units are sold earliest-expiry first.", "Add batch");
        Product start = preselected != null && active.contains(preselected) ? preselected
                : (active.isEmpty() ? null : active.get(0));
        ComboBox<Product> product = dialog.addChoice("Product", active, start);
        TextField units = dialog.addText("Units made", "", "e.g. 24");
        TextField liters = dialog.addText("Raw milk used (L)", "0", "0 if not tracked");
        dialog.addInfo("Raw milk on hand", String.format("%.1f L", system.getInventory().getRawMilkLiters()));
        DatePicker produced = dialog.addDate("Produced on", system.today());
        DatePicker expires = dialog.addDate("Expires on", null);
        Label unitHint = dialog.addInfo("Sold by", start == null ? "" : start.getUnitLabel());

        Runnable suggestExpiry = () -> {
            Product p = product.getValue();
            if (p != null && produced.getValue() != null) {
                expires.setValue(produced.getValue().plusDays(p.getShelfLifeDays()));
                unitHint.setText(p.getUnitLabel() + " (shelf life " + p.getShelfLifeDays() + " days)");
            }
        };
        product.valueProperty().addListener((o, a, b) -> suggestExpiry.run());
        produced.valueProperty().addListener((o, a, b) -> suggestExpiry.run());
        suggestExpiry.run();

        return dialog.show(() -> system.addBatch(
                Input.choice(product.getValue(), "product"),
                Input.positiveInt(units.getText(), "Units made"),
                Input.nonNegativeNumber(liters.getText(), "Raw milk used"),
                produced.getValue(), expires.getValue()));
    }

    // asks for confirmation, then discards every expired batch that still has units
    public static boolean discardExpired(AgriSystem system) {
        LocalDate today = system.today();
        List<ProductBatch> expired = system.getInventory().getExpiredBatchesWithStock(today);
        if (expired.isEmpty()) {
            Dialogs.info("Nothing to discard", "There is no expired stock right now.");
            return false;
        }
        StringBuilder sb = new StringBuilder();
        int units = 0;
        for (ProductBatch b : expired) {
            sb.append(b.getId()).append(": ").append(system.productNameOf(b.getProductId()))
                    .append(" x").append(b.getUnitsRemaining())
                    .append(" (expired ").append(Fmt.date(b.getExpiresOn())).append(")\n");
            units += b.getUnitsRemaining();
        }
        if (!Dialogs.confirm("Discard " + units + " expired units?",
                sb + "\nThis records a write-off and cannot be undone.", "Discard")) {
            return false;
        }
        final WriteOff[] result = new WriteOff[1];
        return Dialogs.attempt(() -> result[0] = system.discardExpired());
    }
}
