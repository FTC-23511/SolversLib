package com.seattlesolvers.solverslib.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.util.MathUtils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * An extended wrapper class for DcMotor incremental/quadrature encoders.
 *
 * @author Daniel - 7854
 */
public class IncrementalEncoder extends EncoderBase<DcMotor, IncrementalEncoder> {
    private final double cpr;
    private int lastPosition;
    private double lastTimeStamp, veloEstimate, dpp, accel, lastVelo;

    /**
     * The constructor for incremental encoders
     * @param encoder the DcMotor which encoder is bound to
     * @param countsPerRevolution the number of encoder ticks per full revolution, aka cycles per revolution
     * @param angleUnit the angle unit of the encoder
     */
    public IncrementalEncoder(DcMotor encoder, double countsPerRevolution, AngleUnit angleUnit) {
        super(encoder);
        this.cpr = countsPerRevolution;
        this.angleUnit = angleUnit;
        dpp = 1;
        lastPosition = 0;
        veloEstimate = 0;
        lastTimeStamp = (double) System.nanoTime() / 1E9;
    }

    /**
     * The constructor for incremental encoders
     * @param hwMap the hardwareMap
     * @param id the ID of the encoder as configured
     * @param countsPerRevolution the number of encoder ticks per full revolution, aka cycles per revolution
     * @param angleUnit the angle unit of the encoder
     */
    public IncrementalEncoder(HardwareMap hwMap, String id, double countsPerRevolution, AngleUnit angleUnit) {
        this(hwMap.get(DcMotor.class, id), countsPerRevolution, angleUnit);
    }

    /**
     * The constructor for incremental encoders, defaults to radians
     * @param encoder the DcMotor which encoder is bound to
     * @param countsPerRevolution the number of encoder ticks per full revolution, aka cycles per revolution
     */
    public IncrementalEncoder(DcMotor encoder, double countsPerRevolution) {
        this(encoder, countsPerRevolution, AngleUnit.RADIANS);
    }

    /**
     * The constructor for incremental encoders, defaults to radians
     * @param hwMap the hardwareMap
     * @param id the ID of the encoder as configured
     * @param countsPerRevolution the number of encoder ticks per full revolution, aka cycles per revolution
     */
    public IncrementalEncoder(HardwareMap hwMap, String id, double countsPerRevolution) {
        this(hwMap.get(DcMotor.class, id), countsPerRevolution, AngleUnit.RADIANS);
    }

    /**
     * @return the current position of the encoder
     */
    public int getPosition() {
        int currentPosition = encoder.getCurrentPosition();
        if (currentPosition != lastPosition) {
            double currentTime = System.nanoTime() / 1E9;
            double dt = currentTime - lastTimeStamp;
            veloEstimate = (currentPosition - lastPosition) / dt;
            lastPosition = currentPosition;
            lastTimeStamp = currentTime;
        }
        return getDirectionMultiplier() * currentPosition - (int) offset;
    }

    /**
     * @return the distance per pulse
     */
    public double getDistancePerPulse() {
        return dpp;
    }

    /**
     * @return the distance traveled by the encoder
     */
    public double getDistance() {
        return dpp * getPosition();
    }

    /**
     * @return the velocity of the encoder adjusted to account for the distance per pulse
     */
    public double getRate() {
        return dpp * ((DcMotorEx) encoder).getVelocity();
    }

    /**
     * Sets the distance per pulse of the encoder.
     *
     * @param distancePerPulse the desired distance per pulse (in units per tick)
     */
    public IncrementalEncoder setDistancePerPulse(double distancePerPulse) {
        dpp = distancePerPulse;
        return this;
    }

    /**
     * @return the number of revolutions turned by the encoder
     */
    public double getRevolutions() {
        return getPosition() / cpr;
    }

    /**
     * @return the raw velocity of the motor reported by the encoder
     */
    public double getRawVelocity() {
        double velo = ((DcMotorEx) encoder).getVelocity();
        if (velo != lastVelo) {
            double currentTime = (double) System.nanoTime() / 1E9;
            double dt = currentTime - lastTimeStamp;
            accel = (velo - lastVelo) / dt;
            lastVelo = velo;
            lastTimeStamp = currentTime;
        }
        return velo;
    }

    /**
     * @return the estimated acceleration of the motor in ticks per second squared
     */
    public double getAcceleration() {
        return accel;
    }

    private final static int CPS_STEP = 0x10000;

    /**
     * Corrects for velocity overflow
     *
     * @return the corrected velocity
     */
    public double getCorrectedVelocity() {
        double real = getRawVelocity();
        while (Math.abs(veloEstimate - real) > CPS_STEP / 2.0) {
            real += Math.signum(veloEstimate - real) * CPS_STEP;
        }
        return real;
    }

    /**
     * Convert an angle to encoder ticks, given the CPR
     * @param angle Angle to convert
     * @return Number of encoder ticks
     */
    public int angleToTicks(double angle) {
        return (int) (angle * cpr / MathUtils.returnMaxForAngleUnit(angleUnit));
    }

    /**
     * Gets the angle before normalizing
     * @return Unnormalized angle
     */
    public double getAngleUnnormalized() {
        return getRevolutions() * MathUtils.returnMaxForAngleUnit(angleUnit);
    }

    @Override
    public IncrementalEncoder setAngle(double angle) {
        offset = getPosition() - angleToTicks(angle);
        return this;
    }

    @Override
    public DcMotor getEncoder() {
        return encoder;
    }

    @Override
    public double getAngle() {
        return MathUtils.normalizeAngle(
                getAngleUnnormalized(),
                true,
                angleUnit
        );
    }

    @Override
    public String getDeviceType() {
        return "Incremental Encoder; " + encoder.getDeviceName();
    }
}