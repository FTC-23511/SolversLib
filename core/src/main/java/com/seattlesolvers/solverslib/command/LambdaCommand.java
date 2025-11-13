package com.seattlesolvers.solverslib.command;

import androidx.annotation.NonNull;

import com.seattlesolvers.solverslib.command.CommandBase;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Command builder without having to create a new class.
 * Analogous to lambda functions or closures.
 * <p>
 * Builder style:
 * <pre>
 * new LambdaCommand()
 *     .setInitialize(() -> drivetrain.resetEncoders())
 *     .setExecute(() -> drivetrain.pidTo(new Pose(100, 100))
 *     .setIsFinished(() -> drivetrain.getDistance() < 5)
 *     .setEnd(() -> drivetrain.stop()),
 *     .setName("DriveForward")
 *     .setRunWhenDisabled(false)
 * </pre>
 * Functional style:
 * <pre>
 * new LambdaCommand(
 *     () -> drivetrain.resetEncoders(),
 *     () -> drivetrain.pidTo(new Pose(100, 100)),
 *     () -> drivetrain.getDistance() < 5,
 *     interrupted -> drivetrain.stop(),
 *     "DriveForward",
 *     () -> false
 * );
 * </pre>
 * </p>
 *
 * @author Daniel - FTC 7854
 */
public class LambdaCommand extends CommandBase {
    // Name, requirements, and subsystem are in CommandBase
    protected Runnable m_initialize = () -> {};
    protected Runnable m_execute = () -> {};
    protected BooleanSupplier m_isFinished = () -> true;
    protected Consumer<Boolean> m_end = interrupted -> {};
    protected BooleanSupplier m_runWhenDisabled = () -> false;

    /**
     * Default constructor for builder
     */
    public LambdaCommand() {}

    /**
     * Constructor with everything specified
     */
    public LambdaCommand(Runnable initialize,
                         Runnable execute,
                         BooleanSupplier isFinished,
                         Consumer<Boolean> end,
                         String name,
                         BooleanSupplier runWhenDisabled) {
        this.m_initialize = initialize;
        this.m_execute = execute;
        this.m_isFinished = isFinished;
        this.m_end = end;
        this.m_name = name;
        this.m_runWhenDisabled = runWhenDisabled;
    }

    @Override
    public void initialize() {
        m_initialize.run();
    }

    @Override
    public void execute() {
        m_execute.run();
    }

    @Override
    public boolean isFinished() {
        return m_isFinished.getAsBoolean();
    }

    @Override
    public void end(boolean interrupted) {
        m_end.accept(interrupted);
    }

    @Override
    public boolean runsWhenDisabled() {
        return m_runWhenDisabled.getAsBoolean();
    }

    // Override to return self type
    @Override
    public final LambdaCommand addRequirements(Subsystem... requirements) {
        m_requirements.addAll(Arrays.asList(requirements));
        return this;
    }

    // Override to return self type
    @Override
    public LambdaCommand setName(String name) {
        m_name = name;
        return this;
    }

    // Override to return self type
    @Override
    public LambdaCommand setSubsystem(String subsystem) {
        this.m_subsystem = subsystem;
        return this;
    }

    /**
     * The initial subroutine of a command.  Called once when the command is initially scheduled.
     *
     * @param initialize initialize method to set
     * @return this object for chaining purposes
     */
    public LambdaCommand setInitialize(Runnable initialize) {
        this.m_initialize = initialize;
        return this;
    }

    /**
     * The main body of a command.  Called repeatedly while the command is scheduled.
     *
     * @param execute execute method to set
     * @return this object for chaining purposes
     */
    public LambdaCommand setExecute(Runnable execute) {
        this.m_execute = execute;
        return this;
    }

    /**
     * Whether the command has finished.  Once a command finishes, the scheduler will call its
     * end() method and un-schedule it.
     *
     * @param isFinished isFinished method to set
     * @return this object for chaining purposes
     */
    public LambdaCommand setIsFinished(BooleanSupplier isFinished) {
        this.m_isFinished = isFinished;
        return this;
    }

    /**
     * The action to take when the command ends.  Called when either the command finishes normally,
     * or when it interrupted/canceled.
     *
     * @param end end method to set. Should have interrupted (whether the command was
     *            interrupted/canceled) as the parameter.
     * @return this object for chaining purposes
     */
    public LambdaCommand setEnd(Consumer<Boolean> end) {
        this.m_end = end;
        return this;
    }

    /**
     * The action to take when the command ends.  Called when either the command finishes normally,
     * or when it interrupted/canceled.
     *
     * @param end end method to set. Ignores if command was interrupted
     * @return this object for chaining purposes
     */
    public LambdaCommand setEnd(Runnable end) {
        return setEnd(interrupted -> end.run());
    }

    /**
     * Whether the given command should run when the robot is disabled.  Override to return true
     * if the command should run when disabled.
     *
     * @param runWhenDisabled runWhenDisabled supplier to set
     * @return this object for chaining purposes
     */
    public LambdaCommand setRunWhenDisabled(BooleanSupplier runWhenDisabled) {
        this.m_runWhenDisabled = runWhenDisabled;
        return this;
    }

    /**
     * Whether the given command should run when the robot is disabled. Override to return true
     * if the command should run when disabled.
     *
     * @param runWhenDisabled runWhenDisabled boolean to set
     * @return this object for chaining purposes
     */
    public LambdaCommand setRunWhenDisabled(boolean runWhenDisabled) {
        return setRunWhenDisabled(() -> runWhenDisabled);
    }

    /**
     * Factory method to create a LambdaCommand from an existing command.
     * Useful when you need to change a little bit from the original.
     *
     * @return a new LambdaCommand instance
     */
    public static LambdaCommand from(Command command) {
        return new LambdaCommand(
                command::initialize,
                command::execute,
                command::isFinished,
                command::end,
                command.getName(),
                command::runsWhenDisabled
        );
    }
}
