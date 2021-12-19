package agh.ics.oop;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Optional;

public class MapTile extends Node {
    private final ImageResourcesManager imageResourcesManager;
    private final StackPane body = new StackPane();
    private final int width;
    private final int height;

    MapTile(Optional<String> contentPath, BackgroundType backgroundType, int width, int height, ImageResourcesManager imageResourcesManager) {
        this.imageResourcesManager = imageResourcesManager;
        this.width = width;
        this.height = height;

        body.getChildren().add(getImageViewFromPath(backgroundType.getImageRepresentationPath(), width, height));
        contentPath.ifPresent(path -> body.getChildren().add(getImageViewFromPath(path, width, height)));
    }

    public void changeContent(Optional<String> contentPath) {
        if (containsContent())
            body.getChildren().remove(1);
        contentPath.ifPresent(path -> body.getChildren().add(getImageViewFromPath(path, width, height)));
    }

    public boolean containsContent() {
        return body.getChildren().size() == 2;
    }

    public StackPane getBody() {
        return body;
    }

    private ImageView getImageViewFromPath(String path, int width, int height) {
        ImageView img = new ImageView();
        img.setImage(imageResourcesManager.getImage(path));
        img.setFitHeight(height);
        img.setFitWidth(width);
        return img;
    }

}
