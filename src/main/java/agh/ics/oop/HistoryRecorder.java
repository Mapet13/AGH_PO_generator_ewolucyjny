package agh.ics.oop;

import java.util.*;

public class HistoryRecorder {
    final List<Map<SimulationDataTrackValueTypes, Number>> history = new ArrayList<>();

    HistoryRecorder() {
        addNextRecord();
    }

    public void toNextDay() {
        final StringBuilder writer = new StringBuilder();
        var lastRecord = getLastRecord();
        Arrays.stream(SimulationDataTrackValueTypes.values()).forEach(type -> {
            Object value = lastRecord.get(type);
            if (value != null)
                writer.append(value);
            writer.append(",");
        });
        writer.deleteCharAt(writer.length() - 1);
        System.out.println(writer);

        addNextRecord();
    }

    public void recordValue(SimulationDataTrackValueTypes type, Number data) {
        getLastRecord().put(type, data);
    }

    private Map<SimulationDataTrackValueTypes, Number> getLastRecord() {
        return history.get(history.size() - 1);
    }

    private void addNextRecord() {
        history.add(new EnumMap<>(SimulationDataTrackValueTypes.class));
    }
}
