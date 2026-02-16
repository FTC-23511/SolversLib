package com.seattlesolvers.solverslib.util;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class TelemetryData {
    private final Telemetry telemetry;

    private final Map<String, String> dataMap = new HashMap<>();

    public TelemetryData(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void addData(String caption, Object value) {
        telemetry.addData(caption, value);
        dataMap.put(caption, value.toString());
    }

    public void update() {
        if (!dataMap.isEmpty()) {
            telemetry.update();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                Log.v(entry.getKey(), entry.getValue());
            }

            dataMap.clear();
        }
    }
}
