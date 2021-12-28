package agh.ics.oop.GUI;

import agh.ics.oop.utilities.UtilityFunctions;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class Chart implements IGuiElement {
    private final LineChart<Number, Number> body = new LineChart<>(new NumberAxis(), new NumberAxis());
    private final XYChart.Series<Number, Number> series = new XYChart.Series<>();

    public Chart(Color color, String name) {
        body.getData().add(series);
        body.setTitle(name);
        body.setCreateSymbols(false);
        body.setLegendVisible(false);
        series.getNode().lookup(".chart-series-line").setStyle(String.format("-fx-stroke: %s;", getColorStyle(color)));
    }

    @Override
    public Node getBody() {
        return body;
    }

    public void add(Number x, Number y) {
        series.getData().add(new XYChart.Data<>(x, y));
    }

    private String getColorStyle(Color color) {
        return String.format("rgba(%s, 1.0)", UtilityFunctions.getStringRGB(color));
    }
}
