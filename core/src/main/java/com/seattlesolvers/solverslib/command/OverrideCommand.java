package com.seattlesolvers.solverslib.command;

/**
 * A command whose lifecycle methods do nothing by default, meant to be used with Java's anonymous
 * class syntax. Only the methods that need custom behavior have to be overridden, without lambdas,
 * and extra member variables can be declared inside the anonymous class. Unless
 * {@link #isFinished()} is overridden, the command never finishes on its own.
 *
 * @see CommandBase
 * @see FunctionalCommand
 *
 * @author Miguel - FTC 21036
 */
public class OverrideCommand extends CommandBase {
    /**
     * Constructs a command that does nothing until its methods are overridden.
     *
     * @param requirements subsystems the command requires
     */
    public OverrideCommand(Subsystem... requirements) {
        addRequirements(requirements);
    }
}
