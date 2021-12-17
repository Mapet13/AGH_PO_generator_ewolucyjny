package agh.ics.oop;

import java.util.Random;

public record Pair<T1, T2>(T1 first, T2 second) {
    public static <T1, T2> Pair ShuffledPair(T1 first, T2 second) {
        return (new Random().nextInt(2)) == 1
                ? new Pair<T1, T2>(first, second)
                : new Pair<T2, T1>(second, first);
    }
}
