/*
 * ----------------------------------------------------------------------------
 *  Copyright (c) 2018-2019 FIRST. All Rights Reserved.
 *  Open Source Software - may be modified and shared by FRC teams. The code
 *  must be accompanied by the FIRST BSD license file in the root directory of
 *  the project.
 * ----------------------------------------------------------------------------
 */

package com.seattlesolvers.solverslib.command;

import static com.seattlesolvers.solverslib.command.CommandGroupBase.registerGroupedCommands;
import static com.seattlesolvers.solverslib.command.CommandGroupBase.requireUngrouped;

import java.util.function.BooleanSupplier;

/**
 * A command that runs another command repeatedly, restarting it when it ends, until this command is
 * interrupted. While this class does not extend {@link CommandGroupBase}, it is still considered a
 * CommandGroup, as it allows one to compose another command within it; the command instances that
 * are passed to it cannot be added to any other groups, or scheduled individually.
 *
 * <p>As a rule, CommandGroups require the union of the requirements of their component commands.
 *
 * @author Ryan
 * @author Arush (for the overloaded constructors)
 */
public class RepeatCommand extends CommandBase{

    protected final Command m_command;
    private int timesRepeated;
    private int maxRepeatTimes;
    private BooleanSupplier condition;

    /**
     * Creates a new RepeatCommand. Will run another command repeatedly, restarting it whenever it
     * ends, until this command is interrupted.
     *
     * @param command the command to run repeatedly
     */
    public RepeatCommand(Command command) {
        requireUngrouped(command);
        registerGroupedCommands(command);
        m_command = command;
        m_requirements.addAll(command.getRequirements());
    }

    /**
     * Creates a new overloaded RepeatCommand. Will run another command repeatedly, restarting it whenever it
     * ends, until this command is interrupted or a condition is met. Effectively acts as a repeat until.
     *
     * @param command the command to run repeatedly
     * @param condition the condition to end the command
     */
    public RepeatCommand(Command command, BooleanSupplier condition) {
        this.condition = condition;

        requireUngrouped(command);
        registerGroupedCommands(command);
        m_command = command;
        m_requirements.addAll(command.getRequirements());
    }

    /**
     * Creates a new overloaded RepeatCommand. Runs another command maxRepeatTimes amount of times, and ends when
     * it has repeated enough times or if this command is interrupted.
     *
     * @param command the command to run repeatedly
     * @param maxRepeatTimes the number of times to repeat the command (has to be greater than 0)
     */
    public RepeatCommand(Command command, int maxRepeatTimes) {
        if (maxRepeatTimes <= 0) {
            throw new IllegalArgumentException("RepeatCommands' maxRepeatTimes cannot be negative or zero!");
        }
        this.maxRepeatTimes = maxRepeatTimes;

        requireUngrouped(command);
        registerGroupedCommands(command);
        m_command = command;
        m_requirements.addAll(command.getRequirements());
    }

    @Override
    public void initialize() {
        timesRepeated = 0;
        m_command.initialize();
    }

    @Override
    public void execute() {
        m_command.execute();

        if (m_command.isFinished()) {
            m_command.end(false);
            timesRepeated++;

            if (!this.isFinished()) {
                m_command.initialize();
            }
        }
    }

    @Override
    public boolean isFinished() {
        return (maxRepeatTimes > 0 && timesRepeated >= maxRepeatTimes) || (condition != null && condition.getAsBoolean());
    }

    @Override
    public void end(boolean interrupted) {
        if (m_command.isScheduled() || !m_command.isFinished()) {
            m_command.end(interrupted);
        }
    }

    @Override
    public boolean runsWhenDisabled() {
        return m_command.runsWhenDisabled();
    }
}