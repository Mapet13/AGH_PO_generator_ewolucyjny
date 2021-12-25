package agh.ics.oop;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class HistoryRecorder {
    final List<Map<SimulationDataTrackValueTypes, Number>> history = new ArrayList<>();

    HistoryRecorder() {
        addNextRecord();
    }

    public void toNextDay() {
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

    public void saveToFile(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            Map<SimulationDataTrackValueTypes, Number> sums = new EnumMap<>(SimulationDataTrackValueTypes.class);

            for (int i = 0; i < history.size() - 1; ++i) {
                boolean isFirst = true;
                for (var type : SimulationDataTrackValueTypes.values()) {
                    if (!isFirst) {
                        writer.append(",");
                    }
                    isFirst = false;
                    Number value = history.get(i).get(type);
                    if (value != null) {
                        writer.append(value.toString());
                        sums.put(type, value.doubleValue() + sums.getOrDefault(type, 0.0).doubleValue());
                    }
                }
                writer.newLine();
            }

            boolean isFirst = true;
            for (var type : SimulationDataTrackValueTypes.values()) {
                if (!isFirst) {
                    writer.append(",");
                }
                isFirst = false;
                Number value = sums.get(type);
                if (value != null) {
                    writer.append(String.valueOf(value.doubleValue() / (double)sums.size()));
                }
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
