package org.firstinspires.ftc.teamcode.PedroCommandSample;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.util.TelemetryData;

@Autonomous
public class PedroAutoSample extends CommandOpMode {
    private Follower follower;
    TelemetryData telemetryData = new TelemetryData(telemetry);

    // Poses (headings in degrees)
    private final PoseFactory p = PoseFactory.degrees();
    private final Pose startPose = p.of(9, 111, -90);
    private final Pose scorePose = p.of(16, 128, -45);
    private final Pose pickup1Pose = p.of(30, 121, 0);
    private final Pose pickup2Pose = p.of(30, 131, 0);
    private final Pose pickup3Pose = p.of(45, 128, 90);
    private final Pose parkPose = p.of(68, 96, -90);

    // Paths
    private Path scorePreload, grabPickup1, grabPickup2, grabPickup3;
    private Path scorePickup1, scorePickup2, scorePickup3, park;

    public void buildPaths() {
        scorePreload = line(startPose, scorePose)
                .linear(startPose, scorePose);

        grabPickup1 = line(scorePose, pickup1Pose)
                .linear(scorePose, pickup1Pose);

        scorePickup1 = line(pickup1Pose, scorePose)
                .linear(pickup1Pose, scorePose);

        grabPickup2 = line(scorePose, pickup2Pose)
                .linear(scorePose, pickup2Pose);

        scorePickup2 = line(pickup2Pose, scorePose)
                .linear(pickup2Pose, scorePose);

        grabPickup3 = line(scorePose, pickup3Pose)
                .linear(scorePose, pickup3Pose);

        scorePickup3 = line(pickup3Pose, scorePose)
                .linear(pickup3Pose, scorePose);

        park = curve(
                scorePose,
                p.of(68, 110, 0), // Control point
                parkPose
        ).linear(scorePose, parkPose);
    }

    // Mechanism commands - replace these with your actual subsystem commands
    private InstantCommand openOuttakeClaw() {
        return new InstantCommand(() -> {
            // Example: outtakeSubsystem.openClaw();
        });
    }

    private InstantCommand grabSample() {
        return new InstantCommand(() -> {
            // Example: intakeSubsystem.grabSample();
        });
    }

    private InstantCommand scoreSample() {
        return new InstantCommand(() -> {
            // Example: outtakeSubsystem.scoreSample();
        });
    }

    private InstantCommand level1Ascent() {
        return new InstantCommand(() -> {
            // Example: hangSubsystem.level1Ascent();
        });
    }

    @Override
    public void initialize() {
        super.reset();

        // Initialize follower
        follower = Constants.createFollower(hardwareMap);
        follower.setPose(startPose);
        buildPaths();

        // Create the autonomous command sequence
        SequentialCommandGroup autonomousSequence = new SequentialCommandGroup(
                // Score preload
                new FollowPathCommand(follower, scorePreload),
                openOuttakeClaw(),
                new WaitCommand(1000), // Wait 1 second

                // First pickup cycle
                new FollowPathCommand(follower, grabPickup1).setGlobalMaxPower(0.5), // Limits this and all future paths (of this follower)
                                                                                     // to 50% of max speed, unless a custom maxPower is given
                grabSample(),
                new FollowPathCommand(follower, scorePickup1),
                scoreSample(),

                // Second pickup cycle
                new FollowPathCommand(follower, grabPickup2),
                grabSample(),
                new FollowPathCommand(follower, scorePickup2, 1.0), // Overrides maxPower to 100% for this path only
                scoreSample(),

                // Third pickup cycle
                new FollowPathCommand(follower, grabPickup3),
                grabSample(),
                new FollowPathCommand(follower, scorePickup3),
                scoreSample(),

                // Park
                new FollowPathCommand(follower, park, false), // park with holdEnd false
                level1Ascent()
        );

        // Schedule the autonomous sequence
        schedule(autonomousSequence);
    }

    @Override
    public void run() {
        super.run();

        // The follower needs to be updated every loop
        follower.update();

        telemetryData.addData("X", follower.pose().x());
        telemetryData.addData("Y", follower.pose().y());
        telemetryData.addData("Heading", follower.pose().heading());
        telemetryData.update();
    }
}
