package agh.ics.oop;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ChartsPane {
    private final Map<ChartTypes, Chart> charts = new EnumMap<>(ChartTypes.class);
    private final HBox body = new HBox();


    public ChartsPane() {
        List<ChartTypes> types = Arrays.stream(ChartTypes.values()).toList();

        types.forEach(type -> charts.put(type, new Chart(type.color, type.name)));

        buildChartsColumns(types);
    }

    private void buildChartsColumns(List<ChartTypes> types) {
        VBox left = new VBox();
        VBox right = new VBox();

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
