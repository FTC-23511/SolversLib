package com.seattlesolvers.solverslib.hardware;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.List;
import java.util.Map;

public interface SensorDistanceEx extends SensorDistance {

    /**
     * Represents a target distance
     */
    class DistanceTarget {
        /**
         * The distance.
         */
        private double target;

        /**
         * The minimum threshold for the actual distance to be in for the target to be reached.
         */
        private double minThreshold;

        /**
         * The maximum threshold for the actual distance to be in for the target to be reached.
         */
        private double maxThreshold;

        /**
         * User-defined name for the target. Optional.
         */
        private String name;

        /**
         * User-defined unit of distance. This unit applies to both the threshold and the target
         */
        private DistanceUnit unit;

        /**
         * Target <b>must</b> be within the range of the Rev 2m Sensor, which is 2 meters
         *
         * @param unit   user-defined unit of distance
         * @param target the target distance
         */
        public DistanceTarget(DistanceUnit unit, double target) {
            this(unit, target - 5, target + 5);
        }

        /**
         * @param unit      user-defined unit of distance
         * @param minThreshold the minimum acceptable distance
         * @param maxThreshold the maximum acceptable distance
         */
        public DistanceTarget(DistanceUnit unit, double minThreshold, double maxThreshold) {
            this(unit, minThreshold, maxThreshold, "Distance Target");
        }

        /**
         * @param minThreshold the minimum acceptable distance
         * @param maxThreshold the maximum acceptable distance
         * @param name      the name of the sensor
         */
        public DistanceTarget(DistanceUnit unit, double minThreshold, double maxThreshold, String name) {
            if (minThreshold < 0) {
                throw new IllegalArgumentException("Minimum threshold for SensorDistanceEx must be positive");
            }
            else if (maxThreshold < 0) {
                throw new IllegalArgumentException("Maximum threshold for SensorDistanceEx must be positive");
            }
            else if (target < 0) {
                throw new IllegalArgumentException("Target for SensorDistanceEx must be positive");
            }
            else if (minThreshold > maxThreshold) {
                throw new IllegalArgumentException("Minimum threshold for SensorDistanceEx must be less than maximum threshold");
            }

            this.unit = unit;
            this.target = (minThreshold + maxThreshold) / 2.0;
            this.minThreshold = minThreshold;
            this.maxThreshold = maxThreshold;
            this.name = name;
        }

        /**
         * Determines if the sensor reached the target value (within the threshold error range)
         *
         * @param currentDistance the current distance to the target
         * @return whether the the sensor reached the the target value (within the threshold error range)
         */
        public boolean atTarget(double currentDistance) {
            currentDistance = unit.fromUnit(unit, currentDistance);
            return (currentDistance >= minThreshold) && (currentDistance <= maxThreshold);
        }

        /**
         * Change the target value
         *
         * @param target the new target
         */
        public void setTarget(double target) {
            this.target = target;
        }

        /**
         * Change the threshold value
         *
         * @param threshold the new threshold
         */
        public void setThreshold(double threshold) {
            this.minThreshold = Math.max((target - threshold) / 2.0, 0);
            this.minThreshold = Math.max((target + threshold) / 2.0, 0);
        }

        /**
         * Changes the threshold values directly
         */
        public void setMinThreshold(double minThreshold) {
            if (minThreshold > maxThreshold) {
                throw new IllegalArgumentException("Minimum threshold for SensorDistanceEx must be less than maximum threshold");
            }
            this.minThreshold = minThreshold;
        }

        public void setMaxThreshold(double maxThreshold) {
            if (minThreshold > maxThreshold) {
                throw new IllegalArgumentException("Maximum threshold for SensorDistanceEx must be greater than minimum  threshold");
            }
            this.maxThreshold = maxThreshold;
        }

        /**
         * Changes the unit of measurement
         *
         * @param unit the new unit value
         */
        public void setUnit(DistanceUnit unit) {
            this.target = unit.fromUnit(this.unit, target);
            this.minThreshold = unit.fromUnit(this.unit, minThreshold);
            this.minThreshold = unit.fromUnit(this.unit, maxThreshold);
            this.unit = unit;
        }

        /**
         * Changes the name of the sensor
         *
         * @param name the new name of the sensor
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the unit of distance
         *
         * @return the unit of distance
         */
        public DistanceUnit getUnit() {
            return unit;
        }

        /**
         * Gets the acceptable error range
         *
         * @return the threshold (acceptable error range)
         */
        public double getThreshold() {
            return maxThreshold - minThreshold;
        }

        /**
         * @return the minimum threshold
         */
        public double getMinThreshold() {
            return minThreshold;
        }

        /**
         * @return the maximum threshold
         */
        public double getMaxThreshold() {
            return maxThreshold;
        }

        /**
         * @return the target distance
         */
        public double getTarget() {
            return target;
        }

        /**
         * @return the name of the sensor
         */
        public String getName() {
            return this.name;
        }
    }

    /**
     * Returns whether a given DistanceTarget has been reached
     */
    boolean targetReached(DistanceTarget target);

    /**
     * Adds a DistanceTarget.
     */
    void addTarget(DistanceTarget target);

    /**
     * Adds an List of DistanceTargets to the targets associated with this device.
     */
    void addTargets(List<DistanceTarget> targets);

    /**
     * Checks all targets currently associated with this device and returns a {@code Map}
     * with the results.
     *
     * @return The results of the checking.
     */
    Map<DistanceTarget, Boolean> checkAllTargets();

}
