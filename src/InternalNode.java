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
        int newDepth = depth + 1;
        
        if (axis == 0) { // X split
            int half = xWid / 2;
            if (obj.intersects(x, y, z, half, yWid, zWid)) {
                left = left.insert(obj, x, y, z, half, yWid, zWid, 
                    newDepth);
            }
            int newX = x + half;
            if (obj.intersects(newX, y, z, half, yWid, zWid)) {
                right = right.insert(obj, newX, y, z, half, yWid, zWid, 
                    newDepth);
            }
        } 
        else if (axis == 1) { // Y split
            int half = yWid / 2;
            if (obj.intersects(x, y, z, xWid, half, zWid)) {
                left = left.insert(obj, x, y, z, xWid, half, zWid, 
                    newDepth);
            }
            int newY = y + half;
            if (obj.intersects(x, newY, z, xWid, half, zWid)) {
                right = right.insert(obj, x, newY, z, xWid, half, zWid, 
                    newDepth);
            }
        } 
        else { // Z split
            int half = zWid / 2;
            if (obj.intersects(x, y, z, xWid, yWid, half)) {
                left = left.insert(obj, x, y, z, xWid, yWid, half, 
                    newDepth);
            }
            int newZ = z + half;
            if (obj.intersects(x, y, newZ, xWid, yWid, half)) {
                right = right.insert(obj, x, y, newZ, xWid, yWid, half, 
                    newDepth);
            }
        }
        
        return this;
    }

    @Override
    public BintreeNode remove(AirObject obj, int x, int y, int z,
                              int xWid, int yWid, int zWid, int depth) {
        int axis = depth % 3;
        int newDepth = depth + 1;
        
        if (axis == 0) { // X split
            int half = xWid / 2;
            if (obj.intersects(x, y, z, half, yWid, zWid)) {
                left = left.remove(obj, x, y, z, half, yWid, zWid, 
                    newDepth);
            }
            int newX = x + half;
            if (obj.intersects(newX, y, z, half, yWid, zWid)) {
                right = right.remove(obj, newX, y, z, half, yWid, zWid, 
                    newDepth);
            }
        } 
        else if (axis == 1) { // Y split
            int half = yWid / 2;
            if (obj.intersects(x, y, z, xWid, half, zWid)) {
                left = left.remove(obj, x, y, z, xWid, half, zWid, 
                    newDepth);
            }
            int newY = y + half;
            if (obj.intersects(x, newY, z, xWid, half, zWid)) {
                right = right.remove(obj, x, newY, z, xWid, half, zWid, 
                    newDepth);
            }
        } 
        else { // Z split
            int half = zWid / 2;
            if (obj.intersects(x, y, z, xWid, yWid, half)) {
                left = left.remove(obj, x, y, z, xWid, yWid, half, 
                    newDepth);
            }
            int newZ = z + half;
            if (obj.intersects(x, y, newZ, xWid, yWid, half)) {
                right = right.remove(obj, x, y, newZ, xWid, yWid, half, 
                    newDepth);
            }
        }

        // 1. Basic Flyweight Collapse
        if (left instanceof EmptyNode && right instanceof EmptyNode) {
            return EmptyNode.getInstance();
        }
        
        // 2. MERGE LOGIC (Without ArrayList/HashSet)
        // Check if we can merge the children back into a single LeafNode
        if (shouldMerge()) {
            LeafNode newLeaf = new LeafNode();
            
            // Add unique objects from left and right to newLeaf
            // Since we already determined we can merge, we know the total 
            // unique count is safe
            addUnique(newLeaf, left);
            addUnique(newLeaf, right);
            
            return newLeaf;
        }
        
        return this;
    }
    
    /**
     * Helper to determine if children should be merged.
     * Merges if total UNIQUE objects <= 3.
     */
    private boolean shouldMerge() {
        if (!isLeafOrEmpty(left) || !isLeafOrEmpty(right)) {
            return false;
        }
        
        // Count unique objects
        // Use a temporary array to track what we've seen to avoid double 
        // counting. Since max objects to consider merge is small (e.g. 3+3=6), 
        // array is fine
        AirObject[] temp = new AirObject[100]; 
        int count = 0;
        
        count = collectUnique(temp, count, left);
        count = collectUnique(temp, count, right);
        
        return count <= 3;
    }
    
    private boolean isLeafOrEmpty(BintreeNode node) {
        return node instanceof LeafNode || node instanceof EmptyNode;
    }
    
    private int collectUnique(AirObject[] arr, int count, BintreeNode node) {
        if (node instanceof LeafNode) {
            SimpleList list = ((LeafNode) node).getData();
            for (int i = 0; i < list.size(); i++) {
                AirObject obj = list.get(i);
                boolean found = false;
                for (int k = 0; k < count; k++) {
                    if (arr[k] == obj) { 
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    arr[count++] = obj;
                }
            }
        }
        return count;
    }
    
    /**
     * Adds objects from node to the newLeaf, 
     * avoiding duplicates in the newLeaf.
     */
    private void addUnique(LeafNode target, BintreeNode source) {
        if (source instanceof LeafNode) {
            SimpleList list = ((LeafNode) source).getData();
            SimpleList targetList = target.getData();
            
            for (int i = 0; i < list.size(); i++) {
                AirObject obj = list.get(i);
                
                // Check if already in target (duplicate check)
                boolean found = false;
                for (int k = 0; k < targetList.size(); k++) {
                    if (targetList.get(k) == obj) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    targetList.add(obj);
                }
            }
        }
    }

    @Override
    public int print(StringBuilder sb, int x, int y, int z,
                     int xWid, int yWid, int zWid, int depth) {
        for (int i = 0; i < depth; i++) sb.append("  ");
        sb.append("I (").append(x).append(", ").append(y).append(", ")
          .append(z).append(", ");
        sb.append(xWid).append(", ").append(yWid).append(", ")
          .append(zWid).append(") ");
        sb.append(depth).append("\n");
        
        int count = 1;
        int axis = depth % 3;
        int newDepth = depth + 1;
        if (axis == 0) {
            int half = xWid / 2;
            count += left.print(sb, x, y, z, half, yWid, zWid, newDepth);
            count += right.print(sb, x + half, y, z, half, yWid, zWid, 
                    newDepth);
        } 
        else if (axis == 1) {
            int half = yWid / 2;
            count += left.print(sb, x, y, z, xWid, half, zWid, newDepth);
            count += right.print(sb, x, y + half, z, xWid, half, zWid, 
                    newDepth);
        } 
        else {
            int half = zWid / 2;
            count += left.print(sb, x, y, z, xWid, yWid, half, newDepth);
            count += right.print(sb, x, y, z + half, xWid, yWid, half, 
                    newDepth);
        }
        return count;
    }

    @Override
    public void collisions(StringBuilder sb, int x, int y, int z,
                           int xWid, int yWid, int zWid, int depth) {
        int axis = depth % 3;
        int newDepth = depth + 1;
        if (axis == 0) {
            int half = xWid / 2;
            left.collisions(sb, x, y, z, half, yWid, zWid, newDepth);
            right.collisions(sb, x + half, y, z, half, yWid, zWid, 
                    newDepth);
        } 
        else if (axis == 1) {
            int half = yWid / 2;
            left.collisions(sb, x, y, z, xWid, half, zWid, newDepth);
            right.collisions(sb, x, y + half, z, xWid, half, zWid, 
                    newDepth);
        } 
        else {
            int half = zWid / 2;
            left.collisions(sb, x, y, z, xWid, yWid, half, newDepth);
            right.collisions(sb, x, y, z + half, xWid, yWid, half, 
                    newDepth);
        }
    }

    @Override
    public int intersect(StringBuilder sb, int qx, int qy, int qz,
                         int qxwid, int qywid, int qzwid,
                         int x, int y, int z, int xWid, int yWid, int zWid, 
                         int depth) {
        
        sb.append("In Internal node (").append(x).append(", ").append(y)
          .append(", ").append(z);
        sb.append(", ").append(xWid).append(", ").append(yWid).append(", ")
          .append(zWid);
        sb.append(") ").append(depth).append("\n");

        int visited = 1;
        int axis = depth % 3;
        int newDepth = depth + 1;
        
        if (axis == 0) {
            int half = xWid / 2;
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y, z, half, 
                             yWid, zWid)) {
                visited += left.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, 
                                         x, y, z, half, yWid, zWid, 
                                         newDepth);
            }
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x + half, y, z, 
                             half, yWid, zWid)) {
                visited += right.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, 
                                          x + half, y, z, half, yWid, zWid, 
                                          newDepth);
            }
        } 
        else if (axis == 1) {
            int half = yWid / 2;
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y, z, xWid, 
                             half, zWid)) {
                visited += left.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, 
                                         x, y, z, xWid, half, zWid, 
                                         newDepth);
            }
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y + half, z, 
                             xWid, half, zWid)) {
                visited += right.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, 
                                          x, y + half, z, xWid, half, zWid, 
                                          newDepth);
            }
        } 
        else {
            int half = zWid / 2;
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y, z, xWid, 
                             yWid, half)) {
                visited += left.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, 
                                         x, y, z, xWid, yWid, half, 
                                         newDepth);
            }
            if (boxesOverlap(qx, qy, qz, qxwid, qywid, qzwid, x, y, z + half, 
                             xWid, yWid, half)) {
                visited += right.intersect(sb, qx, qy, qz, qxwid, qywid, qzwid, 
                                          x, y, z + half, xWid, yWid, half, 
                                          newDepth);
            }
        }
        
        return visited;
    }
    
    public boolean boxesOverlap(int x1, int y1, int z1, int w1, int h1, int d1,
                                 int x2, int y2, int z2, int w2, int h2, 
                                 int d2) {
        return x1 < x2 + w2 && x1 + w1 > x2 &&
               y1 < y2 + h2 && y1 + h1 > y2 &&
               z1 < z2 + d2 && z1 + d1 > z2;
    }
}