package agh.ics.oop;

import java.util.UUID;

public class IDProvider {
    public String getNext() {
        return  UUID.randomUUID().toString();
    }
}
