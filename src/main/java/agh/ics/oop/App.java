package agh.ics.oop;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final Label genomeText = new Label();
    private final Label childrenText = new Label();
    private final Label ancestorsText = new Label();
    private final Label dayOfDeathText = new Label();
    private final ArrayList<MapTile> selectedAnimals = new ArrayList<>();
    private boolean isFollowingSelectedAnimal = false;
    private Animal followingAnimal;
    private MapTypes followingAnimalMapType;
    private int childrenCountUntilFollowing;
    private int ancestorsCountUntilFollowing;
    private final HistoryRecorder[] historyRecorder = new HistoryRecorder[]{new HistoryRecorder(), new HistoryRecorder()};

    @Override
    public void start(Stage primaryStage) {
        BorderPane layout = new BorderPane();
        String saveStatisticsToFileText = "Save statistics to file";
        String choseAllAnimalsWithMostCommonGenotypeText = "Chose All Animals With Most Common Genotype";
        Button[] saveStatisticsToFile = new Button[]{new Button(saveStatisticsToFileText), new Button(saveStatisticsToFileText)};
        Button[] choseAllAnimalsWithMostCommonGenotype = new Button[]{new Button(choseAllAnimalsWithMostCommonGenotypeText), new Button(choseAllAnimalsWithMostCommonGenotypeText)};

        Arrays.stream(MapTypes.values()).forEach(type ->  {
            saveStatisticsToFile[type.value].setOnAction(event ->
                    historyRecorder[type.value].saveToFile("statistics.csv")
            );
        });

        Arrays.stream(MapTypes.values()).forEach(type -> {
            choseAllAnimalsWithMostCommonGenotype[type.value].setOnAction(event -> {
                selectedAnimals.forEach(MapTile::removeSelectionOnContent);
                selectedAnimals.clear();
                var animals = maps[type.value].getAnimalsWithGenome(maps[type.value].getMostCommonGenome());
                animals.forEach(animal -> {
                    var pos = animal.getPosition();
                    if(maps[type.value].objectAt(pos).equals(animal)) {
                        selectedAnimals.add(mapTiles[type.value][pos.x()][pos.y()]);
                    }
                });

                selectedAnimals.forEach(MapTile::applySelectionOnContent);
            });
        });

        layout.setBottom(new VBox(genomeText, childrenText, ancestorsText, dayOfDeathText));

        symulationRunner = new SymulationRunner(maps, 300);

        mostCommonGenome[MapTypes.Bordered.value] = new Label();
        mostCommonGenome[MapTypes.Wrapped.value] = new Label();

        chartsPanes[MapTypes.Bordered.value] = new ChartsPane();
        chartsPanes[MapTypes.Wrapped.value] = new ChartsPane();

        VBox leftLayout = new VBox();
        leftLayout.getChildren().add(mostCommonGenome[MapTypes.Wrapped.value]);
        leftLayout.getChildren().add(chartsPanes[MapTypes.Wrapped.value].getBody());
        leftLayout.getChildren().add(saveStatisticsToFile[MapTypes.Wrapped.value]);
        leftLayout.getChildren().add(choseAllAnimalsWithMostCommonGenotype[MapTypes.Wrapped.value]);

        VBox rightLayout = new VBox();
        rightLayout.getChildren().add(mostCommonGenome[MapTypes.Bordered.value]);
        rightLayout.getChildren().add(chartsPanes[MapTypes.Bordered.value].getBody());
        rightLayout.getChildren().add(saveStatisticsToFile[MapTypes.Bordered.value]);
        rightLayout.getChildren().add(choseAllAnimalsWithMostCommonGenotype[MapTypes.Bordered.value]);

        leftLayout.setMinWidth(400);
        leftLayout.setMaxWidth(400);
        leftLayout.setMaxHeight(320);
        rightLayout.setMinWidth(400);
        rightLayout.setMaxWidth(400);
        rightLayout.setMaxHeight(320);

        mapGrids = new GridPane[mapCount];
        mapTiles = new MapTile[mapCount][config.MapWidth][config.MapHeight];

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
                    if(!isFollowingSelectedAnimal) {
                        selectedAnimals.clear();
                        ancestorsText.setText("");
                        childrenText.setText("");
                        genomeText.setText("");
                        dayOfDeathText.setText("");
                    }
                    rightLayout.getChildren().remove(saveStatisticsToFile[MapTypes.Bordered.value]);
                    leftLayout.getChildren().remove(saveStatisticsToFile[MapTypes.Wrapped.value]);
                    rightLayout.getChildren().remove(choseAllAnimalsWithMostCommonGenotype[MapTypes.Bordered.value]);
                    leftLayout.getChildren().remove(choseAllAnimalsWithMostCommonGenotype[MapTypes.Wrapped.value]);
                }
                case Running -> {
                    startButton.setText("Resume");
                    symulationRunner.pause();
                    rightLayout.getChildren().add(saveStatisticsToFile[MapTypes.Bordered.value]);
                    leftLayout.getChildren().add(saveStatisticsToFile[MapTypes.Wrapped.value]);
                    rightLayout.getChildren().add(choseAllAnimalsWithMostCommonGenotype[MapTypes.Bordered.value]);
                    leftLayout.getChildren().add(choseAllAnimalsWithMostCommonGenotype[MapTypes.Wrapped.value]);
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

        int mapMaxSize = 550;
        int height = mapMaxSize * config.MapHeight / (Math.max(config.MapHeight, config.MapWidth));
        int width = mapMaxSize * config.MapWidth / (Math.max(config.MapHeight, config.MapWidth));

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
                drawObjectAt(new Vector2d(i, j), type);
            }

        }
    }

    private void drawObjectAt(Vector2d position, MapTypes type) {
        BackgroundType backgroundType = jungle.isAt(position)
                ? BackgroundType.Jungle
                : BackgroundType.Regular;

        mapTiles[type.value][position.x()][position.y()] = new MapTile(
                contentPathAt(position, type),
                backgroundType,
                colWidth,
                colHeight,
                imageResourcesManager
        );

        MapTile tile = mapTiles[type.value][position.x()][position.y()];
        tile.setOnMouseClicked(event -> onTileClick(type, position));
        mapGrids[type.value].add(tile.getBody(), position.x(), position.y());
        GridPane.setHalignment(tile.getBody(), HPos.CENTER);
    }

    private void onTileClick(MapTypes type, Vector2d position) {
        if(state == State.Running || !(maps[type.value].objectAt(position) instanceof Animal animal))
            return;

        MapTile tile = mapTiles[type.value][position.x()][position.y()];

        tile.applySelectionOnContent();

        selectedAnimals.forEach(MapTile::removeSelectionOnContent);
        selectedAnimals.clear();
        selectedAnimals.add(tile);

        isFollowingSelectedAnimal = true;
        followingAnimal = animal;
        followingAnimalMapType = type;
        childrenCountUntilFollowing = animal.getChildrenCount();
        ancestorsCountUntilFollowing = animal.getAncestorsCount();

        genomeText.setText(String.format("Genome: %s", animal.getGenome()));
        ancestorsText.setText("Ancestors count since following: 0");
        childrenText.setText("Children count since following: 0");
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
        config.InitialAnimalCount = 20;
        config.MapHeight = 10;
        config.MapWidth = 10;
        config.StartEnergy = 180;
        config.MoveEnergy = 10;
        config.PlantEnergy = 200;
        config.JungleRatio = 0.5f;
        config.IsMagic = new boolean[]{false, false};

        jungle = new Jungle(config.MapWidth, config.MapHeight, config.JungleRatio);

        maps = new WorldMap[mapCount];
        maps[MapTypes.Bordered.value] = new BorderedWorldMap(config, jungle, this, config.IsMagic[MapTypes.Bordered.value]);
        maps[MapTypes.Wrapped.value] = new WrappedWorldMap(config, jungle, this, config.IsMagic[MapTypes.Bordered.value]);
    }

    @Override
    public void onDayChanged(HashSet<Vector2d> changedTiles, MapTypes type) {
        mapGrids[type.value].getChildren().stream()
                .filter(Node::hasProperties)
                .map(node -> new Vector2d(GridPane.getColumnIndex(node), GridPane.getRowIndex(node)))
                .filter(changedTiles::contains)
                .forEach(position -> mapTiles[type.value][position.x()][position.y()].changeContent(contentPathAt(position, type)));

        int day = maps[type.value].getDayCount() - 1;
        recordValue(SimulationDataTrackValueTypes.animalCount, day, type, maps[type.value].getAnimalCount());
        recordValue(SimulationDataTrackValueTypes.grassCount, day, type, maps[type.value].getGrassCount());
        recordValue(SimulationDataTrackValueTypes.averageChildrenCount, day, type, maps[type.value].getAverageChildrenCount());
        recordValue(SimulationDataTrackValueTypes.averageEnergyCount, day, type, maps[type.value].getAverageEnergyLevel());;

        if (maps[type.value].haveAnyDeadAnimals()) {
            recordValue(SimulationDataTrackValueTypes.averageLengthOfLife, day, type, maps[type.value].getAverageLengthOfLife());
        } else {
            historyRecorder[type.value].recordValue(SimulationDataTrackValueTypes.averageLengthOfLife, 0);
        }

        historyRecorder[type.value].toNextDay();

        mostCommonGenome[type.value].setText(maps[type.value].getMostCommonGenome().toString());

        if(isFollowingSelectedAnimal && followingAnimal != null && followingAnimalMapType.equals(type) ) {
            Object atPos = maps[type.value].objectAt(followingAnimal.getPosition());
            if(atPos != null && atPos.equals(followingAnimal))
                mapTiles[followingAnimalMapType.value][followingAnimal.position.x()][followingAnimal.position.y()].applySelectionOnContent();
            childrenText.setText(String.format("Children count since following: %s", followingAnimal.getChildrenCount() - childrenCountUntilFollowing));
            ancestorsText.setText(String.format("Ancestors count since following: %s", followingAnimal.getAncestorsCount() - ancestorsCountUntilFollowing));

            if(followingAnimal.isDead() && dayOfDeathText.getText().isEmpty())
                dayOfDeathText.setText(String.format("Day of death: %s", day));
        }
    }

    private void recordValue(SimulationDataTrackValueTypes valueType, int day,MapTypes mapType, Number value) {
        chartsPanes[mapType.value].addValue(valueType, day, value);
        historyRecorder[mapType.value].recordValue(valueType, value);
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
