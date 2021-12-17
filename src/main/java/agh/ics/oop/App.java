package agh.ics.oop;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Optional;

public class App extends Application implements IDayChangeObserver {

    private final ImageResourcesManager imageResourcesManager = new ImageResourcesManager();
    private WorldMap map;
    private AppConfig config;
    private int colHeight;
    private int colWidth;
    private GridPane[] mapGrids;
    private MapTile[][][] mapTiles;
    private BorderPane layout;
    private HBox mapBox;

    @Override
    public void start(Stage primaryStage) {
        mapGrids = new GridPane[2];
        mapTiles = new MapTile[2][config.MapWidth][config.MapHeight];
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
        for (int i = 0; i < config.MapHeight; i++) {
            mapGrids[type.value].getRowConstraints().add(new RowConstraints(colHeight));
        }

        colWidth = width / config.MapWidth;
        for (int i = 0; i < config.MapWidth; i++) {
            mapGrids[type.value].getColumnConstraints().add(new ColumnConstraints(colWidth));
        }

        for (int i = 0; i < config.MapWidth; i++) {
            for (int j = 0; j < config.MapHeight; j++) {
                drawObjectAt(new Vector2d(j, i), type);
            }

        }
    }

    private void drawObjectAt(Vector2d position, MapTypes type) {
        mapTiles[type.value][position.x()][position.y()] = new MapTile(contentPathAt(position), colWidth, colHeight, imageResourcesManager);
        mapGrids[type.value].add(mapTiles[type.value][position.x()][position.y()].getBody(), position.x(), position.y());
        GridPane.setHalignment(mapTiles[type.value][position.x()][position.y()].getBody(), HPos.CENTER);
    }

    private Optional<String> contentPathAt(Vector2d position) {
        MapEntity object = (MapEntity) map.objectAt(position);

        if (object != null) {
            return Optional.of(object.getImageRepresentationPath());
        }

        return Optional.empty();
    }

    @Override
    public void init() {
        config = new AppConfig();
        config.InitialGrassCount = 700;
        config.InitialAnimalCount = 100;
        config.MapHeight = 30;
        config.MapWidth = 30;
        config.StartEnergy = 80;
        config.MoveEnergy = 10;
        config.PlantEnergy = 50;

        map = new WorldMap(config, this);
    }

    @Override
    public void onDayChanged(HashSet<Vector2d> changedTiles) {
        mapGrids[MapTypes.Left.value].getChildren().stream()
                .filter(Node::hasProperties)
                .map(node -> new Vector2d(GridPane.getColumnIndex(node), GridPane.getRowIndex(node)))
                .filter(changedTiles::contains)
                .forEach(position -> mapTiles[MapTypes.Left.value][position.x()][position.y()].changeContent(contentPathAt(position)));
    }

    enum MapTypes {
        Left(0), Right(1);

        public final int value;

        MapTypes(int value) {
            this.value = value;
        }
    }
}
