package com.seattlesolvers.solverslib.pedroCommand;

import com.pedropathing.algorithm.Algorithm;
import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.Path;
import com.seattlesolvers.solverslib.command.CommandBase;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


// Thanks Powercube from Watt-sUP 16166, we copied verbatim

/**
 * Allows you to run a Pedro Pathing {@link Path} by scheduling it.
 * holdEnd is set to true by default, so you only need to give it your instance of follower and the Path to follow.
 * <p>
 * In Pedro Pathing 3.0 a {@link Path} is either a single curve or several paths combined with
 * {@code Paths.path(...)}, so this command covers what {@code PathChain} used to do in Pedro Pathing 2.x. The path
 * needs a heading interpolation (for example {@code Paths.line(a, b).linear(a, b)}), otherwise Pedro Pathing throws
 * when the path is followed.
 * <p>
 * holdEnd decides whether the follower holds the end pose or goes idle when the path finishes. It temporarily
 * overrides {@link Follower#holdEnd} while this command runs and restores the previous value when the command ends.
 * With holdEnd true, the command finishes once the follower is no longer busy: the robot has settled within the
 * algorithm's end constraints while holding the end pose, or {@link ForesightConfig#timeoutConstraint} (100 ms by
 * default) has elapsed there. With holdEnd false, it finishes as soon as the path ends and the follower goes idle.
 * If the command is interrupted while the path is still being followed, the follower holds its current pose (or
 * stops, with holdEnd false) instead of continuing along the path.
 * <p>
 * The optional maxPower limits the robot's speed for this path only, as a fraction (0, 1] of its maximum achievable
 * speed, by temporarily overriding {@link ForesightConfig#maxPathSpeed} while the path is followed. Unlike Pedro
 * Pathing 2.x's maxPower it does not cap motor power: only the target speed while coasting is limited, and braking,
 * error correction, holding and manual driving are unaffected. Using maxPower requires the follower to use the
 * {@link Foresight} algorithm; commands without a maxPower work with any algorithm.
 * <p>
 * To see an example usage of this command, look at <a href="https://github.com/FTC-23511/SolversLib/blob/master/examples/src/main/java/org/firstinspires/ftc/teamcode/PedroCommandSample/PedroAutoSample.java">https://github.com/FTC-23511/SolversLib/blob/master/examples/src/main/java/org/firstinspires/ftc/teamcode/PedroCommandSample/PedroAutoSample.java</a>
 *
 * @author Arush - FTC 23511
 * @author Saket - FTC 23511
 *
 */
public class FollowPathCommand extends CommandBase {
    /**
     * The global max powers given to {@link #setGlobalMaxPower(double)}, remembered per follower so that they last as
     * long as the follower does (normally the OpMode) without permanently changing a {@link ForesightConfig}, which is
     * usually a static field shared between OpModes.
     */
    private static final Map<Follower, Double> globalMaxPowers = Collections.synchronizedMap(new WeakHashMap<>());

    private final Follower follower;
    private final Path path;
    private final boolean holdEnd;
    private double maxPower = Double.NaN;
    private double globalMaxPower = Double.NaN;
    private boolean previousHoldEnd;

    public FollowPathCommand(Follower follower, Path path) {
        this(follower, path, true);
    }

    public FollowPathCommand(Follower follower, Path path, boolean holdEnd) {
        this.follower = follower;
        this.path = path;
        this.holdEnd = holdEnd;
    }

    public FollowPathCommand(Follower follower, Path path, double maxPower) {
        this(follower, path, true, maxPower);
    }

    public FollowPathCommand(Follower follower, Path path, boolean holdEnd, double maxPower) {
        this(follower, path, holdEnd);
        this.maxPower = validateMaxPower(maxPower, "maxPower");
    }

    /**
     * Sets the Global Maximum Power for the follower when this command is initialized: it applies to this path and to
     * every later FollowPathCommand for the same follower that is not given its own maxPower, and it overwrites the
     * maxPower given in the constructor. Pass 1.0 to remove the limit again.
     * <p>
     * The global max power is remembered per follower, so it lasts as long as the follower does (normally the OpMode)
     * and does not change your {@link ForesightConfig}. It only affects paths followed through FollowPathCommand.
     * Like maxPower, it requires the follower to use the {@link Foresight} algorithm.
     *
     * @param globalMaxPower The new globalMaxPower, as a fraction (0, 1] of the robot's maximum achievable speed
     * @return This command for compatibility in command groups
     */
    public FollowPathCommand setGlobalMaxPower(double globalMaxPower) {
        this.globalMaxPower = validateMaxPower(globalMaxPower, "globalMaxPower");
        this.maxPower = this.globalMaxPower;
        return this;
    }

    @Override
    public void initialize() {
        if (!Double.isNaN(globalMaxPower)) {
            globalMaxPowers.put(follower, globalMaxPower);
        }

        double pathMaxPower = maxPower;

        if (Double.isNaN(pathMaxPower)) {
            Double rememberedGlobalMaxPower = globalMaxPowers.get(follower);

            if (rememberedGlobalMaxPower != null) {
                pathMaxPower = rememberedGlobalMaxPower;
            }
        }

        Path pathToFollow = path;

        if (!Double.isNaN(pathMaxPower)) {
            pathToFollow = path.with(foresightConfig().maxPathSpeed.at(pathMaxPower));
        }

        previousHoldEnd = follower.holdEnd.get();
        follower.holdEnd.set(holdEnd);
        follower.follow(pathToFollow);
    }

    @Override
    public void end(boolean interrupted) {
        if (interrupted && follower.following()) {
            if (holdEnd) {
                follower.hold(follower.pose());
            } else {
                follower.stop();
            }
        }

        follower.holdEnd.set(previousHoldEnd);
    }

    @Override
    public boolean isFinished() {
        // Follower#isBusy() clears once the robot has settled at the end of the path (or the algorithm's hold
        // timeout, 100 ms by default, elapsed) while holding the end pose. With holdEnd false the follower goes idle
        // instead and the algorithm never clears its busy flag, so also finish once the follower is no longer
        // following or holding.
        return !follower.isBusy() || !(follower.following() || follower.holding());
    }

    private double validateMaxPower(double value, String name) {
        if (Double.isNaN(value) || value <= 0) {
            throw new IllegalArgumentException(name + " must be in (0, 1], but was " + value);
        }

        // Fail when the command is created rather than in the middle of an auto if the algorithm cannot limit speed
        foresightConfig();

        return value;
    }

    private ForesightConfig foresightConfig() {
        Algorithm algorithm = follower.algorithm();

        if (!(algorithm instanceof Foresight)) {
            throw new UnsupportedOperationException(
                    "FollowPathCommand's maxPower requires the follower to use Pedro Pathing's Foresight algorithm "
                            + "(the follower's algorithm is " + algorithm + "). "
                            + "Limit the speed through your algorithm's own config instead.");
        }

        return ((Foresight) algorithm).config;
    }
}
