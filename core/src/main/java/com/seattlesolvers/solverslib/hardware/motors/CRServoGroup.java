package com.seattlesolvers.solverslib.hardware.motors;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Allows multiple {@link CRServo} objects to be linked together
 * as a single group. Multiple CRServo's will act together.
 * Based off {@link MotorGroup}, but for CRServo/CRServoEx
 *
 * @author Arush
 * @author Jackson
 */
public class CRServoGroup extends CRServo implements Iterable<CRServo> {

    private final CRServo[] group;

    /**
     * Create a new CRServoGroup with the provided CRServos.
     *
     * @param leader    The leader CRServo.
     * @param followers The follower CRServos which follow the leader CRServo's protocols.
     */
    public CRServoGroup(@NonNull CRServo leader, CRServo... followers) {
        group = new CRServo[followers.length + 1];
        group[0] = leader;
        System.arraycopy(followers, 0, group, 1, followers.length);
    }

    /**
     * Set the speed for each CRServo in the group
     *
     * @param speed The speed to set. Value should be between -1.0 and 1.0.
     */
    @Override
    public void set(double speed) {
        group[0].set(speed);
        for (int i = 1; i < group.length; i++) {
            group[i].set(group[0].get());
        }
    }

    /**
     * @return The speed as a percentage of output
     */
    @Override
    public double get() {
        return group[0].get();
    }

    /**
     * @return The raw power as a percentage of output
     */
    @Override
    public double getRawPower() {
        return group[0].getRawPower();
    }

    /**
     * @return All CRServo target speeds as a percentage of output
     */
    public List<Double> getSpeeds() {
        return Arrays.stream(group)
                .map(CRServo::get)
                .collect(Collectors.toList());
    }

    /**
     * @return All CRServo target powers as a percentage of output
     */
    public List<Double> getRawPowers() {
        return Arrays.stream(group)
                .map(CRServo::getRawPower)
                .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public Iterator<CRServo> iterator() {
        return Arrays.asList(group).iterator();
    }

    /**
     * @return The position of every CRServo in the group in units of distance
     * which is by default ticks
     */
    public List<Double> getPositions() {
        return Arrays.stream(group)
                .map(CRServo::getDistance)
                .collect(Collectors.toList());
    }

    @Override
    public CRServoGroup setRunMode(RunMode runmode) {
        group[0].setRunMode(runmode);

        return this;
    }

    /**
     * @return true if the CRServo group is inverted
     */
    @Override
    public boolean getInverted() {
        return group[0].getInverted();
    }

    /**
     * Set the CRServo group to the inverted direction or forward direction.
     * This directly affects the speed rather than the direction.
     *
     * @param isInverted The state of inversion true is inverted.
     * @return This object for chaining purposes.
     */
    @Override
    public CRServoGroup setInverted(boolean isInverted) {
        for (CRServo crServo : group) {
            crServo.setInverted(isInverted);
        }
        return this;
    }

    /**
     * Disables all the CRServo devices.
     */
    @Override
    public void disable() {
        for (CRServo x : group) {
            x.disable();
        }
    }

    /**
     * @return a string characterizing the device type
     */
    @Override
    public String getDeviceType() {
        return "CRServo Group";
    }

    /**
     * Stops all CRServos in the group.
     */
    @Override
    public void stopMotor() {
        for (CRServo x : group) {
            x.stopMotor();
        }
    }

}
