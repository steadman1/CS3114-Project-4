/**
 * The main Bintree class.
 * This class acts as the public interface for the
 * Bintree node system and manages the root node.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class Bintree {

    /**
     * The root node of the Bintree.
     */
    private BintreeNode root;

    /**
     * The size of the world (e.g., 1024).
     */
    private int worldSize;

    /**
     * Creates a new Bintree.
     *
     * @param worldSize The side length of the cubic world.
     */
    public Bintree(int worldSize) {
        this.worldSize = worldSize;
        // The bintree starts as a single, empty leaf node.
        this.root = EmptyNode.getInstance();
    }


    /**
     * Inserts an AirObject into the Bintree.
     *
     * @param obj The AirObject to insert.
     */
    public void insert(AirObject obj) {
        // Start the recursive insertion at the root.
        // The root's region is (0, 0, 0) with the full world dimensions.
        root = root.insert(obj, 0, 0, 0,
            worldSize, worldSize, worldSize, 0);
    }


    /**
     * Removes an AirObject from the Bintree.
     *
     * @param obj The AirObject to remove.
     */
    public void remove(AirObject obj) {
        // Start the recursive removal at the root.
        root = root.remove(obj, 0, 0, 0,
            worldSize, worldSize, worldSize, 0);
    }


    /**
     * Returns a string representation of the Bintree.
     *
     * @return The formatted string of all nodes.
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        int nodeCount = root.print(sb, 0, 0, 0,
            worldSize, worldSize, worldSize, 0);
        sb.append(nodeCount);
        // The test output uses \r\n, but your original file had \n.
        // Sticking with your original for consistency.
        sb.append(" Bintree nodes printed\n");
        return sb.toString();
    }


    /**
     * Finds and formats all collisions in the Bintree.
     *
     * @return A string listing all collisions.
     */
    public String collisions() {
        StringBuilder sb = new StringBuilder();
        sb.append("The following collisions exist in the database:\n");
        root.collisions(sb, 0, 0, 0,
            worldSize, worldSize, worldSize, 0);
        return sb.toString();
    }


    /**
     * Finds all objects intersecting a given query box.
     *
     * @param x    Query box x-origin.
     * @param y    Query box y-origin.
     * @param z    Query box z-origin.
     * @param xwid Query box x-width.
     * @param ywid Query box y-width.
     * @param zwid Query box z-width.
     * @return A string listing all intersecting objects.
     */
    public String intersect(int x, int y, int z, int xwid, int ywid, int zwid) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "The following objects intersect (%d %d %d %d %d %d):\n",
            x, y, z, xwid, ywid, zwid));
            
        int nodesVisited = root.intersect(
            sb, x, y, z, xwid, ywid, zwid,
            0, 0, 0, worldSize, worldSize, worldSize, 0);
            
        sb.append(nodesVisited);
        sb.append(" nodes were visited in the bintree\n");
        return sb.toString();
    }
}