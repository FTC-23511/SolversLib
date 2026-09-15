package org.firstinspires.ftc.teamcode.PedroCommandSample;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.pedroCommand.HoldPointCommand;
import com.seattlesolvers.solverslib.pedroCommand.TurnCommand;
import com.seattlesolvers.solverslib.pedroCommand.TurnToCommand;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Shows every constructor of the Pedro commands, run one after another in a SequentialCommandGroup.
 * For a full autonomous, look at PedroAutoSample.
 */
@Autonomous
public class PedroCommands extends CommandOpMode {
    Follower follower;

    Pose pose = new Pose(
            72, 72, Math.toRadians(90)
    );

    Path path;

    @Override
    public void initialize() {
        super.reset();

        follower = Constants.createFollower(hardwareMap);

        path = line(
                new Pose(0, 0, Math.toRadians(0)),
                new Pose(16, 28, Math.toRadians(90))
        ).linear(Math.toRadians(0), Math.toRadians(90));

        schedule(new SequentialCommandGroup(
                // HoldPointCommand
                new HoldPointCommand(follower, new Pose(4, 0, 0), false), // Robot centric: 4 inches forwards
                new HoldPointCommand(follower, pose, true), // Field centric

                // TurnCommand
                new TurnCommand(follower, Math.PI / 2, false),
                new TurnCommand(follower, 90.0, true, AngleUnit.DEGREES),

                // TurnToCommand
                new TurnToCommand(follower, Math.PI / 2),
                new TurnToCommand(follower, 90.0, AngleUnit.DEGREES).setHeadingTolerance(1.0, AngleUnit.DEGREES),

                // FollowPathCommand
                new FollowPathCommand(follower, path),
                new FollowPathCommand(follower, path, true),
                new FollowPathCommand(follower, path, true, 1.0),
                new FollowPathCommand(follower, path, true, 1.0).setGlobalMaxPower(1.0)
        ));
    }

    @Override
    public void run() {
        super.run();

        // The follower needs to be updated every loop
        follower.update();
    }
}
