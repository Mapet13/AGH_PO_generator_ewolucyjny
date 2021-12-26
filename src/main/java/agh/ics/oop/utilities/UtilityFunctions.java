package agh.ics.oop.utilities;

import javafx.scene.paint.Color;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

public class UtilityFunctions {
    public static int randFromRange(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    public static <T> float getAverageOfList(List<T> list, ToIntFunction<T> mapper) {
        return list.stream().mapToInt(mapper).sum() / (float)list.size();
    }

    public static String getStringRGB(Color color) {
        return String.format("%d, %d, %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    
}
