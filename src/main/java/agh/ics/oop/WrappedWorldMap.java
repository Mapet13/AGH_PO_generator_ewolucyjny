package agh.ics.oop;

public class WrappedWorldMap extends WorldMap {
    public WrappedWorldMap(AppConfig startingConfig, IDayChangeObserver dayChangeObserver) {
        super(startingConfig, dayChangeObserver);
    }

    @Override
    protected Vector2d getProperPosition(Vector2d position) {
        return position.moduloWith(startingConfig.MapWidth, startingConfig.MapHeight);
    }

    @Override
    public boolean canMoveTo(Vector2d pos) {
        return true;
    }
}
