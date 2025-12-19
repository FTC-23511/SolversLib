package com.seattlesolvers.solverslib.drivebase.swerve.coaxial;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.seattlesolvers.solverslib.controller.PIDFController;
import com.seattlesolvers.solverslib.geometry.Vector2d;
import com.seattlesolvers.solverslib.hardware.motors.CRServoEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.kinematics.wpilibkinematics.ChassisSpeeds;
import com.seattlesolvers.solverslib.util.MathUtils;

public class CoaxialSwerveModule {
    private final MotorEx motor;
    private final CRServoEx swervo;
    private double maxSpeed;
    private PIDFController swervoPIDF;
    // Angle that is tangential to the circle made by the 4 modules relative to the robot
    private final double tangentialAngle;
    private final double circumference;
    private Vector2d targetVelocity = new Vector2d();
    private double angleError = 0;
    public boolean wheelFlipped = false;

    /**
     * The constructor that sets up the swerve module object.
     *
     * @param motor the SolversMotor for the swerve module.
     * @param swervo the SolversAxonServo for the pod rotation, with the proper absolute encoder and angle offset so that the wheel is moving the robot forward when motor power is positive.
     * @param offset the offset of the center of the wheel/pod from the center of the robot, in inches.
     * @param maxSpeed the maximum linear speed of the wheel/pod in inches/second.
     * @param swervoPIDFCoefficients the coefficients for the swervo PIDF controller.
     */
    public CoaxialSwerveModule(MotorEx motor, CRServoEx swervo, Vector2d offset, double maxSpeed, PIDFCoefficients swervoPIDFCoefficients) {
        this.motor = motor;
        this.swervo = swervo;
        this.maxSpeed = maxSpeed;
        this.swervoPIDF = new PIDFController(swervoPIDFCoefficients);

        tangentialAngle = offset.angle();
        circumference = offset.magnitude() * Math.PI * 2;
    }

    /**
     * The main kinematics for robot centric module movements. Made so that it is easy to follow/understand: <a href="https://www.desmos.com/calculator/8sm94so6ud">see Desmos link</a>.
     *
     * @param target the target ChassisSpeeds for the robot to follow
     * @return returns vector for module to follow from target translational and rotational velocity parameters (NOT NORMALIZED).
     */
    public Vector2d calculateVectorRobotCentric(ChassisSpeeds target) {
        // Turning vector
        // See calculations for tangentialAngle and circumference in constructor, as they don't need to be recalculated every time
        double turningVectorMagnitude = circumference * target.omegaRadiansPerSecond / (Math.PI * 2);
        Vector2d turningVector = new Vector2d(Math.cos(tangentialAngle) * turningVectorMagnitude, Math.sin(tangentialAngle) * turningVectorMagnitude);

        // Final vector: adding turning and translational vectors
        return turningVector.plus(target.getTranslationalVector());
    }

    /**
     * Sets the target velocity
     * @param velocity the vector that represents the velocity the pod/wheel has to follow in inches/second.
     */
    public void setTargetVelocity(Vector2d velocity) {
        targetVelocity = velocity;
    }

    /**
     * Updates the module/hardware to follow the known, previous target velocity set in the object.
     */
    public void updateModule() {
        // Wheel flipping optimization (if its quicker to swap motor direction and rotate the pod less, then do that)
        wheelFlipped = false;
        angleError = MathUtils.normalizeRadians(MathUtils.normalizeRadians(targetVelocity.angle(), true) - swervo.getAbsoluteEncoder().getCurrentPosition(), false);
        if (Math.abs(angleError) > Math.PI/2) {
            angleError += Math.PI * -Math.signum(angleError);
            wheelFlipped = true;
        }

        // Set wheel speed
        if (wheelFlipped) {
            motor.set(-targetVelocity.magnitude() / maxSpeed * Math.cos(angleError));
        } else {
            motor.set(targetVelocity.magnitude() / maxSpeed * Math.cos(angleError));
        }

        // Set swervo speed for pod rotation
        swervo.set(swervoPIDF.calculate(0, angleError));
    }

    /**
     * Updates the module/hardware to follow the target velocity passed in the parameter.
     * @param velocity the velocity the pod/wheel should have, robot centric and in inches/second.
     */
    public void updateModuleWithVelocity(Vector2d velocity) {
        setTargetVelocity(velocity);
        updateModule();
    }

    public void stop() {
        swervo.stop();
        motor.stopMotor();
    }

    /**
     * Sets the hardware minimum caching tolerance/difference between writes
     * and the requested new power before it is actually written to the hardware
     * @param motorCachingTolerance the caching tolerance for the motor
     * @param swervoCachingTolerance the caching tolerance for the servo
     * @return this object for chaining purposes
     */
    public CoaxialSwerveModule setCachingTolerance(double motorCachingTolerance, double swervoCachingTolerance) {
        motor.setCachingTolerance(motorCachingTolerance);
        swervo.setCachingTolerance(swervoCachingTolerance);
        return this;
    }

    public String getPowerTelemetry() {
        return "Motor=" + MathUtils.round(motor.get(), 3) +
                ",Servo=" + MathUtils.round(swervo.get(), 3) +
                ",Absolute Encoder=" + MathUtils.round(swervo.getAbsoluteEncoder().getCurrentPosition(), 3);
    }

    public Vector2d getTargetVelocity() {
        return targetVelocity;
    }

    public void setSwervoPIDF(PIDFCoefficients pidfCoefficients) {
        this.swervoPIDF.setPIDF(pidfCoefficients.p, pidfCoefficients.i, pidfCoefficients.d, pidfCoefficients.f);
    }

    public double getAngleError() {
        return angleError;
    }
}