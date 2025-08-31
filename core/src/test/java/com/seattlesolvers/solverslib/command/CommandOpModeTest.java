package com.seattlesolvers.solverslib.command;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CommandOpModeTest extends CommandOpMode {

    public static int x = 3;
    public static int x1 = 3;

    @Override
    public void initialize() {
        x = 3;
        Robot.enable();
        schedule(new CommandBase() {
            @Override
            public void execute() {
                x = 5;
            }
        });
    }

    @Override
    public void end() {
        if (x == 5) {
            x1 = x;
        }
    }

    @Test
    public void testRunOpMode() {
        initialize();
        run();
        end();
        assertEquals(5, x);
        assertEquals(5, x1);
        reset();
    }
}
