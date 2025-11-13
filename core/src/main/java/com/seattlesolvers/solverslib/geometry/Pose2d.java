package com.seattlesolvers.solverslib.geometry;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

/**
 * A two-dimensional position that includes a {@link Vector2d} and
 * a heading angle. Look at {@link Vector2d} for information on what
 * some of these methods do.
 */
public class Pose2d {
    private Translation2d m_translation;
    private Rotation2d m_rotation;

    /**
     * Constructs a pose at the origin facing toward the positive X axis.
     * (Translation2d{0, 0} and Rotation{0})
     */
    public Pose2d() {
        m_translation = new Translation2d();
        m_rotation = new Rotation2d();
    }

    /**
     * Constructs a pose with the specified translation and rotation.
     *
     * @param translation The translational component of the pose.
     * @param rotation    The rotational component of the pose.
     */
    public Pose2d(Translation2d translation, Rotation2d rotation) {
        m_translation = translation;
        m_rotation = rotation;
    }

    /**
     * Convenience constructors that takes in x and y values directly instead of
     * having to construct a Translation2d.
     *
     * @param x        The x component of the translational component of the pose.
     * @param y        The y component of the translational component of the pose.
     * @param rotation The rotational component of the pose.
     */
    @SuppressWarnings("ParameterName")
    public Pose2d(double x, double y, Rotation2d rotation) {
        m_translation = new Translation2d(x, y);
        m_rotation = rotation;
    }

    /**
     * Convenience constructors that takes in x, y, and heading values directly instead of
     * having to construct a Translation2d and Rotation2d.
     *
     * @param x        The x component of the translational component of the pose.
     * @param y        The y component of the translational component of the pose.
     * @param rotation The rotational component of the pose.
     */
    @SuppressWarnings("ParameterName")
    public Pose2d(double x, double y, double rotation) {
        m_translation = new Translation2d(x, y);
        m_rotation = new Rotation2d(rotation);
    }

    /**
     * Convenience constructor to convert SDK Pose2D objects to SolversLib Pose2d objects
     * @param pose2D the SDK Pose2D object
     * @param distanceUnit the distance unit to be used for translational values
     * @param angleUnit the angle unit to be used for rotational values
     */
    public Pose2d(Pose2D pose2D, DistanceUnit distanceUnit, AngleUnit angleUnit) {
        this(pose2D.getX(distanceUnit), pose2D.getY(distanceUnit), new Rotation2d(pose2D.getHeading(angleUnit)));
    }

    /**
     * Transforms the pose by the given transformation and returns the new
     * transformed pose.
     *
     * <p>The matrix multiplication is as follows
     * [x_new]    [cos, -sin, 0][transform.x]
     * [y_new] += [sin,  cos, 0][transform.y]
     * [t_new]    [0,    0,   1][transform.t]
     *
     * @param other The transform to transform the pose by.
     * @return The transformed pose.
     */
    public Pose2d plus(Transform2d other) {
        return transformBy(other);
    }

    /**
     * Returns the Transform2d that maps the one pose to another.
     *
     * @param other The initial pose of the transformation.
     * @return The transform that maps the other pose to the current pose.
     */
    public Transform2d minus(Pose2d other) {
        final Pose2d pose = this.relativeTo(other);
        return new Transform2d(pose.getTranslation(), pose.getRotation());
    }

    /**
     * Returns the translation component of the transformation.
     *
     * @return The translational component of the pose.
     */
    public Translation2d getTranslation() {
        return m_translation;
    }

    /**
     * Returns the rotational component of the transformation.
     *
     * @return The rotational component of the pose.
     */
    public Rotation2d getRotation() {
        return m_rotation;
    }

    /**
     * Transforms the pose by the given transformation and returns the new pose.
     * See + operator for the matrix multiplication performed.
     *
     * @param other The transform to transform the pose by.
     * @return The transformed pose.
     */
    public Pose2d transformBy(Transform2d other) {
        return new Pose2d(m_translation.plus(other.getTranslation().rotateBy(m_rotation)),
                m_rotation.plus(other.getRotation()));
    }

    /**
     * Returns the other pose relative to the current pose.
     *
     * <p>This function can often be used for trajectory tracking or pose
     * stabilization algorithms to get the error between the reference and the
     * current pose.
     *
     * @param other The pose that is the origin of the new coordinate frame that
     *              the current pose will be converted into.
     * @return The current pose relative to the new origin pose.
     */
    public Pose2d relativeTo(Pose2d other) {
        Transform2d transform = new Transform2d(other, this);
        return new Pose2d(transform.getTranslation(), transform.getRotation());
    }

    /**
     * @return the x value from the {@link Translation2d}
     */
    public double getX() {
        return m_translation.getX();
    }

    /**
     * @return the y value from the {@link Translation2d}
     */
    public double getY() {
        return m_translation.getY();
    }

    /**
     * Obtain a new Pose2d from a (constant curvature) velocity.
     *
     * <p>See <a href="https://file.tavsys.net/control/state-space-guide.pdf">
     * Controls Engineering in the FIRST Robotics Competition</a>
     * section on nonlinear pose estimation for derivation.
     *
     * <p>The twist is a change in pose in the robot's coordinate frame since the
     * previous pose update. When the user runs exp() on the previous known
     * field-relative pose with the argument being the twist, the user will
     * receive the new field-relative pose.
     *
     * <p>"Exp" represents the pose exponential, which is solving a differential
     * equation moving the pose forward in time.
     *
     * @param twist The change in pose in the robot's coordinate frame since the
     *              previous pose update. For example, if a non-holonomic robot moves forward
     *              0.01 meters and changes angle by 0.5 degrees since the previous pose update,
     *              the twist would be Twist2d{0.01, 0.0, toRadians(0.5)}
     * @return The new pose of the robot.
     */
    @SuppressWarnings("LocalVariableName")
    public Pose2d exp(Twist2d twist) {
        double dx = twist.dx;
        double dy = twist.dy;
        double dtheta = twist.dtheta;

        double sinTheta = Math.sin(dtheta);
        double cosTheta = Math.cos(dtheta);

        double s;
        double c;
        if (Math.abs(dtheta) < 1E-9) {
            s = 1.0 - 1.0 / 6.0 * dtheta * dtheta;
            c = 0.5 * dtheta;
        } else {
            s = sinTheta / dtheta;
            c = (1 - cosTheta) / dtheta;
        }
        Transform2d transform = new Transform2d(new Translation2d(dx * s - dy * c, dx * c + dy * s),
                new Rotation2d(cosTheta, sinTheta));

        return this.plus(transform);
    }

    /**
     * Returns a Twist2d that maps this pose to the end pose. If c is the output
     * of a.Log(b), then a.Exp(c) would yield b.
     *
     * @param end The end pose for the transformation.
     * @return The twist that maps this to end.
     */
    public Twist2d log(Pose2d end) {
        final Pose2d transform = end.relativeTo(this);
        final double dtheta = transform.getRotation().getRadians();
        final double halfDtheta = dtheta / 2.0;

        final double cosMinusOne = transform.getRotation().getCos() - 1;

        double halfThetaByTanOfHalfDtheta;
        if (Math.abs(cosMinusOne) < 1E-9) {
            halfThetaByTanOfHalfDtheta = 1.0 - 1.0 / 12.0 * dtheta * dtheta;
        } else {
            halfThetaByTanOfHalfDtheta = -(halfDtheta * transform.getRotation().getSin()) / cosMinusOne;
        }

        Translation2d translationPart = transform.getTranslation().rotateBy(
                new Rotation2d(halfThetaByTanOfHalfDtheta, -halfDtheta)
        ).times(Math.hypot(halfThetaByTanOfHalfDtheta, halfDtheta));

        return new Twist2d(translationPart.getX(), translationPart.getY(), dtheta);
    }

    @Override
    public String toString() {
        return String.format("Pose2d(%s, %s)", m_translation, m_rotation);
    }

    /**
     * Checks equality between this Pose2d and another object.
     *
     * @param obj The other object.
     * @return Whether the two objects are equal or not.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pose2d) {
            return ((Pose2d) obj).m_translation.equals(m_translation)
                    && ((Pose2d) obj).m_rotation.equals(m_rotation);
        }
        return false;
    }

    public Pose2d rotate(double deltaTheta) {
        return new Pose2d(m_translation, new Rotation2d(getHeading() + deltaTheta));
    }

    public double getHeading() {
        return m_rotation.getRadians();
    }

    /**
     * Flips and updates this pose's values (including heading) across x=0 using the static method {@link Pose2d#mirrorPose(Pose2d)}
     */
    public void mirror() {
        Pose2d mirrored = mirrorPose(this);
        m_translation = mirrored.getTranslation();
        m_rotation = mirrored.getRotation();
    }

    /**
     * Converts SolversLib's Pose2d objects into the SDK's Pose2D objects
     * @return the SDK Pose2D object
     */
    public static Pose2D convertToPose2D(Pose2d pose, DistanceUnit distanceUnit, AngleUnit angleUnit) {
        return new Pose2D(distanceUnit, pose.getX(), pose.getY(), angleUnit, pose.getHeading());
    }

    /**
     * Reflects poses across x=0 (including heading), assuming heading is scaled from zero to max
     * @param pose pose to be flipped
     * @return flipped pose
     */
    public static Pose2d mirrorPose(Pose2d pose) {
        return new Pose2d(-pose.getX(), pose.getY(), Math.atan2(Math.sin(pose.getHeading()), -Math.cos(pose.getHeading())));
    }
}