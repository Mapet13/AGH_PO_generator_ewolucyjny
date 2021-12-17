package agh.ics.oop;

public class BorderedWorldMap extends WorldMap {
    public BorderedWorldMap(AppConfig startingConfig, IDayChangeObserver dayChangeObserver) {
        super(startingConfig, dayChangeObserver);
    }

    @Override
    protected Vector2d getProperPosition(Vector2d position) {
        return position;
    }

    @Override
    protected MapTypes getMapType() {
        return MapTypes.Bordered;
    }

    @Override
    public boolean canMoveTo(Vector2d pos) {
        return pos.follows(new Vector2d(0, 0))
                && pos.precedes(new Vector2d(startingConfig.MapWidth - 1, startingConfig.MapHeight - 1));
    }
}
