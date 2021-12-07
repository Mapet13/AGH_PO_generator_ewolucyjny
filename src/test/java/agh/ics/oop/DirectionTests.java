package agh.ics.oop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectionTests {
  @Test
  void testToUnitVector() {
    class TestCase {
      public static void test(Direction dir, int x, int y) {
        assertEquals(new Vector2d(x, y), dir.toUnitVector());
      }
    }

    TestCase.test(Direction.N, 0, 1);
    TestCase.test(Direction.W, 1, 0);
    TestCase.test(Direction.S, 0, -1);
    TestCase.test(Direction.E, -1, 0);
    TestCase.test(Direction.NW, 1, 1);
    TestCase.test(Direction.SW, 1, -1);
    TestCase.test(Direction.SE, -1, -1);
    TestCase.test(Direction.NE, -1, 1);
  }

  @Test
  void testRotate() {
    class TestCase {
      public static void test(Direction initial, Direction rotation, Direction result) {
        assertEquals(result, initial.rotateTowards(rotation));
      }
    }

    TestCase.test(Direction.N, Direction.N, Direction.N);
    TestCase.test(Direction.SW, Direction.N, Direction.SW);
    TestCase.test(Direction.SW, Direction.S, Direction.NE);
    TestCase.test(Direction.N, Direction.NW, Direction.NW);
    TestCase.test(Direction.E, Direction.W, Direction.N);
    TestCase.test(Direction.W, Direction.E, Direction.N);
  }
}
