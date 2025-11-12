package com.seattlesolvers.solverslib.util;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

/**
 * Class for a stopwatch.
 */
public class Timing {

    /**
     * Class for a stopwatch. This can be used for getting the elapsed time of a process, checking
     * the loop time, and so forth.
     * <p>
     * A more simple version of a timer better suited for quick uses rather than an
     * {@link ElapsedTime} object.
     */
    public static class Stopwatch {
        protected ElapsedTime time;
        protected long pauseTime; // in nanoseconds, regardless of unit
        protected long previousTime;
        protected final TimeUnit unit;
        protected boolean timerOn;

        /**
         * Creates a new timer object.
         *
         * @param unit        The unit of timerLength.
         */
        public Stopwatch(TimeUnit unit) {
            this.unit = unit;
            this.time = new ElapsedTime();
            start(true);
        }

        /**
         * Creates a new timer object in seconds.
         */
        public Stopwatch() {
            this(TimeUnit.SECONDS);
        }

        /**
         * Starts or restarts this timer.
         *
         * @param paused Pause the timer when resetting
         */
        public void start(boolean paused) {
            time.reset();
            pauseTime = 0;
            previousTime = 0;
            timerOn = !paused;
        }

        /**
         * Starts or restarts this timer.
         */
        public void start() {
            start(false);
        }

        /**
         * Pauses this timer. While the timer is paused, elapsedTime() and remainingTime() will not change.
         */
        public void pause() {
            if (timerOn) {
                pauseTime = time.nanoseconds();
                timerOn = false;
            }
        }

        /**
         * Resumes this timer if it is running and paused.
         */
        public void resume() {
            if (!timerOn) {
                // we start the timer with a time in the past, since we're starting in the middle of the timer
                time = new ElapsedTime(System.nanoTime() - pauseTime);
                timerOn = true;
            }
        }

        /**
         * Get the elapsed time since this time was started.
         *
         * @return The elapsed time, in the units specified in the constructor.
         * If the timer was not started, return 0.
         * If the timer is paused, return the time at which the timer was paused.
         */
        public long elapsedTime() {
            if (timerOn) return time.time(unit);
            else return unit.convert(pauseTime, TimeUnit.NANOSECONDS);
        }

        /**
         * Gets the time since {@link #start()} or {@link #deltaTime()} was last called.
         * Useful for finding loop times.
         *
         * @return The requested time gap
         */
        public long deltaTime() {
            long now = elapsedTime();
            long delta = now - previousTime;
            previousTime = now;
            return delta;
        }

        /**
         * Check if this timer is running.
         *
         * @return True if this timer has been started and is not paused, false otherwise.
         */
        public boolean isTimerOn() {
            return timerOn;
        }
    }

    /**
     * Class for a timer. This can be used for checking if a duration has passed, only continuing
     * if the timer has finished, and so forth.
     * <p>
     * A more simple version of a timer better suited for quick uses rather than an
     * {@link ElapsedTime} object.
     */
    public static class Timer extends Stopwatch {
        protected final long timerLength;

        /**
         * Creates a new timer object.
         *
         * @param timerLength The length of the timer, in the units specified by unit.
         * @param unit        The unit of timerLength.
         */
        public Timer(long timerLength, TimeUnit unit) {
            super(unit);
            this.timerLength = timerLength;
        }

        /**
         * Creates a new timer object.
         *
         * @param timerLength The length of the timer, in seconds.
         */
        public Timer(long timerLength) {
            this(timerLength, TimeUnit.SECONDS);
        }

        /**
         * Get the remaining time until this timer is done.
         *
         * @return The remaining time, in the units specified in the constructor.
         * If it was not started, returns the timer length.
         * If it was paused, return the remaining time at the time the timer was paused.
         */
        public long remainingTime() {
            return timerLength - elapsedTime();
        }

        /**
         * Check if this timer has finished.
         *
         * @return True if at least timerLength of unpaused time has elapsed since the start of this timer. False otherwise.
         */
        public boolean done() {
            return elapsedTime() >= timerLength;
        }
    }

    /**
     * Very simple class for a refresh rate timer. Can be used to limit hardware writing/reading,
     * or other fast-time cases. Only uses milliseconds. Starts counting on creation, can be reset.
     */
    public class Rate {

        protected ElapsedTime time;
        protected long rate;

        public Rate(long rateMillis) {
            rate = rateMillis;
            time = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        }

        public void reset() {
            time.reset();
        }

        public boolean atTime() {
            boolean done = (time.milliseconds() >= rate);
            time.reset();
            return done;
        }

    }

}