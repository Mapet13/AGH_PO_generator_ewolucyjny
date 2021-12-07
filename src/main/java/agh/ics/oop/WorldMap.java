package agh.ics.oop;

import java.util.ArrayList;
import java.util.Map;

public class WorldMap {
    public WorldMap(AppConfig startingConfig) {
        this.startingConfig = startingConfig;
    }

    public void toNextDay() {
        

    }


    private final AppConfig startingConfig;
    private Map<Vector2d, ArrayList<Animal>> animals;
    private Map<Vector2d, Grass> grasses;
}
