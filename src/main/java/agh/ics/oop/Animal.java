package agh.ics.oop;

import java.util.ArrayList;
import java.util.Random;

public class Animal extends MapEntity {

  private final String id;
  private final ArrayList<IMoveObserver> moveObservers = new ArrayList<>();
  private final Genome genome;
  private int age = 0;
  private int energy = 0; // VO?
  private Direction direction = Direction.N;
  WorldMap map;


  public Animal(String id, Animal firstParent, Animal secondParent, IMoveObserver moveObserver, WorldMap map) {
    addObserver(moveObserver);
    this.id = id;
    this.map = map;
    Animal[] parents = getOrderedParents(firstParent, secondParent);

    genome =
        Genome.From(
            parents[0].genome,
            parents[1].genome,
            (float) parents[0].energy / (float) parents[1].energy);

    position = firstParent.position;

    appropriateParentalEnergy(parents);
  }

  public Animal(String id, Vector2d position, int energy, IMoveObserver moveObserver, WorldMap map) {
    this.id = id;
    this.position = position;
    this.map = map;
    this.energy = energy;
    genome = Genome.Randomize();

    addObserver(moveObserver);
  }

  private void addObserver(IMoveObserver observer) {
    moveObservers.add(observer);
  }

  public int getEnergy() {
    return energy;
  }

  public void eat(int e) {
    energy += e;
  }

  public void makeOlder() {
    age += 1;
  }

  public void move(int energyCost) {
    makeOlder();

    energy -= energyCost;

    Direction moveDirection = getNextDirection();

    direction = direction.rotateTowards(moveDirection);

    if (moveDirection == Direction.N || moveDirection == Direction.S) {
      Vector2d oldPos = position;
      Vector2d newPos = position.add(direction.toUnitVector());
      if (map.canMoveTo(newPos)) {
        position = newPos;
        moveObservers.forEach(observer -> observer.onAnimalMove(this, oldPos));
      }
    }

  }

  public boolean isDead() {
    return energy < 0;
  }

  private Animal[] getOrderedParents(Animal first, Animal second) {
    return (new Random().nextInt(2)) == 1
        ? new Animal[] {first, second}
        : new Animal[] {second, first};
  }

  private void appropriateParentalEnergy(Animal[] parents) {
    for (Animal parent : parents) {
      int parentalEnergy = Math.round(parent.energy / 4.0f);
      parent.energy -= parentalEnergy;
      energy += parentalEnergy;
    }
  }

  private Direction getNextDirection() {
    return Direction.FromValue(genome.pickRandom());
  }

  @Override
  public String getImageRepresentationPath() {
    return "src/main/resources/animal.png";
  }

  public String getID() {
    return id;
  }
}
