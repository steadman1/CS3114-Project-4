/**
 * Abstract base class for all objects in the Air Traffic Control system.
 * It stores the object's name, 3D position (x, y, z), and
 * 3D bounding box dimensions (width in x, y, z).
 *
 * Implements Comparable to allow sorting by name.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public abstract class AirObject implements Comparable<AirObject> {

    // Common data fields for all AirObjects
    private String name;
    private int x;
    private int y;
    private int z;
    private int xwid;
    private int ywid;
    private int zwid;

    /**
     * Constructor for the AirObject.
     *
     * @param name The name of the object.
     * @param x    The x-coordinate of the origin.
     * @param y    The y-coordinate of the origin.
     * @param z    The z-coordinate of the origin.
     * @param xwid The width in the x-dimension.
     * @param ywid The width in the y-dimension.
     * @param zwid The width in the z-dimension.
     */
    public AirObject(
        String name, int x, int y, int z, int xwid, int ywid, int zwid) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xwid = xwid;
        this.ywid = ywid;
        this.zwid = zwid;
    }


    // --- Getters for common fields ---

    /**
     * Get the x-origin coordinate.
     * @return The x-coordinate.
     */
    public int getXorig() {
        return x;
    }


    /**
     * Get the y-origin coordinate.
     * @return The y-coordinate.
     */
    public int getYorig() {
        return y;
    }


    /**
     * Get the z-origin coordinate.
     * @return The z-coordinate.
     */
    public int getZorig() {
        return z;
    }


    /**
     * Get the width in the x-dimension.
     * @return The x-width.
     */
    public int getXwidth() {
        return xwid;
    }


    /**
     * Get the width in the y-dimension.
     * @return The y-width.
     */
    public int getYwidth() {
        return ywid;
    }


    /**
     * Get the width in the z-dimension.
     * @return The z-width.
     */
    public int getZwidth() {
        return zwid;
    }


    /**
     * Get the name of the object.
     * @return The object's name.
     */
    public String getName() {
        return name;
    }


    /**
     * Compares this AirObject to another based on their names.
     * This is required for the SkipList.
     *
     * @param other The other AirObject to compare to.
     * @return A negative, zero, or positive integer as this object's
     * name is less than, equal to, or greater than the other's.
     */
    @Override
    public int compareTo(AirObject other) {
        return this.name.compareTo(other.getName());
    }
    

    /**
     * Returns a string representation of the common fields.
     * Subclasses should call this and append their own fields.
     *
     * @return A string with the common fields.
     */
    protected String commonToString() {
        return String.format("%s %d %d %d %d %d %d",
            getName(),
            getXorig(), getYorig(), getZorig(),
            getXwidth(), getYwidth(), getZwidth());
    }


    /**
     * Abstract toString method to be implemented by all subclasses.
     *
     * @return A string representation of the specific object.
     */
    @Override
    public abstract String toString();


    /**
     * Checks if this AirObject intersects with another AirObject.
     * Used for collision detection.
     * @param other The other AirObject.
     * @return True if they intersect, false otherwise.
     */
    public boolean intersects(AirObject other) {
        return this.intersects(
            other.getXorig(), other.getYorig(), other.getZorig(),
            other.getXwidth(), other.getYwidth(), other.getZwidth()
        );
    }

    /**
     * Checks if this AirObject's bounding box intersects with a
     * query box.
     * @param qx Query box x-origin.
     * @param qy Query box y-origin.
     * @param qz Query box z-origin.
     * @param qxwid Query box x-width.
     * @param qywid Query box y-width.
     * @param qzwid Query box z-width.
     * @return True if they intersect, false otherwise.
     */
    public boolean intersects(int qx, int qy, int qz, 
                              int qxwid, int qywid, int qzwid) {
        
        // This object's bounds
        int x1 = this.getXorig();
        int x2 = x1 + this.getXwidth();
        int y1 = this.getYorig();
        int y2 = y1 + this.getYwidth();
        int z1 = this.getZorig();
        int z2 = z1 + this.getZwidth();

        // Query box's bounds
        int qx1 = qx;
        int qx2 = qx + qxwid;
        int qy1 = qy;
        int qy2 = qy + qywid;
        int qz1 = qz;
        int qz2 = qz + qzwid;

        // Check for non-intersection
        // "touching is not intersecting"
        boolean noXOverlap = (x1 >= qx2) || (x2 <= qx1);
        boolean noYOverlap = (y1 >= qy2) || (y2 <= qy1);
        boolean noZOverlap = (z1 >= qz2) || (z2 <= qz1);

        // If any axis has no overlap, they do not intersect.
        return !(noXOverlap || noYOverlap || noZOverlap);
    }

    //
    // The 'intersectsRegion(int rx, int ry, int rz, int rSize)' method
    // has been removed, as it was based on the incorrect
    // Octree (cubic) assumption.
    // The 'intersects(x, y, z, xwid, ywid, zwid)' method
    // is the correct one to use for all region intersections.
    //
}