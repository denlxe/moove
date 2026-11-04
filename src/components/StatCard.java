package components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

// small summary card with caption and number
public final class StatCard extends VBox {

    private final Label valueLabel = new Label();
    private final Label noteLabel = new Label();


    public StatCard(String title) {
        getStyleClass().add("stat-card");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");
        valueLabel.getStyleClass().add("stat-value");
        noteLabel.getStyleClass().add("stat-note");
        noteLabel.setManaged(false);
        noteLabel.setVisible(false);
        getChildren().addAll(titleLabel, valueLabel, noteLabel);
        setMinWidth(170);
    }

    public StatCard setValue(String value) {
        valueLabel.setText(value);
        return this;
    }

    public StatCard setNote(String note) {
        boolean show = note != null && !note.isEmpty();
        noteLabel.setText(show ? note : "");
        noteLabel.setManaged(show);
        noteLabel.setVisible(show);
        return this;
    }


    public StatCard setWarning(boolean warn) {
        if (warn) {
            if (!getStyleClass().contains("stat-card-warn")) {
                getStyleClass().add("stat-card-warn");
            }
        } else {
            getStyleClass().remove("stat-card-warn");
        }
        return this;
    }
}
