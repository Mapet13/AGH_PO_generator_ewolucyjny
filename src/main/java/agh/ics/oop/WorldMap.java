package agh.ics.oop;

import javafx.application.Platform;

import java.util.*;
import java.util.stream.IntStream;

public class WorldMap implements IMoveObserver {
    private final IDProvider animalIDProvider = new IDProvider();
    private final AppConfig startingConfig;
    private final int minCoordinate = 0;
    private final Map<Vector2d, TreeSet<Animal>> animals = new LinkedHashMap<>();
    private final ArrayList<Pair<Animal ,Vector2d>> animalsToMove = new ArrayList<>();
    private final Map<Vector2d, Grass> grasses = new LinkedHashMap<>();
    private final IDayChangeObserver dayChangeObserver;

    public WorldMap(AppConfig startingConfig, IDayChangeObserver dayChangeObserver) {
        this.dayChangeObserver = dayChangeObserver;
        this.startingConfig = startingConfig;

        placeStartingObjects(startingConfig.InitialAnimalCount, this::placeStartingAnimal);
        placeStartingObjects(startingConfig.InitialGrassCount, this::placeStartingGrass);
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
        return objects.get(position);
    }

    private void placeStartingObjects(int count, Runnable creationMethod) {
        IntStream.range(0, count).forEach(i -> creationMethod.run());
    }

    public void toNextDay() {
        removeDeadAnimals();
        moveAnimals();
        feedAnimals();

        Platform.runLater(dayChangeObserver::onDayChanged);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void feedAnimals() {
        grasses.entrySet().removeIf(grass -> {
            if (animals.containsKey(grass.getKey())) {
                var animalAtPos = animals.get(grass.getKey());
                var animalWithBiggestEnergyValue = animalAtPos.stream().filter(animal -> animal.getEnergy() == animalAtPos.first().getEnergy()).toList();
                animalWithBiggestEnergyValue.forEach(animal -> animal.eat(animalAtPos.first().getEnergy() / animalWithBiggestEnergyValue.size()));
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
        animals.values().forEach(animalsAtSamePosition -> animalsAtSamePosition.removeIf(Animal::isDead));
    }

    private Vector2d getRandomPosition() {
        return Vector2d.MapFrom(arg -> Utilities.randFromRange(minCoordinate, arg),
                startingConfig.MapWidth,
                startingConfig.MapHeight);
    }

    private void placeStartingGrass() {
        grasses.put(getUniqueKeyFrom(), new Grass());
    }

    private Vector2d getUniqueKeyFrom() {
        Vector2d position;

        do { position = getRandomPosition(); }
        while (isOccupied(position));

        return position;
    }

    private void placeStartingAnimal() {
        final Vector2d position = getUniqueKeyFrom();

        addAnimalAtSpecificPosHolder(position);
        animals.get(position).add(new Animal(animalIDProvider.getNext(), position, startingConfig.StartEnergy, this));
    }

    private void addAnimalAtSpecificPosHolder(Vector2d position) {
        animals.put(position, new TreeSet<>(Comparator.comparingInt(Animal::getEnergy).thenComparing(Animal::getID)));
    }

    @Override
    public void onAnimalMove(Animal animal, Vector2d oldPosition) {
        animalsToMove.add(new Pair<>(animal, oldPosition));
    }

    private void moveAnimalOnMap(Animal animal, Vector2d oldPosition) {
        animals.get(oldPosition).remove(animal);

        Vector2d currentPosition = animal.getPosition();
        if(!animals.containsKey(currentPosition))
            addAnimalAtSpecificPosHolder(currentPosition);

        animals.get(currentPosition).add(animal);
    }
}
