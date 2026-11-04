package components;

import exception.AgriException;
import ui.ThrowingAction;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

import java.util.Optional;

/// helpers for pop-ups
public final class Dialogs {

    private static Window owner;

    private Dialogs() {
    }

    public static void setOwner(Window window) {
        owner = window;
    }

    // returns the main window, or null before the app has started
    static Window getOwner() {
        return owner;
    }

    // applies the app stylesheet to a dialog so it matches the rest of the UI.
    static void style(DialogPane pane) {
        pane.getStylesheets().add(Dialogs.class.getResource("/css/app.css").toExternalForm());
    }

    // shows an error message
    public static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("That did not work");
        prepare(alert);
        alert.showAndWait();
    }

    // shows an information message
    public static void info(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(title);
        prepare(alert);
        alert.showAndWait();
    }

    // asks a yes/no question
    public static boolean confirm(String title, String message, String okText) {
        ButtonType ok = new ButtonType(okText, javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ok, ButtonType.CANCEL);
        alert.setHeaderText(title);
        prepare(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ok;
    }

    // runs an action and shows its error, if any
    // this for buttons that do not need a form
    public static boolean attempt(ThrowingAction action) {
        try {
            action.run();
            return true;
        } catch (AgriException e) {
            error(e.getMessage());
        } catch (RuntimeException e) {
            error("Something unexpected happened: " + e.getMessage());
        }
        return false;
    }

    private static void prepare(Alert alert) {
        if (owner != null && owner.isShowing()) {
            alert.initOwner(owner);
        }
        style(alert.getDialogPane());
    }
}
