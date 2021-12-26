package agh.ics.oop.GUI;

import javafx.scene.control.RadioButton;

public class StartScreenInputBoolean extends StartScreenInputField<Boolean> {
    private final RadioButton button = new RadioButton();

    public StartScreenInputBoolean(String text, boolean defaultValue) {
        super(text);

        button.setSelected(defaultValue);
        body.getChildren().add(button);
    }

    @Override
    public Boolean getInput() {
        return button.isSelected();
    }
}
