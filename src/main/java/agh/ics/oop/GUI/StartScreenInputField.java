package agh.ics.oop.GUI;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public abstract class StartScreenInputField<T> implements IGuiElement {
    protected final HBox body = new HBox();

    public StartScreenInputField(String text) {
        Label label = new Label(text + ": ");
        body.getChildren().add(label);
    }

    @Override
    public Node getBody() {
        return body;
    }

    public abstract T getInput();
}
