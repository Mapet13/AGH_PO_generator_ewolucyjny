package agh.ics.oop.utilities;

import java.util.Objects;
import java.util.function.IntBinaryOperator;
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

    public Vector2d moduloWith(int dx, int dy) {
        IntBinaryOperator wrappingModulo = (num, mod) -> num - (int)(mod * Math.floor((double)num / mod));
        return new Vector2d(wrappingModulo.applyAsInt(x, dx), wrappingModulo.applyAsInt(y, dy));
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