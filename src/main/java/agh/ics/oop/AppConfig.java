package agh.ics.oop;

import java.util.EnumMap;

public class AppConfig {

    private final EnumMap<Type, Object> values = new EnumMap<>(Type.class);

    public AppConfig() {
        values.put(Type.MapHeight, 10);
        values.put(Type.MapWidth, 10);
        values.put(Type.StartEnergy, 180);
        values.put(Type.MoveEnergy, 10);
        values.put(Type.InitialAnimalCount, 20);
        values.put(Type.PlantEnergy, 200);
        values.put(Type.InitialGrassCount, 40);
        values.put(Type.JungleRatio, 0.4);
        values.put(Type.IsMagicLeft, false);
        values.put(Type.IsMagicRight, false);
    }

    public Object get(Type type) {
        return values.get(type);
    }

    public void set(Type type, Object value) {
        values.put(type, value);
    }

    public void parseAndAssign(Type type, String text) {
        Object value = get(type);

        if (value instanceof Integer i) set(type, Integer.parseInt(text));
        if (value instanceof Double d) set(type, Double.parseDouble(text));
        if (value instanceof Boolean b) set(type, Boolean.parseBoolean(text));
    }

    public enum Type {
        MapHeight("Map Height", "h"),
        MapWidth("Map Width", "w"),
        StartEnergy("Start Energy", "se"),
        MoveEnergy("Move Energy", "me"),
        PlantEnergy("Plant Energy", "pe"),
        InitialAnimalCount("Initial Animal Count", "ac"),
        InitialGrassCount("Initial Grass Count", "gc"),
        JungleRatio("Jungle Ratio", "j"),
        IsMagicLeft("Is Left Map Magical", "lm"),
        IsMagicRight("Is Right Map Magical", "rm");

        public String name;
        public String shortName;

        Type(String name, String shortName) {
            this.name = name;
            this.shortName = shortName;
        }
    }
}
