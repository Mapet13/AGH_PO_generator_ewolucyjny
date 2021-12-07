package agh.ics.oop;

import java.util.Arrays;

public enum Direction {
    N(0), NW(1), W(2), SW(3), S(4), SE(5), E(6), NE(7);

    private final static int SIZE = 8;
    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public Vector2d toUnitVector() {
        return switch(this) {
            case N -> new Vector2d(0, 1);
            case NW -> new Vector2d(1, 1);
            case W -> new Vector2d(1, 0);
            case SW -> new Vector2d(1, -1);
            case S -> new Vector2d(0, -1);
            case SE -> new Vector2d(-1, -1);
            case E -> new Vector2d(-1, 0);
            case NE -> new Vector2d(-1, 1);
        };
    }

    public Direction rotateTowards(Direction moveDirection) {
        return fromValue((value + moveDirection.value) % SIZE);
    }

    private Direction fromValue(int value) {
        return Arrays.stream(values())
                .filter(x -> x.value == value)
                .findFirst()
                .orElse(null);
    }
}
