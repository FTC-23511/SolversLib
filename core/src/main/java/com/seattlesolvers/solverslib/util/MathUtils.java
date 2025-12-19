/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.seattlesolvers.solverslib.util;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtils {
    private MathUtils() {
        throw new AssertionError("utility class");
    }

    /**
     * Returns value clamped between low and high boundaries.
     *
     * @param value Value to clamp.
     * @param low   The lower boundary to which to clamp value.
     * @param high  The higher boundary to which to clamp value.
     */
    public static int clamp(int value, int low, int high) {
        return Math.max(low, Math.min(value, high));
    }

    /**
     * Returns value clamped between low and high boundaries.
     *
     * @param value Value to clamp.
     * @param low   The lower boundary to which to clamp value.
     * @param high  The higher boundary to which to clamp value.
     */
    public static double clamp(double value, double low, double high) {
        return Math.max(low, Math.min(value, high));
    }

    /**
     * Returns value rounded to specified places
     *
     * @param number Number to round
     * @param places The number of decimal places to round to
     */
    public static double round(double number, int places) {
        return new BigDecimal((String.valueOf(number))).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Function to normalize all angles
     *
     * @param angle the angle to be normalized, in degrees or radians
     * @param zeroToMax whether the returned value should be normalized to 0 to max or -midpoint to midpoint
     * @param angleUnit the unit the angle parameter is in
     * @return the normalized angle
     */
    public static double normalizeAngle(double angle, boolean zeroToMax, AngleUnit angleUnit) {
        double max = returnMaxForAngleUnit(angleUnit);
        double angle2 = angle % max;
        if (zeroToMax && angle2 < 0) {
            return angle2 + max;
        } else if (!zeroToMax) {
            if (angle2 > max/2) {
                return angle2 - max;
            } else if (angle2 < -max/2) {
                return angle2 + max;
            }
        }

        return angle2;
    }

    public static double normalizeRadians(double angle, boolean zeroToFull) {
        return normalizeAngle(angle, zeroToFull, AngleUnit.RADIANS);
    }

    public static double normalizeDegrees(double angle, boolean zeroToFull) {
        return normalizeAngle(angle, zeroToFull, AngleUnit.DEGREES);
    }

    public static double returnMaxForAngleUnit(AngleUnit angleUnit) {
        if (angleUnit.equals(AngleUnit.RADIANS)) {
            return Math.PI * 2;
        } else {
            return 360;
        }
    }

    public static double sqrtWithSig(double val) {
        return Math.sqrt(Math.abs(val)) * Math.signum(val);
    }
}