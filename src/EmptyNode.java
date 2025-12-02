/**
 * Represents an empty leaf node in the Bintree.
 * This class is an implementation of the Flyweight design pattern.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class EmptyNode implements BintreeNode {

    private static final EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode() {
    }

    public static EmptyNode getInstance() {
        return INSTANCE;
    }

    @Override
    public BintreeNode insert(
        AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // When inserting into an empty node, it becomes a LeafNode
        LeafNode newLeaf = new LeafNode();
        return newLeaf.insert(obj, x, y, z, xWid, yWid, zWid, depth);
    }

    @Override
    public BintreeNode remove(
        AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        return this;
    }

    @Override
    public int print(
        StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        
        sb.append("E (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");
        
        return 1;
    }

    @Override
    public void collisions(
        StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        // No collisions
    }

    @Override
    public int intersect(
        StringBuilder sb,
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
        int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
        // Visited, but no data
        return 1;
    }
}