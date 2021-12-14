package agh.ics.oop;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class App extends Application {
    WorldMap map;
    AppConfig config;
    int colHeight;
    int colWidth;
    GridPane grid;

    @Override
    public void start(Stage primaryStage) {
        BorderPane layout = new BorderPane();

        grid = new GridPane();
        grid.gridLinesVisibleProperty();
        grid.setGridLinesVisible(true);
        layout.setCenter(grid);

        int height = 500;
        int width = 500;

        colHeight = height / config.MapHeight;
        for (int i = 0; i <= config.MapHeight; i++) {
            grid.getRowConstraints().add(new RowConstraints(colHeight));
        }

        colWidth = width / config.MapWidth;
        for (int i = 0; i <= config.MapWidth; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(colWidth));
        }

        for (int i = 0; i <= config.MapWidth; i++) {
            for (int j = 0; j <= config.MapHeight; j++) {
                drawObjectAt(new Vector2d(j, i));
            }

        }

        Scene scene = new Scene(layout, 1400, 720);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawObjectAt(Vector2d position) {
        if (map.isOccupied(position)) {
            MapEntity object = (MapEntity)map.objectAt(position);
            if (object != null) {
              try {
                ImageView img = new ImageView(new Image(new FileInputStream(object.getImageRepresentationPath())));
                img.setFitWidth(colWidth);
                img.setFitHeight(colHeight);
                grid.add(img, position.x(), position.y());
                GridPane.setHalignment(img, HPos.CENTER);
              } catch (FileNotFoundException e) {
                e.printStackTrace();
              }
            }
        }
    }

    @Override
    public void init() {
        config = new AppConfig();
        config.InitialGrassCount = 20;
        config.InitialAnimalCount = 10;
        config.MapHeight = 50;
        config.MapWidth = 50;

        map = new WorldMap(config);
    }
}
