package agh.ics.oop;

public class SymulationRunner implements Runnable {
    private final long sleepTime;
    private final WorldMap[] maps;
    private boolean paused = false;

    public SymulationRunner(WorldMap[] maps, long sleepTime) {
        this.maps = maps;
        this.sleepTime = sleepTime;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    @Override
    public void run() {
        while (!paused) {
            for (WorldMap map : maps) {
                map.toNextDay();
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
