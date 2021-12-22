package agh.ics.oop;

public abstract class MapEntity {
    protected Vector2d position;

    public Vector2d getPosition() {
        return position;
    }

    public abstract String getImageRepresentationPath();
    public abstract String getRepresentationLabel();
}
