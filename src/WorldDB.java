import java.util.Random;

/**
 * The world for this project. We have a Skip List and a Bintree
 *
 * @author adsleptsov
 * @version {Put Something Here}
 */
public class WorldDB implements ATC {
    private final int worldSize = 1024;
    private Random rnd;


    private SkipList<String, AirObject> skipList;
    private Bintree bintree;

    /**
     * Create a brave new World.
     * @param r A random number generator to use
     *
     */
    public WorldDB(Random r) {
        rnd = r;
        if (rnd == null) {
            rnd = new Random();
        }
        clear();
    }

    /**
     * Clear the world
     *
     */
    public void clear() {
        skipList = new SkipList<>(rnd);
        bintree = new Bintree(worldSize);
    }


    // ----------------------------------------------------------
    /**
     * (Try to) insert an AirObject into the database
     * @param a An AirObject.
     * @return True iff the AirObject is successfully entered into the database
     */
    public boolean add(AirObject a) {
        if (!isValid(a)) {
            return false;
        }
        
        if (skipList.find(a.getName()) != null) {
            return false;
        }

        skipList.insert(a.getName(), a);
        bintree.insert(a);
        return true;
    }


    // ----------------------------------------------------------
    /**
     * The AirObject with this name is deleted from the database (if it exists).
     * Print the AirObject's toString value if one with that name exists.
     * If no such AirObject with this name exists, return null.
     * @param name AirObject name.
     * @return A string representing the AirObject, or null if no such name.
     */
    public String delete(String name) {
        if (name == null) {
            return null;
        }
        
        // Remove from SkipList first to get the object
        AirObject obj = skipList.remove(name);
        
        if (obj == null) {
            return null; // Object not found
        }
        
        // If found, remove from Bintree as well
        bintree.remove(obj);
        
        // Return the string representation
        return obj.toString();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of the Skiplist in alphabetical order on the names.
     * See the sample test cases for details on format.
     * @return String listing the AirObjects in the Skiplist as specified.
     */
    public String printskiplist() {
        return skipList.print();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of the Bintree nodes in preorder.
     * See the sample test cases for details on format.
     * @return String listing the Bintree nodes as specified.
     */
    public String printbintree() {
        return bintree.print();
    }



    // ----------------------------------------------------------
    /**
     * Print an AirObject with a given name if it exists
     * @param name The name of the AirObject to print
     * @return String showing the toString for the AirObject if it exists
     *         Return null if there is no such name
     */
    public String print(String name) {
        if (name == null) {
            return null;
        }
        
        AirObject obj = skipList.find(name);
        if (obj == null) {
            return null;
        }
        
        return obj.toString();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of the AirObjects found in the database between the
     * min and max values for names.
     * See the sample test cases for details on format.
     * @param start Minimum of range
     * @param end Maximum of range
     * @return String listing the AirObjects in the range as specified.
     *         Null if the parameters are bad
     */
    public String rangeprint(String start, String end) {
        if (start == null || end == null) {
            return null;
        }
        
        // Per testBadInput, rangeprint("z", "a") is null
        if (start.compareTo(end) > 0) {
            return null;
        }
        
        // Delegate the range search and formatting to the SkipList class
        return skipList.range(start, end);
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of all collisions between AirObjects bounding boxes
     * that are found in the database.
     * See the sample test cases for details on format.
     * Note that the collision is only reported for the node that contains the
     * origin of the intersection box.
     * @return String listing the AirObjects that participate in collisions.
     */
    public String collisions() {
        return bintree.collisions();
    }


    // ----------------------------------------------------------
    /**
     * Return a listing of all AirObjects whose bounding boxes
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
     *         Return null if any input parameters are bad
     */
    public String intersect(int x, int y, int z, int xwid, int ywid, int zwid) {
        if (x < 0 || x >= worldSize || y < 0 || y >= worldSize || z < 0 || z >= worldSize) {
            return null;
        }
        if (xwid <= 0 || xwid > worldSize || ywid <= 0 || ywid > worldSize || zwid <= 0 || zwid > worldSize) {
            return null;
        }
        // Check if the box extends outside the world
        if (x + xwid > worldSize || y + ywid > worldSize || z + zwid > worldSize) {
            return null;
        }

        // Delegate the intersection search and formatting to the Bintree class
        return bintree.intersect(x, y, z, xwid, ywid, zwid);
    }


    
/**
     * Private helper method to validate an AirObject.
     * Checks all common and subclass-specific fields based
     * on the rules in the project description and testBadInput.
     *
     * @param a The AirObject to validate.
     * @return True if the object is valid, false otherwise.
     */
    private boolean isValid(AirObject a) {
        if (a == null || a.getName() == null || a.getName().isEmpty()) {
            return false;
        }

        // Validate coordinates and widths
        int x = a.getXorig();
        int y = a.getYorig();
        int z = a.getZorig();
        int xw = a.getXwidth();
        int yw = a.getYwidth();
        int zw = a.getZwidth();

        // Coords must be in the range [0, 1023]
        if (x < 0 || x >= worldSize || y < 0 || y >= worldSize || z < 0 || z >= worldSize) {
            return false;
        }
        // Widths must be in the range [1, 1024]
        if (xw <= 0 || xw > worldSize || yw <= 0 || yw > worldSize || zw <= 0 || zw > worldSize) {
            return false;
        }

        // The entire box must be within the world [0, 1024]
        if (x + xw > worldSize || y + yw > worldSize || z + zw > worldSize) {
            return false;
        }

        // Validate subclass-specific fields
        // This assumes the subclass files (AirPlane, Balloon, etc.) exist
        // and have the getters for their respective fields.
        try {
            if (a instanceof AirPlane) {
                AirPlane p = (AirPlane) a;
                if (p.getCarrier() == null || p.getFlightNum() <= 0 || p.getNumEngines() <= 0) {
                    return false;
                }
            } 
            else if (a instanceof Balloon) {
                Balloon b = (Balloon) a;
                if (b.getType() == null || b.getAscentRate() < 0) {
                    return false;
                }
            }
            else if (a instanceof Bird) {
                Bird b = (Bird) a;
                if (b.getType() == null || b.getNumber() <= 0) {
                    return false;
                }
            }
            else if (a instanceof Drone) {
                Drone d = (Drone) a;
                if (d.getBrand() == null || d.getNumEngines() <= 0) {
                    return false;
                }
            }
            else if (a instanceof Rocket) {
                Rocket r = (Rocket) a;
                if (r.getAscentRate() < 0 || r.getTrajectory() < 0) {
                    return false;
                }
            }
        } 
        catch (Exception e) {
            // This could happen if the assumed getters don't exist,
            // which would indicate an invalid object.
            return false;
        }

        // All checks passed
        return true;
    }


}
