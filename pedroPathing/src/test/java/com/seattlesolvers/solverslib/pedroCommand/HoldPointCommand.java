package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.seattlesolvers.solverslib.command.CommandBase;

/**
 * A command that calls {@link Follower#hold(Pose, boolean)}. It finishes immediately; the follower
 * keeps holding the point until it is given something else to do.
 *
 * @author Arush - FTC 23511
 */
public class HoldPointCommand extends CommandBase {
    private final Follower follower;
    private final Pose pose;
    private final boolean isFieldCentric;

    /**
     * Moves robot to a new {@link Pose} that is either field or robot centric
     * @param follower The follower object
     * @param pose The pose that the robot should go to (see isFieldCentric parameter)
     *             The following robot centric movements are true assuming that the robot is facing forwards to the long side on the submersible:
     *             {@link Pose#x()} -Y is forwards, +Y is backwards
     *             {@link Pose#y()} +X is left, -X is right
     *             {@link Pose#heading()} Heading is in radians, +heading turns left and -heading turns right
     * @param isFieldCentric Whether the move should be field centric or robot centric (based off the follower's position at the time of scheduling the command)
     */
    public HoldPointCommand(Follower follower, Pose pose, boolean isFieldCentric) {
        this.follower = follower;
        this.pose = pose;
        this.isFieldCentric = isFieldCentric;
    }

    @Override
    public void initialize() {
        Pose target = isFieldCentric ? pose : follower.pose().plus(pose);

        // Pedro 2's holdPoint(Pose) used hold scaling; Pedro 3's hold(Pose) does not
        follower.hold(target, true);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}