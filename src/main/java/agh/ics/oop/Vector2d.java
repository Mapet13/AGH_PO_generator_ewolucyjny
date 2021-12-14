package agh.ics.oop;

import java.util.Objects;
import java.util.function.ToIntFunction;

public record Vector2d(int x, int y) {
    public static <T> Vector2d MapFrom(ToIntFunction<T> func, T x, T y) {
        return new Vector2d(func.applyAsInt(x), func.applyAsInt(y));
    }

    public boolean precedes(Vector2d other) {
        return x <= other.x && y <= other.y;
    }

    public boolean follows(Vector2d other) {
        return x >= other.x && y >= other.y;
    }

    public Vector2d upperRight(Vector2d other) {
        return new Vector2d(
                Math.max(x, other.x),
                Math.max(y, other.y)
        );
    }

    public Vector2d lowerLeft(Vector2d other) {
        return new Vector2d(
                Math.min(x, other.x),
                Math.min(y, other.y)
        );
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(
                x + other.x,
                y + other.y
        );
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(
                x - other.x,
                y - other.y
        );
    }

    public Vector2d opposite() {
        return new Vector2d(y, x);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Vector2d that))
            return false;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}