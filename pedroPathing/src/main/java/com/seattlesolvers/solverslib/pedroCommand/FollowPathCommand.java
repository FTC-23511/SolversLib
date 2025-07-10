package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.pathgen.PathChain;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.Path;


// Thanks Powercube from Watt-sUP 16166, we copied verbatim

/**
 * Allows you to run a PathChain or a Path (which is then converted into a PathChain) by scheduling it.
 * holdEnd is set to true by default, so you only need to give it your instance of follower and the Path to follow.
 * <p>
 * To see an example usage of this command, look at <a href="https://github.com/FTC-23511/SolversLib/blob/master/examples/src/main/java/org/firstinspires/ftc/teamcode/PedroCommandSample/FollowPedroSample.java">https://github.com/FTC-23511/SolversLib/blob/master/examples/src/main/java/org/firstinspires/ftc/teamcode/PedroCommandSample/FollowPedroSample.java</a>
 *
 * @author Arush - FTC 23511
 * @author Saket - FTC 23511
 */
public class FollowPathCommand extends CommandBase {

    private final Follower follower;
    private final PathChain path;
    private boolean holdEnd;
    private double maxPower = 1.0;

    public FollowPathCommand(Follower follower, PathChain path) {
        this(follower, path, true);
    }

    public FollowPathCommand(Follower follower, PathChain path, boolean holdEnd) {
        this(follower, path, holdEnd, 1.0);
    }

    public FollowPathCommand(Follower follower, PathChain path, double maxPower) {
        this(follower, path, true, maxPower);
    }

    public FollowPathCommand(Follower follower, PathChain path, boolean holdEnd, double maxPower) {
        this.follower = follower;
        this.path = path;
        this.holdEnd = holdEnd;
        this.maxPower = maxPower;
    }

    public FollowPathCommand(Follower follower, Path path) {
        this(follower, path, true);
    }

    public FollowPathCommand(Follower follower, Path path, boolean holdEnd) {
        this(follower, path, holdEnd, 1.0);
    }

    public FollowPathCommand(Follower follower, Path path, double maxPower) {
        this(follower, path, true, maxPower);
    }

    public FollowPathCommand(Follower follower, Path path, boolean holdEnd, double maxPower) {
        this.follower = follower;
        this.path = new PathChain(path);
        this.holdEnd = holdEnd;
        this.maxPower = maxPower;
    }


    /**
     * Decides whether or not to make the robot maintain its position once the path ends.
     *
     * @param holdEnd If the robot should maintain its ending position
     * @return This command for compatibility in command groups
     */
    public FollowPathCommand setHoldEnd(boolean holdEnd) {
        this.holdEnd = holdEnd;
        return this;
    }

    /**
     * Decides whether or not to make the robot maintain its position once the path ends.
     *
     * @param maxPower If the robot should maintain its ending position
     * @return This command for compatibility in command groups
     */
    public FollowPathCommand setMaxPower(double maxPower) {
        this.maxPower = maxPower;
        return this;
    }

    @Override
    public void initialize() {
        follower.setMaxPower(maxPower);
        follower.followPath(path, holdEnd);
    }

    @Override
    public boolean isFinished() {
        return !follower.isBusy();
    }
}