package agh.ics.oop;

import java.util.ArrayList;

public class SimulationEngine {
  final WorldMap map;
  final ArrayList<Animal> animals;
  public SimulationEngine(AppConfig config) {
    map = new WorldMap(config);
    animals = new ArrayList<>();
  }
}
