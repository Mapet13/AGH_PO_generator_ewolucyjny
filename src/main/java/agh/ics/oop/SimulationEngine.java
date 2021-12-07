package agh.ics.oop;

import java.util.ArrayList;
import java.util.Map;

public class SimulationEngine {
    public SimulationEngine(AppConfig config) {
        map = new WorldMap(config);
        animals = new ArrayList<>();
    }

    final WorldMap map;
    final ArrayList<Animal> animals;
}
