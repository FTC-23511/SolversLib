package com.seattlesolvers.solverslib.util;
import java.util.LinkedList;

/**
 * A simple Moving Average Filter to smooth noisy sensor readings.
 */
public class MovingAverageFilter {
    private final LinkedList<Double> window;
    private final int windowSize;
    private double currentSum;

    /**
     * Initializes the filter with a specified window size.
     * @param windowSize The number of past samples to average.
     */
    public MovingAverageFilter(int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be positive.");
        }
        this.windowSize = windowSize;
        this.window = new LinkedList<>();
        this.currentSum = 0;
    }

    /**
     * Adds a new raw sensor reading and calculates the new smoothed average.
     * @param newReading The latest, noisy reading.
     * @return The smoothed, averaged reading.
     */
    public double filter(double newReading) {
        update(newReading);
        return get();
    }

    public void update(double newReading) {
        window.add(newReading);
        currentSum += newReading;

        if (window.size() > windowSize) {
            double oldestReading = window.removeFirst();
            currentSum -= oldestReading;
        }
    }

    public double get() {
        if (!window.isEmpty()) {
            return currentSum / window.size();
        } else {
            return Double.NaN;
        }
    }

    public void clear() {
        window.clear();
        currentSum = 0;
    }
}