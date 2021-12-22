package agh.ics.oop;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MapTile {
    private final ImageResourcesManager imageResourcesManager;
    private final StackPane body = new StackPane();
    private final StackPane content = new StackPane();
    private final int width;
    private final int height;


    MapTile(ContentData data, BackgroundType backgroundType, int width, int height, ImageResourcesManager imageResourcesManager) {
        this.imageResourcesManager = imageResourcesManager;
        this.width = width;
        this.height = height;

        content.setMaxHeight(height);
        content.setMaxHeight(width);
        body.getChildren().add(getImageViewFromPath(backgroundType.getImageRepresentationPath(), width, height));
        body.getChildren().add(content);

        addContent(data);
    }

    public void changeContent(ContentData data) {
        content.getChildren().clear();
        addContent(data);
    }

    public StackPane getBody() {
        return body;
    }

    private void addContent(ContentData data) {
        if (!data.isEmpty)
            content.getChildren().add(getImageViewFromPath(data.contentPath, width, height));
        if (!data.text.isEmpty()) {
            content.getChildren().add(getLabel(data));
        }
    }

    private VBox getLabel(ContentData data) {
        Label text = new Label(data.text);
        text.setFont(new Font(Math.min(10, width / 2)));
        text.setTextFill(Color.BLACK);
        text.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.8),
                new CornerRadii(10.0),
                new Insets(-0.5)
        )));
        text.setAlignment(Pos.BOTTOM_CENTER);

        VBox box = new VBox(text);
        box.setAlignment(Pos.BOTTOM_CENTER);
        return box;
    }

    private ImageView getImageViewFromPath(String path, int width, int height) {
        ImageView img = new ImageView();
        img.setImage(imageResourcesManager.getImage(path));
        img.setFitHeight(height);
        img.setFitWidth(width);
        return img;
    }
}
