package ui;

import model.Product;
import model.ProductCategory;
import service.AgriSystem;
import components.Dialogs;
import components.FormDialog;
import components.InventoryDialogs;
import components.Tables;
import util.Fmt;
import util.Input;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/*
 * Product management (manager only): the catalog of what the shop sells
 * products are never deleted but are hidden from the sales screen, so old transactions still make sense
 */
public class ProductScreen extends Screen {

    private final AgriSystem system;
    private final TableView<Product> table = new TableView<>();
    private final TextField search = new TextField();
    private FilteredList<Product> filtered;

    public ProductScreen(AgriSystem system) {
        this.system = system;
    }

    @Override
    public String getName() {
        return "Products";
    }

    @Override
    protected Parent build() {
        table.getColumns().add(Tables.text("ID", 60, Product::getId));
        table.getColumns().add(Tables.text("Name", 210, Product::getName));
        table.getColumns().add(Tables.text("Category", 90, p -> p.getCategory().getLabel()));
        table.getColumns().add(Tables.text("Description", 260, Product::getDescription));
        table.getColumns().add(Tables.number("Price", 110, p -> Fmt.money(p.getPrice())));
        table.getColumns().add(Tables.text("Unit", 70, Product::getUnitLabel));
        table.getColumns().add(Tables.number("Shelf life", 90, p -> p.getShelfLifeDays() + " days"));
        table.getColumns().add(Tables.number("In stock", 80, p -> Integer.toString(system.availableUnits(p))));
        table.getColumns().add(Tables.chip("Status", 100, p -> p.isActive() ? "On sale" : "Hidden",
                p -> p.isActive() ? "chip-good" : "chip-plain"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // minimum widths
        int[] minimums = {60, 190, 100, 120, 105, 70, 85, 75, 95};
        for (int i = 0; i < minimums.length; i++) {
            table.getColumns().get(i).setMinWidth(minimums[i]);
        }
        table.setPlaceholder(new Label("No products."));
        table.setPrefHeight(460);

        search.setPromptText("Search products...");
        search.setMaxWidth(260);
        search.textProperty().addListener((o, a, b) -> applyFilter());

        Button add = new Button("Add product");
        add.getStyleClass().add("btn-primary");
        add.setOnAction(e -> addProduct());
        Button edit = new Button("Edit selected");
        edit.setOnAction(e -> withSelection(this::editProduct));
        Button toggle = new Button("Show / hide on sale");
        toggle.setOnAction(e -> withSelection(this::toggleActive));
        Button restock = new Button("Add stock for selected");
        restock.setOnAction(e -> withSelection(p -> {
            if (InventoryDialogs.addBatch(system, p)) {
                refresh();
            }
        }));

        HBox bar = new HBox(10, add, edit, toggle, restock, search);
        VBox.setVgrow(table, Priority.ALWAYS);
        return page("Products", "Add products, change prices and descriptions, or hide items from the sales screen.",
                bar, table);
    }

    private void withSelection(java.util.function.Consumer<Product> action) {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.info("Select a product", "Click a row in the table first.");
            return;
        }
        action.accept(selected);
    }

    private void addProduct() {
        FormDialog dialog = new FormDialog("Add product", "Add a new item to the catalog.", "Add product");
        ComboBox<ProductCategory> category = dialog.addChoice("Category",
                java.util.Arrays.asList(ProductCategory.values()), ProductCategory.LIQUID);
        TextField name = dialog.addText("Name", "", "e.g. Goat milk");
        TextField description = dialog.addText("Description", "", "Short description");
        TextField price = dialog.addText("Price (PHP)", "", "e.g. 85.00");
        TextField shelf = dialog.addText("Shelf life (days)",
                Integer.toString(ProductCategory.LIQUID.defaultShelfLifeDays()), "Days a fresh batch stays sellable");
        category.valueProperty().addListener((o, a, b) -> {
            if (b != null) {
                shelf.setText(Integer.toString(b.defaultShelfLifeDays()));
            }
        });
        boolean done = dialog.show(() -> system.addProduct(
                Input.choice(category.getValue(), "category"), name.getText(), description.getText(),
                Input.positiveNumber(price.getText(), "Price"),
                Input.positiveInt(shelf.getText(), "Shelf life")));
        if (done) {
            refresh();
        }
    }

    private void editProduct(Product product) {
        FormDialog dialog = new FormDialog("Edit " + product.getName(),
                "Past sales keep the name and price they were sold at.", "Save changes");
        dialog.addInfo("Category", product.getCategory().getLabel());
        TextField name = dialog.addText("Name", product.getName(), "");
        TextField description = dialog.addText("Description", product.getDescription(), "");
        TextField price = dialog.addText("Price (PHP)", String.format("%.2f", product.getPrice()), "");
        TextField shelf = dialog.addText("Shelf life (days)", Integer.toString(product.getShelfLifeDays()), "");
        boolean done = dialog.show(() -> system.updateProduct(product, name.getText(), description.getText(),
                Input.positiveNumber(price.getText(), "Price"),
                Input.positiveInt(shelf.getText(), "Shelf life")));
        if (done) {
            refresh();
        }
    }

    private void toggleActive(Product product) {
        boolean next = !product.isActive();
        if (Dialogs.attempt(() -> system.setProductActive(product, next))) {
            refresh();
        }
    }

    private void applyFilter() {
        if (filtered == null) {
            return;
        }
        String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();
        filtered.setPredicate(p -> q.isEmpty() || p.getName().toLowerCase().contains(q)
                || p.getDescription().toLowerCase().contains(q) || p.getCategory().getLabel().toLowerCase().contains(q));
    }

    @Override
    public void refresh() {
        filtered = new FilteredList<>(FXCollections.observableArrayList(system.getProducts()));
        table.setItems(filtered);
        applyFilter();
        table.refresh();
    }
}
