package components;

import exception.AgriException;
import ui.ThrowingAction;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/*
 * reusable form pop-up 
 * screens add labelled fields, then call show with the action to run when the user presses the main button
 */
public class FormDialog {

    private final Dialog<ButtonType> dialog = new Dialog<>();
    private final GridPane grid = new GridPane();
    private final Label errorLabel = new Label();
    private final ButtonType okType;
    private int row;


    public FormDialog(String title, String header, String okText) {
        dialog.setTitle(title);
        dialog.setHeaderText(header == null || header.isEmpty() ? null : header);
        okType = new ButtonType(okText, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(8, 4, 4, 4));

        errorLabel.getStyleClass().add("form-error");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(380);
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        VBox content = new VBox(8, grid, errorLabel);
        content.setPadding(new Insets(4));
        dialog.getDialogPane().setContent(content);
        Dialogs.style(dialog.getDialogPane());
        if (Dialogs.getOwner() != null && Dialogs.getOwner().isShowing()) {
            dialog.initOwner(Dialogs.getOwner());
        }
    }


    public TextField addText(String label, String initial, String prompt) {
        TextField field = new TextField(initial == null ? "" : initial);
        field.setPromptText(prompt);
        field.setPrefWidth(260);
        addRow(label, field);
        return field;
    }


    public <T> ComboBox<T> addChoice(String label, Collection<T> items, T selected) {
        ComboBox<T> box = new ComboBox<>();
        box.getItems().addAll(items);
        box.setValue(selected);
        box.setPrefWidth(260);
        addRow(label, box);
        return box;
    }

    public DatePicker addDate(String label, LocalDate initial) {
        DatePicker picker = new DatePicker(initial);
        picker.setPrefWidth(260);
        addRow(label, picker);
        return picker;
    }


    public CheckBox addCheck(String label, boolean checked) {
        CheckBox box = new CheckBox();
        box.setSelected(checked);
        addRow(label, box);
        return box;
    }


    public Label addInfo(String label, String text) {
        Label value = new Label(text);
        value.getStyleClass().add("form-info");
        addRow(label, value);
        return value;
    }


    public void addRow(String label, Node node) {
        Label caption = new Label(label);
        caption.getStyleClass().add("form-caption");
        grid.add(caption, 0, row);
        grid.add(node, 1, row);
        GridPane.setHgrow(node, Priority.ALWAYS);
        row++;
    }

    public boolean show(ThrowingAction onOk) {
        Button ok = (Button) dialog.getDialogPane().lookupButton(okType);
        ok.getStyleClass().add("btn-primary");
        ok.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                onOk.run();
            } catch (AgriException e) {
                showError(e.getMessage());
                event.consume();
            } catch (RuntimeException e) {
                showError("Something unexpected happened: " + e.getMessage());
                event.consume();
            }
        });
        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == okType;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
        dialog.getDialogPane().getScene().getWindow().sizeToScene();
    }
}
