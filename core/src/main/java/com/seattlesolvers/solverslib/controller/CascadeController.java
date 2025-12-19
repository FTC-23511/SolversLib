package com.seattlesolvers.solverslib.controller;

public class CascadeController extends Controller {
    private final Controller primary;
    private final Controller secondary;
    private double velMeasuredValue;
    private double velSetPoint;

    public CascadeController(Controller primary, Controller secondary) {
        this.primary = primary;
        this.secondary = secondary;
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
            velMeasuredValue = (errorVal_p - prevErrorVal) / period;
        } else {
            velMeasuredValue = 0;
        }

        errorVal_v = velSetPoint - velMeasuredValue;

        double sp2 = primary.calculate(pv);
        double co2 = secondary.calculate(velMeasuredValue, sp2 + velSetPoint);

        return co2;
    }

    @Override
    public void setSetPoint(double sp) {
        setSetPoints(sp, 0);
    }

    public void setSetPoints(double psp, double vsp) {
        setPoint = psp;
        velSetPoint = vsp;
        errorVal_p = setPoint - measuredValue;
        velMeasuredValue = (errorVal_p - prevErrorVal) / period;
        errorVal_v = velSetPoint - velMeasuredValue;
    }
}
