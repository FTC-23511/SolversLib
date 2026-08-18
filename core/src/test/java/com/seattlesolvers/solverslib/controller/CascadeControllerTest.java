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
    public void measuredVelocityNegativeForNegativeMotion() throws InterruptedException {
        m_controller.setSetPoints(-100, 0);
        m_controller.calculate(0);
        Thread.sleep(20);
        m_controller.calculate(-1);

        assertEquals(-1 / m_controller.getPeriod(), m_controller.getMeasuredVel(), 1e-6);
        assertTrue(m_controller.getMeasuredVel() < 0);
    }

    @Test
    public void measuredVelocityTracksConstantMotion() throws InterruptedException {
        double v = 250;
        m_controller.setSetPoints(10000, 0);
        long start = System.nanoTime();
        for (int i = 0; i < 40; i++) {
            m_controller.calculate(v * ((System.nanoTime() - start) / 1E9));
            if (i % 5 == 0) {
                // immediate back-to-back call must not corrupt the estimate
                m_controller.calculate(v * ((System.nanoTime() - start) / 1E9));
            }
            Thread.sleep(5);
        }
        assertEquals(v, m_controller.getMeasuredVel(), v * 0.05);
    }

    @Test
    public void noNaNOnFirstCycle() {
        m_controller.setSetPoint(50);
        double out = m_controller.calculate(0);

        assertFalse(Double.isNaN(m_controller.getMeasuredVel()));
        assertFalse(Double.isNaN(out));
    }

    @Test
    public void firstCycleStartingAwayFromZeroHasNoVelocitySpike() throws InterruptedException {
        // controller constructed while the mechanism sits at position 500
        m_controller.setSetPoints(500, 0);
        m_controller.calculate(500);
        Thread.sleep(20);
        m_controller.calculate(500);

        // stationary mechanism must read zero velocity, not (500 - 0) / period
        assertEquals(0, m_controller.getMeasuredVel(), 1e-9);
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

    @Test
    public void cascadeGainsCompose() {
        CascadeController controller = new CascadeController(
                new PIDController(2, 0, 0),
                new PIDController(0.5, 0, 0)
        );
        controller.setSetPoints(10, 0);

        // stationary at 0: primary commands 2 * 10, secondary scales by 0.5
        assertEquals(10, controller.calculate(0), 1e-9);
    }

    @Test
    public void velocitySetPointFeedsForward() throws InterruptedException {
        CascadeController controller = new CascadeController(
                new PIDController(0, 0, 0),
                new PIDController(1, 0, 0)
        );
        controller.setSetPoints(0, 3.5);
        controller.calculate(0);
        Thread.sleep(10);

        // stationary with zero position error: output is exactly the velocity setpoint
        assertEquals(3.5, controller.calculate(0), 1e-9);
    }

    @Test
    public void convergesToSetPointAndRetargets() throws InterruptedException {
        CascadeController controller = new CascadeController(
                new PIDController(3, 0, 0),
                new PIDController(0.02, 0, 0)
        );
        double[] state = {0, 0}; // position, velocity
        controller.setSetPoints(50, 0);
        simulatePlant(controller, state, 2.5);
        assertEquals(50, state[0], 2);

        controller.setSetPoints(-20, 0);
        simulatePlant(controller, state, 2.5);
        assertEquals(-20, state[0], 2);
    }

    /**
     * Runs the controller against a simple first-order motor model: output is
     * clamped to [-1, 1] power, velocity lags the commanded velocity, position
     * integrates velocity. Asserts every output is finite.
     */
    private void simulatePlant(CascadeController controller, double[] state, double seconds)
            throws InterruptedException {
        final double maxVelocity = 100;
        final double lag = 0.1;
        long start = System.nanoTime();
        long last = start;
        while ((System.nanoTime() - start) / 1E9 < seconds) {
            long now = System.nanoTime();
            double dt = (now - last) / 1E9;
            last = now;

            double out = controller.calculate(state[0]);
            assertTrue("controller output must be finite", Double.isFinite(out));

            double power = Math.max(-1, Math.min(1, out));
            state[1] += (power * maxVelocity - state[1]) * Math.min(1, dt / lag);
            state[0] += state[1] * dt;
            Thread.sleep(4);
        }
    }

    @Test
    public void resetClearsCascadeAndChildState() throws InterruptedException {
        // pure-integral secondary winds up while a velocity setpoint is held
        CascadeController controller = new CascadeController(
                new PIDController(1, 0, 0),
                new PIDFController(0, 1, 0, 0)
        );
        controller.setSetPoints(0, 10);
        for (int i = 0; i < 6; i++) {
            controller.calculate(0);
            Thread.sleep(5);
        }
        assertTrue(Math.abs(controller.calculate(0)) > 0.01);

        controller.reset();
        assertEquals(0, controller.getMeasuredVel(), 0);

        // after reset with zero setpoints, no wound-up integral may leak through
        controller.setSetPoints(0, 0);
        assertEquals(0, controller.calculate(0), 1e-9);
        Thread.sleep(10);
        assertEquals(0, controller.calculate(0), 1e-9);
    }

    @Test
    public void respectsMaxOutput() throws InterruptedException {
        m_controller.setMaxOutput(0.25);
        m_controller.setSetPoints(1000, 0);
        m_controller.calculate(0);
        Thread.sleep(10);
        double out = m_controller.calculate(0);

        assertEquals(0.25, out, 1e-9);
    }

    @Test
    public void setSetPointClearsVelocitySetPoint() {
        m_controller.setSetPoints(10, 5);
        assertEquals(5, m_controller.getVelocityError(), 1e-9);

        m_controller.setSetPoint(20);
        assertEquals(20, m_controller.getSetPoint(), 0);
        assertEquals(0, m_controller.getVelocityError(), 1e-9);
    }

    @Test
    public void staysFiniteUnderRapidCalls() {
        m_controller.setSetPoints(5, 0);
        for (int i = 0; i < 20000; i++) {
            double out = m_controller.calculate(i * 0.001);
            assertTrue(Double.isFinite(out));
            assertTrue(Double.isFinite(m_controller.getMeasuredVel()));
        }
    }
}
