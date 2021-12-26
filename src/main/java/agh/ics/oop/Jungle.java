package agh.ics.oop;

import agh.ics.oop.utilities.Vector2d;

public class Jungle {
    public final Vector2d lowerLeft;
    public final Vector2d upperRight;

    public Jungle(int width, int height, float jungleRatio) {
        int newHalfWidth = (int) (width * jungleRatio / 2.0);
        int newHalfHeight = (int) (height * jungleRatio / 2.0);
        Vector2d center = new Vector2d(width / 2, height / 2);

        lowerLeft = new Vector2d(center.x() - newHalfWidth, center.y() - newHalfHeight);
        upperRight = new Vector2d(center.x() + newHalfWidth, center.y() + newHalfHeight);
    }

    public boolean isAt(Vector2d position) {
        return position.precedes(upperRight) && position.follows(lowerLeft);
    }
}
