package agh.ics.oop;

import java.util.Random;

public record Pair<T1, T2>(T1 first, T2 second) {
    public static <T> Pair<T, T> ShuffledPair(T first, T second) {
        return (new Random().nextInt(2)) == 1
                ? new Pair<T, T>(first, second)
                : new Pair<T, T>(second, first);
    }
}
