package components;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.util.function.Function;

// column builders shared by every table
public final class Tables {

    private Tables() {
    }

    public static <T> TableColumn<T, String> text(String title, double width, Function<T, String> text) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(c -> new ReadOnlyStringWrapper(text.apply(c.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    public static <T> TableColumn<T, String> number(String title, double width, Function<T, String> text) {
        TableColumn<T, String> column = text(title, width, text);
        column.setStyle("-fx-alignment: CENTER-RIGHT;");
        return column;
    }

    public static <T> TableColumn<T, String> chip(String title, double width,
                                                  Function<T, String> text, Function<T, String> styleClass) {
        TableColumn<T, String> column = text(title, width, text);
        column.setCellFactory(col -> new TableCell<T, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty() || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                @SuppressWarnings("unchecked")
                T row = getTableRow().getItem();
                Label chip = new Label(item);
                chip.getStyleClass().addAll("chip", styleClass.apply(row));
                setGraphic(chip);
                setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });
        return column;
    }
}
