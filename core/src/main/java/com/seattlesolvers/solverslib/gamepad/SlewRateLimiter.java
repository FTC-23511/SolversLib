package com.seattlesolvers.solverslib.gamepad;
import androidx.core.math.MathUtils;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Smooths out gamepad joystick inputs and limits the rate of change of the inputs.
 * Credit to FTC 16379 Kookybotz for this code
 */
public class SlewRateLimiter {
    private double m_positiveRateLimit;
    private double m_negativeRateLimit;
    private final ElapsedTime m_timer;
    private double m_prevVal;
    private double m_prevTime;

    public SlewRateLimiter(double positiveRateLimit, double negativeRateLimit, double initialValue) {
        m_positiveRateLimit = positiveRateLimit;
        m_negativeRateLimit = negativeRateLimit;
        m_prevVal = initialValue;
        m_prevTime = 0;
        m_timer = new ElapsedTime();
    }

    public SlewRateLimiter(double rateLimit, double initalValue) {
        this(rateLimit, -rateLimit, initalValue);
    }

    public SlewRateLimiter(double rateLimit) {
        this(rateLimit, -rateLimit, 0);
    }

    public void updateRateLimit(double rateLimit) {
        m_positiveRateLimit = rateLimit;
        m_negativeRateLimit = -rateLimit;
    }

    public double calculate(double input) {
        double currentTime = m_timer.seconds();
        double elapsedTime = currentTime - m_prevTime;
        m_prevVal +=
                MathUtils.clamp(
                        input - m_prevVal,
                        m_negativeRateLimit * elapsedTime,
                        m_positiveRateLimit * elapsedTime);
        m_prevTime = currentTime;
        return m_prevVal;
    }
}