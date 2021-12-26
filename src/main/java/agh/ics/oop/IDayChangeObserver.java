package agh.ics.oop;

import agh.ics.oop.utilities.Vector2d;

import java.util.HashSet;

public interface IDayChangeObserver {
    void onDayChanged(HashSet<Vector2d> changedTiles, MapTypes type);
}
