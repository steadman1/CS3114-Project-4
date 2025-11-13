/**
 * Represents an empty leaf node in the Bintree.
 * This is the Flyweight object. There should be only
 * one instance of this class.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class EmptyNode implements BintreeNode {

    /**
     * The singleton instance of the EmptyNode.
     */
    private static final EmptyNode INSTANCE = new EmptyNode();

    /**
     * Private constructor to prevent instantiation.
     */
    private EmptyNode() {
        // Nothing to do
    }


    /**
     * Gets the singleton instance of the EmptyNode.
     *
     * @return The single EmptyNode instance.
     */
    public static EmptyNode getInstance() {
        return INSTANCE;
    }


    @Override
    public BintreeNode insert(AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // 1. Create a new DataNode
        DataNode newDataNode = new DataNode();
        
        // 2. Add the 'obj' to this new DataNode's list.
        // 3. Return the new DataNode, which will replace this
        //    EmptyNode in the parent InternalNode.
        return newDataNode.insert(obj, x, y, z, xWid, yWid, zWid, depth);
    }


    @Override
    public BintreeNode remove(AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // Removing from an empty node. This should not happen if
        // logic is correct, but we are robust.
        return this;
    }


    /**
     * Helper to create the indentation string.
     * @param depth The current depth.
     * @return A string of spaces.
     */
    private String indent(int depth) {
        return "  ".repeat(depth);
    }


    @Override
    public int print(StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        sb.append(indent(depth));
        sb.append("E (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        // Use the new region dimensions
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");

        return 1; // This is 1 node.
    }


    @Override
    public void collisions(StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // Print the "In leaf node..." header
        sb.append(indent(depth));
        sb.append("In leaf node (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        // Use the new region dimensions
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");
        // No objects, so no collisions to report
    }


    @Override
    public int intersect(
        StringBuilder sb,
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
        int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
            
        // Print the "In leaf node..." header
        sb.append(indent(depth));
        sb.append("In leaf node (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        // Use the new region dimensions
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");
        
        return 1; // Visited this 1 node.
    }
}