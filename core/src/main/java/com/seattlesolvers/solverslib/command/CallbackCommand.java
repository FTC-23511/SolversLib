package com.seattlesolvers.solverslib.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Wrapper to easily add callbacks to a command
 * @author Daniel - FTC 7854
 */
public class CallbackCommand<T extends Command> implements Command {
    private final Map<BooleanSupplier, Runnable> whenRunnables = new HashMap<>();
    private final Map<BooleanSupplier, Command> whenCommands = new HashMap<>();
    private final Map<BooleanSupplier, Consumer<T>> whenConsumers = new HashMap<>();
    private final Map<Predicate<T>, Runnable> whenSelfRunnables = new HashMap<>();
    private final Map<Predicate<T>, Command> whenSelfCommands = new HashMap<>();
    private final Map<Predicate<T>, Consumer<T>> whenSelfConsumers = new HashMap<>();
    protected Set<Subsystem> m_requirements = new HashSet<>();
    private final T command;

    /**
     * Wrapper for adding custom callbacks to commands. This expects a single command,
     * so multiple commands need to be put in a CommandGroup first:
     * @param command the command to be schedules as uninterruptible
     * {@link SequentialCommandGroup}
     * {@link ParallelCommandGroup}
     */
    public CallbackCommand(T command) {
        this.command = command;
    }

    public final void addRequirements(Subsystem... requirements) {
        m_requirements.addAll(Arrays.asList(requirements));
    }

    /**
     * Adds a callback with a boolean supplier
     * @param condition Runs the runnable the first time this is true
     * @param action Callback to run
     * @return Itself for chaining purposes
     */
    @Override
    public CallbackCommand<T> when(BooleanSupplier condition, Runnable action) {
        whenRunnables.put(condition, action);
        return this;
    }

    /**
     * Adds a callback with a boolean supplier
     * @param condition Schedules the command the first time this is true
     * @param action Command to schedule
     * @return Itself for chaining purposes
     */
    @Override
    public CallbackCommand<T> when(BooleanSupplier condition, Command action) {
        whenCommands.put(condition, action);
        return this;
    }

    /**
     * Adds a callback with a boolean supplier
     * @param condition Schedules the command the first time this is true
     * @param action Consumer for using the inner command
     * @return Itself for chaining purposes
     */
    public CallbackCommand<T> whenSelf(BooleanSupplier condition, Consumer<T> action) {
        whenConsumers.put(condition, action);
        return this;
    }

    /**
     * Adds a callback with access to the inner command
     * @param condition Runs the runnable the first time this is true
     * @param action Callback to run
     * @return Itself for chaining purposes
     */
    public CallbackCommand<T> whenSelf(Predicate<T> condition, Runnable action) {
        whenSelfRunnables.put(condition, action);
        return this;
    }

    /**
     * Adds a callback with access to the inner command
     * @param condition Schedules the command the first time this is true
     * @param action Consumer for using the inner command
     * @return Itself for chaining purposes
     */
    public CallbackCommand<T> whenSelf(Predicate<T> condition, Command action) {
        whenSelfCommands.put(condition, action);
        return this;
    }

    /**
     * Adds a callback with access to the inner command
     * @param condition Schedules the command the first time this is true
     * @param action Command to schedule
     * @return Itself for chaining purposes
     */
    public CallbackCommand<T> whenSelf(Predicate<T> condition, Consumer<T> action) {
        whenSelfConsumers.put(condition, action);
        return this;
    }

    @Override
    public void initialize() {
        command.schedule();
    }

    @Override
    public void execute() {
        // Callbacks
        for (Iterator<Map.Entry<BooleanSupplier, Runnable>> it = whenRunnables.entrySet().iterator(); it.hasNext();) {
            Map.Entry<BooleanSupplier, Runnable> action = it.next();
            if (action.getKey().getAsBoolean()) {
                action.getValue().run();
                it.remove();
            }
        }
        for (Iterator<Map.Entry<BooleanSupplier, Command>> it = whenCommands.entrySet().iterator(); it.hasNext();) {
            Map.Entry<BooleanSupplier, Command> action = it.next();
            if (action.getKey().getAsBoolean()) {
                action.getValue().schedule();
                it.remove();
            }
        }
        for (Iterator<Map.Entry<BooleanSupplier, Consumer<T>>> it = whenConsumers.entrySet().iterator(); it.hasNext();) {
            Map.Entry<BooleanSupplier, Consumer<T>> action = it.next();
            if (action.getKey().getAsBoolean()) {
                action.getValue().accept(command);
                it.remove();
            }
        }

        // Self callbacks
        for (Iterator<Map.Entry<Predicate<T>, Runnable>> it = whenSelfRunnables.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Predicate<T>, Runnable> action = it.next();
            if (action.getKey().test(command)) {
                action.getValue().run();
                it.remove();
            }
        }
        for (Iterator<Map.Entry<Predicate<T>, Command>> it = whenSelfCommands.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Predicate<T>, Command> action = it.next();
            if (action.getKey().test(command)) {
                action.getValue().schedule();
                it.remove();
            }
        }
        for (Iterator<Map.Entry<Predicate<T>, Consumer<T>>> it = whenSelfConsumers.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Predicate<T>, Consumer<T>> action = it.next();
            if (action.getKey().test(command)) {
                action.getValue().accept(command);
                it.remove();
            }
        }
    }

    @Override
    public boolean isFinished() {
        return !CommandScheduler.getInstance().isScheduled(command);
    }

    @Override
    public Set<Subsystem> getRequirements() {
        return m_requirements;
    }
}
