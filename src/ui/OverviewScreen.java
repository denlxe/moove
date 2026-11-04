package ui;

import model.Product;
import model.ProductBatch;
import model.User;
import service.AgriSystem;
import components.StatCard;
import components.Tables;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;

import java.time.LocalDate;
import java.util.List;

/**
 * Manager Dashboard's main screen
 * + shop stats for products and stock
 * + alerts for expired and near-expiry stock
 * + a list of clerks, each with a button that opens that clerk's dashboard in a new window
 */
public class OverviewScreen extends Screen {

    private final SceneManager sceneManager;
    private final AgriSystem system;

    private final StatCard productsCard = new StatCard("Products on sale");
    private final StatCard milkCard = new StatCard("Raw milk in storage");
    private final StatCard nearCard = new StatCard("Batches near expiry");
    private final StatCard outCard = new StatCard("Products out of stock");
    private final Label stockAlert = new Label();
    private final TableView<User> clerkTable = new TableView<>();

    public OverviewScreen(SceneManager sceneManager, AgriSystem system) {
        this.sceneManager = sceneManager;
        this.system = system;
    }

    @Override
    public String getName() {
        return "Overview";
    }

    @Override
    protected Parent build() {
        FlowPane stats = new FlowPane(14, 14, productsCard, milkCard, nearCard, outCard);

        stockAlert.getStyleClass().add("alert-warn");
        stockAlert.setWrapText(true);
        stockAlert.setMaxWidth(Double.MAX_VALUE);

        buildClerkTable();
        clerkTable.setPrefHeight(150);

        return page("Manager dashboard",
                "Monitor products and stock, and open each clerk's dashboard.",
                stats, stockAlert, section("Clerks"), clerkTable);
    }

    private void buildClerkTable() {
        clerkTable.getColumns().add(Tables.text("Clerk", 200, User::getDisplayName));
        clerkTable.getColumns().add(Tables.text("Username", 160, User::getUsername));

        TableColumn<User, User> open = new TableColumn<>("");
        open.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        open.setPrefWidth(180);
        open.setSortable(false);
        open.setCellFactory(col -> new TableCell<User, User>() {
            private final Button button = new Button("Open dashboard");

            {
                button.getStyleClass().add("btn-small");
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                button.setOnAction(e -> sceneManager.openClerkWindow(item));
                setGraphic(button);
            }
        });
        clerkTable.getColumns().add(open);
        clerkTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clerkTable.setPlaceholder(new Label("No clerk accounts."));
    }

    @Override
    public void refresh() {
        LocalDate today = system.today();
        productsCard.setValue(Integer.toString(system.getActiveProducts().size()));
        milkCard.setValue(String.format("%,.1f L", system.getInventory().getRawMilkLiters()));

        List<ProductBatch> near = system.getInventory().getNearExpiryBatches(today);
        List<ProductBatch> expired = system.getInventory().getExpiredBatchesWithStock(today);
        nearCard.setValue(Integer.toString(near.size())).setWarning(!near.isEmpty());
        int out = 0;
        for (Product p : system.getActiveProducts()) {
            if (system.availableUnits(p) == 0) {
                out++;
            }
        }
        outCard.setValue(Integer.toString(out)).setWarning(out > 0);

        StringBuilder alert = new StringBuilder();
        if (!expired.isEmpty()) {
            int units = 0;
            for (ProductBatch b : expired) {
                units += b.getUnitsRemaining();
            }
            alert.append(units).append(" expired units are still in stock. Use Inventory > Discard expired. ");
        }
        if (!near.isEmpty()) {
            alert.append(near.size()).append(" batch(es) expire within ")
                    .append(ProductBatch.NEAR_EXPIRY_DAYS).append(" days.");
        }
        boolean showAlert = alert.length() > 0;
        stockAlert.setText(alert.toString().trim());
        stockAlert.setVisible(showAlert);
        stockAlert.setManaged(showAlert);

        clerkTable.setItems(FXCollections.observableArrayList(system.getClerks()));
        clerkTable.refresh();
    }
}
