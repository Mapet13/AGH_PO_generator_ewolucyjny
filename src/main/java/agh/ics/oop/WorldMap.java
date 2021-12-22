package agh.ics.oop;

import javafx.application.Platform;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class WorldMap implements IMoveObserver {
    protected final IDProvider animalIDProvider = new IDProvider();
    protected final ArrayList<Animal> livingAnimals = new ArrayList<>();
    protected final ArrayList<Animal> deadAnimals = new ArrayList<>();
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
    private int dayCount = 0;

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

    public int getDayCount() {
        return dayCount;
    }

    public void toNextDay() {
        changedTiles.clear();

        removeDeadAnimals();
        moveAnimals();
        feedAnimals();
        breedAnimals();
        addDailyGrasses();

        dayCount += 1;

        Platform.runLater(() -> dayChangeObserver.onDayChanged(changedTiles, getMapType()));
    }

    private void breedAnimals() {
        animals.values().stream()
                .filter(animalsOnSamePos -> animalsOnSamePos.size() > 2)
                .forEach(animalsOnSamePos -> {
                    Pair<Animal, Animal> parents = new Pair<>(animalsOnSamePos.first(), animalsOnSamePos.iterator().next());
                    Animal child = new Animal(animalIDProvider.getNext(), parents.first(), parents.second(), this, this);
                    animalsOnSamePos.add(child);
                    livingAnimals.add(child);
                    parents.first().addChild(child);
                    parents.second().addChild(child);
                });
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
            var animalsToRemove = animals.get(pos).stream().filter(Animal::isDead).toList();
            for (var animal : animalsToRemove) {
                animals.get(pos).remove(animal);
                livingAnimals.remove(animal);
                deadAnimals.add(animal);
            }
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
        Animal animal = new Animal(animalIDProvider.getNext(), animalPos, startingConfig.StartEnergy, this, this);
        animals.get(animalPos).add(animal);
        livingAnimals.add(animal);
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

    public int getAnimalCount() {
        return livingAnimals.size();
    }

    public int getGrassCount() {
        return grasses.size();
    }

    public float getAverageEnergyLevel() {
        return getAverageValueOfAnimals(livingAnimals, Animal::getEnergy);
    }

    public float getAverageLengthOfLife() {
        return getAverageValueOfAnimals(deadAnimals, Animal::getAge);
    }

    public boolean haveAnyDeadAnimals() {
        return !deadAnimals.isEmpty();
    }

    public float getAverageChildrenCount() {
        return getAverageValueOfAnimals(livingAnimals, Animal::getChildrenCount);
    }

    private float getAverageValueOfAnimals(ArrayList<Animal> list, ToIntFunction<Animal> mapper) {
        return list.stream().mapToInt(mapper).sum() / (float)list.size();
    }

    public Genome getMostCommonGenome() {
        Map<Genome, Integer> map = new HashMap<>();
        livingAnimals.stream().map(Animal::getGenome).forEach(t -> map.compute(t, (k, i) -> i == null ? 1 : i + 1));
        return Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
