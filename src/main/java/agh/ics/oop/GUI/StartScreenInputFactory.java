package agh.ics.oop.GUI;

import java.util.Optional;

public class StartScreenInputFactory {
    public static Optional<StartScreenInputField<?>> get(String text, Object value) {
        if (value instanceof Integer i) return Optional.of(new StartScreenIntegerInputField(text, i));
        if (value instanceof Double d) return Optional.of(new StartScreenInputSlider(text, d));
        if (value instanceof Boolean b) return Optional.of(new StartScreenInputBoolean(text, b));

        return Optional.empty();
    }
}
