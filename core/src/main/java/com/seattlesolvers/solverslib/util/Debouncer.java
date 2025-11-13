package com.seattlesolvers.solverslib.util;

/**
 * A filter to eliminate unwanted quick on/off cycles (termed “bounces”).
 * These cycles are usually due to a sensor error like noise or reflections
 * and not the actual event the sensor is trying to record.
 * More powerful version of WPILib's Debouncer.
 *
 * @author Daniel - FTC 7854
 */
public class Debouncer {
    /** Type of debouncing to perform. */
    public enum DebounceType {
        /** Rising edge. */
        Rising,
        /** Falling edge. */
        Falling,
        /** Both rising and falling edges. */
        Both
    }

    private double debounceRising;
    private double debounceFalling;

    private long previousTime;
    private boolean state;
    private boolean lastInput;

    /**
     * Creates a fully specified debouncer
     *
     * @param debounceRising Debounce time on the rising edge in seconds
     * @param debounceFalling Debounce time on the falling edge in seconds
     * @param initial If it is initially true or falling false
     */
    public Debouncer(double debounceRising, double debounceFalling, boolean initial) {
        this.debounceRising = debounceRising;
        this.debounceFalling = debounceFalling;
        reset(initial);
    }

    /**
     * Creates a WPILib style debouncer.
     * For more versatility, use {@link #Debouncer(double, double, boolean)}<br>
     * {@link DebounceType#Rising} - rising edge: {@code debounce}, falling edge: {@code 0}, initially {@code false}<br>
     * {@link DebounceType#Falling} - rising edge: {@code 0}, falling edge: {@code debounce}, initially {@code true}<br>
     * {@link DebounceType#Both} - rising edge: {@code debounce}, falling edge: {@code debounce}, initially {@code false}
     *
     * @param debounce Debounce time in seconds
     * @param type Rising, falling, or both edges
     */
    public Debouncer(double debounce, DebounceType type) {
        this(
                type == DebounceType.Falling ? 0 : debounce,
                type == DebounceType.Rising ? 0 : debounce,
                type == DebounceType.Falling
        );
    }

    /**
     * Creates a rising edge debouncer.
     * Equivalent to {@code Debouncer(debounceRising, DebounceType.Rising);}
     *
     * @param debounceRising Debounce time on the rising edge in seconds
     */
    public Debouncer(double debounceRising) {
        this(debounceRising, 0, false);
    }

    /**
     * Applies the debouncer to the input stream.
     *
     * @param input The measured value
     * @return The debounced value
     */
    public boolean calculate(boolean input) {
        if (input != lastInput) {
            lastInput = input;
            previousTime = System.nanoTime();
        }

        double debounce = input ? debounceRising : debounceFalling;

        // This will still work before the first input change
        if (System.nanoTime() - previousTime >= debounce * 1e6) {
            state = input;
        }

        return state;
    }

    /**
     * Resets the debouncer to true or false.
     * Useful if you know for sure something has changed and don't want to wait.
     *
     * @param state The new state of the debouncer
     */
    public void reset(boolean state) {
        this.previousTime = 0;
        this.state = state;
        this.lastInput = state;
    }
}
