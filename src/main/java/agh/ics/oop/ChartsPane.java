package agh.ics.oop;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.IntStream;

public class ChartsPane {
    public enum ChartTypes {
        animalCount(Color.BROWN, "Living animal count"),
        grassCount(Color.GREEN, "Grass count"),
        averageEnergyCount(Color.RED, "Average energy value"),
        averageLengthOfLife(Color.BLACK, "Average length of life"),
        averageChildrenCount(Color.BLUE, "Average children count");

        public final Color color;
        public final String name;

        ChartTypes(Color color, String name) {
            this.color = color;
            this.name = name;
        }
    }

    private final Map<ChartTypes, Chart> charts = new EnumMap<>(ChartTypes.class);
    private final HBox body = new HBox();
    private final VBox left = new VBox();
    private final VBox right = new VBox();

    public ChartsPane() {
        List<ChartTypes> types = Arrays.stream(ChartTypes.values()).toList();

        for (ChartTypes type : types) {
            charts.put(type, new Chart(type.color, type.name));
        }

        IntStream.range(0, types.size()).forEach(i ->
            (i % 2 == 0 ? left : right).getChildren().add(charts.get(types.get(i)).getBody())
        );

        body.getChildren().add(left);
        body.getChildren().add(right);
    }
    
    public void addValue(ChartTypes type, Number x, Number y) {
        charts.get(type).add(x, y);
    }

    public HBox getBody() {
        return body;
    }
}
