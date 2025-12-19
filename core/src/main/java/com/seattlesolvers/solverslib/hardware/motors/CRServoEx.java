package com.seattlesolvers.solverslib.hardware.motors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.seattlesolvers.solverslib.controller.PIDFController;
import com.seattlesolvers.solverslib.hardware.AbsoluteAnalogEncoder;
import com.seattlesolvers.solverslib.util.MathUtils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * An extended wrapper class for CRServos with more features
 * such as integration with absolute analog encoders for Axon servos
 * and their absolute encoders and power caching to reduce loop times.
 *
 * @author Saket
 */
public class CRServoEx extends CRServo {
    private AbsoluteAnalogEncoder absoluteEncoder;
    private double cachingTolerance = 0.0001;
    private PIDFController pidf;

    /**
     * The mode in which the CR servo should behave.
     */
    public enum RunMode {
        /**
         * Mode in which the CR servo takes the shortest path to reach a specific angle
         */
        OptimizedPositionalControl,
        /**
         * Mode in which the CR servo is controlled with raw power
         */
        RawPower
    }
    private RunMode runmode;

    /**
     * The constructor for the CR Servo.
     *
     * @param hwMap hardwareMap
     * @param id ID of the CR servo as configured
     * @param encoderID ID of the absolute encoder as configured
     * @param analogRange the range of voltage for the absolute encoder
     * @param angleUnit the angle unit for the absolute encoder
     * @param runmode the runmode of the CR servo
     */
    public CRServoEx(HardwareMap hwMap, String id, String encoderID, double analogRange, AngleUnit angleUnit, RunMode runmode) {
        super(hwMap, id);
        this.absoluteEncoder = new AbsoluteAnalogEncoder(hwMap, encoderID, analogRange, angleUnit);
        this.runmode = runmode;
    }

    /**
     * The constructor for the CR Servo.
     *
     * @param hwMap hardwareMap
     * @param id ID of the CR servo as configured
     * @param absoluteEncoder the object for the absolute encoder to be associated with this servo
     * @param runmode the runmode of the CR servo
     */
    public CRServoEx(HardwareMap hwMap, String id, AbsoluteAnalogEncoder absoluteEncoder, RunMode runmode) {
        super(hwMap, id);
        this.absoluteEncoder = absoluteEncoder;
        this.runmode = runmode;
    }

    /**
     * A simple constructor for the CR Servo with no absolute encoder.
     * @param hwMap hardwareMap
     * @param id ID of the CR servo as configured
     */
    public CRServoEx(HardwareMap hwMap, String id) {
        super(hwMap, id);
        this.absoluteEncoder = null;
        this.runmode = RunMode.RawPower;
    }

    /**
     * @param runmode the new runmode to be set
     * @return this object for chaining purposes
     */
    public CRServoEx setRunMode(RunMode runmode) {
        this.runmode = runmode;
        return this;
    }

    /**
     * Sets the PIDF coefficients of the CR Servo for the PositionalControl runmodes
     * @param coefficients the coefficients for the PIDF controller
     * @return this object for chaining purposes
     */
    public CRServoEx setPIDF(PIDFCoefficients coefficients) {
        this.pidf = new PIDFController(coefficients);
        return this;
    }

    /**
     * @param cachingTolerance the new caching tolerance between CR servo writes
     * @return this object for chaining purposes
     */
    public CRServoEx setCachingTolerance(double cachingTolerance) {
        this.cachingTolerance = cachingTolerance;
        return this;
    }

    /**
     * @return the caching tolerance of the CR servo before it writes a new power to the CR servo
     */
    public double getCachingTolerance() {
        return cachingTolerance;
    }

    /**
     * @param absoluteEncoder the new absolute encoder to be associated with the CR servo
     * @return this object for chaining purposes
     */
    public CRServoEx setAbsoluteEncoder(AbsoluteAnalogEncoder absoluteEncoder) {
        this.absoluteEncoder = absoluteEncoder;
        return this;
    }

    /**
     * @return the absolute encoder object associated with the CR servo
     */
    public AbsoluteAnalogEncoder getAbsoluteEncoder() {
        return absoluteEncoder;
    }

    /**
     * @param output the raw power or angle (based on the runmode of the CR servo) that the CR servo should be set to
     */
    @Override
    public void set(double output) {
        if (runmode == RunMode.OptimizedPositionalControl) {
            if (absoluteEncoder == null) {
                throw new IllegalStateException("Must have absolute encoder and PIDF coefficients for CR Servo to be in positional control");
            }
            double normalizedTarget = MathUtils.normalizeAngle(output, true, absoluteEncoder.getAngleUnit());
            double error = MathUtils.normalizeAngle(normalizedTarget - absoluteEncoder.getCurrentPosition(), false, absoluteEncoder.getAngleUnit());
            double power = pidf.calculate(0, error);
            setPower(power);
        } else {
            setPower(output);
        }
    }

    /**
     * @param pwmRange the PWM range the CR servo should be set to
     * @return this object for chaining purposes
     */
    public CRServoEx setPwm(PwmControl.PwmRange pwmRange) {
        getController().setServoPwmRange(crServo.getPortNumber(), pwmRange);
        return this;
    }

    /**
     * @return the extended servo controller object for the servo
     */
    public ServoControllerEx getController() {
        return (ServoControllerEx) crServo.getController();
    }

    /**
     * @return the SDK, unwrapped CR servo object
     */
    public com.qualcomm.robotcore.hardware.CRServo getServo() {
        return this.crServo;
    }

    /**
     * @param power power to be assigned to the servo if difference is greater than caching tolerance or if power is exactly 0
     */
    private void setPower(double power) {
        if ((Math.abs(power - lastPower) > cachingTolerance) || (power == 0 && lastPower != 0)) {
            crServo.setPower(power);
            lastPower = power;
        }
    }

    @Override
    public String getDeviceType() {
        return "Extended " + super.getDeviceType();
    }
}
