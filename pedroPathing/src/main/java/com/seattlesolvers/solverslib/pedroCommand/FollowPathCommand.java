package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.pathgen.Path;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;


// Thanks Powercube from Watt-sUP 16166, we copied verbatim

/**
 * Allows you to run a PathChain by scheduling it.
 * holdEnd is set to true by default, so you only need to give it your instance of follower and the PathChain to follow.
 * To run a PathChain instead of a Path, see {@link FollowPathChainCommand}.

 * @author Arush - FTC 23511
 * @author Saket - FTC 23511
 */
public class FollowPathCommand extends CommandBase {

    private final Follower follower;
    private final Path path;
    private boolean holdEnd = true;

    public FollowPathCommand(Follower follower, Path path) {
        this.follower = follower;
        this.path = path;
    }

    public FollowPathCommand(Follower follower, Path path, boolean holdEnd) {
        this.follower = follower;
        this.path = path;
        this.holdEnd = holdEnd;
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

    @Override
    public void initialize() {
        follower.followPath(path, holdEnd);
    }

    @Override
    public boolean isFinished() {
        return !follower.isBusy();
    }
}