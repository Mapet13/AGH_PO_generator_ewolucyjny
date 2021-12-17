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

    private static final int mapCount = 2;
    private final ImageResourcesManager imageResourcesManager = new ImageResourcesManager();
    private WorldMap[] maps;
    private AppConfig config;
    private int colHeight;
    private int colWidth;
    private GridPane[] mapGrids;
    private MapTile[][][] mapTiles;
    private BorderPane layout;
    private HBox mapBox;

    @Override
    public void start(Stage primaryStage) {
        mapGrids = new GridPane[mapCount];
        mapTiles = new MapTile[mapCount][config.MapWidth][config.MapHeight];
        layout = new BorderPane();
        mapBox = new HBox();
        mapBox.setSpacing(24);

        Button startButton = new Button();
        layout.setLeft(startButton);

        startButton.setText("To Next Day");
        startButton.setOnAction(event -> new Thread(() -> {
            maps[MapTypes.Wrapped.value].toNextDay();
            maps[MapTypes.Bordered.value].toNextDay();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start());
        layout.setCenter(mapBox);

        initializeMap(MapTypes.Wrapped);
        initializeMap(MapTypes.Bordered);

        Scene scene = new Scene(layout, 1400, 720);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeMap(MapTypes type) {
        mapGrids[type.value] = new GridPane();
        mapBox.getChildren().add(type.value, mapGrids[type.value]);
        createMap(type);
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
        mapTiles[type.value][position.x()][position.y()] = new MapTile(contentPathAt(position, type), colWidth, colHeight, imageResourcesManager);
        mapGrids[type.value].add(mapTiles[type.value][position.x()][position.y()].getBody(), position.x(), position.y());
        GridPane.setHalignment(mapTiles[type.value][position.x()][position.y()].getBody(), HPos.CENTER);
    }

    private Optional<String> contentPathAt(Vector2d position, MapTypes type) {
        MapEntity object = (MapEntity) maps[type.value].objectAt(position);

        if (object != null) {
            return Optional.of(object.getImageRepresentationPath());
        }

        return Optional.empty();
    }

    @Override
    public void init() {
        config = new AppConfig();
        config.InitialGrassCount = 100;
        config.InitialAnimalCount = 70;
        config.MapHeight = 15;
        config.MapWidth = 15;
        config.StartEnergy = 80;
        config.MoveEnergy = 10;
        config.PlantEnergy = 50;

        maps = new WorldMap[mapCount];
        maps[MapTypes.Bordered.value] = new BorderedWorldMap(config, this);
        maps[MapTypes.Wrapped.value] = new WrappedWorldMap(config, this);
    }

    @Override
    public void onDayChanged(HashSet<Vector2d> changedTiles, MapTypes type) {
        mapGrids[type.value].getChildren().stream()
                .filter(Node::hasProperties)
                .map(node -> new Vector2d(GridPane.getColumnIndex(node), GridPane.getRowIndex(node)))
                .filter(changedTiles::contains)
                .forEach(position -> mapTiles[type.value][position.x()][position.y()].changeContent(contentPathAt(position, type)));
    }
}
