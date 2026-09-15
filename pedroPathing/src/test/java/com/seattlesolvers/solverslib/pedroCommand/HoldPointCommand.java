package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.seattlesolvers.solverslib.command.CommandBase;

/**
 * A command that calls {@link Follower#hold(Pose)}, to hold a pose or make a small adjustment to the robot's position.
 * <p>
 * The command finishes once the follower is no longer busy, which Pedro Pathing decides the same way as at the end of
 * a path: the robot has settled within the algorithm's translational, heading and velocity end constraints, or
 * {@link com.pedropathing.algorithm.ForesightConfig#timeoutConstraint} has elapsed. That timeout defaults to 100 ms
 * and starts on the first follower update after the command starts, so for a pose that is far away the command ends
 * while the robot is still moving. Use a {@link FollowPathCommand} for longer moves, or raise the timeout constraint
 * if you need to wait for the robot to arrive. The follower keeps holding the pose after the command ends, until it
 * is given something else to do.
 * <p>
 * The pose is held at full correction power, without Pedro Pathing's hold-point scaling. In Pedro Pathing 2.x this
 * command finished immediately, used the follower's hold-point scaling, and ignored isFieldCentric because of a bug
 * (the pose was always treated as field centric).
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
     * @param pose The pose that the robot should hold (see isFieldCentric parameter). Heading is in radians.
     * @param isFieldCentric If true, the pose is an absolute pose on the field.
     *                       If false, the pose is relative to the robot's pose at the time the command is initialized:
     *                       {@link Pose#x()} +x is forwards, -x is backwards
     *                       {@link Pose#y()} +y is left, -y is right
     *                       {@link Pose#heading()} +heading turns left and -heading turns right
     */
    public HoldPointCommand(Follower follower, Pose pose, boolean isFieldCentric) {
        this.follower = follower;
        this.pose = pose;
        this.isFieldCentric = isFieldCentric;
    }

    @Override
    public void initialize() {
        Pose target = pose;

        if (!isFieldCentric) {
            target = follower.pose().compose(pose);
        }

        follower.hold(target);
        // Follower#hold(Pose) does not reset the algorithm like Follower#follow(Path) does, so reset it ourselves to
        // start with fresh controllers and a busy flag that clears once the robot has settled at the pose.
        follower.algorithm().reset();
    }

    @Override
    public boolean isFinished() {
        return !follower.isBusy();
    }
}
