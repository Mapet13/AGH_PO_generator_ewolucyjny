package agh.ics.oop.utilities;

import java.util.UUID;

public class IDProvider {
    public String getNext() {
        return UUID.randomUUID().toString();
    }
}
