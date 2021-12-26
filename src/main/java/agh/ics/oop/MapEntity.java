package agh.ics.oop;

import agh.ics.oop.utilities.Vector2d;

public abstract class MapEntity {
    protected Vector2d position;

    public Vector2d getPosition() {
        return position;
    }

    public abstract String getImageRepresentationPath();
    public abstract String getRepresentationLabel();
}
