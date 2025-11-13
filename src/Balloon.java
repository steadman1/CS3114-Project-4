/**
 * Represents a Balloon object.
 * Extends AirObject with balloon type and ascent rate.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class Balloon extends AirObject {

    private String type;
    private int ascentRate;

    /**
     * Constructor for Balloon.
     *
     * @param name       The name.
     * @param x          The x-coordinate.
     * @param y          The y-coordinate.
     * @param z          The z-coordinate.
     * @param xwid       The x-width.
     * @param ywid       The y-width.
     * @param zwid       The z-width.
     * @param type       The type of balloon (e.g., "hot_air").
     * @param ascentRate The ascent rate.
     */
    public Balloon(
        String name, int x, int y, int z, int xwid, int ywid, int zwid,
        String type, int ascentRate) {
        super(name, x, y, z, xwid, ywid, zwid);
        this.type = type;
        this.ascentRate = ascentRate;
    }


    // --- Getters for Balloon-specific fields ---

    /**
     * Get the balloon type.
     * @return The type string.
     */
    public String getType() {
        return type;
    }


    /**
     * Get the ascent rate.
     * @return The ascent rate.
     */
    public int getAscentRate() {
        return ascentRate;
    }


    /**
     * Returns a string representation of the Balloon.
     * Format: "Balloon [name] [x] [y] [z] [xwid] [ywid] [zwid]
     * [type] [ascent_rate]"
     *
     * @return The formatted string.
     */
    @Override
    public String toString() {
        return "Balloon " + commonToString() + " " + type + " " + ascentRate;
    }
}