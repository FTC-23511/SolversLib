package com.seattlesolvers.solverslib.command;

/**
 * Schedules a Command as uninterruptible
 * @author Arush - FTC 23511
 */
public class UninterruptibleCommand extends CommandBase {
    private final Command command;

    /**
     * This expects a single command, so multiple commands need to be put in a
     * CommandGroup first:
     * @param command the command to be schedules as uninterruptible
     * {@link SequentialCommandGroup}
     * {@link ParallelCommandGroup}
     */
    public UninterruptibleCommand(Command command) {
        this.command = command;
    }

    @Override
    public void initialize() {
        command.schedule(false);
    }

    @Override
    public boolean isFinished() {
        return !CommandScheduler.getInstance().isScheduled(command);
    }
}
