package agh.ics.oop;

import java.util.Random;

public class Animal extends MapEntity {

  private final Genome genome;
  private int age = 0;
  private int energy = 0; // VO?
  private Direction direction;

  public Animal(Vector2d position, int energy) {
    this.position = position;
    this.energy = energy;
    genome = Genome.Randomize();
  }

  public Animal(Animal firstParent, Animal secondParent) {
    Animal[] parents = getOrderedParents(firstParent, secondParent);

    genome =
        Genome.From(
            parents[0].genome,
            parents[1].genome,
            (float) parents[0].energy / (float) parents[1].energy);

    position = firstParent.position;

    appropriateParentalEnergy(parents);
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

  public void move() {
    Direction moveDirection = getNextDirection();

    direction = direction.rotateTowards(moveDirection);

    if (moveDirection == Direction.N || moveDirection == Direction.S)
      position = position.add(direction.toUnitVector());
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
}
