package ui;

import exception.AuthenticationException;
import model.User;
import service.AgriSystem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginScreen extends Screen {

    private final SceneManager sceneManager;
    private final AgriSystem system;
    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final Label error = new Label();

    public LoginScreen(SceneManager sceneManager, AgriSystem system) {
        this.sceneManager = sceneManager;
        this.system = system;
    }

    @Override
    public String getName() {
        return "Login";
    }

    @Override
    protected Parent build() {
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/moove-logo.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        Label tagline = new Label("Moving local dairy forward.");
        tagline.getStyleClass().add("login-tagline");
        Label welcome = new Label("Tara, moo-ve na! Sign in to continue.");
        welcome.getStyleClass().add("page-subtitle");

        username.setPromptText("Username");
        password.setPromptText("Password");
        username.setMaxWidth(320);
        password.setMaxWidth(320);
        password.setOnAction(e -> login());
        username.setOnAction(e -> password.requestFocus());

        error.getStyleClass().add("form-error");
        error.setWrapText(true);
        error.setMaxWidth(320);
        error.setManaged(false);
        error.setVisible(false);

        Button loginButton = new Button("Log in");
        loginButton.getStyleClass().add("btn-primary");
        loginButton.setMaxWidth(320);
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> login());

        Label hint = new Label("Demo accounts (change them in data/users.csv)\n"
                + "Manager:  manager / manager123\n"
                + "Clerk:  clerk1 / clerk123   or   clerk2 / clerk123");
        hint.getStyleClass().add("login-hint");

        VBox card = new VBox(12, logo, tagline, welcome, username, password, error, loginButton, hint);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 48, 32, 48));
        card.setMaxWidth(440);
        card.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

        StackPane root = new StackPane(card);
        root.getStyleClass().add("login-root");
        return root;
    }

    private void login() {
        try {
            User user = system.login(username.getText(), password.getText());
            error.setVisible(false);
            error.setManaged(false);
            password.clear();
            sceneManager.showDashboardFor(user);
        } catch (AuthenticationException e) {
            error.setText(e.getMessage());
            error.setVisible(true);
            error.setManaged(true);
            password.clear();
            password.requestFocus();
        }
    }
}
