package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


/**
 * A command that turns the robot in place by a relative angle, holding its current position.
 * Pedro Pathing 3 removed {@code Follower.turn}, so this holds a rotated pose and finishes once
 * the heading error is below {@link #headingTolerance}.
 *
 * @author Arush - FTC 23511
 */
public class TurnCommand extends CommandBase {
    /**
     * Heading error, in radians, at which turn commands finish. Matches Pedro 2's
     * {@code turnHeadingErrorThreshold} default.
     */
    public static double headingTolerance = 0.01;

    private final Follower follower;
    private final double angle;
    private final boolean isLeft;
    private double targetHeading;

    public TurnCommand(Follower follower, double angle, boolean isLeft) {
        this(follower, angle, isLeft, AngleUnit.RADIANS);
    }

    public TurnCommand(Follower follower, double angle, boolean isLeft, AngleUnit angleUnit) {
        this.follower = follower;
        this.angle = angleUnit.toRadians(angle);
        this.isLeft = isLeft;
    }

    @Override
    public void initialize() {
        Pose current = follower.pose();
        targetHeading = current.heading() + (isLeft ? angle : -angle);
        follower.hold(current.withHeading(targetHeading));
    }

    @Override
    public boolean isFinished() {
        return headingReached(follower, targetHeading);
    }

    static boolean headingReached(Follower follower, double targetHeading) {
        return Math.abs(Angle.normalizeSigned(targetHeading - follower.pose().heading())) < headingTolerance;
    }
}