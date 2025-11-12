package com.seattlesolvers.solverslib.util;

import java.util.Objects;

/**
 * A generic debouncer that works across type.
 * Useful if you have multiple values, like a color sensor to determine red, green, or blue.
 * Or if you needed to detect April Tags.
 *
 * @author Daniel - FTC 7854
 */
public class GenericDebouncer<T> {
    private double debounce;

    private long previousTime;
    private T state;
    private T lastInput;

    /**
     * Creates a fully specified debouncer
     *
     * @param debounce Debounce time
     * @param initial What the initial value is
     */
    public GenericDebouncer(double debounce, T initial) {
        this.debounce = debounce;
        reset(initial);
    }

    /**
     * Applies the debouncer to the input stream.
     *
     * @param input The measured value
     * @return The debounced value
     */
    public T calculate(T input) {
        if (Objects.equals(input, lastInput)) {
            lastInput = input;
            previousTime = System.nanoTime();
        }

        // This will still work before the first input change
        if (System.nanoTime() - previousTime >= debounce * 1e6) {
            state = input;
        }

        return state;
    }

    /**
     * Resets the debouncer to a state.
     * Useful if you know for sure something has changed and don't want to wait.
     *
     * @param state The new state of the debouncer
     */
    public void reset(T state) {
        this.previousTime = 0;
        this.state = state;
        this.lastInput = state;
    }
}
