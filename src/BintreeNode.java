/**
 * The interface for the Bintree node, part of the
 * Composite design pattern.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public interface BintreeNode {

    /**
     * Inserts an AirObject into the Bintree.
     *
     * @param obj    The AirObject to insert.
     * @param x      The x-origin of the current node's region.
     * @param y      The y-origin of the current node's region.
     * @param z      The z-origin of the current node's region.
     * @param xWid   The width of the region in x.
     * @param yWid   The width of the region in y.
     * @param zWid   The width of the region in z.
     * @param depth  The depth of the current node in the tree.
     * @return The BintreeNode that should be in this position
     * (e.g., a leaf might return a new InternalNode if it splits).
     */
    BintreeNode insert(AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth);


    /**
     * Removes an AirObject from the Bintree.
     *
     * @param obj    The AirObject to remove.
     * @param x      The x-origin of the current node's region.
     * @param y      The y-origin of the current node's region.
     * @param z      The z-origin of the current node's region.
     * @param xWid   The width of the region in x.
     * @param yWid   The width of the region in y.
     * @param zWid   The width of the region in z.
     * @param depth  The depth of the current node in the tree.
     * @return The BintreeNode that should be in this position
     * (e.g., an InternalNode might return a LeafNode if it becomes empty).
     */
    BintreeNode remove(AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth);


    /**
     * Appends a string representation of this node (and its children)
     * to the given StringBuilder.
     *
     * @param sb     The StringBuilder to append to.
     * @param x      The x-origin of the current node's region.
     * @param y      The y-origin of the current node's region.
     * @param z      The z-origin of the current node's region.
     * @param xWid   The width of the region in x.
     * @param yWid   The width of the region in y.
     * @param zWid   The width of the region in z.
     * @param depth  The depth of the current node in the tree.
     * @return The total number of nodes printed (including this one).
     */
    int print(StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth);


    /**
     * Finds all collisions within this node (and its children) and
     * appends them to the StringBuilder.
     *
     * @param sb     The StringBuilder to append to.
     * @param x      The x-origin of the current node's region.
     * @param y      The y-origin of the current node's region.
     * @param z      The z-origin of the current node's region.
     * @param xWid   The width of the region in x.
     * @param yWid   The width of the region in y.
     * @param zWid   The width of the region in z.
     * @param depth  The depth of the current node in the tree.
     */
    void collisions(StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth);


    /**
     * Finds all AirObjects that intersect with the given query box
     * within this node (and its children).
     *
     * @param sb      The StringBuilder to append results to.
     * @param qx      The x-origin of the query box.
     * @param qy      The y-origin of the query box.
     * @param qz      The z-origin of the query box.
     * @param qxwid   The x-width of the query box.
     * @param qywid   The y-width of the query box.
     * @param qzwid   The z-width of the query box.
     * @param x       The x-origin of the current node's region.
     * @param y       The y-origin of the current node's region.
     * @param z       The z-origin of the current node's region.
     * @param xWid    The width of the region in x.
     * @param yWid    The width of the region in y.
     * @param zWid    The width of the region in z.
     * @param depth   The depth of the current node in the tree.
     * @return The total number of nodes visited.
     */
    int intersect(
        StringBuilder sb,
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
        int x, int y, int z, int xWid, int yWid, int zWid, int depth);
}