/**
 * Represents a leaf node in the Bintree that contains data.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class DataNode implements BintreeNode {

    /**
     * A list of AirObjects in this node.
     * We must use our own list class, not ArrayList.
     */
    private SimpleList objects;

    /**
     * Max objects before we *must* try to split.
     * The rule is "splits if it contains more than three boxes"
     */
    private static final int MAX_OBJECTS = 3;


    /**
     * Creates a new, empty DataNode.
     */
    public DataNode() {
        this.objects = new SimpleList();
    }


    /**
     * Helper to create the indentation string.
     * @param depth The current depth.
     * @return A string of spaces.
     */
    private String indent(int depth) {
        return "  ".repeat(depth);
    }


    /**
     * Checks the split criteria: "unless all of the boxes
     * have a non-empty intersection box"
     * @return True if all objects intersect, false otherwise.
     */
    private boolean checkAllIntersect() {
        if (objects.size() <= 1) {
            return true;
        }
        
        // Find the intersection of all boxes.
        // Start with the bounds of the first box.
        AirObject first = objects.get(0);
        int ix1 = first.getXorig();
        int iy1 = first.getYorig();
        int iz1 = first.getZorig();
        int ix2 = ix1 + first.getXwidth();
        int iy2 = iy1 + first.getYwidth();
        int iz2 = iz1 + first.getZwidth();

        for (int i = 1; i < objects.size(); i++) {
            AirObject current = objects.get(i);
            int cx1 = current.getXorig();
            int cy1 = current.getYorig();
            int cz1 = current.getZorig();
            int cx2 = cx1 + current.getXwidth();
            int cy2 = cy1 + current.getYwidth();
            int cz2 = cz1 + current.getZwidth();

            // Update the intersection box
            ix1 = Math.max(ix1, cx1);
            iy1 = Math.max(iy1, cy1);
            iz1 = Math.max(iz1, cz1);
            ix2 = Math.min(ix2, cx2);
            iy2 = Math.min(iy2, cy2);
            iz2 = Math.min(iz2, cz2);

            // Check if the intersection is non-empty
            // Per spec, touching (width=0) is empty.
            if (ix1 >= ix2 || iy1 >= iy2 || iz1 >= iz2) {
                return false; // Intersection is empty
            }
        }
        return true; // Intersection is non-empty
    }


    @Override
    public BintreeNode insert(AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // 1. Add the object to the list.
        objects.add(obj);

        // 2. Check if we need to split.
        if (objects.size() > MAX_OBJECTS) {
            // We have 4 (or more) objects.
            
            // 3. Check the split criteria
            boolean allIntersect = checkAllIntersect();
            
            if (!allIntersect) {
                // 4. We must split!
                // 4a. Create a new InternalNode.
                //    (This node will be a Bintree internal node now)
                InternalNode newInternal = new InternalNode();
                
                // 4c. Re-insert all objects *from this node* into the
                //     new InternalNode.
                for (int i = 0; i < objects.size(); i++) {
                    // Pass the new region parameters
                    newInternal.insert(objects.get(i), x, y, z,
                        xWid, yWid, zWid, depth);
                }
                
                // 4d. Return the new InternalNode
                return newInternal;
            }
            // If all objects *do* intersect, we don't split.
        }

        // If we didn't split, just return ourselves.
        return this;
    }


    @Override
    public BintreeNode remove(AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // 1-3. Find and remove the object.
        objects.remove(obj);
        
        // 4. Check if this node is now empty.
        if (objects.size() == 0) {
            // 5. If it is, return the Flyweight EmptyNode.
            return EmptyNode.getInstance();
        }
        
        // 6. Otherwise, return this.
        return this;
    }


    @Override
    public int print(StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {

        // 1-6. Print header
        sb.append(indent(depth));
        sb.append("Leaf with ").append(objects.size()).append(" objects (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        // Use the new region dimensions
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");
        
        // 7. Loop through all objects
        String objIndent = indent(depth + 1);
        for (int i = 0; i < objects.size(); i++) {
            sb.append(objIndent);
            sb.append("(").append(objects.get(i).toString()).append(")\n");
        }
        
        return 1; // This is 1 node.
    }


    @Override
    public void collisions(StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // 1. Print the "In leaf node..." header
        sb.append(indent(depth));
        sb.append("In leaf node (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        // Use the new region dimensions
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");
        
        // 2. Iterate through all pairs of objects
        String objIndent = indent(depth + 1);
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                AirObject obj1 = objects.get(i);
                AirObject obj2 = objects.get(j);
                
                if (obj1.intersects(obj2)) {
                    // 3. If they intersect, format the output
                    sb.append(objIndent);
                    sb.append("(").append(obj1.toString()).append(")");
                    sb.append(" and ");
                    sb.append("(").append(obj2.toString()).append(")\n");
                }
            }
        }
    }


    @Override
    public int intersect(
        StringBuilder sb,
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
        int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
            
        // 1. Print the "In leaf node..." header
        sb.append(indent(depth));
        sb.append("In leaf node (");
        sb.append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ");
        // Use the new region dimensions
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ");
        sb.append(depth);
        sb.append("\n");
        
        // 2. Iterate through all objects
        String objIndent = indent(depth + 1);
        for (int i = 0; i < objects.size(); i++) {
            AirObject obj = objects.get(i);
            
            // 3. Check if object intersects the query box
            if (obj.intersects(qx, qy, qz, qxwid, qywid, qzwid)) {
                // 4. If it does, print the object's toString
                sb.append(objIndent);
                sb.append(obj.toString());
                sb.append("\n");
            }
        }
        return 1; // Visited this 1 node
    }
}