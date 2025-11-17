/**
 * Represents an internal node in the Bintree.
 * This class is part of the Composite design pattern.
 * It is required to make BintreeDataNode testable.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class InternalNode implements BintreeNode {

    /**
     * The children of this node.
     */
    private BintreeNode left;
    private BintreeNode right;

    /**
     * Creates a new BintreeInternalNode.
     *
     * @param left  The left child.
     * @param right The right child.
     */
    public InternalNode(BintreeNode left, BintreeNode right) {
        this.left = left;
        this.right = right;
    }


    /**
     * {@inheritDoc}
     *
     * Delegates the insertion to the appropriate child node(s)
     * based on the splitting axis for this level.
     */
    @Override
    public BintreeNode insert(
        AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {

        // Determine splitting dimension and midpoint
        int dim = depth % 3;
        int mid;
        int leftX = x, leftY = y, leftZ = z;
        int leftXWid = xWid, leftYWid = yWid, leftZWid = zWid;
        int rightX = x, rightY = y, rightZ = z;
        int rightXWid = xWid, rightYWid = yWid, rightZWid = zWid;

        switch (dim) {
            case 0: // X-axis
                mid = x + (xWid / 2);
                leftXWid = xWid / 2;
                rightX = mid;
                rightXWid = xWid - leftXWid;
                break;
            case 1: // Y-axis
                mid = y + (yWid / 2);
                leftYWid = yWid / 2;
                rightY = mid;
                rightYWid = yWid - leftYWid;
                break;
            default: // Z-axis
                mid = z + (zWid / 2);
                leftZWid = zWid / 2;
                rightZ = mid;
                rightZWid = zWid - leftZWid;
                break;
        }

        // Check which child(ren) the object intersects
        // An object is stored in EVERY leaf it intersects.
        boolean intersectsLeft = intersectsRegion(
            obj, leftX, leftY, leftZ, leftXWid, leftYWid, leftZWid);
        boolean intersectsRight = intersectsRegion(
            obj, rightX, rightY, rightZ, rightXWid, rightYWid, rightZWid);

        if (intersectsLeft) {
            left = left.insert(obj, leftX, leftY, leftZ,
                leftXWid, leftYWid, leftZWid, depth + 1);
        }
        if (intersectsRight) {
            right = right.insert(obj, rightX, rightY, rightZ,
                rightXWid, rightYWid, rightZWid, depth + 1);
        }

        return this;
    }


    /**
     * {@inheritDoc}
     *
     * Delegates the removal to the appropriate child node(s).
     */
    @Override
    public BintreeNode remove(
        AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {

        // Determine splitting dimension and midpoint (same as insert)
        int dim = depth % 3;
        int mid;
        int leftX = x, leftY = y, leftZ = z;
        int leftXWid = xWid, leftYWid = yWid, leftZWid = zWid;
        int rightX = x, rightY = y, rightZ = z;
        int rightXWid = xWid, rightYWid = yWid, rightZWid = zWid;

        switch (dim) {
            case 0: // X-axis
                mid = x + (xWid / 2);
                leftXWid = xWid / 2;
                rightX = mid;
                rightXWid = xWid - leftXWid;
                break;
            case 1: // Y-axis
                mid = y + (yWid / 2);
                leftYWid = yWid / 2;
                rightY = mid;
                rightYWid = yWid - leftYWid;
                break;
            default: // Z-axis
                mid = z + (zWid / 2);
                leftZWid = zWid / 2;
                rightZ = mid;
                rightZWid = zWid - leftZWid;
                break;
        }

        // Recurse down to find the object
        // Note: This logic assumes an object doesn't change position.
        // If it straddled, it must be removed from both.
        boolean intersectsLeft = intersectsRegion(
            obj, leftX, leftY, leftZ, leftXWid, leftYWid, leftZWid);
        boolean intersectsRight = intersectsRegion(
            obj, rightX, rightY, rightZ, rightXWid, rightYWid, rightZWid);

        if (intersectsLeft) {
            left = left.remove(obj, leftX, leftY, leftZ,
                leftXWid, leftYWid, leftZWid, depth + 1);
        }
        if (intersectsRight) {
            right = right.remove(obj, rightX, rightY, rightZ,
                rightXWid, rightYWid, rightZWid, depth + 1);
        }

        // Check for node collapse (optional, but good for testing)
        // If both children are now empty, this internal node becomes empty.
        if (left == EmptyNode.getInstance() &&
            right == EmptyNode.getInstance()) {
            return EmptyNode.getInstance();
        }

        return this;
    }


    /**
     * {@inheritDoc}
     *
     * Prints "I" for internal and recurses to children.
     */
    @Override
    public int print(
        StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        String indent = "  ".repeat(depth);
        sb.append(indent).append("I\n");

        int nodes = 1;

        // Determine splitting dimension and midpoint (same as insert)
        int dim = depth % 3;
        int mid;
        int leftX = x, leftY = y, leftZ = z;
        int leftXWid = xWid, leftYWid = yWid, leftZWid = zWid;
        int rightX = x, rightY = y, rightZ = z;
        int rightXWid = xWid, rightYWid = yWid, rightZWid = zWid;

        switch (dim) {
            case 0: mid = x + (xWid / 2); leftXWid = xWid / 2;
                rightX = mid; rightXWid = xWid - leftXWid; break;
            case 1: mid = y + (yWid / 2); leftYWid = yWid / 2;
                rightY = mid; rightYWid = yWid - leftYWid; break;
            default: mid = z + (zWid / 2); leftZWid = zWid / 2;
                rightZ = mid; rightZWid = zWid - leftZWid; break;
        }

        nodes += left.print(sb, leftX, leftY, leftZ,
            leftXWid, leftYWid, leftZWid, depth + 1);
        nodes += right.print(sb, rightX, rightY, rightZ,
            rightXWid, rightYWid, rightZWid, depth + 1);
        
        return nodes;
    }


    /**
     * {@inheritDoc}
     *
     * Recurses to children to find collisions.
     */
    @Override
    public void collisions(
        StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // Determine splitting dimension and midpoint (same as insert)
        int dim = depth % 3;
        int mid;
        int leftX = x, leftY = y, leftZ = z;
        int leftXWid = xWid, leftYWid = yWid, leftZWid = zWid;
        int rightX = x, rightY = y, rightZ = z;
        int rightXWid = xWid, rightYWid = yWid, rightZWid = zWid;

        switch (dim) {
            case 0: mid = x + (xWid / 2); leftXWid = xWid / 2;
                rightX = mid; rightXWid = xWid - leftXWid; break;
            case 1: mid = y + (yWid / 2); leftYWid = yWid / 2;
                rightY = mid; rightYWid = yWid - leftYWid; break;
            default: mid = z + (zWid / 2); leftZWid = zWid / 2;
                rightZ = mid; rightZWid = zWid - leftZWid; break;
        }

        left.collisions(sb, leftX, leftY, leftZ,
            leftXWid, leftYWid, leftZWid, depth + 1);
        right.collisions(sb, rightX, rightY, rightZ,
            rightXWid, rightYWid, rightZWid, depth + 1);
    }


    /**
     * {@inheritDoc}
     *
     * Recurses to children that intersect the query box.
     */
    @Override
    public int intersect(
        StringBuilder sb,
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
        int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
        
        int nodesVisited = 1; // Count this node

        // Determine splitting dimension and midpoint (same as insert)
        int dim = depth % 3;
        int mid;
        int leftX = x, leftY = y, leftZ = z;
        int leftXWid = xWid, leftYWid = yWid, leftZWid = zWid;
        int rightX = x, rightY = y, rightZ = z;
        int rightXWid = xWid, rightYWid = yWid, rightZWid = zWid;

        switch (dim) {
            case 0: mid = x + (xWid / 2); leftXWid = xWid / 2;
                rightX = mid; rightXWid = xWid - leftXWid; break;
            case 1: mid = y + (yWid / 2); leftYWid = yWid / 2;
                rightY = mid; rightYWid = yWid - leftYWid; break;
            default: mid = z + (zWid / 2); leftZWid = zWid / 2;
                rightZ = mid; rightZWid = zWid - leftZWid; break;
        }

        // Check if query intersects left child's region
        if (queryIntersectsRegion(qx, qy, qz, qxwid, qywid, qzwid,
            leftX, leftY, leftZ, leftXWid, leftYWid, leftZWid)) {
            nodesVisited += left.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid,
                leftX, leftY, leftZ, leftXWid, leftYWid, leftZWid, depth + 1);
        }

        // Check if query intersects right child's region
        if (queryIntersectsRegion(qx, qy, qz, qxwid, qywid, qzwid,
            rightX, rightY, rightZ, rightXWid, rightYWid, rightZWid)) {
            nodesVisited += right.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid,
                rightX, rightY, rightZ, rightXWid, rightYWid, rightZWid, depth + 1);
        }

        return nodesVisited;
    }


    /**
     * Helper method to check if an AirObject intersects a region.
     */
    private boolean intersectsRegion(
        AirObject obj,
        int rx, int ry, int rz, int rxw, int ryw, int rzw) {
        
        boolean noOverlap =
            (obj.getXorig() + obj.getXwidth() <= rx) ||
            (obj.getXorig() >= rx + rxw) ||
            (obj.getYorig() + obj.getYwidth() <= ry) ||
            (obj.getYorig() >= ry + ryw) ||
            (obj.getZorig() + obj.getZwidth() <= rz) ||
            (obj.getZorig() >= rz + rzw);
        return !noOverlap;
    }


    /**
     * Helper method to check if a query box intersects a region.
     */
    private boolean queryIntersectsRegion(
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
        int rx, int ry, int rz, int rxw, int ryw, int rzw) {
        
        boolean noOverlap =
            (qx + qxwid <= rx) ||
            (qx >= rx + rxw) ||
            (qy + qywid <= ry) ||
            (qy >= ry + ryw) ||
            (qz + qzwid <= rz) ||
            (qz >= rz + rzw);
        return !noOverlap;
    }


    /**
     * Getter for the left child (for testing).
     */
    public BintreeNode getLeft() {
        return left;
    }


    /**
     * Getter for the right child (for testing).
     */
    public BintreeNode getRight() {
        return right;
    }
}