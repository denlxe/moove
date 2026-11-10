package ui;

import model.Role;
import model.User;
import service.AgriSystem;
import components.Dialogs;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Handles window switching
 * + The main window swaps between the login screen and a dashboard
 * + manager can open a clerk's dashboard in a separate window.
 */
public class SceneManager {

    private static final double WIDTH = 1240;
    private static final double HEIGHT = 780;

    private final Stage stage;
    private final AgriSystem system;
    private final Scene scene = new Scene(new StackPane(), WIDTH, HEIGHT);
    private final Image icon = new Image(getClass().getResourceAsStream("/images/moove-icon.png"));

    public SceneManager(Stage stage, AgriSystem system) {
        this.stage = stage;
        this.system = system;
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.getIcons().add(icon);
        stage.setMinWidth(1020);
        stage.setMinHeight(660);
        Dialogs.setOwner(stage);
    }

    public void showLogin() {
        stage.setTitle("Moove");
        scene.setRoot(new LoginScreen(this, system).getView());
    }

    // shows the right dashboard for a signed-in user in the main window
    public void showDashboardFor(User user) {
        Dashboard dashboard = user.getRole() == Role.MANAGER
                ? new ManagerDashboard(this, system, user)
                : new ClerkDashboard(this, system, user, false);
        stage.setTitle("Moove: " + user.getDisplayName());
        scene.setRoot(dashboard.getRoot());
    }

    // opens clerk's dashboard in a separate view-only window (for manager)
    public void openClerkWindow(User clerk) {
        Stage window = new Stage();
        ClerkDashboard dashboard = new ClerkDashboard(this, system, clerk, true) {
            @Override
            protected void onExit() {
                window.close();
            }
        };
        Scene clerkScene = new Scene(dashboard.getRoot(), 1100, 700);
        clerkScene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        window.setScene(clerkScene);
        window.getIcons().add(icon);
        window.setTitle("Moove: " + clerk.getDisplayName() + " (view only)");
        window.show();
    }

    public void logout() {
        system.logout();
        showLogin();
    }
}
