package com.seattlesolvers.solverslib.controller;

public class CascadeController extends Controller {
    private final Controller primary;
    private final Controller secondary;
    private double velMeasuredValue;
    private double prevMeasuredValue;
    private double velSetPoint;

    public CascadeController(Controller primary, Controller secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    protected double calculateOutput(double pv) {
        prevErrorVal = errorVal_p;

        double currentTimeStamp = (double) System.nanoTime() / 1E9;
        if (lastTimeStamp == 0) {
            lastTimeStamp = currentTimeStamp;
            prevMeasuredValue = pv;
        }
        period = currentTimeStamp - lastTimeStamp;

        measuredValue = pv;
        errorVal_p = setPoint - measuredValue;

        if (Math.abs(period) > 1E-6) {
            velMeasuredValue = (measuredValue - prevMeasuredValue) / period;
            prevMeasuredValue = measuredValue;
            lastTimeStamp = currentTimeStamp;
        }

        errorVal_v = velSetPoint - velMeasuredValue;

        double sp2 = primary.calculate(pv, setPoint);
        double co2 = secondary.calculate(velMeasuredValue, sp2 + velSetPoint);

        return co2;
    }

    @Override
    public void reset() {
        super.reset();
        velMeasuredValue = 0;

        if (primary != null) {
            primary.reset();
        }
        if (secondary != null) {
            secondary.reset();
        }
    }

    @Override
    public void setSetPoint(double sp) {
        setSetPoints(sp, 0);
    }

    public void setSetPoints(double psp, double vsp) {
        setPoint = psp;
        velSetPoint = vsp;
        errorVal_p = setPoint - measuredValue;
        errorVal_v = velSetPoint - velMeasuredValue;
    }

    public double getMeasuredVel() {
        return velMeasuredValue;
    }
}
