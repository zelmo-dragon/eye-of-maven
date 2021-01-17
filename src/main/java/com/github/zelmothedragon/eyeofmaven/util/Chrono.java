package com.github.zelmothedragon.eyeofmaven.util;

/**
 *
 * @author MOSELLE Maxime
 */
public final class Chrono {

    private long startTime;

    public Chrono() {
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public long stop() {
        return System.currentTimeMillis() - startTime;
    }

}
