package com.seattlesolvers.solverslib.controller;

import com.seattlesolvers.solverslib.util.MathUtils;

public abstract class Controller {
    private double minOutput = 0;
    private double maxOutput = Double.POSITIVE_INFINITY;
    private double openF = 0;
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
     * other features such as {@link #setMinOutput(double)}
     *
     * @param pv The given measured value.
     * @return the value produced by u(t).
     */
    protected abstract double calculateOutput(double pv);

    /**
     * Calculates the control value, u(t). Also follows the minimum output
     * (see: {@link #setMinOutput(double)}) if set.
     *
     * @param pv The given measured value.
     * @return the value produced by u(t).
     */
    public double calculate(double pv) {
        double output = calculateOutput(pv);
        output += Math.signum(errorVal_p) * openF;
        if (atSetPoint()) {
            return output;
        } else {
            return MathUtils.clamp(Math.abs(output), minOutput, maxOutput) * Math.signum(output);
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
    public Controller setMinOutput(double minOutput) {
        this.minOutput = Math.abs(minOutput);
        return this;
    }

    /**
     * Gets the minimum output (0 by default)
     *
     * @return the minimum output
     */
    public double getMinOutput() {
        return minOutput;
    }

    /**
     * An option to enforce a max (magnitude of the / absolute value of the) output from
     * subsequent calculations from the controller if the controller is not {@link #atSetPoint()}
     * @param maxOutput the max (magnitude of the / absolute value of the) output for the controller
     * @return this object for chaining purposes
     */
    public Controller setMaxOutput(double maxOutput) {
        this.maxOutput = maxOutput;
        return this;
    }

    /**
     * Gets the maximum output (Positive Infinity by default)
     *
     * @return the maximum output
     */
    public double getMaxOutput() {
        return maxOutput;
    }

    /**
     * Adds a basic open-loop feedforward that is added to any calls to {@link #calculate(double)} and its variants.
     * Term will flip to match sign error when being added.
     * @param f the feedforward term
     * @return this object for chaining purposes
     */
    public Controller setOpenF(double f) {
        this.openF = f;
        return this;
    }
}
