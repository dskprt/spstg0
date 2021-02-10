package org.example.game.util;

public class Timer {

    private double lastFrame;
    private int frames;
    private int fps;
    private double lastFPS;

    public Timer() {
        getDelta();
        lastFPS = getTime();
    }

    public void tick() {
        double time = getTime();

        if (time - lastFPS > 1000) {
            fps = frames;
            frames = 0;
            lastFPS += 1000;
        }

        lastFrame = time;
        frames++;
    }

    public double getDelta() {
        return getTime() - lastFrame;
    }

    public double getTime() {
        return System.nanoTime() / 1000000d;
    }

    public int getFps() {
        return fps;
    }
}
