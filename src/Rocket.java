/**
 * Represents a Rocket object.
 * Extends AirObject with ascent rate and trajectory.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class Rocket extends AirObject {

    private int ascentRate;
    private double trajectory; // Note: Project spec says 'floating point'

    /**
     * Constructor for Rocket.
     *
     * @param name       The name.
     * @param x          The x-coordinate.
     * @param y          The y-coordinate.
     * @param z          The z-coordinate.
     * @param xwid       The x-width.
     * @param ywid       The y-width.
     * @param zwid       The z-width.
     * @param ascentRate The ascent rate.
     * @param trajectory The trajectory angle/value.
     */
    public Rocket(
        String name, int x, int y, int z, int xwid, int ywid, int zwid,
        int ascentRate, double trajectory) {
        super(name, x, y, z, xwid, ywid, zwid);
        this.ascentRate = ascentRate;
        this.trajectory = trajectory;
    }


    // --- Getters for Rolcket-specific fields ---

    /**
     * Get the ascent rate.
     * @return The ascent rate.
     */
    public int getAscentRate() {
        return ascentRate;
    }


    /**
     * Get the trajectory.
     * @return The trajectory value.
     */
    public double getTrajectory() {
        return trajectory;
    }


    /**
     * Returns a string representation of the Rocket.
     * Format: "Rocket [name] [x] [y] [z] [xwid] [ywid] [zwid]
     * [ascent_rate] [trajectory]"
     *
     * @return The formatted string.
     */
    @Override
    public String toString() {
        return "Rocket " + commonToString() + " " + ascentRate + " "
            + trajectory;
    }
}