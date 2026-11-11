package ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/* screen builds its view once, then refresh() reloads the data every time it is shown
 * Subclasses:
 * + 
 */
public abstract class Screen {

    private Parent view;

    public abstract String getName();

    protected abstract Parent build();
    
    public void refresh() {
        // screens with no changing data need not override this
    }

    public final Parent getView() {
        if (view == null) {
            view = build();
        }
        return view;
    }

    // builds page layout
    // Node... acts like an array but is flexible, it can handle 0 or more Node objects as argument
    protected static Parent page(String title, String subtitle, Node... content) {
        Label heading = new Label(title);
        
        VBox box = new VBox(16);
        box.getChildren().add(heading);
        if (subtitle != null && !subtitle.isEmpty()) {
            Label sub = new Label(subtitle);
            sub.getStyleClass().add("page-subtitle");
            sub.setWrapText(true);
            box.getChildren().add(sub);
        }
        box.getChildren().addAll(content);
        
        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        
        heading.getStyleClass().add("page-title");
        box.getStyleClass().add("page");
        box.setPadding(new Insets(24, 28, 24, 28));
        scroll.getStyleClass().add("page-scroll");
        
        return scroll;
    }

    // label for a section inside a page
    protected static Label section(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }
}
