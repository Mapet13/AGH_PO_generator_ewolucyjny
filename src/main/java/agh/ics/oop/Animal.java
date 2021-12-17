package agh.ics.oop;

import java.util.ArrayList;

public class Animal extends MapEntity {

    private final static float parentalEnergyFactor = 0.25f;
    private final String id;
    private final ArrayList<IMoveObserver> moveObservers = new ArrayList<>();
    private final Genome genome;
    private final WorldMap map;
    private int age = 0;
    private int energy = 0; // VO?
    private Direction direction = Direction.N;

    public Animal(String id, Animal firstParent, Animal secondParent, IMoveObserver moveObserver, WorldMap map) {
        addObserver(moveObserver);
        this.id = id;
        this.map = map;
        Pair<Animal, Animal> parents = Pair.ShuffledPair(firstParent, secondParent);

        genome =
                Genome.From(
                        parents.first().genome,
                        parents.second().genome,
                        (float) parents.first().energy / (float) parents.second().energy);

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

    public void addObserver(IMoveObserver observer) {
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

    @Override
    public String getImageRepresentationPath() {
        return "src/main/resources/animal.png";
    }

    public String getID() {
        return id;
    }

    private void appropriateParentalEnergy(Pair<Animal, Animal> parents) {
        for (Animal parent : new Animal[]{parents.first(), parents.second()}) {
            int parentalEnergy = Math.round(parent.energy * parentalEnergyFactor);
            parent.energy -= parentalEnergy;
            energy += parentalEnergy;
        }
    }

    private Direction getNextDirection() {
        return Direction.FromValue(genome.pickRandom());
    }
}
