/**
 * Represents an AirPlane object.
 * Extends AirObject with carrier, flight number, and engine count.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class AirPlane extends AirObject {

    private String carrier;
    private int flightNum;
    private int numEngines;

    /**
     * Constructor for AirPlane.
     *
     * @param name       The name.
     * @param x          The x-coordinate.
     * @param y          The y-coordinate.
     * @param z          The z-coordinate.
     * @param xwid       The x-width.
     * @param ywid       The y-width.
     * @param zwid       The z-width.
     * @param carrier    The airline carrier.
     * @param flightNum  The flight number.
     * @param numEngines The number of engines.
     */
    public AirPlane(
        String name, int x, int y, int z, int xwid, int ywid, int zwid,
        String carrier, int flightNum, int numEngines) {
        super(name, x, y, z, xwid, ywid, zwid);
        this.carrier = carrier;
        this.flightNum = flightNum;
        this.numEngines = numEngines;
    }


    // --- Getters for AirPlane-specific fields ---

    /**
     * Get the airline carrier.
     * @return The carrier string.
     */
    public String getCarrier() {
        return carrier;
    }


    /**
     * Get the flight number.
     * @return The flight number.
     */
    public int getFlightNum() {
        return flightNum;
    }


    /**
     * Get the number of engines.
     * @return The engine count.
     */
    public int getNumEngines() {
        return numEngines;
    }


    /**
     * Returns a string representation of the AirPlane.
     * Format: "Airplane [name] [x] [y] [z] [xwid] [ywid] [zwid]
     * [carrier] [flight#] [#engines]"
     *
     * @return The formatted string.
     */
    @Override
    public String toString() {
        return "Airplane " + commonToString() + " " + carrier + " "
            + flightNum + " " + numEngines;
    }
}