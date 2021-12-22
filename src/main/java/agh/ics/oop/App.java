package agh.ics.oop;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashSet;

public class App extends Application implements IDayChangeObserver {
    private static final int mapCount = 2;
    private final ImageResourcesManager imageResourcesManager = new ImageResourcesManager();
    private final ChartsPane[] chartsPanes = new ChartsPane[mapCount];
    private final Label[] mostCommonGenome = new Label[mapCount];
    private State state = State.Stopped;
    private WorldMap[] maps;
    private AppConfig config;
    private int colHeight;
    private int colWidth;
    private GridPane[] mapGrids;
    private MapTile[][][] mapTiles;
    private HBox mapBox;
    private SymulationRunner symulationRunner;
    private Jungle jungle;

    @Override
    public void start(Stage primaryStage) {
        symulationRunner = new SymulationRunner(maps, 300);

        mostCommonGenome[MapTypes.Bordered.value] = new Label();
        mostCommonGenome[MapTypes.Wrapped.value] = new Label();

        chartsPanes[MapTypes.Bordered.value] = new ChartsPane();
        chartsPanes[MapTypes.Wrapped.value] = new ChartsPane();

        VBox leftLayout = new VBox();
        leftLayout.getChildren().add(mostCommonGenome[MapTypes.Wrapped.value]);
        leftLayout.getChildren().add(chartsPanes[MapTypes.Wrapped.value].getBody());

        VBox rightLayout = new VBox();
        rightLayout.getChildren().add(mostCommonGenome[MapTypes.Bordered.value]);
        rightLayout.getChildren().add(chartsPanes[MapTypes.Bordered.value].getBody());

        leftLayout.setMinWidth(400);
        leftLayout.setMaxWidth(400);
        leftLayout.setMaxHeight(320);
        rightLayout.setMinWidth(400);
        rightLayout.setMaxWidth(400);
        rightLayout.setMaxHeight(320);

        mapGrids = new GridPane[mapCount];
        mapTiles = new MapTile[mapCount][config.MapWidth][config.MapHeight];
        BorderPane layout = new BorderPane();
        mapBox = new HBox();
        mapBox.setSpacing(25);

        Button startButton = new Button();
        layout.setTop(startButton);

        layout.setLeft(leftLayout);
        layout.setRight(rightLayout);

        startButton.setText("Press to START");
        startButton.setOnAction(event -> {
            switch (state) {
                case Stopped -> {
                    startButton.setText("Pause");
                    symulationRunner.resume();
                    new Thread(symulationRunner).start();
                }
                case Running -> {
                    startButton.setText("Resume");
                    symulationRunner.pause();
                }
            }

            state = state.next();
        });

        layout.setCenter(mapBox);

        initializeMap(MapTypes.Wrapped);
        initializeMap(MapTypes.Bordered);

        Scene scene = new Scene(layout, 1920, 1080);

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

        int height = 550;
        int width = 550;

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
        BackgroundType bgType = jungle.isAt(position)
                ? BackgroundType.Jungle
                : BackgroundType.Regular;

        mapTiles[type.value][position.x()][position.y()] = new MapTile(contentPathAt(position, type), bgType, colWidth, colHeight, imageResourcesManager);
        mapGrids[type.value].add(mapTiles[type.value][position.x()][position.y()].getBody(), position.x(), position.y());
        GridPane.setHalignment(mapTiles[type.value][position.x()][position.y()].getBody(), HPos.CENTER);
    }

    private ContentData contentPathAt(Vector2d position, MapTypes type) {
        MapEntity object = (MapEntity) maps[type.value].objectAt(position);

        if (object == null) {
            return ContentData.Empty();
        }

        return new ContentData(object.getImageRepresentationPath(), object.getRepresentationLabel());
    }

    @Override
    public void init() {
        config = new AppConfig();
        config.InitialGrassCount = 40;
        config.InitialAnimalCount = 90;
        config.MapHeight = 40;
        config.MapWidth = 40;
        config.StartEnergy = 180;
        config.MoveEnergy = 10;
        config.PlantEnergy = 200;
        config.JungleRatio = 0.5f;

        jungle = new Jungle(config.MapWidth, config.MapHeight, config.JungleRatio);

        maps = new WorldMap[mapCount];
        maps[MapTypes.Bordered.value] = new BorderedWorldMap(config, jungle, this);
        maps[MapTypes.Wrapped.value] = new WrappedWorldMap(config, jungle, this);
    }

    @Override
    public void onDayChanged(HashSet<Vector2d> changedTiles, MapTypes type) {
        mapGrids[type.value].getChildren().stream()
                .filter(Node::hasProperties)
                .map(node -> new Vector2d(GridPane.getColumnIndex(node), GridPane.getRowIndex(node)))
                .filter(changedTiles::contains)
                .forEach(position -> mapTiles[type.value][position.x()][position.y()].changeContent(contentPathAt(position, type)));

        int day = maps[type.value].getDayCount() - 1;
        chartsPanes[type.value].addValue(ChartTypes.animalCount, day, maps[type.value].getAnimalCount());
        chartsPanes[type.value].addValue(ChartTypes.grassCount, day, maps[type.value].getGrassCount());
        chartsPanes[type.value].addValue(ChartTypes.averageChildrenCount, day, maps[type.value].getAverageChildrenCount());
        chartsPanes[type.value].addValue(ChartTypes.averageEnergyCount, day, maps[type.value].getAverageEnergyLevel());

        if (maps[type.value].haveAnyDeadAnimals())
            chartsPanes[type.value].addValue(ChartTypes.averageLengthOfLife, day, maps[type.value].getAverageLengthOfLife());

        mostCommonGenome[type.value].setText(maps[type.value].getMostCommonGenome().toString());
    }

    private enum State {
        Running, Stopped;

        public State next() {
            return switch (this) {
                case Stopped -> Running;
                case Running -> Stopped;
            };
        }
    }
}
