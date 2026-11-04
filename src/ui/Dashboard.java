package ui;

import model.User;
import service.AgriSystem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Components
 * + top bar
 * + navigation column 
 * + content area
 *  
 * Subclasses 
 * + ClerkDashboard 
 * + ManagerDashboard
 */
public abstract class Dashboard {

    protected final SceneManager sceneManager; // for switching windows and sign out
    protected final AgriSystem system;
    protected final User user;

    private final StackPane content = new StackPane();
    private final Map<Screen, ToggleButton> navButtons = new LinkedHashMap<>();
    private BorderPane root;

    protected Dashboard(SceneManager sceneManager, AgriSystem system, User user) {
        this.sceneManager = sceneManager;
        this.system = system;
        this.user = user;
    }

    protected abstract String getTitle();
    
    protected abstract List<Screen> createScreens();

    protected boolean isSecondaryWindow() {
        return false;
    }

    protected void onExit() {
        sceneManager.logout();
    }

    public final Parent getRoot() {
        if (root == null) {
            build();
        }
        return root;
    }

    /* Dashboard - BorderPane (root)
     * title - root.top, HBox
     * nav - root.left, VBox
     * content - root.center, StackPane (updates on screne switch -> Overview, Products, Inventory, etc)
     * */
    private void build() {
        root = new BorderPane();
        root.getStyleClass().add("dashboard");
        root.setTop(buildTopBar());

        List<Screen> screens = createScreens();
        ToggleGroup group = new ToggleGroup();
        VBox nav = new VBox(6);
        
        nav.getStyleClass().add("nav");
        nav.setPadding(new Insets(16, 12, 16, 12));
        nav.setMinWidth(190);
        for (Screen screen : screens) {
            ToggleButton button = new ToggleButton(screen.getName());
            button.getStyleClass().add("nav-button");
            button.setToggleGroup(group);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(e -> {
                if (!button.isSelected()) {
                    button.setSelected(true); // clicking active tab must not deselect it
                }
                show(screen);
            });
            navButtons.put(screen, button);
            nav.getChildren().add(button);
        }
        root.setLeft(nav);

        content.getStyleClass().add("content");
        root.setCenter(content);
        if (!screens.isEmpty()) {
            show(screens.get(0));
        }
    }

    // Switches the content area to a screen and refreshes its data.
    public void show(Screen screen) {
        Parent view = screen.getView();
        screen.refresh(); // update data
        content.getChildren().setAll(view);
        ToggleButton button = navButtons.get(screen);
        if (button != null) {
            button.setSelected(true);
        }
    }

    private HBox buildTopBar() {
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/moove-icon.png")));
        logo.setFitHeight(36);
        logo.setFitWidth(36);
        logo.setPreserveRatio(true);

        Label brand = new Label("Moo-ve Dairy");
        Label title = new Label(getTitle());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label who = new Label(user.getDisplayName() + "  |  " + user.getRole().getLabel());
        Button exit = new Button(isSecondaryWindow() ? "Close" : "Log out");
        exit.setOnAction(e -> onExit());

        HBox bar = new HBox(12, logo, brand, title, spacer, who, exit);
        bar.getStyleClass().add("topbar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 20, 10, 20));
        
        brand.getStyleClass().add("topbar-brand");
        title.getStyleClass().add("topbar-title");
        who.getStyleClass().add("topbar-user");
        exit.getStyleClass().add("btn-light");

        return bar;
    }
}
