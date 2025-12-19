package com.seattlesolvers.solverslib.hardware;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class SensorDigitalDevice {
    private final ElapsedTime timer;
    public final DigitalChannel digitalChannel;
    private double threshold;
    private boolean debouncedState;
    private boolean lastState;

    public SensorDigitalDevice(DigitalChannel digitalChannel, double threshold) {
        timer = new ElapsedTime();
        this.digitalChannel = digitalChannel;
        this.threshold = Math.max(0, threshold);
        this.digitalChannel.setMode(DigitalChannel.Mode.INPUT);

        this.debouncedState = false;
        this.lastState = false;
    }

    public SensorDigitalDevice(DigitalChannel digitalChannel) {
        this(digitalChannel, 0);
    }

    public SensorDigitalDevice(@NonNull HardwareMap hardwareMap, String name) {
        this(hardwareMap.get(DigitalChannel.class, name));
    }

    public SensorDigitalDevice(@NonNull HardwareMap hardwareMap, String name, double threshold) {
        this(hardwareMap.get(DigitalChannel.class, name), threshold);
    }

    public void setMode(DigitalChannel.Mode mode) {
        digitalChannel.setMode(mode);
    }

    public DigitalChannel.Mode getMode() {
        return digitalChannel.getMode();
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = Math.max(0, threshold);
    }

    public void update() {
        boolean state = digitalChannel.getState();

        if (threshold == 0) {
            debouncedState = state;
            lastState = state;
            return;
        }

        if (state) {
            if (!lastState) {
                timer.reset();
            }

            if (timer.milliseconds() >= threshold) {
                debouncedState = true;
            }
        } else {
            debouncedState = false;
            timer.reset();
        }

        lastState = state;
    }


    public boolean isActive() {
        if (threshold == 0) {
            return digitalChannel.getState();
        }

        return debouncedState && digitalChannel.getState();
    }
}