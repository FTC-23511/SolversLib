package com.seattlesolvers.solverslib.controller;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.seattlesolvers.solverslib.util.MathUtils;

/**
 * This is a SquIDF controller (based off the PIDF controller, but with error square rooted)
 * for your robot. Internally, it performs all the calculations for you.
 * You need to tune your values to the appropriate amounts in order
 * to properly utilize these calculations.
 * <p>
 * The equation we will use is:
 * u(t) = kP * sqrt(e(t)) + kI * int(0,t)[e(t')dt'] + kD * e'(t) + kF * r(t)
 * where e(t) = r(t) - y(t) and r(t) is the setpoint and y(t) is the
 * measured value. If we consider e(t) the positional error, then
 * int(0,t)[e(t')dt'] is the total error and e'(t) is the velocity error.
 */
public class SquIDFController extends PIDFController {
    /**
     * The base constructor for the PIDF controller
     */
    public SquIDFController(double kp, double ki, double kd, double kf) {
        super(kp, ki, kd, kf);
    }

    /**
     * Constructor for the PIDF controller with PIDFCoefficients
     */
    public SquIDFController(PIDFCoefficients coefficients) {
        super(coefficients);
    }

    /**
     * This is the full constructor for the PIDF controller. Our PIDF controller
     * includes a feed-forward value which is useful for fighting friction and gravity.
     * Our errorVal represents the return of e(t) and prevErrorVal is the previous error.
     *
     * @param sp The setpoint of the pid control loop.
     * @param pv The measured value of he pid control loop. We want sp = pv, or to the degree
     *           such that sp - pv, or e(t) < tolerance.
     */
    public SquIDFController(double kp, double ki, double kd, double kf, double sp, double pv) {
        super(kp, ki, kd, kf, sp, pv);
    }

    @Override
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
            totalError = 0;
        }
        // returns u(t)
        return kP * MathUtils.sqrtWithSig(errorVal_p) + kI * totalError + kD * errorVal_v + kF * setPoint;
    }
}