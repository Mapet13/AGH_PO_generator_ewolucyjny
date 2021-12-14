package agh.ics.oop;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class WorldMap {
    private final AppConfig startingConfig;
    private final int minCoordinate = 0;
    private final Map<Vector2d, ArrayList<Animal>> animals = new LinkedHashMap<>();
    private final Map<Vector2d, Grass> grasses = new LinkedHashMap<>();

    public WorldMap(AppConfig startingConfig) {
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
        ArrayList<Animal> animal = specificObjectAt(animals, position);

        return animal != null && !animal.isEmpty()
                ? animal.get(0)
                : specificObjectAt(grasses, position);
    }

    private <T> T specificObjectAt(Map<Vector2d, T> objects, Vector2d position) {
        return objects.get(position);
    }

    private void placeStartingObjects(int count, Runnable creationMethod) {
        IntStream.range(0, count).forEach(i -> creationMethod.run());
    }

    public void toNextDay() {
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

        animals.put(position, new ArrayList<>());
        animals.get(position).add(new Animal(position, startingConfig.StartEnergy));
    }
}
