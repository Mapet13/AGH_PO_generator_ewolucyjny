package agh.ics.oop;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public record Genome(int[] genome) {
    public static final int SIZE = 32;
    public static final int MAX = 7;

    public static Genome Randomize() {
        final Random rand = new Random();
        return new Genome(IntStream.generate(() -> rand.nextInt(MAX)).limit(SIZE).toArray());
    }

    public static Genome From(Genome first, Genome second, float ratio) {
        int[] result = new int[SIZE];

        int splitValue = Math.round(ratio * SIZE);
        System.arraycopy(first.genome, 0, result, 0, splitValue);
        System.arraycopy(second.genome, splitValue, result, splitValue, SIZE - splitValue);

        return new Genome(result);
    }

    public int pickRandom() {
        return genome[new Random().nextInt(SIZE)];
    }

    @Override
    public String toString() {
        return Arrays.toString(genome);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Genome that))
            return false;
        return Arrays.equals(genome, that.genome);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genome);
    }
}
