package com.seattlesolvers.solverslib.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.util.MathUtils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * An extended wrapper class for AnalogInput absolute encoders.
 *
 * @author Saket
 */
public class AbsoluteAnalogEncoder implements HardwareDevice {
    public static double DEFAULT_RANGE = 3.3;
    private final AnalogInput encoder;
    private final String id;
    private double offset = 0.0;
    private final double range;
    private final AngleUnit angleUnit;
    private boolean reversed;
    private ElapsedTime timer = new ElapsedTime();
    private double lastPos;
    private double vel = 0;

    /**
     * The constructor for absolute analog encoders
     * @param hwMap the hardwareMap
     * @param id the ID of the encoder as configured
     * @param range the range of voltage returned by the sensor
     */
    public AbsoluteAnalogEncoder(HardwareMap hwMap, String id, double range, AngleUnit angleUnit) {
        this.encoder = hwMap.get(AnalogInput.class, id);
        this.angleUnit = angleUnit;
        this.range = range;
        this.id = id;
        reversed = false;
    }

    /**
     * The constructor for absolute analog encoders, with default values of 3.3v and radians for the range and angle unit respectively
     * @param hwMap the hardwareMap
     * @param id the ID of the encoder as configured
     */
    public AbsoluteAnalogEncoder(HardwareMap hwMap, String id) {
        this(hwMap, id, DEFAULT_RANGE, AngleUnit.RADIANS);
    }

    /**
     * Sets an angular offset for any future values returned when reading the encoder
     * @param offset The angular offset in the units specified by the user previously
     * @return The object itself for chaining purposes
     */
    public AbsoluteAnalogEncoder zero(double offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Sets whether or not the encoder should be reversed for any future values returned when reading the encoder
     * @param reversed Whether or not the encoder should be reversed for any future values
     * @return The object itself for chaining purposes
     */
    public AbsoluteAnalogEncoder setReversed(boolean reversed) {
        this.reversed = reversed;
        return this;
    }

    /**
     * Gets whether the encoder is reversed or not
     * @return Whether the encoder is reversed
     */
    public boolean getReversed() {
        return reversed;
    }

    /**
     * @return The normalized angular position of the encoder in the unit previously specified by the user from 0 to max
     */
    public double getCurrentPosition() {
        double newPos = MathUtils.normalizeAngle(
                (!reversed ? 1 - getVoltage() / range : getVoltage() / range) * MathUtils.returnMaxForAngleUnit(angleUnit) - offset,
                true,
                angleUnit
        );
        vel = (newPos - lastPos) / timer.seconds();
        timer.reset();
        lastPos = newPos;
        return lastPos;
    }

    public double getVelocity() {
        return vel;
    }

    /**
     * @return The AnalogInput object of the encoder itself
     */
    public AnalogInput getEncoder() {
        return encoder;
    }

    /**
     * @return The raw voltage returned by the encoder
     */
    public double getVoltage(){
        return encoder.getVoltage();
    }

    @Override
    public void disable() {
        // "take no action" (encoder.close() call in SDK)
    }

    @Override
    public String getDeviceType() {
        return "Absolute Analog Encoder; " + id;
    }

    /**
     * @return The angle unit associated with the absolute encoder
     */
    public AngleUnit getAngleUnit() {
        return angleUnit;
    }
}
