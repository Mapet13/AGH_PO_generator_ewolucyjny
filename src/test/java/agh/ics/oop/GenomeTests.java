package agh.ics.oop;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class GenomeTests {
    @Test
    void createGenomeTest() {
        final int[] expected = new int[]{
                0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
        };

        Genome first = new Genome(IntStream.generate(() -> 0).limit(Genome.SIZE).toArray());
        Genome second = new Genome(IntStream.generate(() -> 1).limit(Genome.SIZE).toArray());

        assertArrayEquals(expected, Genome.From(first, second, 0.25f).genome());
    }
}
