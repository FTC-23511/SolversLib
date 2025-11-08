package com.seattlesolvers.solverslib.hardware;

import com.seattlesolvers.solverslib.util.RotationDirection;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * A base class for {@link Encoder}.
 * @author Daniel - 7854
 */
public abstract class EncoderBase<E, T extends EncoderBase<E, T>> implements Encoder<E, T> {
    protected double offset = 0.0;
    protected RotationDirection direction = RotationDirection.FORWARD;
    protected AngleUnit angleUnit = AngleUnit.RADIANS;
    protected final E encoder;

    protected EncoderBase(E encoder) {
        this.encoder = encoder;
    }

    @Override
    public int getDirectionMultiplier() {
        return direction.getMultiplier();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setDirection(RotationDirection direction) {
        this.direction = direction;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setReversed(boolean reversed) {
        direction = reversed ? RotationDirection.REVERSE : RotationDirection.FORWARD;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setOffset(double offset) {
        this.offset = offset;
        return (T) this;
    }

    @Override
    public RotationDirection getDirection() {
        return direction;
    }

    @Override
    public boolean getReversed() {
        return direction == RotationDirection.REVERSE;
    }

    @Override
    public AngleUnit getAngleUnit() {
        return angleUnit;
    }

    @Override
    public E getEncoder() {
        return encoder;
    }
}