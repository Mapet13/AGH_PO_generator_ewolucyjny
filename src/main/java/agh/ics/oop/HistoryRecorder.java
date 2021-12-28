package agh.ics.oop;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HistoryRecorder {
    final List<Map<SimulationDataTrackValueTypes, Number>> history = new ArrayList<>();

    public HistoryRecorder() {
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
                final int historyRecordID = i;
                write(writer, type -> {
                    Number value = history.get(historyRecordID).get(type);
                    if (value != null) {
                        sums.put(type, value.doubleValue() + sums.getOrDefault(type, 0.0).doubleValue());
                        return value.toString();
                    }
                    return null;
                });
                writer.newLine();
            }

            write(writer, type -> {
                Number value = sums.get(type);
                if (value != null)
                    return String.valueOf(value.doubleValue() / (double) sums.size());
                return null;
            });
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(BufferedWriter writer, Function<SimulationDataTrackValueTypes, String> supplier) throws IOException {
        boolean isFirst = true;
        for (var type : SimulationDataTrackValueTypes.values()) {
            if (!isFirst) {
                writer.append(",");
            }
            isFirst = false;
            writer.append(supplier.apply(type));
        }
    }
}
