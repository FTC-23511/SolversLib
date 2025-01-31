package com.seattlesolvers.solverslib.command;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * As opposed to the general WPILib-style Robot paradigm, FTCLib also offers a command opmode
 * for individual opmodes.
 *
 * @author Jackson
 */
public abstract class CommandOpMode extends LinearOpMode {

    /**
     * Cancels all previous commands
     */
    public void reset() {
        CommandScheduler.getInstance().reset();
    }

    /**
     * Runs the {@link CommandScheduler} instance
     */
    public void run() {
        CommandScheduler.getInstance().run();
    }

    /**
     * Schedules {@link com.seattlesolvers.solverslib.command.Command} objects to the scheduler
     */
    public void schedule(Command... commands) {
        CommandScheduler.getInstance().schedule(commands);
    }

    /**
     * Registers {@link com.seattlesolvers.solverslib.command.Subsystem} objects to the scheduler
     */
    public void register(Subsystem... subsystems) {
        CommandScheduler.getInstance().registerSubsystem(subsystems);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();

        waitForStart();

        // run the scheduler
        while (!isStopRequested() && opModeIsActive()) {
            run();
        }

        end();
        reset();
    }

    public abstract void initialize();

    /**
     * Runs at the end (when opMode is no longer active) of CommandOpMode
     */

    public void end() { }

    public static void disable() {
        Robot.disable();
    }

    public static void enable() {
        Robot.enable();
    }


}
