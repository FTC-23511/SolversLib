package com.seattlesolvers.solverslib.purepursuit;

import com.seattlesolvers.solverslib.geometry.Pose2d;
import com.seattlesolvers.solverslib.purepursuit.types.WaypointType;
import com.seattlesolvers.solverslib.purepursuit.waypoints.GeneralWaypoint;

/**
 * A pure pursuit Waypoint is a point in which the robot traverses. Using Waypoints
 * one can construct a pure pursuit path for their robot to follow.
 *
 * @author Michael Baljet, Team 14470
 * @version 1.2
 * @see GeneralWaypoint , EndWaypoint, StartWaypoint, PointTurnWaypoint, InterruptWaypoint
 */
public interface Waypoint {

    /**
     * Returns this WayPoint's type.
     *
     * @return this WayPoint's type.
     */
    public WaypointType getType();

    /**
     * Returns this Waypoint's position.
     *
     * @return this Waypoint's position.
     */
    public Pose2d getPose();

    /**
     * Returns the follow distance for this waypoint.
     *
     * @return the follow distance for this waypoint.
     */
    public double getFollowDistance();

    /**
     * Returns the timeout period of this waypoint.
     *
     * @return the timeout period of this waypoint.
     */
    public long getTimeout();

}
