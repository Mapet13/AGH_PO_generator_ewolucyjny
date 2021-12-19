package agh.ics.oop;

import javafx.application.Platform;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class WorldMap implements IMoveObserver {
    protected final IDProvider animalIDProvider = new IDProvider();
    protected final Map<Vector2d, TreeSet<Animal>> animals = new LinkedHashMap<>();
    protected final ArrayList<Pair<Animal, Vector2d>> animalsToMove = new ArrayList<>();
    protected final Map<Vector2d, Grass> grasses = new LinkedHashMap<>();
    protected final HashSet<Vector2d> emptyPositions = new HashSet<>();
    protected final HashSet<Vector2d> changedTiles = new HashSet<>();
    protected final IDayChangeObserver dayChangeObserver;
    protected final AppConfig startingConfig;
    protected final int minCoordinate = 0;
    protected final int grassDailyIncrease = 2;
    protected final Jungle jungle;

    public WorldMap(AppConfig startingConfig, Jungle jungle, IDayChangeObserver dayChangeObserver) {
        this.dayChangeObserver = dayChangeObserver;
        this.startingConfig = startingConfig;
        this.jungle = jungle;

        for (int i = 0; i < startingConfig.MapHeight; i++) {
            for (int j = 0; j < startingConfig.MapWidth; j++) {
                emptyPositions.add(new Vector2d(i, j));
            }
        }

        placeStartingObjects(startingConfig.InitialAnimalCount, this::placeStartingAnimal);
        placeStartingObjects(startingConfig.InitialGrassCount, () -> placeGrass(emptyPositions));
    }

    public boolean isOccupied(Vector2d position) {
        return isOccupiedBy(animals, position) || isOccupiedBy(grasses, position);
    }

    private boolean isOccupiedBy(Map<Vector2d, ?> objects, Vector2d position) {
        return specificObjectAt(objects, position) != null;
    }

    public Object objectAt(Vector2d position) {
        TreeSet<Animal> animal = specificObjectAt(animals, position);

        return animal != null && !animal.isEmpty()
                ? animal.first()
                : specificObjectAt(grasses, position);
    }

    private <T> T specificObjectAt(Map<Vector2d, T> objects, Vector2d position) {
        return objects.get(getProperPosition(position));
    }

    private void placeStartingObjects(int count, Runnable creationMethod) {
        IntStream.range(0, count).forEach(i -> creationMethod.run());
    }

    public void toNextDay() {
        changedTiles.clear();

        removeDeadAnimals();
        moveAnimals();
        feedAnimals();
        breedAnimals();
        addDailyGrasses();

        Platform.runLater(() -> dayChangeObserver.onDayChanged(changedTiles, getMapType()));
    }

    private void breedAnimals() {
        animals.values().stream()
                .filter(animalsOnSamePos -> animalsOnSamePos.size() > 2)
                .forEach(animalsOnSamePos -> animalsOnSamePos.add(new Animal(animalIDProvider.getNext(), animalsOnSamePos.first(), animalsOnSamePos.iterator().next(), this, this)));
    }

    private void addDailyGrasses() {
        Map<Boolean, List<Vector2d>> positionsByTerrainType = emptyPositions.stream()
                .collect(Collectors.partitioningBy(jungle::isAt));

        IntStream.range(0, grassDailyIncrease).forEach(i -> placeGrass(positionsByTerrainType.get(true)));
        IntStream.range(0, grassDailyIncrease).forEach(i -> placeGrass(positionsByTerrainType.get(false)));
    }

    private void feedAnimals() {
        grasses.entrySet().removeIf(grass -> {
            var animalAtPos = animals.get(grass.getKey());
            if (animalAtPos != null && animalAtPos.size() > 0) {
                var animalWithBiggestEnergyValue = animalAtPos.stream().filter(animal -> animal.getEnergy() == animalAtPos.first().getEnergy()).toList();
                animalWithBiggestEnergyValue.forEach(animal -> animal.eat(startingConfig.PlantEnergy / animalWithBiggestEnergyValue.size()));
                return true;
            }
            return false;
        });
    }

    private void moveAnimals() {
        animals.values().forEach(animalsAtSamePosition -> animalsAtSamePosition.forEach(animal -> animal.move(startingConfig.MoveEnergy)));
        animalsToMove.forEach(pair -> moveAnimalOnMap(pair.first(), pair.second()));
        animalsToMove.clear();
    }

    private void removeDeadAnimals() {
        animals.keySet().forEach(pos -> {
            animals.get(pos).removeIf(Animal::isDead);
            if (animals.get(pos).isEmpty()) {
                emptyPositions.add(pos);
                changedTiles.add(pos);
            }
        });
    }

    private Vector2d getRandomPosition() {
        return Vector2d.MapFrom(arg -> Utilities.randFromRange(minCoordinate, arg),
                startingConfig.MapWidth,
                startingConfig.MapHeight);
    }

    private void placeGrass(Collection<Vector2d> availablePositions) {
        getUniquePosition(availablePositions).ifPresent(pos -> {
            grasses.put(pos, new Grass());
            emptyPositions.remove(pos);
            changedTiles.add(pos);
        });
    }

    private Optional<Vector2d> getUniquePosition(Collection<Vector2d> availablePositions) {
        if (availablePositions.isEmpty())
            return Optional.empty();

        return Optional.of((Vector2d) availablePositions.toArray()[new Random().nextInt(availablePositions.size())]);
    }

    private void placeStartingAnimal() {
        final Optional<Vector2d> position = getUniquePosition(emptyPositions.stream().toList());
        position.ifPresent(this::addAnimalAtSpecificPosHolder);
        Vector2d animalPos = position.orElse(getRandomPosition());
        animals.get(animalPos).add(new Animal(animalIDProvider.getNext(), animalPos, startingConfig.StartEnergy, this, this));
    }

    private void addAnimalAtSpecificPosHolder(Vector2d position) {
        emptyPositions.remove(position);
        animals.put(position, new TreeSet<>(Comparator.comparingInt(Animal::getEnergy).thenComparing(Animal::getID)));
    }

    @Override
    public void onAnimalMove(Animal animal, Vector2d oldPosition) {
        animalsToMove.add(new Pair<>(animal, oldPosition));
    }

    private void moveAnimalOnMap(Animal animal, Vector2d oldPosition) {
        oldPosition = getProperPosition(oldPosition);

        changedTiles.add(oldPosition);
        changedTiles.add(getProperPosition(animal.getPosition()));

        animals.get(oldPosition).remove(animal);

        Vector2d currentPosition = getProperPosition(animal.getPosition());
        if (!animals.containsKey(currentPosition))
            addAnimalAtSpecificPosHolder(currentPosition);

        animals.get(currentPosition).add(animal);

        if (animals.get(oldPosition).isEmpty()) {
            emptyPositions.add(oldPosition);
        }
    }

    abstract protected Vector2d getProperPosition(Vector2d position);
    abstract protected MapTypes getMapType();
    abstract public boolean canMoveTo(Vector2d pos);
}
