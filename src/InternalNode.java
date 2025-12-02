/**
 * Represents an internal node in the Bintree.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class InternalNode implements BintreeNode {

    private BintreeNode left;
    private BintreeNode right;

    public InternalNode() {
        left = EmptyNode.getInstance();
        right = EmptyNode.getInstance();
    }

    @Override
    public BintreeNode insert(AirObject obj, int x, int y, int z,
                              int xWid, int yWid, int zWid, int depth) {
        
        int axis = depth % 3; // 0=x, 1=y, 2=z
        
        // Determine bounds of children
        if (axis == 0) { // X split
            int half = xWid / 2;
            // Left child: x, width half. Right child: x+half, width half.
            // Check intersection with left
            if (obj.intersects(x, y, z, half, yWid, zWid)) {
                left = left.insert(obj, x, y, z, half, yWid, zWid, depth + 1);
            }
            // Check intersection with right
            if (obj.intersects(x + half, y, z, half, yWid, zWid)) {
                right = right.insert(obj, x + half, y, z, half, yWid, zWid, depth + 1);
            }
        } else if (axis == 1) { // Y split
            int half = yWid / 2;
            if (obj.intersects(x, y, z, xWid, half, zWid)) {
                left = left.insert(obj, x, y, z, xWid, half, zWid, depth + 1);
            }
            if (obj.intersects(x, y + half, z, xWid, half, zWid)) {
                right = right.insert(obj, x, y + half, z, xWid, half, zWid, depth + 1);
            }
        } else { // Z split
            int half = zWid / 2;
            if (obj.intersects(x, y, z, xWid, yWid, half)) {
                left = left.insert(obj, x, y, z, xWid, yWid, half, depth + 1);
            }
            if (obj.intersects(x, y, z + half, xWid, yWid, half)) {
                right = right.insert(obj, x, y, z + half, xWid, yWid, half, depth + 1);
            }
        }
        
        return this;
    }

    @Override
    public BintreeNode remove(AirObject obj, int x, int y, int z,
                              int xWid, int yWid, int zWid, int depth) {
        int axis = depth % 3;
        
        if (axis == 0) { // X split
            int half = xWid / 2;
            if (obj.intersects(x, y, z, half, yWid, zWid)) {
                left = left.remove(obj, x, y, z, half, yWid, zWid, depth + 1);
            }
            if (obj.intersects(x + half, y, z, half, yWid, zWid)) {
                right = right.remove(obj, x + half, y, z, half, yWid, zWid, depth + 1);
            }
        } else if (axis == 1) { // Y split
            int half = yWid / 2;
            if (obj.intersects(x, y, z, xWid, half, zWid)) {
                left = left.remove(obj, x, y, z, xWid, half, zWid, depth + 1);
            }
            if (obj.intersects(x, y + half, z, xWid, half, zWid)) {
                right = right.remove(obj, x, y + half, z, xWid, half, zWid, depth + 1);
            }
        } else { // Z split
            int half = zWid / 2;
            if (obj.intersects(x, y, z, xWid, yWid, half)) {
                left = left.remove(obj, x, y, z, xWid, yWid, half, depth + 1);
            }
            if (obj.intersects(x, y, z + half, xWid, yWid, half)) {
                right = right.remove(obj, x, y, z + half, xWid, yWid, half, depth + 1);
            }
        }

        // Flyweight collapse: If both children are empty, this node becomes empty
        if (left instanceof EmptyNode && right instanceof EmptyNode) {
            return EmptyNode.getInstance();
        }
        
        return this;
    }

    @Override
    public int print(StringBuilder sb, int x, int y, int z,
                     int xWid, int yWid, int zWid, int depth) {
        for (int i = 0; i < depth; i++) sb.append("  ");
        sb.append("I (").append(x).append(", ").append(y).append(", ").append(z).append(", ");
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid).append(") ");
        sb.append(depth).append("\n");
        
        int count = 1;
        int axis = depth % 3;
        if (axis == 0) {
            int half = xWid / 2;
            count += left.print(sb, x, y, z, half, yWid, zWid, depth + 1);
            count += right.print(sb, x + half, y, z, half, yWid, zWid, depth + 1);
        } else if (axis == 1) {
            int half = yWid / 2;
            count += left.print(sb, x, y, z, xWid, half, zWid, depth + 1);
            count += right.print(sb, x, y + half, z, xWid, half, zWid, depth + 1);
        } else {
            int half = zWid / 2;
            count += left.print(sb, x, y, z, xWid, yWid, half, depth + 1);
            count += right.print(sb, x, y, z + half, xWid, yWid, half, depth + 1);
        }
        return count;
    }

    @Override
    public void collisions(StringBuilder sb, int x, int y, int z,
                           int xWid, int yWid, int zWid, int depth) {
        // Internal nodes just recurse.
        // BUT we check for collisions in the left and right children.
        // We do NOT check across the split here because objects are duplicated in both leaves if they overlap.
        
        int axis = depth % 3;
        if (axis == 0) {
            int half = xWid / 2;
            left.collisions(sb, x, y, z, half, yWid, zWid, depth + 1);
            right.collisions(sb, x + half, y, z, half, yWid, zWid, depth + 1);
        } else if (axis == 1) {
            int half = yWid / 2;
            left.collisions(sb, x, y, z, xWid, half, zWid, depth + 1);
            right.collisions(sb, x, y + half, z, xWid, half, zWid, depth + 1);
        } else {
            int half = zWid / 2;
            left.collisions(sb, x, y, z, xWid, yWid, half, depth + 1);
            right.collisions(sb, x, y, z + half, xWid, yWid, half, depth + 1);
        }
    }

    @Override
    public int intersect(StringBuilder sb, int qx, int qy, int qz,
                         int qxwid, int qywid, int qzwid,
                         int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
        
        // Check intersection with children
        // But first, report visit to this internal node?
        // "In Internal node..."
        // Only if it intersects the query.
        // Since we are IN this node, we assume the parent checked overlap (or it's root).
        
        // Output format requires listing the Internal node first if we visit it/it overlaps
        sb.append("In Internal node (").append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ").append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ").append(depth).append("\n");

        int visited = 1;
        int axis = depth % 3;
        
        // Need to check overlap with children before recursing to act efficiently
        // Overlap logic helper
        
        if (axis == 0) {
            int half = xWid / 2;
            // Left box: x, y, z, half, yWid, zWid
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y, z, half, yWid, zWid)) {
                visited += left.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, x, y, z, half, yWid, zWid, depth + 1);
            }
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x + half, y, z, half, yWid, zWid)) {
                visited += right.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, x + half, y, z, half, yWid, zWid, depth + 1);
            }
        } else if (axis == 1) {
            int half = yWid / 2;
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y, z, xWid, half, zWid)) {
                visited += left.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, x, y, z, xWid, half, zWid, depth + 1);
            }
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y + half, z, xWid, half, zWid)) {
                visited += right.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, x, y + half, z, xWid, half, zWid, depth + 1);
            }
        } else {
            int half = zWid / 2;
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y, z, xWid, yWid, half)) {
                visited += left.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, x, y, z, xWid, yWid, half, depth + 1);
            }
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y, z + half, xWid, yWid, half)) {
                visited += right.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, x, y, z + half, xWid, yWid, half, depth + 1);
            }
        }
        
        return visited;
    }
    
    private boolean boxesOverlap(int x1, int y1, int z1, int w1, int h1, int d1,
                                 int x2, int y2, int z2, int w2, int h2, int d2) {
        return x1 < x2 + w2 && x1 + w1 > x2 &&
               y1 < y2 + h2 && y1 + h1 > y2 &&
               z1 < z2 + d2 && z1 + d1 > z2;
    }
}