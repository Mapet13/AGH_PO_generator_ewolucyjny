package agh.ics.oop;

public enum MapTypes {
    Wrapped(0), Bordered(1);

    public final int value;

    MapTypes(int value) {
        this.value = value;
    }
}