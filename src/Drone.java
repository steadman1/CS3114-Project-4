/**
 * Represents a Drone object.
 * Extends AirObject with brand and number of engines.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class Drone extends AirObject {

    private String brand;
    private int numEngines;

    /**
     * Constructor for Drone.
     *
     * @param name       The name.
     * @param x          The x-coordinate.
     * @param y          The y-coordinate.
     * @param z          The z-coordinate.
     * @param xwid       The x-width.
     * @param ywid       The y-width.
     * @param zwid       The z-width.
     * @param brand      The drone brand.
     * @param numEngines The number of engines.
     */
    public Drone(
        String name, int x, int y, int z, int xwid, int ywid, int zwid,
        String brand, int numEngines) {
        super(name, x, y, z, xwid, ywid, zwid);
        this.brand = brand;
        this.numEngines = numEngines;
    }


    // --- Getters for Drone-specific fields ---

    /**
     * Get the drone brand.
     * @return The brand string.
     */
    public String getBrand() {
        return brand;
    }


    /**
     * Get the number of engines.
     * @return The engine count.
     */
    public int getNumEngines() {
        return numEngines;
    }


    /**
     * Returns a string representation of the Drone.
     * Format: "Drone [name] [x] [y] [z] [xwid] [ywid] [zwid]
     * [brand] [#engines]"
     *
     * @return The formatted string.
     */
    @Override
    public String toString() {
        return "Drone " + commonToString() + " " + brand + " " + numEngines;
    }
}