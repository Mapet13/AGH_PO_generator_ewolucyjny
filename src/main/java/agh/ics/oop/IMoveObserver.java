package agh.ics.oop;

import agh.ics.oop.utilities.Vector2d;

public interface IMoveObserver {
    void onAnimalMove(Animal animal, Vector2d oldPosition);
}
