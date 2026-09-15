package org.firstinspires.ftc.teamcode.PedroCommandSample;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Example Pedro Pathing 3.0 constants. Replace these values with the ones from your own tuning
 * (see https://pedropathing.com/docs/pathing/tuning); the field names match the code that AutoTune generates.
 */
public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("lf");
        c.backLeftName.set("lr");
        c.frontRightName.set("rf");
        c.backRightName.set("rr");

        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);

        c.powerThreshold.set(0.01);
        c.manualBrakeMode.set(false);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.xPodOffset.set(-5.0);
        c.yPodOffset.set(0.5);
        c.offsetUnits.set(DistanceUnit.INCH);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
        // The end constraints (timeoutConstraint etc.) that decide when FollowPathCommand and HoldPointCommand finish
        // also go here, see https://pedropathing.com/docs/pathing/reference/endconstraints

        Controller primaryTranslationalForward = Controller.proportional(0.3);
        Controller secondaryTranslationalForward = Controller.proportional(0.1);
        Controller primaryTranslationalLateral = Controller.proportional(0.3);
        Controller secondaryTranslationalLateral = Controller.proportional(0.1);

        c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
        c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

        c.coast.set(Controller.proportionalFeedforward(0.010978350889324107));
        c.brake.set(Controller.proportionalFeedforward(0.008731598255925491));

        c.headingFeedback.set(Controller.proportional(5.258721785960744));
        c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05642143125655298, 0.0063829525363003695));

        c.linearBrakeCoefficients.set(Matrix.diag(0.10605894992901523, 0.08719146175596092));
        c.quadraticBrakeCoefficients.set(Matrix.diag(0.0014663966976606565, 0.0013837064502458813));

        c.maxAchievableForwardVelocity.set(72.72923108818539);
        c.maxAchievableStrafeVelocity.set(52.34323936525474);
        c.naturalForwardDeceleration.set(85.01144677379789);
        c.naturalStrafeDeceleration.set(104.49787535782846);
    });

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new Follower(
                new PinpointLocalizer(hardwareMap, localizerConfig),
                new Mecanum(hardwareMap, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}
