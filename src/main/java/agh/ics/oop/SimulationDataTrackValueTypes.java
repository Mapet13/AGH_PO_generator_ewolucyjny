package agh.ics.oop;

import javafx.scene.paint.Color;

public enum SimulationDataTrackValueTypes {
    animalCount(Color.BROWN, "Living animal count"),
    grassCount(Color.GREEN, "Grass count"),
    averageEnergyCount(Color.RED, "Average energy value"),
    averageLengthOfLife(Color.BLACK, "Average length of life"),
    averageChildrenCount(Color.BLUE, "Average children count");

    public final Color color;
    public final String name;

    SimulationDataTrackValueTypes(Color color, String name) {
        this.color = color;
        this.name = name;
    }
}