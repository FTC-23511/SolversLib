package com.seattlesolvers.solverslib.hardware.servos;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.seattlesolvers.solverslib.hardware.HardwareDevice;

/**
 * An extended Servo wrapper class which implements utility features such as
 * caching to reduce loop times and custom angle ranges.
 *
 * @author Saket
 */
public class ServoEx implements HardwareDevice {
    private final Servo servo;
    private final String id;
    private double min = 0.0;
    private double max = 1.0;
    private double cachingTolerance = 0.0001;
    private double lastPos = Double.NaN;

    /**
     * The main constructor for the ServoEx object.
     * @param hwMap hardwareMap
     * @param id the ID of the servo as configured
     * @param min the minimum angle of the servo in the specified angle unit (when the servo is set to 0)
     * @param max the maximum angle of the servo in the specified angle unit (when the servo is set to 1)
     */
    public ServoEx(HardwareMap hwMap, String id, double min, double max) {
        if (max < min) {
            throw new IllegalArgumentException("Minimum angle should be less than maximum angle!");
        }
        if (min < 0) {
            throw new IllegalArgumentException("Minimum angle should be greater than or equal to 0!");
        }
        this.servo = hwMap.get(Servo.class, id);
        this.id = id;
        this.min = min;
        this.max = max;
    }

    /**
     *
     * @param hwMap hardwareMap
     * @param id the ID of the servo as configured
     * @param range the range of the servo (from when the servo is set to 0 to 1)
     */
    public ServoEx(HardwareMap hwMap, String id, double range) {
        this(hwMap, id, 0.0, range);
    }

    /**
     * @param hwMap hardwareMap
     * @param id the ID of the servo as configured
     */
    public ServoEx(HardwareMap hwMap, String id) {
        this(hwMap, id, 0.0, 1.0);
    }

    /**
     * A base constructor used for inheritance
     */
    public ServoEx() {
        this.id = "";
        this.servo = null;
    }

//    TODO: Actually implement this (needs more research on how it behaves with get and set positions)
//    public void scaleRange(double min, double max) {
//        servo.scaleRange(min, max);
//    }

    /**
     * @param output the raw position (or angle if range or max + min were defined in constructor) the servo should be set to
     */
    public void set(double output) {
        setPosition((output - min) / (max - min));
    }

    /**
     * Method for wrapping all writes to setPositions to the servo to check for caching tolerance
     * @param pos position requested to be written to the servo
     */
    private void setPosition(double pos) {
        if (Double.isNaN(lastPos) || Math.abs(pos - lastPos) > cachingTolerance) {
            servo.setPosition(pos);
            lastPos = pos;
        }
    }

    /**
     * @return the position of the servo based on the last call to the ServoEx object
     */
    public double get() {
        return lastPos * (max - min) + min;
    }

    /**
     * @return the raw position of the servo between 0 and 1
     */
    public double getRawPosition() {
        return servo.getPosition();
    }

    /**
     * @param inverted whether the servo should be inverted/reversed
     * @return this object for chaining purposes
     */
    public ServoEx setInverted(boolean inverted) {
        servo.setDirection(inverted ? Servo.Direction.REVERSE : Servo.Direction.FORWARD);
        return this;
    }

    /**
     * @return whether the servo is inverted/reversed
     */
    public boolean getInverted() {
        return servo.getDirection().equals(Servo.Direction.REVERSE);
    }

    /**
     * @param pwmRange the PWM range the servo should be set to
     * @return this object for chaining purposes
     */
    public ServoEx setPwm(PwmControl.PwmRange pwmRange) {
        getController().setServoPwmRange(servo.getPortNumber(), pwmRange);
        return this;
    }

    /**
     * @return the SDK, unwrapped Servo object
     */
    public Servo getServo() {
        return servo;
    }

    /**
     * @return the extended servo controller object for the servo
     */
    public ServoControllerEx getController() {
        return (ServoControllerEx) servo.getController();
    }

    /**
     * @return the port the servo controller is controlling the servo from
     */
    public int getPortNumber() {
        return this.servo.getPortNumber();
    }

    /**
     * @param cachingTolerance the new caching tolerance between servo writes
     * @return this object for chaining purposes
     */
    public ServoEx setCachingTolerance(double cachingTolerance) {
        this.cachingTolerance = cachingTolerance;
        return this;
    }

    /**
     * @return the caching tolerance of the servo before it writes a new power to the CR servo
     */
    public double getCachingTolerance() {
        return cachingTolerance;
    }

    @Override
    public void disable() {
        servo.close();
    }

    @Override
    public String getDeviceType() {
        return "Extended Servo; " + id + " from " + servo.getPortNumber();
    }
}
