package com.seattlesolvers.solverslib.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.seattlesolvers.solverslib.command.button.GamepadButton;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys.Button;

import java.util.HashMap;

/**
 * An extended gamepad for more advanced toggles, key events,
 * and other control processors.
 */
public class GamepadEx {

    /**
     * The retrievable gamepad object
     */
    public Gamepad gamepad;

    private HashMap<Button, ButtonReader> buttonReaders;
    private HashMap<Button, GamepadButton> gamepadButtons;
    private SlewRateLimiter LX = null;
    private SlewRateLimiter LY = null;
    private SlewRateLimiter RX = null;
    private SlewRateLimiter RY = null;

    private final Button[] buttons = {
            Button.Y, Button.X, Button.A, Button.B, Button.LEFT_BUMPER, Button.RIGHT_BUMPER, Button.BACK,
            Button.START, Button.OPTIONS, Button.DPAD_UP, Button.DPAD_DOWN, Button.DPAD_LEFT, Button.DPAD_RIGHT,
            Button.LEFT_STICK_BUTTON, Button.RIGHT_STICK_BUTTON,
            Button.TRIANGLE, Button.SQUARE, Button.CROSS, Button.CIRCLE,
            Button.PS, Button.SHARE, Button.TOUCHPAD, Button.TOUCHPAD_FINGER_1, Button.TOUCHPAD_FINGER_2
    };

    /**
     * The constructor, that contains the gamepad object from the
     * opmode.
     *
     * @param gamepad the gamepad object from the opmode
     */
    public GamepadEx(Gamepad gamepad) {
        this.gamepad = gamepad;
        buttonReaders = new HashMap<>();
        gamepadButtons = new HashMap<>();
        for (Button button : buttons) {
            buttonReaders.put(button, new ButtonReader(this, button));
            gamepadButtons.put(button, new GamepadButton(this, button));
        }
    }

    /**
     * @param button the button object
     * @return the boolean value as to whether the button is active or not
     */
    public boolean getButton(Button button) {
        boolean buttonValue = false;
        switch (button) {
            case A:
            case CROSS:
                buttonValue = gamepad.a;
                break;
            case B:
            case CIRCLE:
                buttonValue = gamepad.b;
                break;
            case SQUARE:
            case X:
                buttonValue = gamepad.x;
                break;
            case TRIANGLE:
            case Y:
                buttonValue = gamepad.y;
                break;
            case LEFT_BUMPER:
                buttonValue = gamepad.left_bumper;
                break;
            case RIGHT_BUMPER:
                buttonValue = gamepad.right_bumper;
                break;
            case DPAD_UP:
                buttonValue = gamepad.dpad_up;
                break;
            case DPAD_DOWN:
                buttonValue = gamepad.dpad_down;
                break;
            case DPAD_LEFT:
                buttonValue = gamepad.dpad_left;
                break;
            case DPAD_RIGHT:
                buttonValue = gamepad.dpad_right;
                break;
            case BACK:
                buttonValue = gamepad.back;
                break;
            case START:
                buttonValue = gamepad.start;
                break;
            case OPTIONS:
                buttonValue = gamepad.options;
                break;
            case LEFT_STICK_BUTTON:
                buttonValue = gamepad.left_stick_button;
                break;
            case RIGHT_STICK_BUTTON:
                buttonValue = gamepad.right_stick_button;
                break;
            case PS:
                buttonValue = gamepad.ps;
                break;
            case SHARE:
                buttonValue = gamepad.share;
                break;
            case TOUCHPAD:
                buttonValue = gamepad.touchpad;
                break;
            case TOUCHPAD_FINGER_1:
                buttonValue = gamepad.touchpad_finger_1;
                break;
            case TOUCHPAD_FINGER_2:
                buttonValue = gamepad.touchpad_finger_2;
                break;
            default:
                buttonValue = false;
                break;
        }
        return buttonValue;
    }

    /**
     * @param trigger the trigger object
     * @return the value returned by the trigger in question
     */
    public double getTrigger(GamepadKeys.Trigger trigger) {
        double triggerValue = 0;
        switch (trigger) {
            case LEFT_TRIGGER:
                triggerValue = gamepad.left_trigger;
                break;
            case RIGHT_TRIGGER:
                triggerValue = gamepad.right_trigger;
                break;
            default:
                break;
        }
        return triggerValue;
    }

    /**
     * Enables and sets the slew rate limiting for the joysticks.
     * Parameters are labelled by L/R for left/right joystick on the controller and X/Y for axis of joystick movement.
     * Set any parameters not to be enabled/used as null.
     * @return this object for chaining purposes
     */
    public GamepadEx setJoystickSlewRateLimiters(SlewRateLimiter LX, SlewRateLimiter LY, SlewRateLimiter RX, SlewRateLimiter RY) {
        this.LX = LX;
        this.LY = LY;
        this.RX = RX;
        this.RY = RY;
        return this;
    }

    /**
     * @return the y-value on the left analog stick
     */
    public double getLeftY() {
        if (LY == null) {
            return -gamepad.left_stick_y;
        } else {
            return LY.calculate(-gamepad.left_stick_y);
        }
    }

    /**
     * @return the y-value on the right analog stick
     */
    public double getRightY() {
        if (RY == null) {
            return gamepad.right_stick_y;
        } else {
            return RY.calculate(gamepad.right_stick_y);
        }
    }

    /**
     * @return the x-value on the left analog stick
     */
    public double getLeftX() {
        if (LX == null) {
            return gamepad.left_stick_x;
        } else {
            return LX.calculate(gamepad.left_stick_x);
        }
    }

    /**
     * @return the x-value on the right analog stick
     */
    public double getRightX() {
        if (RX == null) {
            return gamepad.right_stick_x;
        } else {
            return RX.calculate(gamepad.right_stick_x);
        }
    }

    /**
     * Returns if the button was just pressed
     *
     * @param button the desired button to read from
     * @return if the button was just pressed
     */
    public boolean wasJustPressed(Button button) {
        return buttonReaders.get(button).wasJustPressed();
    }

    /**
     * Returns if the button was just released
     *
     * @param button the desired button to read from
     * @return if the button was just released
     */
    public boolean wasJustReleased(Button button) {
        return buttonReaders.get(button).wasJustReleased();
    }

    /**
     * Updates the value for each {@link ButtonReader}.
     * Call this once in your loop.
     */
    public void readButtons() {
        for (Button button : buttons) {
            buttonReaders.get(button).readValue();
        }
    }

    /**
     * Returns if the button is down
     *
     * @param button the desired button to read from
     * @return if the button is down
     */
    public boolean isDown(Button button) {
        return buttonReaders.get(button).isDown();
    }

    /**
     * Returns if the button's state has just changed
     *
     * @param button the desired button to read from
     * @return if the button's state has just changed
     */
    public boolean stateJustChanged(Button button) {
        return buttonReaders.get(button).stateJustChanged();
    }

    /**
     * @param button the matching button key to the gamepad button
     * @return the commandable button
     */
    public GamepadButton getGamepadButton(Button button) {
        return gamepadButtons.get(button);
    }

}