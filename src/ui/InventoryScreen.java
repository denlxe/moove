package ui;

import model.MilkIntake;
import model.Product;
import model.ProductBatch;
import service.AgriSystem;
import components.InventoryDialogs;
import components.StatCard;
import components.Tables;
import util.Fmt;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/* + everyone can look at stock levels and expiry
 * + only a manager (canEdit is true) sees the buttons to receive milk, add batches and discard expired stock.
 */
public class InventoryScreen extends Screen {

    private final AgriSystem system;
    private final boolean canEdit;

    private final StatCard milkCard = new StatCard("Raw milk on hand");
    private final StatCard unitsCard = new StatCard("Units in stock");
    private final StatCard nearCard = new StatCard("Batches near expiry");
    private final StatCard expiredCard = new StatCard("Expired units");

    private final TableView<ProductBatch> batchTable = new TableView<>();
    private final TableView<Product> stockTable = new TableView<>();
    private final TableView<MilkIntake> intakeTable = new TableView<>();
    private final CheckBox showEmpty = new CheckBox("Show empty and discarded batches");


    public InventoryScreen(AgriSystem system, boolean canEdit) {
        this.system = system;
        this.canEdit = canEdit;
    }

    @Override
    public String getName() {
        return "Inventory";
    }

    @Override
    protected Parent build() {
        HBox stats = new HBox(14, milkCard, unitsCard, nearCard, expiredCard);

        VBox body = new VBox(14, stats);
        if (canEdit) {
            Button receive = new Button("Receive milk");
            receive.getStyleClass().add("btn-primary");
            receive.setOnAction(e -> changed(InventoryDialogs.receiveMilk(system)));
            Button add = new Button("Add batch / restock");
            add.getStyleClass().add("btn-primary");
            add.setOnAction(e -> changed(InventoryDialogs.addBatch(system, null)));
            Button discard = new Button("Discard expired");
            discard.getStyleClass().add("btn-light-dark");
            discard.setOnAction(e -> changed(InventoryDialogs.discardExpired(system)));
            body.getChildren().add(new HBox(10, receive, add, discard));
        }

        buildBatchTable();
        buildStockTable();
        buildIntakeTable();

        showEmpty.selectedProperty().addListener((o, a, b) -> refresh());
        VBox batches = new VBox(10, showEmpty, batchTable);
        VBox.setVgrow(batchTable, Priority.ALWAYS);
        batches.setPadding(new Insets(12, 0, 0, 0));

        TabPane tabs = new TabPane(
                tab("Batches (earliest expiry first)", batches),
                tab("Stock by product", stockTable),
                tab("Milk intake log", intakeTable));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setPrefHeight(480);
        body.getChildren().add(tabs);

        String subtitle = canEdit
                ? "Receive milk, record production batches and discard expired stock."
                : "Check stock levels and expiry dates. Only a manager can change inventory.";
        return page("Inventory", subtitle, body);
    }

    private static Tab tab(String title, javafx.scene.Node content) {
        Tab tab = new Tab(title, content);
        tab.setClosable(false);
        return tab;
    }

    private void changed(boolean didChange) {
        if (didChange) {
            refresh();
        }
    }

    private void buildBatchTable() {
        batchTable.getColumns().add(Tables.text("Product", 200, b -> system.productNameOf(b.getProductId())));
        batchTable.getColumns().add(Tables.text("Batch", 80, ProductBatch::getId));
        batchTable.getColumns().add(Tables.text("Produced", 110, b -> Fmt.date(b.getProducedOn())));
        batchTable.getColumns().add(Tables.text("Expires", 110, b -> Fmt.date(b.getExpiresOn())));
        batchTable.getColumns().add(Tables.text("Time left", 130,
                b -> Fmt.daysPhrase(b.daysUntilExpiry(system.today()))));
        batchTable.getColumns().add(Tables.number("Units left / made", 130,
                b -> b.getUnitsRemaining() + " / " + b.getUnitsProduced()));
        batchTable.getColumns().add(Tables.chip("Status", 120,
                b -> b.getUnitsRemaining() == 0 ? "Empty" : b.getFreshness(system.today()).getLabel(),
                b -> b.getUnitsRemaining() == 0 ? "chip-plain" : freshnessStyle(b.getFreshness(system.today()))));
        batchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        batchTable.setPlaceholder(new Label("No batches yet."));
        batchTable.setPrefHeight(400);
    }

    private void buildStockTable() {
        stockTable.getColumns().add(Tables.text("Product", 220, Product::getName));
        stockTable.getColumns().add(Tables.text("Category", 100, p -> p.getCategory().getLabel()));
        stockTable.getColumns().add(Tables.number("Available", 100,
                p -> system.availableUnits(p) + " " + p.getUnitLabel() + "s"));
        stockTable.getColumns().add(Tables.text("Next batch expires", 170, p -> {
            ProductBatch next = system.getInventory().nextBatchToSell(p.getId(), system.today());
            return next == null ? "-" : Fmt.date(next.getExpiresOn());
        }));
        stockTable.getColumns().add(Tables.chip("Status", 130, this::stockStatus, this::stockStatusStyle));
        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stockTable.setPlaceholder(new Label("No products."));
    }

    private void buildIntakeTable() {
        intakeTable.getColumns().add(Tables.text("#", 60, m -> Integer.toString(m.getId())));
        intakeTable.getColumns().add(Tables.text("Date", 130, m -> Fmt.date(m.getDate())));
        intakeTable.getColumns().add(Tables.number("Liters", 100, m -> String.format("%.1f L", m.getLiters())));
        intakeTable.getColumns().add(Tables.text("Source", 220, MilkIntake::getSource));
        intakeTable.getColumns().add(Tables.text("Recorded by", 140, m -> system.displayNameOf(m.getRecordedBy())));
        intakeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        intakeTable.setPlaceholder(new Label("No milk intake recorded yet."));
    }

    private String stockStatus(Product p) {
        if (!p.isActive()) {
            return "Not on sale";
        }
        int units = system.availableUnits(p);
        if (units == 0) {
            return "Out of stock";
        }
        ProductBatch next = system.getInventory().nextBatchToSell(p.getId(), system.today());
        if (next != null && next.getFreshness(system.today()) == ProductBatch.Freshness.NEAR_EXPIRY) {
            return "Near expiry";
        }
        return units <= 5 ? "Low stock" : "In stock";
    }

    private String stockStatusStyle(Product p) {
        switch (stockStatus(p)) {
            case "In stock":
                return "chip-good";
            case "Out of stock":
                return "chip-crit";
            case "Not on sale":
                return "chip-plain";
            default:
                return "chip-warn";
        }
    }

    private static String freshnessStyle(ProductBatch.Freshness f) {
        switch (f) {
            case FRESH:
                return "chip-good";
            case NEAR_EXPIRY:
                return "chip-warn";
            default:
                return "chip-crit";
        }
    }

    @Override
    public void refresh() {
        LocalDate today = system.today();
        milkCard.setValue(String.format("%.1f L", system.getInventory().getRawMilkLiters()));

        int units = 0;
        int expiredUnits = 0;
        for (ProductBatch b : system.getInventory().getBatches()) {
            if (b.isExpired(today)) {
                expiredUnits += b.getUnitsRemaining();
            } else {
                units += b.getUnitsRemaining();
            }
        }
        unitsCard.setValue(Integer.toString(units));
        int near = system.getInventory().getNearExpiryBatches(today).size();
        nearCard.setValue(Integer.toString(near)).setWarning(near > 0);
        expiredCard.setValue(Integer.toString(expiredUnits)).setWarning(expiredUnits > 0);

        List<ProductBatch> rows = new ArrayList<>();
        for (ProductBatch b : system.getInventory().getBatchesByExpiry()) {
            if (showEmpty.isSelected() || b.getUnitsRemaining() > 0) {
                rows.add(b);
            }
        }
        batchTable.setItems(FXCollections.observableArrayList(rows));
        batchTable.refresh();
        stockTable.setItems(FXCollections.observableArrayList(system.getProducts()));
        stockTable.refresh();
        List<MilkIntake> intakes = new ArrayList<>(system.getInventory().getIntakes());
        java.util.Collections.reverse(intakes);
        intakeTable.setItems(FXCollections.observableArrayList(intakes));
    }
}
