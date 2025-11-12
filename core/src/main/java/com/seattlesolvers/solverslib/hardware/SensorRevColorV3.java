package com.seattlesolvers.solverslib.hardware;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * An extended wrapper class for the REV ColorSensor v3
 *
 * @author Arush - FTC 23511
 */

public class SensorRevColorV3 implements HardwareDevice {
    private final RevColorSensorV3 colorSensorV3;
    private DistanceUnit distanceUnit;

    /**
     * Constructs a color sensor, defaults to ARGB
     */
    public SensorRevColorV3(RevColorSensorV3 colorSensorV3) {
        this.colorSensorV3 = colorSensorV3;
    }

    /**
     * Constructs a color sensor, defaults to ARGB
     */
    public SensorRevColorV3(RevColorSensorV3 colorSensorV3, DistanceUnit distanceUnit) {
        this(colorSensorV3);
        this.distanceUnit = distanceUnit;
    }

    /**
     * Constructs a color sensor using the given hardware map and name, defaults to ARGB
     */
    public SensorRevColorV3(@NonNull HardwareMap hardwareMap, String name) {
        this((RevColorSensorV3) hardwareMap.colorSensor.get(name));
    }

    /**
     * Constructs a color sensor using the given hardware map and name, defaults to ARGB
     */
    public SensorRevColorV3(@NonNull HardwareMap hardwareMap, String name, DistanceUnit distanceUnit) {
        this((RevColorSensorV3) hardwareMap.colorSensor.get(name));
        this.distanceUnit = distanceUnit;
    }

    /**
     * Convert HSV value to an ARGB one. Includes alpha.
     *
     * @return an int representing the ARGB values
     */
    public int[] HSVtoARGB(int alpha, float[] hsv) {
        int color = Color.HSVToColor(alpha, hsv);
        return new int[]{Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color)};
    }

    /**
     * Converts an RGB value to an HSV value. Provide the float[] to be used.
     */
    public float[] RGBtoHSV(int red, int green, int blue, float[] hsv) {
        Color.RGBToHSV(red, green, blue, hsv);
        return hsv;
    }

    /**
     * Get all the ARGB values in an array from the sensor
     *
     * @return an int array representing ARGB
     */
    public int[] getARGB() {
        return new int[]{alpha(), red(), green(), blue()};
    }

    /**
     * Gets the alpha value from the sensor
     */
    public int alpha() {
        return colorSensorV3.alpha();
    }

    /**
     * Gets the red value from the sensor
     */
    public int red() {
        return colorSensorV3.red();
    }

    /**
     * Gets the green value from the sensor
     */
    public int green() {
        return colorSensorV3.green();
    }

    /**
     * Gets the blue value from the sensor
     */
    public int blue() {
        return colorSensorV3.blue();
    }

    /**
     * Gets the distance value from the sensor in the specified unit
     */
    public double distance(DistanceUnit unit) {
        return colorSensorV3.getDistance(unit);
    }

    public double distance() {
        return colorSensorV3.getDistance(distanceUnit);
    }

    /**
     * Gets the underlying RevColorSensorV3 object
     */
    public RevColorSensorV3 getColorSensor() {
        return colorSensorV3;
    }

    @Override
    public void disable() {
        colorSensorV3.close();
    }

    @Override
    public String getDeviceType() {
        return "REV Color Sensor v3";
    }
}

