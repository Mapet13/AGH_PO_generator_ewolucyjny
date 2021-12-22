package agh.ics.oop;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class Chart {
    private final LineChart<Number, Number> body = new LineChart<>(new NumberAxis(), new NumberAxis());
    private final XYChart.Series<Number, Number> series = new XYChart.Series<>();

    Chart(Color color, String name) {
        body.getData().add(series);
        body.setTitle(name);
        body.setCreateSymbols(false);
        body.setLegendVisible(false);
        series.getNode().lookup(".chart-series-line").setStyle(String.format("-fx-stroke: %s;", getColorStyle(color)));
    }

    public LineChart<Number, Number> getBody(){
        return body;
    }

    public void add(Number x, Number y) {
        series.getData().add(new XYChart.Data<>(x, y));
    }

    private String getColorStyle(Color color) {
        String rgb = String.format("%d, %d, %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));

        return String.format("rgba(%s, 1.0)", rgb);
    }
}
