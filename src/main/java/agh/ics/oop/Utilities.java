package agh.ics.oop;

import java.util.Random;

public class Utilities {
    static int randFromRange(int min, int max) {
        return new Random().nextInt(max - min) + min;
    };
}
