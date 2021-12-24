package agh.ics.oop;

import javafx.event.EventHandler;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class MapTile {
    private static final double maxFontSize = 10.0f;
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

    public void setOnMouseClicked(EventHandler<? super MouseEvent> onMouseClick) {
        body.setOnMouseClicked(onMouseClick);
    }

    public void changeContent(ContentData data) {
        content.getChildren().clear();
        addContent(data);
    }

    public StackPane getBody() {
        return body;
    }

    public void applySelectionOnContent() {
        if (!content.getChildren().isEmpty()) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(-0.8);
            content.getChildren().get(0).setEffect(colorAdjust);
        }
    }

    private void addContent(ContentData data) {
        if (!data.isEmpty)
            content.getChildren().add(getImageViewFromPath(data.contentPath, width, height));
        if (!data.text.isEmpty())
            content.getChildren().add(new BoxedLabel(data.text, Math.min(maxFontSize, width / 2.0f)).body);
    }

    private ImageView getImageViewFromPath(String path, int width, int height) {
        ImageView img = new ImageView();
        img.setImage(imageResourcesManager.getImage(path));
        img.setFitHeight(height);
        img.setFitWidth(width);
        return img;
    }

    public void removeSelectionOnContent() {
        if (!content.getChildren().isEmpty()) {
            content.getChildren().get(0).setEffect(null);
        }
    }
}
