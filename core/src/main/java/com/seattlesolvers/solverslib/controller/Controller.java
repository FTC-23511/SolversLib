package com.seattlesolvers.solverslib.controller;

public abstract class Controller {
    private double minOutput = 0;
    protected double setPoint;
    protected double measuredValue;

    protected double errorVal_p;
    protected double errorVal_v;
    protected double errorTolerance_p = 0.05;
    protected double errorTolerance_v = Double.POSITIVE_INFINITY;

    protected double prevErrorVal;
    protected double lastTimeStamp;
    protected double period;

    public Controller() {
        reset();
        period = 0;
    }

    /**
     * Resets previous error value and timestamp
     */
    public void reset() {
        prevErrorVal = 0;
        lastTimeStamp = 0;
    }

    /**
     * Calculates the control value, u(t). <br>
     *
     * NOTE: Is not used publicly and is instead wrapped in
     * {@link #calculate(double)} before being visible to user to allow for
     * other features such as {@link #setMinimumOutput(double)}
     *
     * @param pv The given measured value.
     * @return the value produced by u(t).
     */
    protected abstract double calculateOutput(double pv);

    /**
     * Calculates the control value, u(t). Also follows the minimum output
     * (see: {@link #setMinimumOutput(double)}) if set.
     *
     * @param pv The given measured value.
     * @return the value produced by u(t).
     */
    public double calculate(double pv) {
        double rawOutput = calculateOutput(pv);
        if (atSetPoint()) {
            return rawOutput;
        } else {
            return Math.max(Math.abs(rawOutput), minOutput) * Math.signum(rawOutput);
        }
    }

    /**
     * Calculates the next output of the controller.
     *
     * @param pv The given measured value.
     * @param sp The given setpoint.
     * @return the next output using the given measured value via
     * {@link #calculate(double)}.
     */
    public double calculate(double pv, double sp) {
        setSetPoint(sp);
        return calculate(pv);
    }

    /**
     * Calculates the next output the controller.
     *
     * @return the next output using the current measured value via
     * {@link #calculate(double)}.
     */
    public double calculate() {
        return calculate(measuredValue);
    }

    /**
     * Sets the setpoint
     *
     * @param sp The desired setpoint.
     */
    public void setSetPoint(double sp) {
        setPoint = sp;
        errorVal_p = setPoint - measuredValue;
        errorVal_v = (errorVal_p - prevErrorVal) / period;
    }

    /**
     * Returns the current setpoint
     *
     * @return The current setpoint.
     */
    public double getSetPoint() {
        return setPoint;
    }

    /**
     * Returns true if the error is within the percentage of the total input range, determined by
     * {@link #setTolerance}.
     *
     * @return Whether the error is within the acceptable bounds.
     */
    public boolean atSetPoint() {
        return Math.abs(errorVal_p) < errorTolerance_p
                && Math.abs(errorVal_v) < errorTolerance_v;
    }

    /**
     * Returns true if the error is within the percentage of the total input range, determined by
     * {@link #setTolerance}.
     *
     * @return Whether the error is within the acceptable bounds.
     */
    public boolean atSetPoint(double pv) {
        return Math.abs(errorVal_p) < errorTolerance_p
                && Math.abs(errorVal_v) < errorTolerance_v;
    }

    /**
     * @return the positional error e(t)
     */
    public double getPositionError() {
        return errorVal_p;
    }

    /**
     * @return the velocity error e'(t)
     */
    public double getVelocityError() {
        return errorVal_v;
    }

    /**
     * Sets the error which is considered tolerable for use with {@link #atSetPoint()}.
     *
     * @param positionTolerance Position error which is tolerable.
     */
    public void setTolerance(double positionTolerance) {
        setTolerance(positionTolerance, Double.POSITIVE_INFINITY);
    }

    /**
     * Sets the error which is considered tolerable for use with {@link #atSetPoint()}.
     *
     * @param positionTolerance Position error which is tolerable.
     * @param velocityTolerance Velocity error which is tolerable.
     */
    public void setTolerance(double positionTolerance, double velocityTolerance) {
        errorTolerance_p = positionTolerance;
        errorTolerance_v = velocityTolerance;
    }

    /**
     * @return the tolerances of the controller
     */
    public double[] getTolerance() {
        return new double[]{errorTolerance_p, errorTolerance_v};
    }

    public double getPeriod() {
        return period;
    }

    /**
     * An option to enforce a minimum (magnitude of the / absolute value of the) output from
     * subsequent calculations from the controller if the controller is not {@link #atSetPoint()}
     * @param minOutput the minimum (magnitude of the / absolute value of the) output for the controller
     * @return this object for chaining purposes
     */
    public Controller setMinimumOutput(double minOutput) {
        this.minOutput = Math.abs(minOutput);
        return this;
    }

    /**
     * Gets the minimum output (0 by default)
     *
     * @return the minimum output
     */
    public double getMinimumOutput() {
        return minOutput;
    }
}
