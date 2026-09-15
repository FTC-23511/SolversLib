package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


/**
 * A command that turns the robot in place by a given angle, relative to its heading when the command is initialized.
 * <p>
 * Pedro Pathing 3.0 no longer has {@code Follower#turn}, so this holds the follower's current position with the new
 * heading using {@link Follower#hold(Pose)}. The command finishes once the heading error is within the heading
 * tolerance ({@link #DEFAULT_HEADING_TOLERANCE} by default, see {@link #setHeadingTolerance(double)}), and the
 * follower keeps holding the pose afterwards.
 * <p>
 * Only the heading error is checked, not the angular velocity, so the command can finish while the robot is still
 * rotating through the target. There is no timeout: if the heading controller cannot get within the tolerance the
 * command never finishes, so loosen the tolerance or add {@code withTimeout(...)} in autonomous routines.
 *
 * @author Arush - FTC 23511
 */
public class TurnCommand extends CommandBase {
    /**
     * The default heading tolerance, 0.01 radians (about 0.57 degrees), the same default as Pedro Pathing 2.x's
     * turnHeadingErrorThreshold. If you tuned that FollowerConstant in 2.x, re-apply it with
     * {@link #setHeadingTolerance(double)}.
     */
    public static final double DEFAULT_HEADING_TOLERANCE = 0.01;

    private final Follower follower;
    private final double angle;
    private final boolean isLeft;
    private double headingTolerance = DEFAULT_HEADING_TOLERANCE;
    private double targetHeading;

    public TurnCommand(Follower follower, double angle, boolean isLeft) {
        this(follower, angle, isLeft, AngleUnit.RADIANS);
    }

    public TurnCommand(Follower follower, double angle, boolean isLeft, AngleUnit angleUnit) {
        this.follower = follower;
        this.angle = angleUnit.toRadians(angle);
        this.isLeft = isLeft;
    }

    /**
     * Sets how close the robot's heading must be to the target heading for the command to finish
     *
     * @param headingTolerance The heading tolerance in radians
     * @return This command for compatibility in command groups
     */
    public TurnCommand setHeadingTolerance(double headingTolerance) {
        return setHeadingTolerance(headingTolerance, AngleUnit.RADIANS);
    }

    /**
     * Sets how close the robot's heading must be to the target heading for the command to finish
     *
     * @param headingTolerance The heading tolerance
     * @param angleUnit The unit of the heading tolerance
     * @return This command for compatibility in command groups
     */
    public TurnCommand setHeadingTolerance(double headingTolerance, AngleUnit angleUnit) {
        this.headingTolerance = angleUnit.toRadians(headingTolerance);
        return this;
    }

    @Override
    public void initialize() {
        Pose current = follower.pose();

        if (isLeft) {
            targetHeading = current.heading() + angle;
        } else {
            targetHeading = current.heading() - angle;
        }

        follower.hold(current.withHeading(targetHeading));
        follower.algorithm().reset();
    }

    @Override
    public boolean isFinished() {
        return Math.abs(Angle.error(follower.pose().heading(), targetHeading)) < headingTolerance;
    }
}
