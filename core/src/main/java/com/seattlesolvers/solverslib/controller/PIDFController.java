package com.seattlesolvers.solverslib.controller;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.seattlesolvers.solverslib.util.MathUtils;

/**
 * This is a PID controller (https://en.wikipedia.org/wiki/PID_controller)
 * for your robot. Internally, it performs all the calculations for you.
 * You need to tune your values to the appropriate amounts in order
 * to properly utilize these calculations.
 * <p>
 * The equation we will use is:
 * u(t) = kP * e(t) + kI * int(0,t)[e(t')dt'] + kD * e'(t) + kF * r(t)
 * where e(t) = r(t) - y(t) and r(t) is the setpoint and y(t) is the
 * measured value. If we consider e(t) the positional error, then
 * int(0,t)[e(t')dt'] is the total error and e'(t) is the velocity error.
 */
public class PIDFController extends Controller {
    protected double kP, kI, kD, kF;

    protected double totalError;

    /**
     * Extra functionality and behavior of the controller to deal with integration build-up.
     */
    public enum IntegrationBehavior {
        /**
         * No special behavior apart from restricting to integration bounds and decaying as set in the constructor.
         */
        NONE,
        /**
         * Clears total integration when the controller reaches the setpoint within tolerance.
         */
        CLEAR_AT_SP
    }

    /**
     * Internal configuration class to deal with integration build-up. 
     * Has configurations for setting total integration bounds, decaying (if integration term becomes opposite of error), and {@link IntegrationBehavior}.
     */
    public static class IntegrationControl {
        IntegrationBehavior integrationBehavior;
        double decayFactor;
        double minIntegral;
        double maxIntegral;

        public IntegrationControl(IntegrationBehavior integrationBehavior, double decayFactor, double minIntegral, double maxIntegral) {
            this.integrationBehavior = integrationBehavior;
            this.decayFactor = decayFactor;
            this.minIntegral = minIntegral;
            this.maxIntegral = maxIntegral;
        }

        IntegrationControl() {
            this(IntegrationBehavior.NONE, 1, -1.0, 1.0);
        }

        public void setIntegrationBehavior(IntegrationBehavior integrationBehavior) {
            this.integrationBehavior = integrationBehavior;
        }
        public IntegrationBehavior getIntegrationBehavior() {
            return integrationBehavior;
        }

        public void setDecayFactor(double decayFactor) {
            this.decayFactor = decayFactor;
        }
        public double getDecayFactor() {
            return decayFactor;
        }

        public void setIntegrationBounds(double min, double max) {
            this.minIntegral = min;
            this.maxIntegral = max;
        }
        public double getMinIntegral() {
            return minIntegral;
        }
        public double getMaxIntegral() {
            return maxIntegral;
        }
    }
    
    public IntegrationControl integrationControl;

    /**
     * The base constructor for the PIDF controller
     */
    public PIDFController(double kp, double ki, double kd, double kf) {
        this(kp, ki, kd, kf, 0, 0);
    }

    /**
     * Constructor for the PIDF controller with PIDFCoefficients
     */
    public PIDFController(PIDFCoefficients coefficients) {
        this(coefficients.p, coefficients.i, coefficients.d, coefficients.f);
    }
    
    /**
     * This is the full constructor for the PIDF controller. Our PIDF controller
     * includes a feed-forward value which is useful for fighting friction and gravity.
     * Our errorVal represents the return of e(t) and prevErrorVal is the previous error.
     *
     * @param sp The setpoint of the pid control loop.
     * @param pv The measured value of the pid control loop. We want sp = pv, or to the degree
     *           such that sp - pv, or e(t) < tolerance.
     */
    public PIDFController(double kp, double ki, double kd, double kf, double sp, double pv) {
        kP = kp;
        kI = ki;
        kD = kd;
        kF = kf;

        setPoint = sp;
        measuredValue = pv;

        integrationControl = new IntegrationControl();

        errorVal_p = setPoint - measuredValue;
    }

    /**
     * Set the controller to deal with the common issue of integration build-up. For the modes, see #IntegrationControl
     * @return this object for chaining purposes
     */
    public PIDFController setIntegrationControl(IntegrationControl integrationControl) {
        this.integrationControl = integrationControl;
        return this;
    }

    @Override
    public void reset() {
        totalError = 0;
        super.reset();
    }

    /**
     * @return the PIDF coefficients
     */
    public double[] getCoefficients() {
        return new double[]{kP, kI, kD, kF};
    }

    /**
     * Calculates the control value, u(t).
     *
     * @param pv The current measurement of the process variable.
     * @return the value produced by u(t).
     */
    protected double calculateOutput(double pv) {
        prevErrorVal = errorVal_p;

        double currentTimeStamp = (double) System.nanoTime() / 1E9;
        if (lastTimeStamp == 0) lastTimeStamp = currentTimeStamp;
        period = currentTimeStamp - lastTimeStamp;
        lastTimeStamp = currentTimeStamp;

        if (measuredValue == pv) {
            errorVal_p = setPoint - measuredValue;
        } else {
            errorVal_p = setPoint - pv;
            measuredValue = pv;
        }

        if (Math.abs(period) > 1E-6) {
            errorVal_v = (errorVal_p - prevErrorVal) / period;
        } else {
            errorVal_v = 0;
        }

        /*
        if total error is the integral from 0 to t of e(t')dt', and
        e(t) = sp - pv, then the total error, E(t), equals sp*t - pv*t.
         */
        totalError += period * (setPoint - measuredValue);
        totalError = MathUtils.clamp(totalError, integrationControl.getMinIntegral(), integrationControl.getMaxIntegral());
        if ((Math.signum(totalError) != Math.signum(errorVal_p))) {
            totalError *= integrationControl.getDecayFactor();
        }
        if (atSetPoint() && integrationControl.getIntegrationBehavior().equals(IntegrationBehavior.CLEAR_AT_SP)) {
            clearTotalError();
        }

        // returns u(t)
        return kP * errorVal_p + kI * totalError + kD * errorVal_v + kF * setPoint;
    }

    public void setPIDF(double kp, double ki, double kd, double kf) {
        kP = kp;
        kI = ki;
        kD = kd;
        kF = kf;
    }

    public void setCoefficients(PIDFCoefficients coefficients) {
        setPIDF(coefficients.p, coefficients.i, coefficients.d, coefficients.f);
    }

    /**
     * Please use the internal {@link #integrationControl} object and its setIntegrationBounds method.
     */
    @Deprecated
    public void setIntegrationBounds(double integralMin, double integralMax) {
        integrationControl.setIntegrationBounds(integralMin, integralMax);
    }

    public void clearTotalError() {
        totalError = 0;
    }

    public void setP(double kp) {
        kP = kp;
    }

    public void setI(double ki) {
        kI = ki;
    }

    public void setD(double kd) {
        kD = kd;
    }

    public void setF(double kf) {
        kF = kf;
    }

    public double getP() {
        return kP;
    }

    public double getI() {
        return kI;
    }

    public double getD() {
        return kD;
    }

    public double getF() {
        return kF;
    }

}