package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


/**
 * A command that turns the robot in place to a field heading, holding its current position.
 * Pedro Pathing 3 removed {@code Follower.turnTo}, so this holds a rotated pose and finishes once
 * the heading error is below {@link TurnCommand#headingTolerance}.
 *
 * @author Arush - FTC 23511
 */
public class TurnToCommand extends CommandBase {
    private final Follower follower;
    private final double angle;

    public TurnToCommand(Follower follower, double angle) {
        this(follower, angle, AngleUnit.RADIANS);
    }

    public TurnToCommand(Follower follower, double angle, AngleUnit angleUnit) {
        this.follower = follower;
        this.angle = angleUnit.toRadians(angle);
    }

    @Override
    public void initialize() {
        Pose current = follower.pose();
        // Foresight normalizes heading error, so this takes the shorter way around
        follower.hold(current.withHeading(angle));
    }

    @Override
    public boolean isFinished() {
        return TurnCommand.headingReached(follower, angle);
    }
}