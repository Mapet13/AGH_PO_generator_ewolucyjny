package agh.ics.oop;

public class Animal extends MapEntity {
    public int getEnergy() {
        return energy;
    }

    public void eat(int e) {
        energy += e;
    }

    public void move() {
        Direction moveDirection = getNextDirection();

        direction = direction.rotateTowards(moveDirection);

        if (moveDirection == Direction.N || moveDirection == Direction.S)
            position = position.add(direction.toUnitVector());
    }

    Direction getNextDirection() {
        return Direction.N; // todo
    }

    // info o child
    private int energy; // VO?
    private int age;
    private Direction direction;
    private Genome genome;
}
