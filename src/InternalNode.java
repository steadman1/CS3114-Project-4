/**
 * Represents an internal node in the Bintree.
 * This node splits the world on one axis at a time,
 * rotating between x, y, and z based on depth.
 * It has two children, "left" (low) and "right" (high).
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class InternalNode implements BintreeNode {

    /**
     * The "left" (or "low") child.
     * For X-split, this is region with lower X.
     * For Y-split, this is region with lower Y.
     * For Z-split, this is region with lower Z.
     */
    private BintreeNode left;

    /**
     * The "right" (or "high") child.
     * For X-split, this is region with higher X.
     * For Y-split, this is region with higher Y.
     * For Z-split, this is region with higher Z.
     */
    private BintreeNode right;

    /**
     * Creates a new InternalNode.
     * Initializes both children to the Flyweight EmptyNode.
     */
    public InternalNode() {
        this.left = EmptyNode.getInstance();
        this.right = EmptyNode.getInstance();
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
    public BintreeNode insert(AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {

        // Determine which axis to split on
        int axis = depth % 3;

        if (axis == 0) { // --- Split on X ---
            // --- FIX: Calculate both widths to avoid losing pixels ---
            int leftXWid = xWid / 2;
            int rightXWid = xWid - leftXWid;
            int midX = x + leftXWid;
            // --- END FIX ---

            // Check left child (low X)
            if (obj.intersects(x, y, z, leftXWid, yWid, zWid)) {
                left = left.insert(obj, x, y, z,
                    leftXWid, yWid, zWid, depth + 1);
            }
            // Check right child (high X)
            if (obj.intersects(midX, y, z, rightXWid, yWid, zWid)) {
                right = right.insert(obj, midX, y, z,
                    rightXWid, yWid, zWid, depth + 1);
            }
        }
        else if (axis == 1) { // --- Split on Y ---
            // --- FIX: Calculate both widths to avoid losing pixels ---
            int leftYWid = yWid / 2;
            int rightYWid = yWid - leftYWid;
            int midY = y + leftYWid;
            // --- END FIX ---

            // Check left child (low Y)
            if (obj.intersects(x, y, z, xWid, leftYWid, zWid)) {
                left = left.insert(obj, x, y, z,
                    xWid, leftYWid, zWid, depth + 1);
            }
            // Check right child (high Y)
            if (obj.intersects(x, midY, z, xWid, rightYWid, zWid)) {
                right = right.insert(obj, x, midY, z,
                    xWid, rightYWid, zWid, depth + 1);
            }
        }
        else { // --- Split on Z ---
            // --- FIX: Calculate both widths to avoid losing pixels ---
            int leftZWid = zWid / 2;
            int rightZWid = zWid - leftZWid;
            int midZ = z + leftZWid;
            // --- END FIX ---

            // Check left child (low Z)
            if (obj.intersects(x, y, z, xWid, yWid, leftZWid)) {
                left = left.insert(obj, x, y, z,
                    xWid, yWid, leftZWid, depth + 1);
            }
            // Check right child (high Z)
            if (obj.intersects(x, y, midZ, xWid, yWid, rightZWid)) {
                right = right.insert(obj, x, y, midZ,
                    xWid, yWid, rightZWid, depth + 1);
            }
        }
        
        return this; // An internal node never replaces itself on insert
    }


    @Override
    public BintreeNode remove(AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // Determine which axis to split on
        int axis = depth % 3;

        if (axis == 0) { // --- Split on X ---
            // --- FIX: Calculate both widths ---
            int leftXWid = xWid / 2;
            int rightXWid = xWid - leftXWid;
            int midX = x + leftXWid;
            // --- END FIX ---
            if (obj.intersects(x, y, z, leftXWid, yWid, zWid)) {
                left = left.remove(obj, x, y, z,
                    leftXWid, yWid, zWid, depth + 1);
            }
            if (obj.intersects(midX, y, z, rightXWid, yWid, zWid)) {
                right = right.remove(obj, midX, y, z,
                    rightXWid, yWid, zWid, depth + 1);
            }
        }
        else if (axis == 1) { // --- Split on Y ---
            // --- FIX: Calculate both widths ---
            int leftYWid = yWid / 2;
            int rightYWid = yWid - leftYWid;
            int midY = y + leftYWid;
            // --- END FIX ---
            if (obj.intersects(x, y, z, xWid, leftYWid, zWid)) {
                left = left.remove(obj, x, y, z,
                    xWid, leftYWid, zWid, depth + 1);
            }
            if (obj.intersects(x, midY, z, xWid, rightYWid, zWid)) {
                right = right.remove(obj, x, midY, z,
                    xWid, rightYWid, zWid, depth + 1);
            }
        }
        else { // --- Split on Z ---
            // --- FIX: Calculate both widths ---
            int leftZWid = zWid / 2;
            int rightZWid = zWid - leftZWid;
            int midZ = z + leftZWid;
            // --- END FIX ---
            if (obj.intersects(x, y, z, xWid, yWid, leftZWid)) {
                left = left.remove(obj, x, y, z,
                    xWid, yWid, leftZWid, depth + 1);
            }
            if (obj.intersects(x, y, midZ, xWid, yWid, rightZWid)) {
                right = right.remove(obj, x, y, midZ,
                    xWid, yWid, rightZWid, depth + 1);
            }
        }

        // **Consolidation**
        // If both children are now empty, this internal node
        // becomes an empty node.
        if (left == EmptyNode.getInstance() && right == EmptyNode.getInstance()) {
            return EmptyNode.getInstance();
        }
        
        return this;
    }


    @Override
    public int print(StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // 1. Print this node's header
        sb.append(indent(depth));
        sb.append("I (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");
        
        int nodesPrinted = 1; // Count this node

        // 2. Determine child regions and recurse
        int axis = depth % 3;

        if (axis == 0) { // --- Split on X ---
            // --- FIX: Calculate both widths ---
            int leftXWid = xWid / 2;
            int rightXWid = xWid - leftXWid;
            int midX = x + leftXWid;
            // --- END FIX ---
            nodesPrinted += left.print(sb, x, y, z,
                leftXWid, yWid, zWid, depth + 1);
            nodesPrinted += right.print(sb, midX, y, z,
                rightXWid, yWid, zWid, depth + 1);
        }
        else if (axis == 1) { // --- Split on Y ---
            // --- FIX: Calculate both widths ---
            int leftYWid = yWid / 2;
            int rightYWid = yWid - leftYWid;
            int midY = y + leftYWid;
            // --- END FIX ---
            nodesPrinted += left.print(sb, x, y, z,
                xWid, leftYWid, zWid, depth + 1);
            nodesPrinted += right.print(sb, x, midY, z,
                xWid, rightYWid, zWid, depth + 1);
        }
        else { // --- Split on Z ---
            // --- FIX: Calculate both widths ---
            int leftZWid = zWid / 2;
            int rightZWid = zWid - leftZWid;
            int midZ = z + leftZWid;
            // --- END FIX ---
            nodesPrinted += left.print(sb, x, y, z,
                xWid, yWid, leftZWid, depth + 1);
            nodesPrinted += right.print(sb, x, y, midZ,
                xWid, yWid, rightZWid, depth + 1);
        }
            
        return nodesPrinted;
    }


    @Override
    public void collisions(StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // Internal nodes don't report collisions, they just
        // delegate to their children.
        
        int axis = depth % 3;

        if (axis == 0) { // --- Split on X ---
            // --- FIX: Calculate both widths ---
            int leftXWid = xWid / 2;
            int rightXWid = xWid - leftXWid;
            int midX = x + leftXWid;
            // --- END FIX ---
            left.collisions(sb, x, y, z, leftXWid, yWid, zWid, depth + 1);
            right.collisions(sb, midX, y, z, rightXWid, yWid, zWid, depth + 1);
        }
        else if (axis == 1) { // --- Split on Y ---
            // --- FIX: Calculate both widths ---
            int leftYWid = yWid / 2;
            int rightYWid = yWid - leftYWid;
            int midY = y + leftYWid;
            // --- END FIX ---
            left.collisions(sb, x, y, z, xWid, leftYWid, zWid, depth + 1);
            right.collisions(sb, x, midY, z, xWid, rightYWid, zWid, depth + 1);
        }
        else { // --- Split on Z ---
            // --- FIX: Calculate both widths ---
            int leftZWid = zWid / 2;
            int rightZWid = zWid - leftZWid;
            int midZ = z + leftZWid;
            // --- END FIX ---
            left.collisions(sb, x, y, z, xWid, yWid, leftZWid, depth + 1);
            right.collisions(sb, x, y, midZ, xWid, yWid, rightZWid, depth + 1);
        }
    }


    @Override
    public int intersect(
        StringBuilder sb,
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
        int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
            
        // 1. Print this node's header
        sb.append(indent(depth));
        sb.append("In Internal node (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");
        
        int nodesVisited = 1; // Count this node
        
        // 2. Determine child regions and recurse
        int axis = depth % 3;

        if (axis == 0) { // --- Split on X ---
            // --- FIX: Calculate both widths ---
            int leftXWid = xWid / 2;
            int rightXWid = xWid - leftXWid;
            int midX = x + leftXWid;
            // --- END FIX ---
            // Check left
            if (intersectsQuery(qx, qy, qz, qxwid, qywid, qzwid,
                x, y, z, leftXWid, yWid, zWid)) {
                nodesVisited += left.intersect(sb,
                    qx, qy, qz, qxwid, qywid, qzwid,
                    x, y, z, leftXWid, yWid, zWid, depth + 1);
            }
            // Check right
            if (intersectsQuery(qx, qy, qz, qxwid, qywid, qzwid,
                midX, y, z, rightXWid, yWid, zWid)) {
                nodesVisited += right.intersect(sb,
                    qx, qy, qz, qxwid, qywid, qzwid,
                    midX, y, z, rightXWid, yWid, zWid, depth + 1);
            }
        }
        else if (axis == 1) { // --- Split on Y ---
            // --- FIX: Calculate both widths ---
            int leftYWid = yWid / 2;
            int rightYWid = yWid - leftYWid;
            int midY = y + leftYWid;
            // --- END FIX ---
            // Check left
            if (intersectsQuery(qx, qy, qz, qxwid, qywid, qzwid,
                x, y, z, xWid, leftYWid, zWid)) {
                nodesVisited += left.intersect(sb,
                    qx, qy, qz, qxwid, qywid, qzwid,
                    x, y, z, xWid, leftYWid, zWid, depth + 1);
            }
            // Check right
            if (intersectsQuery(qx, qy, qz, qxwid, qywid, qzwid,
                x, midY, z, xWid, rightYWid, zWid)) {
                nodesVisited += right.intersect(sb,
                    qx, qy, qz, qxwid, qywid, qzwid,
                    x, midY, z, xWid, rightYWid, zWid, depth + 1);
            }
        }
        else { // --- Split on Z ---
            // --- FIX: Calculate both widths ---
            int leftZWid = zWid / 2;
            int rightZWid = zWid - leftZWid;
            int midZ = z + leftZWid;
            // --- END FIX ---
            // Check left
            if (intersectsQuery(qx, qy, qz, qxwid, qywid, qzwid,
                x, y, z, xWid, yWid, leftZWid)) {
                nodesVisited += left.intersect(sb,
                    qx, qy, qz, qxwid, qywid, qzwid,
                    x, y, z, xWid, yWid, leftZWid, depth + 1);
            }
            // Check right
            if (intersectsQuery(qx, qy, qz, qxwid, qywid, qzwid,
                x, y, midZ, xWid, yWid, rightZWid)) {
                nodesVisited += right.intersect(sb,
                    qx, qy, qz, qxwid, qywid, qzwid,
                    x, y, midZ, xWid, yWid, rightZWid, depth + 1);
            }
        }
        
        return nodesVisited;
    }


    /**
     * Helper method to check intersection between a query box and
     * a child region.
     */
    private boolean intersectsQuery(
        int qx, int qy, int qz, int qxw, int qyw, int qzw,
        int rx, int ry, int rz, int rxw, int ryw, int rzw) {

        // Query box coords
        int qx1 = qx;
        int qx2 = qx + qxw;
        int qy1 = qy;
        int qy2 = qy + qyw;
        int qz1 = qz;
        int qz2 = qz + qzw;
        
        // Region coords
        int rx1 = rx;
        int rx2 = rx + rxw;
        int ry1 = ry;
        int ry2 = ry + ryw;
        int rz1 = rz;
        int rz2 = rz + rzw;

        // Check for non-intersection
        boolean noXOverlap = (qx1 >= rx2) || (qx2 <= rx1);
        boolean noYOverlap = (qy1 >= ry2) || (qy2 <= ry1);
        boolean noZOverlap = (qz1 >= rz2) || (qz2 <= rz1);

        return !(noXOverlap || noYOverlap || noZOverlap);
    }
}