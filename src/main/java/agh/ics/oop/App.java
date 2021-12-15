package agh.ics.oop;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class App extends Application implements IDayChangeObserver {
    enum MapTypes {
        Left(0), Right(1);

        public final int value;

        MapTypes(int value) {
            this.value = value;
        }
    };

    private WorldMap map;
    private AppConfig config;
    private int colHeight;
    private int colWidth;
    private GridPane[] mapGrids;
    private BorderPane layout;
    private HBox mapBox;


    @Override
    public void start(Stage primaryStage) {
        mapGrids = new GridPane[2];
        layout = new BorderPane();
        mapBox = new HBox();
        mapBox.setSpacing(24);

        Button startButton = new Button();
        layout.setLeft(startButton);

        startButton.setText("To Next Day");
        startButton.setOnAction(event -> new Thread(() -> map.toNextDay()).start());
        layout.setCenter(mapBox);

        mapGrids[MapTypes.Right.value] = new GridPane();
        mapGrids[MapTypes.Left.value] = new GridPane();

        mapBox.getChildren().add(MapTypes.Left.value, mapGrids[MapTypes.Left.value]);
        mapBox.getChildren().add(MapTypes.Right.value, mapGrids[MapTypes.Right.value]);

        createMap(MapTypes.Left);
        createMap(MapTypes.Right);

        Scene scene = new Scene(layout, 1400, 720);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createMap(MapTypes type) {
        mapGrids[type.value] = new GridPane();
        mapGrids[type.value].gridLinesVisibleProperty();
        mapGrids[type.value].setGridLinesVisible(true);
        mapBox.getChildren().set(type.value, mapGrids[type.value]);

        int height = 500;
        int width = 500;

        colHeight = height / config.MapHeight;
        for (int i = 0; i <= config.MapHeight; i++) {
            mapGrids[type.value].getRowConstraints().add(new RowConstraints(colHeight));
        }

        colWidth = width / config.MapWidth;
        for (int i = 0; i <= config.MapWidth; i++) {
            mapGrids[type.value].getColumnConstraints().add(new ColumnConstraints(colWidth));
        }

        for (int i = 0; i <= config.MapWidth; i++) {
            for (int j = 0; j <= config.MapHeight; j++) {
                drawObjectAt(new Vector2d(j, i), type);
            }

        }
    }

    private void drawObjectAt(Vector2d position, MapTypes type) {
        if (map.isOccupied(position)) {
            MapEntity object = (MapEntity)map.objectAt(position);
            if (object != null) {
              try {
                ImageView img = new ImageView(new Image(new FileInputStream(object.getImageRepresentationPath())));
                img.setFitWidth(colWidth);
                img.setFitHeight(colHeight);
                mapGrids[type.value].add(img, position.x(), position.y());
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
        //todo: ??
        createMap(MapTypes.Left);
        createMap(MapTypes.Right);
    }
}
