package com.seattlesolvers.solverslib.p2p;

import com.seattlesolvers.solverslib.controller.Controller;
import com.seattlesolvers.solverslib.gamepad.SlewRateLimiter;
import com.seattlesolvers.solverslib.geometry.Pose2d;
import com.seattlesolvers.solverslib.geometry.Rotation2d;
import com.seattlesolvers.solverslib.geometry.Transform2d;
import com.seattlesolvers.solverslib.kinematics.wpilibkinematics.ChassisSpeeds;
import com.seattlesolvers.solverslib.util.MathUtils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class P2PController {
    public final Controller translationalController;
    public final Controller headingController;
    public final AngleUnit angleUnit;

    private Pose2d target;
    private Pose2d current;
    private Transform2d error;

    private SlewRateLimiter magnitudeLimiter = null;
    private SlewRateLimiter hLimiter = null;

    /**
     * The constructor for a P2PController object
     * @param translationalController the controller for x/y axis movement (field-centric)
     * @param headingController the controller for robot heading
     * @param angleUnit the angle of the heading controller and positions being passed to this object
     * @param start the starting pose of the robot
     * @param target the first target pose of the robot
     * @param positionalTolerance the positional tolerance allowed for this controller at which the robot is considered to be at the target
     * @param angularTolerance the positional tolerance allowed for this controller at which the robot is considered to be at the target, in the units specified in the constructor
     */
    public P2PController(Controller translationalController, Controller headingController, AngleUnit angleUnit, Pose2d start, Pose2d target, double positionalTolerance, double angularTolerance) {
        this.translationalController = translationalController;
        this.headingController = headingController;
        this.angleUnit = angleUnit;
        this.current = start;
        this.target = target;
        getError(); // updates error
        setTolerance(positionalTolerance, angularTolerance);
    }

    /**
     * A simplified constructor for a P2PController object.
     *
     * @param translationalController the controller for x/y axis movement (field-centric)
     * @param headingController the controller for robot heading
     * @param angleUnit the angle of the heading controller and positions being passed to this object
     * @param positionalTolerance the positional tolerance allowed for this controller at which the robot is considered to be at the target
     * @param angularTolerance the positional tolerance allowed for this controller at which the robot is considered to be at the target, in the units specified in the constructor
     */
    public P2PController(Controller translationalController, Controller headingController, AngleUnit angleUnit, double positionalTolerance, double angularTolerance) {
        this(translationalController, headingController, angleUnit, new Pose2d(), new Pose2d(), positionalTolerance, angularTolerance);
    }

    /**
     * The main method to calculate and return an output for robot movement
     * @param pv the last known position of the robot
     * @return field-centric chassis speeds/power
     */
    public ChassisSpeeds calculate(Pose2d pv) {
        current = pv;

        double errorX = target.getX() - current.getX();
        double errorY = target.getY() - current.getY();

        error = new Transform2d(
                new Pose2d(errorX, errorY, new Rotation2d(0)),
                new Pose2d()
        );

        double distanceToTarget = Math.hypot(errorX, errorY);
        double errorAngle = Math.atan2(errorY, errorX);
        double magnitudeVal = translationalController.calculate(0, distanceToTarget);

        if (magnitudeLimiter != null) {
            magnitudeVal = magnitudeLimiter.calculate(magnitudeVal);
        }

        double xVal = magnitudeVal * Math.cos(errorAngle);
        double yVal = magnitudeVal * Math.sin(errorAngle);

        double currentHeading = current.getRotation().getAngle(angleUnit);
        double targetHeading = target.getRotation().getAngle(angleUnit);

        double headingError = MathUtils.normalizeAngle(targetHeading - currentHeading, false, angleUnit);
        double headingVal = headingController.calculate(0, headingError);

        if (hLimiter != null) {
            headingVal = hLimiter.calculate(headingVal);
        }

        return new ChassisSpeeds(xVal, yVal, headingVal);
    }

    public P2PController setSlewRateLimiters(SlewRateLimiter magnitudeLimiter, SlewRateLimiter hLimiter) {
        this.magnitudeLimiter = magnitudeLimiter;
        this.hLimiter = hLimiter;
        return this;
    }

    /**
     * Sets the target pose
     *
     * @param sp The desired pose.
     */
    public void setTarget(Pose2d sp) {
        target = sp;
    }

    /**
     * @return The current target pose.
     */
    public Pose2d getTarget() {
        return target;
    }

    /**
     * Sets the error which is considered tolerable for use with {@link #atTarget()}.
     *
     * @param positionTolerance Position error which is tolerable.
     * @param angularTolerance Angular error which is tolerable, in the angle unit specified.
     */
    public void setTolerance(double positionTolerance, double angularTolerance) {
        translationalController.setTolerance(positionTolerance);
        headingController.setTolerance(angularTolerance);
    }

    /**
     * Gets tolerances of the translational and angular controllers
     *
     * @return the positional and angular tolerances of the controllers respectively
     */
    public double[] getTolerance() {
        return new double[]{translationalController.getTolerance()[0], headingController.getTolerance()[0]};
    }

    /**
     * Returns true if the error is within the tolerance set by the user through {@link #setTolerance}.
     *
     * @return Whether the error is within the acceptable bounds.
     */
    public boolean atTarget() {
        return translationalController.atSetPoint() && headingController.atSetPoint();
    }

    /**
     * Updates the internal object for error and returns it
     *
     * @return the positional and angular error
     */
    public Transform2d getError() {
        // Re-calculate simply for accessors
        double errorX = target.getX() - current.getX();
        double errorY = target.getY() - current.getY();
        double errorH = MathUtils.normalizeAngle(target.getRotation().getAngle(angleUnit) - current.getRotation().getAngle(angleUnit), false, angleUnit);

        error = new Transform2d(new Pose2d(errorX, errorY, new Rotation2d(errorH)), new Pose2d());
        return error;
    }
}