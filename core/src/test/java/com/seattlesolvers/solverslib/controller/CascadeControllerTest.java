package com.seattlesolvers.solverslib.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CascadeControllerTest {
    private CascadeController m_controller;

    @Before
    public void setUp() {
        m_controller = new CascadeController(
                new PIDController(1, 0, 0),
                new PIDController(1, 0, 0)
        );
    }

    @Test
    public void measuredVelocityMatchesPositionDelta() throws InterruptedException {
        m_controller.setSetPoints(100, 0);
        m_controller.calculate(0);
        Thread.sleep(20);
        m_controller.calculate(1);

        // moved +1 over the measured period, so velocity must be +1 / period
        assertEquals(1 / m_controller.getPeriod(), m_controller.getMeasuredVel(), 1e-6);
        assertTrue(m_controller.getMeasuredVel() > 0);
    }

    @Test
    public void noNaNOnFirstCycle() {
        m_controller.setSetPoint(50);
        double out = m_controller.calculate(0);

        assertFalse(Double.isNaN(m_controller.getMeasuredVel()));
        assertFalse(Double.isNaN(out));
    }

    @Test
    public void outputDrivesTowardSetpoint() throws InterruptedException {
        m_controller.setSetPoints(100, 0);
        m_controller.calculate(0);
        Thread.sleep(20);
        double out = m_controller.calculate(0);

        // stationary and below the setpoint: the cascade must command positive output
        assertTrue(out > 0);
    }
}
