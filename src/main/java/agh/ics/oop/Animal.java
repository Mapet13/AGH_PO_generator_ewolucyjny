package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class Animal extends MapEntity {

    private int age = 0;
    private int energy;
    private final String id;
    private final Genome genome;
    private final List<Animal> children = new ArrayList<>();
    private final List<IMoveObserver> moveObservers = new ArrayList<>();
    private Direction direction = Direction.N;

    public Animal(String id, Vector2d position, int energy, IMoveObserver moveObserver, Genome genome) {
        addObserver(moveObserver);

        this.id = id;
        this.position = position;
        this.energy = energy;
        this.genome = genome;
    }

    public Animal(String id, Vector2d position, int energy, IMoveObserver moveObserver) {
        this(id, position, energy, moveObserver, Genome.Randomize());
    }

    public void addObserver(IMoveObserver observer) {
        moveObservers.add(observer);
    }

    public void addChild(Animal child) {
        children.add(child);
    }

    public int getChildrenCount() {
        return children.size();
    }

    public int getAncestorsCount() {
        return children.size() + children.stream().mapToInt(Animal::getAncestorsCount).sum();
    }

    public int getEnergy() {
        return energy;
    }

    public void subtractEnergy(int energy) {
        this.energy -= energy;
    }

    public void eat(int e) {
        energy += e;
    }

    public void makeOlder() {
        age += 1;
    }

    public String getID() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public Genome getGenome() {
        return genome;
    }

    public void move(int energyCost, IMoveLimiter moveLimiter) {
        makeOlder();
        subtractEnergy(energyCost);

        Direction moveDirection = getNextDirection();
        direction = direction.rotateTowards(moveDirection);

        if (moveDirection == Direction.N || moveDirection == Direction.S) {
            moveForwardIfPossible(moveLimiter);
        }
    }

    public boolean isDead() {
        return energy < 0;
    }

    @Override
    public String getImageRepresentationPath() {
        return "src/main/resources/animal.png";
    }

    @Override
    public String getRepresentationLabel() {
        return String.valueOf(energy);
    }

    private void moveForwardIfPossible(IMoveLimiter moveLimiter) {
        Vector2d oldPos = position;
        Vector2d newPos = position.add(direction.toUnitVector());
        if (moveLimiter.canMoveTo(newPos)) {
            position = newPos;
            moveObservers.forEach(observer -> observer.onAnimalMove(this, oldPos));
        }
    }
    
    private Direction getNextDirection() {
        return Direction.FromValue(genome.pickRandom());
    }
}
