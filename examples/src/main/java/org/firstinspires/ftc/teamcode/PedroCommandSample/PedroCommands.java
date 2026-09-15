package org.firstinspires.ftc.teamcode.PedroCommandSample;



import com.pedropathing.api.Paths;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;

import com.pedropathing.paths.Path;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.RunCommand;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.pedroCommand.HoldPointCommand;
import com.seattlesolvers.solverslib.pedroCommand.TurnCommand;
import com.seattlesolvers.solverslib.pedroCommand.TurnToCommand;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

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

        Pose start = new Pose(0, 0, Math.toRadians(0));
        Pose end = new Pose(16, 28, Math.toRadians(90));
        path = Paths.line(start, end).linear(start, end);

        schedule(
                // Updates follower to follow path
                new RunCommand(() -> follower.update()),

                // HoldPointCommand
                new HoldPointCommand(follower, new Pose(0, 4, 0), false),
                new HoldPointCommand(follower, pose, true),

                // TurnCommand
                new TurnCommand(follower, Math.PI / 2, false),
                new TurnCommand(follower, 90.0, true, AngleUnit.DEGREES),

                // TurnToCommand
                new TurnToCommand(follower, Math.PI / 2),
                new TurnToCommand(follower, 90.0, AngleUnit.DEGREES),

                // FollowPathCommand
                new FollowPathCommand(follower, path),
                new FollowPathCommand(follower, path, true)
        );
    }

    @Override
    public void run() {
        super.run();
    }
}