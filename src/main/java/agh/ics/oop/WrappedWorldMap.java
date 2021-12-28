package agh.ics.oop;

import agh.ics.oop.utilities.Vector2d;

public class WrappedWorldMap extends WorldMap {
    public WrappedWorldMap(AppConfig startingConfig, Jungle jungle, IDayChangeObserver dayChangeObserver, boolean isMagic) {
        super(startingConfig, jungle, dayChangeObserver, isMagic);
    }

    @Override
    public Vector2d getProperPosition(Vector2d position) {
        return position.moduloWith((int)startingConfig.get(AppConfig.Type.MapWidth),  (int)startingConfig.get(AppConfig.Type.MapHeight));
    }

    @Override
    protected MapTypes getMapType() {
        return MapTypes.Wrapped;
    }

    @Override
    public boolean canMoveTo(Vector2d pos) {
        return true;
    }
}
