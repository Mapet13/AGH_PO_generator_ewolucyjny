package agh.ics.oop.GUI;

import agh.ics.oop.*;
import agh.ics.oop.utilities.Vector2d;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class App extends Application implements IDayChangeObserver {
    private static final int mapCount = 2;
    private final ImageResourcesManager imageResourcesManager = new ImageResourcesManager();
    private final ChartsPane[] chartsPanes = new ChartsPane[mapCount];
    private final Label[] mostCommonGenome = new Label[mapCount];
    private State state = State.Stopped;
    private WorldMap[] maps;
    private final AppConfig config =  new AppConfig();
    private int colHeight;
    private int colWidth;
    private GridPane[] mapGrids;
    private MapTile[][][] mapTiles;
    private HBox mapBox;
    private SimulationRunner simulationRunner;
    private Jungle jungle;
    private final ArrayList<MapTile> selectedAnimals = new ArrayList<>();
    private final HistoryRecorder[] historyRecorder = new HistoryRecorder[]{new HistoryRecorder(), new HistoryRecorder()};
    private final AnimalSelectionDisplayer selectionDisplayer = new AnimalSelectionDisplayer();
    private final BorderPane layout = new BorderPane();

    @Override
    public void start(Stage primaryStage) {

        final EnumMap<AppConfig.Type, StartScreenInputField<?>> inputFields = new EnumMap<>(AppConfig.Type.class);
        Arrays.stream(AppConfig.Type.values()).forEach(type ->
                StartScreenInputFactory.get(type.name, config.get(type)).ifPresent(field ->
                    inputFields.put(type, field)
        ));

        Button applyConfig = new Button("Apply Config");

        applyConfig.setOnAction( event -> {
            Arrays.stream(AppConfig.Type.values()).forEach(type ->
                config.set(type, inputFields.get(type).getInput())
            );

            initializeSimulation();
        });

        VBox box = new VBox();
        box.getChildren().addAll(inputFields.values().stream().map(IGuiElement::getBody).toList());
        box.getChildren().add(applyConfig);
        layout.setCenter(box);

        Scene scene = new Scene(layout, 1920, 1080);

        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeSimulation() {
        layout.getChildren().clear();

        jungle = new Jungle((int)config.get(AppConfig.Type.MapWidth), (int)config.get(AppConfig.Type.MapHeight), (double)config.get(AppConfig.Type.JungleRatio));

        maps = new WorldMap[mapCount];
        maps[MapTypes.Wrapped.value] = new WrappedWorldMap(config, jungle, this, (boolean)config.get(AppConfig.Type.IsMagicLeft));
        maps[MapTypes.Bordered.value] = new BorderedWorldMap(config, jungle, this, (boolean)config.get(AppConfig.Type.IsMagicRight));

        String saveStatisticsToFileText = "Save statistics to file";
        String choseAllAnimalsWithMostCommonGenotypeText = "Chose All Animals With Most Common Genotype";
        Button[] saveStatisticsToFile = new Button[]{new Button(saveStatisticsToFileText), new Button(saveStatisticsToFileText)};
        Button[] choseAllAnimalsWithMostCommonGenotype = new Button[]{new Button(choseAllAnimalsWithMostCommonGenotypeText), new Button(choseAllAnimalsWithMostCommonGenotypeText)};

        Arrays.stream(MapTypes.values()).forEach(type -> saveStatisticsToFile[type.value].setOnAction(event ->
                historyRecorder[type.value].saveToFile("statistics.csv")
        ));

        Arrays.stream(MapTypes.values()).forEach(type -> choseAllAnimalsWithMostCommonGenotype[type.value].setOnAction(event -> {
            selectionDisplayer.clear();
            selectedAnimals.forEach(MapTile::removeSelectionOnContent);
            selectedAnimals.clear();
            var animals = maps[type.value].getAnimalsWithGenome(maps[type.value].getMostCommonGenome());
            animals.forEach(animal -> {
                var pos = maps[type.value].getProperPosition(animal.getPosition());
                if(maps[type.value].objectAt(pos).equals(animal)) {
                    selectedAnimals.add(mapTiles[type.value][pos.x()][pos.y()]);
                }
            });

            selectedAnimals.forEach(MapTile::applySelectionOnContent);
        }));

        layout.setBottom(selectionDisplayer.getBody());

        simulationRunner = new SimulationRunner(maps, 300);

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
        mapTiles = new MapTile[mapCount][(int)config.get(AppConfig.Type.MapWidth)][(int)config.get(AppConfig.Type.MapHeight)];

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
                    simulationRunner.resume();
                    new Thread(simulationRunner).start();
                    if(!selectionDisplayer.isFollowing()) {
                        selectedAnimals.forEach(MapTile::removeSelectionOnContent);
                        selectedAnimals.clear();
                    }
                    rightLayout.getChildren().remove(saveStatisticsToFile[MapTypes.Bordered.value]);
                    leftLayout.getChildren().remove(saveStatisticsToFile[MapTypes.Wrapped.value]);
                    rightLayout.getChildren().remove(choseAllAnimalsWithMostCommonGenotype[MapTypes.Bordered.value]);
                    leftLayout.getChildren().remove(choseAllAnimalsWithMostCommonGenotype[MapTypes.Wrapped.value]);
                }
                case Running -> {
                    startButton.setText("Resume");
                    simulationRunner.pause();
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
        int mapHeight = (int)config.get(AppConfig.Type.MapHeight);
        int mapWidth = (int)config.get(AppConfig.Type.MapWidth);
        int height = mapMaxSize * mapHeight / (Math.max(mapHeight, mapWidth));
        int width = mapMaxSize * mapWidth / (Math.max(mapHeight, mapWidth));

        colHeight = height / mapHeight;
        for (int i = 0; i < mapHeight; i++) {
            mapGrids[type.value].getRowConstraints().add(new RowConstraints(colHeight));
        }

        colWidth = width / mapWidth;
        for (int i = 0; i < mapWidth; i++) {
            mapGrids[type.value].getColumnConstraints().add(new ColumnConstraints(colWidth));
        }

        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
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

        selectionDisplayer.set(animal, type);
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
        List<String> input = getParameters().getRaw();
        input.forEach(arg -> {
            try {
                String[] parts = arg.split("=");
                Arrays.stream(AppConfig.Type.values()).filter(type -> parts[0].equals("-" + type.shortName)).findAny().ifPresent(
                        type -> config.parseAndAssign(type, parts[1])
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
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
        recordValue(SimulationDataTrackValueTypes.averageEnergyCount, day, type, maps[type.value].getAverageEnergyLevel());

        if (maps[type.value].haveAnyDeadAnimals()) {
            recordValue(SimulationDataTrackValueTypes.averageLengthOfLife, day, type, maps[type.value].getAverageLengthOfLife());
        } else {
            historyRecorder[type.value].recordValue(SimulationDataTrackValueTypes.averageLengthOfLife, 0);
        }

        historyRecorder[type.value].toNextDay();

        mostCommonGenome[type.value].setText(maps[type.value].getMostCommonGenome().toString());

        selectionDisplayer.update(type, maps[type.value], mapTiles, day);
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
