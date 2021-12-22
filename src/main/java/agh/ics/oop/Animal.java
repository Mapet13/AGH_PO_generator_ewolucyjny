package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class Animal extends MapEntity {

    private final static float parentalEnergyFactor = 0.25f;
    private int age = 0;
    private int energy = 0;
    private final String id;
    private final Genome genome;
    private final WorldMap map;
    private Direction direction = Direction.N;
    private final List<Animal> children = new ArrayList<>();
    private final List<IMoveObserver> moveObservers = new ArrayList<>();

    public Animal(String id, Animal firstParent, Animal secondParent, IMoveObserver moveObserver, WorldMap map) {
        addObserver(moveObserver);

        this.id = id;
        this.map = map;
        Pair<Animal, Animal> parents = Pair.ShuffledPair(firstParent, secondParent);

        genome = Genome.From(
                        parents.first().genome,
                        parents.second().genome,
                        (float) parents.first().energy / (float) parents.second().energy);

        position = firstParent.position;

        appropriateParentalEnergy(parents);
    }

    public Animal(String id, Vector2d position, int energy, IMoveObserver moveObserver, WorldMap map) {
        addObserver(moveObserver);

        this.id = id;
        this.position = position;
        this.map = map;
        this.energy = energy;
        genome = Genome.Randomize();
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

    public int getEnergy() {
        return energy;
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

    public void move(int energyCost) {
        makeOlder();

        energy -= energyCost;

        Direction moveDirection = getNextDirection();
        direction = direction.rotateTowards(moveDirection);

        if (moveDirection == Direction.N || moveDirection == Direction.S) {
            moveForwardIfPossible();
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

    private void appropriateParentalEnergy(Pair<Animal, Animal> parents) {
        for (Animal parent : new Animal[]{parents.first(), parents.second()}) {
            int parentalEnergy = Math.round(parent.energy * parentalEnergyFactor);
            parent.energy -= parentalEnergy;
            energy += parentalEnergy;
        }
    }

    private void moveForwardIfPossible() {
        Vector2d oldPos = position;
        Vector2d newPos = position.add(direction.toUnitVector());
        if (map.canMoveTo(newPos)) {
            position = newPos;
            moveObservers.forEach(observer -> observer.onAnimalMove(this, oldPos));
        }
    }
    
    private Direction getNextDirection() {
        return Direction.FromValue(genome.pickRandom());
    }
}
