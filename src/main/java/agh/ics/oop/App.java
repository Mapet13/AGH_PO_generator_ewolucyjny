package agh.ics.oop;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class App extends Application implements IDayChangeObserver {
    private WorldMap map;
    private AppConfig config;
    private int colHeight;
    private int colWidth;
    private GridPane grid;
    private BorderPane layout;

    @Override
    public void start(Stage primaryStage) {
        layout = new BorderPane();

        Button startButton = new Button();
        layout.setLeft(startButton);

        startButton.setText("To Next Day");
        startButton.setOnAction(event -> new Thread(() -> map.toNextDay()).start());

        createMap();

        Scene scene = new Scene(layout, 1400, 720);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createMap() {
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
        config.InitialGrassCount = 10;
        config.InitialAnimalCount = 7;
        config.MapHeight = 5;
        config.MapWidth = 5;
        config.StartEnergy = 500;
        config.MoveEnergy = 10;

        map = new WorldMap(config, this);
    }

    @Override
    public void onDayChanged() {
        createMap();
    }
}
