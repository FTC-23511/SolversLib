package com.seattlesolvers.solverslib.pedroCommand;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.Path;


// Thanks Powercube from Watt-sUP 16166, we copied verbatim

/**
 * Allows you to run a Path by scheduling it. In Pedro Pathing 3 a chain of paths is also a Path
 * (see {@code Paths.path(Path...)}), so there is no separate PathChain constructor.
 * holdEnd is set to true by default, so you only need to give it your instance of follower and the Path to follow.
 * <p>
 * holdEnd is written to {@link Follower#holdEnd} when the command starts, and stays set on the
 * follower afterwards. (A path modifier won't work instead: Pedro reverts path modifiers before it
 * reads holdEnd, so {@code path.with(follower.holdEnd.at(false))} has no effect.)
 * <p>
 * To see an example usage of this command, look at <a href="https://github.com/FTC-23511/SolversLib/blob/master/examples/src/main/java/org/firstinspires/ftc/teamcode/PedroCommandSample/PedroAutoSample.java">https://github.com/FTC-23511/SolversLib/blob/master/examples/src/main/java/org/firstinspires/ftc/teamcode/PedroCommandSample/PedroAutoSample.java</a>
 *
 * @author Arush - FTC 23511
 * @author Saket - FTC 23511
 *
 */
public class FollowPathCommand extends CommandBase {
    private final Follower follower;
    private final Path path;
    private final boolean holdEnd;

    public FollowPathCommand(Follower follower, Path path) {
        this(follower, path, true);
    }

    public FollowPathCommand(Follower follower, Path path, boolean holdEnd) {
        this.follower = follower;
        this.path = path;
        this.holdEnd = holdEnd;
    }

    @Override
    public void initialize() {
        follower.holdEnd.set(holdEnd);
        follower.follow(path);
    }

    @Override
    public boolean isFinished() {
        // Not isBusy(): Foresight only clears busy while holding, so with holdEnd = false it never clears
        return !follower.following();
    }
}