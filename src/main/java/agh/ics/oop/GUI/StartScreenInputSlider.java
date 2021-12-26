package agh.ics.oop.GUI;

import javafx.scene.control.Slider;

public class StartScreenInputSlider extends StartScreenInputField<Double> {
    private final Slider slider = new Slider();

    public StartScreenInputSlider(String text, double initialValue) {
        super(text);
        slider.setMin(0);
        slider.setMax(1);
        slider.setValue(initialValue);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(0.2);
        body.getChildren().add(slider);
    }

    @Override
    public Double getInput() {
        return slider.getValue();
    }
}
