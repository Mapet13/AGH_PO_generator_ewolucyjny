package agh.ics.oop;

public enum BackgroundType {
    Regular,
    Jungle;

    public String getImageRepresentationPath() {
        return switch (this) {
            case Regular -> "src/main/resources/background.jpg";
            case Jungle -> "src/main/resources/jungle.jpg";
        };
    }
}
