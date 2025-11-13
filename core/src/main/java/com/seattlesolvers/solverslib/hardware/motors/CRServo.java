package com.seattlesolvers.solverslib.hardware.motors;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * A continuous rotation servo that uses a motor object to
 * and a P controller to limit speed and acceleration.
 *
 * @author Jackson
 */
public class CRServo extends Motor {

    /**
     * The CR ServoEx motor object.
     */
    protected com.qualcomm.robotcore.hardware.CRServo crServo;
    private final String id;

    /**
     * The implicit constructor for the CR Servo used for inheritance.
     */
    public CRServo() {
        this.id = "";
    }

    /**
     * The constructor for the CR Servo used for actual usage.
     */
    public CRServo(HardwareMap hMap, String id) {
        crServo = hMap.get(com.qualcomm.robotcore.hardware.CRServo.class, id);
        this.id = id;
    }

    @Override
    public void set(double output) {
        crServo.setPower(output);
        lastPower = output;
    }

    @Override
    public double getRawPower() {
        return crServo.getPower();
    }

    @Override
    public CRServo setInverted(boolean isInverted) {
        crServo.setDirection(isInverted ? com.qualcomm.robotcore.hardware.CRServo.Direction.REVERSE
                : com.qualcomm.robotcore.hardware.CRServo.Direction.FORWARD);
        return this;
    }

    @Override
    public boolean getInverted() {
        return crServo.getDirection() == com.qualcomm.robotcore.hardware.CRServo.Direction.REVERSE;
    }

    @Override
    public void disable() {
        crServo.close();
    }

    public void stop() {
        set(0);
    }

    @Override
    public void stopMotor() {
        stop();
    }

    @Override
    public String getDeviceType() {
        return "CR Servo; " + id + " in port " + crServo.getPortNumber();
    }
}
