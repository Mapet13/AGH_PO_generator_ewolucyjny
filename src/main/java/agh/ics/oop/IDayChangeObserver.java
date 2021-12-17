package agh.ics.oop;

import java.util.HashSet;

public interface IDayChangeObserver {
    void onDayChanged(HashSet<Vector2d> changedTiles, MapTypes type);
}
