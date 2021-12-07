package agh.ics.oop;

import java.util.Random;
import java.util.stream.IntStream;

public class Genome {
  public static final int SIZE = 32;
  public static final int MAX = 7;
  public static final int MIN = 0;

  public final int[] genome;

  public Genome(int[] genome) {
    this.genome = genome;
  }

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
}
