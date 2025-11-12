//-------------------------------------------------------------------------
/**
 * Interface class for the AirControl project
 * You probably don't need to edit this file
 *
 * @author CS3114/5040 Staff
 * @version Fall 2025
 *
 */
public interface ATC {

    // ----------------------------------------------------------
    /**
     * Reinitialize the database
     */
    public void clear();


    // ----------------------------------------------------------
    /**
     * (Try to) insert an AirObject into the database
     * @param a An AirObject.
     * @return True iff the AirObject is successfully entered into the database
     */
    public boolean add(AirObject a);


    // ----------------------------------------------------------
    /**
     * The AirObject with this name is deleted from the database (if it exists).
     * Print the AirObject's toString value if one with that name exists.
     * If no such AirObject with this name exists, return null.
     * @param name AirObject name.
     * @return A string representing the AirObject, or null if no such name.
     */
    public String delete(String name);


    // ----------------------------------------------------------
    /**
     * Return a listing of the Skiplist in alphabetical order on the names.
     * See the sample test cases for details on format.
     * @return String listing the AirObjects in the Skiplist as specified.
     */
    public String printskiplist();


    // ----------------------------------------------------------
    /**
     * Return a listing of the Bintree nodes in preorder.
     * See the sample test cases for details on format.
     * @return String listing the Bintree nodes as specified.
     */
    public String printbintree();


    // ----------------------------------------------------------
    /**
     * Print an AirObject with a given name if it exists
     * @param name The name of the AirObject to print
     * @return String showing the toString for the AirObject if it exists
     *         Return null if there is no such name
     */
    public String print(String name);


    // ----------------------------------------------------------
    /**
     * Return a listing of the AirObjects found in the database between the
     * min and max values for names.
     * See the sample test cases for details on format.
     * @param min Minimum of range
     * @param max Maximum of range
     * @return String listing the AirObjects in the range as specified.
     */
    public String rangeprint(String min, String max);


    // ----------------------------------------------------------
    /**
     * Return a listing of all collisions between AirObjects bounding boxes
     * that are found in the database.
     * See the sample test cases for details on format.
     * Note that the collision is only reported for the node that contains the
     * origin of the intersection box.
     * @return String listing the AirObjects that participate in collisions.
     */
    public String collisions();


    // ----------------------------------------------------------
    /**
     * Return a listing of all AirObjects bounding boxes
     * that intersect the given bounding box.
     * Note that the collision is only reported for the node that contains the
     * origin of the intersection box.
     * See the sample test cases for details on format.
     * @param x Bounding box upper left x
     * @param y Bounding box upper left y
     * @param z Bounding box upper left z
     * @param xwid Bounding box x width
     * @param ywid Bounding box y width
     * @param zwid Bounding box z width
     * @return String listing the AirObjects that intersect the given box.
     */
    public String intersect(int x, int y, int z, int xwid, int ywid, int zwid);
}
