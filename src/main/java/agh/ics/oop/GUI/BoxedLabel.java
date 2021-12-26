package agh.ics.oop.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BoxedLabel implements IGuiElement {
    private final VBox body;

    public BoxedLabel(String text, double fontSize) {
        Label label = new Label(text);
        label.setFont(new Font(fontSize));
        label.setTextFill(Color.BLACK);
        label.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.8),
                new CornerRadii(10.0),
                new Insets(-0.5)
        )));
        label.setAlignment(Pos.BOTTOM_CENTER);

        body = new VBox(label);
        body.setAlignment(Pos.BOTTOM_CENTER);
    }

    @Override
    public Node getBody() {
        return body;
    }
}
