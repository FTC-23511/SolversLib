package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


/**
 * A command that calls {@link Follower#turn(double, boolean)}
 *
 * @author Arush - FTC 23511
 */
public class TurnCommand extends CommandBase {
    private final Follower follower;
    private final double angle;
    private final boolean isLeft;

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
        follower.turn(angle, isLeft);
    }

    @Override
    public boolean isFinished() {
        return !follower.isBusy();
    }
}