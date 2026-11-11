package components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Product;
import util.Fmt;

/* TODO:
 * A tappable product tile for the sales screen. 
 * - It shows the name, price and stock, flags near-expiry stock, and disables itself when nothing can be sold.
 * - extend the Button class
 */

public class ProductCard extends Button {
	/* Constructor should contain:
	 * product    the product to show
     * stock      units that can be sold right now
     * nearExpiry true if the next batch to sell is close to expiring
     * enabled    false makes the card read-only (for example a manager viewing a clerk)
     * onClick    what to do when the card is clicked
     */
	
	public ProductCard(Product product, int stock, boolean nearExpiry, boolean enabled, Runnable onClick) {
		// reuse the yellow button style as a warning colour for near-expiry stock
        if (nearExpiry && stock > 0) {
            getStyleClass().add("btn-light-dark");
        }
        setPrefWidth(200);
        setMinHeight(110);
        setPrefHeight(130);
        setAlignment(Pos.TOP_LEFT);
 
        Label category = new Label(product.getCategory().getLabel());
        category.getStyleClass().addAll("chip", "chip-plain");
 
        Label name = new Label(product.getName());
        name.setWrapText(true);
        name.setStyle("-fx-font-size: 15px;");
 
        Label price = new Label(Fmt.money(product.getPrice()) + " / " + product.getUnitLabel());
 
        // the stock chip turns red when nothing is left
        Label stockChip = new Label(stock > 0 ? stock + " in stock" : "Out of stock");
        stockChip.getStyleClass().addAll("chip", stock > 0 ? "chip-good" : "chip-crit");
 
        HBox footer = new HBox(6, stockChip);
        if (nearExpiry && stock > 0) {
            Label nearChip = new Label("Near expiry");
            nearChip.getStyleClass().addAll("chip", "chip-warn");
            footer.getChildren().add(nearChip);
        }
	
		VBox content = new VBox(6, category, name, price, footer);
	    content.setAlignment(Pos.TOP_LEFT);
	    setGraphic(content);
	
	    // a card with no stock, or a read-only card, cannot be clicked
	    setDisable(!enabled || stock <= 0);
	    setOnAction(e -> {
	        if (onClick != null) {
	            onClick.run();
	        }
	    });
    
	}
}