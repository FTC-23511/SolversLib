package org.firstinspires.ftc.teamcode.PedroCommandSample;



import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.RunCommand;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.pedroCommand.HoldPointCommand;

@Autonomous
public class HoldPointSample extends CommandOpMode {
    Follower follower;

    Pose pose = new Pose(
            72, 72, 90
    );

    @Override
    public void initialize() {
        super.reset();

        schedule(
                // Updates follower to follow path
                new RunCommand(() -> follower.update()),

                new HoldPointCommand(follower, new Pose(0, 4, 0), false),
                new HoldPointCommand(follower, pose, true)
        );
    }

    @Override
    public void run() {
        super.run();
    }
}