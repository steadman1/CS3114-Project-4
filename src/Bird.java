/**
 * Represents a Bird (or flock of birds).
 * Extends AirObject with bird type and number.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class Bird extends AirObject {

    private String type;
    private int number;

    /**
     * Constructor for Bird.
     *
     * @param name    The name.
     * @param x       The x-coordinate.
     * @param y       The y-coordinate.
     * @param z       The z-coordinate.
     * @param xwid    The x-width.
     * @param ywid    The y-width.
     * @param zwid    The z-width.
     * @param type    The type of bird.
     * @param number  The number of birds.
     */
    public Bird(
        String name, int x, int y, int z, int xwid, int ywid, int zwid,
        String type, int number) {
        super(name, x, y, z, xwid, ywid, zwid);
        this.type = type;
        this.number = number;
    }


    // --- Getters for Bird-specific fields ---

    /**
     * Get the bird type.
     * @return The type string.
     */
    public String getType() {
        return type;
    }


    /**
     * Get the number of birds.
     * @return The number.
     */
    public int getNumber() {
        return number;
    }


    /**
     * Returns a string representation of the Bird.
     * Format: "Bird [name] [x] [y] [z] [xwid] [ywid] [zwid]
     * [type] [number]"
     *
     * @return The formatted string.
     */
    @Override
    public String toString() {
        return "Bird " + commonToString() + " " + type + " " + number;
    }
}