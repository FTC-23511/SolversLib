package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


/**
 * A command that calls {@link Follower#holdPoint(Pose)}
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
        follower.turnTo(angle);
    }

    @Override
    public boolean isFinished() {
        return !follower.isBusy();
    }
}