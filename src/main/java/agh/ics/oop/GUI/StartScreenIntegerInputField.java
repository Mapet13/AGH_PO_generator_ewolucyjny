package agh.ics.oop.GUI;

import javafx.scene.control.TextField;

public class StartScreenIntegerInputField extends StartScreenInputField<Integer> {
    private final TextField input = new TextField();

    public StartScreenIntegerInputField(String text, int defaultValue) {
        super(text);
        input.setText(String.valueOf(defaultValue));
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) return;
            input.setText(newValue.replaceAll("[^\\d]", ""));
        });
        body.getChildren().add(input);
    }

    @Override
    public Integer getInput() {
        return Integer.parseInt(input.getText());
    }
}
