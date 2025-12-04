/**
 * Represents a leaf node in the Bintree that contains AirObjects.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class LeafNode implements BintreeNode {

    private SimpleList data;

    /**
     * Constructor.
     */
    public LeafNode() {
        data = new SimpleList();
    }

    /**
     * Expose data for InternalNode to use during merge operations.
     * @return The list of objects in this leaf.
     */
    public SimpleList getData() {
        return data;
    }

    @Override
    public BintreeNode insert(AirObject obj, int x, int y, int z,
                              int xWid, int yWid, int zWid, int depth) {
        data.add(obj);

        // Split condition: > 3 objects
        if (data.size() > 3) {
            // Exception: Do not split if ALL objects intersect each other's bounding boxes
            if (!allIntersect()) {
                // Split required
                InternalNode newInternal = new InternalNode();
                // Re-insert all objects into the new internal node
                for (int i = 0; i < data.size(); i++) {
                    newInternal.insert(data.get(i), x, y, z, xWid, yWid, zWid, depth);
                }
                return newInternal;
            }
        }
        return this;
    }

    /**
     * Helper to check if all objects in the list share a common intersection.
     * @return True if they all intersect, false otherwise.
     */
    private boolean allIntersect() {
        if (data.size() == 0) return false;
        
        AirObject first = data.get(0);
        int ix = first.getXorig();
        int iy = first.getYorig();
        int iz = first.getZorig();
        int ixw = first.getXwidth();
        int iyw = first.getYwidth();
        int izw = first.getZwidth();

        for (int i = 1; i < data.size(); i++) {
            AirObject curr = data.get(i);
            
            int maxX = Math.max(ix, curr.getXorig());
            int maxY = Math.max(iy, curr.getYorig());
            int maxZ = Math.max(iz, curr.getZorig());
            
            int minX2 = Math.min(ix + ixw, curr.getXorig() + curr.getXwidth());
            int minY2 = Math.min(iy + iyw, curr.getYorig() + curr.getYwidth());
            int minZ2 = Math.min(iz + izw, curr.getZorig() + curr.getZwidth());
            
            if (maxX >= minX2 || maxY >= minY2 || maxZ >= minZ2) {
                return false; // No overlap
            }
            
            ix = maxX;
            iy = maxY;
            iz = maxZ;
            ixw = minX2 - ix;
            iyw = minY2 - iy;
            izw = minZ2 - iz;
        }
        return true;
    }

    @Override
    public BintreeNode remove(AirObject obj, int x, int y, int z,
                              int xWid, int yWid, int zWid, int depth) {
        data.remove(obj);
        if (data.size() == 0) {
            return EmptyNode.getInstance();
        }
        return this;
    }

    @Override
    public int print(StringBuilder sb, int x, int y, int z,
                     int xWid, int yWid, int zWid, int depth) {
        for (int i = 0; i < depth; i++) sb.append("  ");
        
        sb.append("Leaf with ").append(data.size()).append(" objects (");
        sb.append(x).append(", ").append(y).append(", ").append(z).append(", ");
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid).append(") ");
        sb.append(depth).append("\n");

        // 1. Copy to Array
        AirObject[] items = new AirObject[data.size()];
        for(int i = 0; i < data.size(); i++) {
            items[i] = data.get(i);
        }

        // 2. Sort Array (Alphabetical)
        for(int i = 0; i < items.length - 1; i++) {
            for(int j = 0; j < items.length - i - 1; j++) {
                if(items[j].compareTo(items[j+1]) > 0) {
                    AirObject temp = items[j];
                    items[j] = items[j+1];
                    items[j+1] = temp;
                }
            }
        }

        // 3. Print Sorted Array
        for (int i = 0; i < items.length; i++) {
            AirObject obj = items[i];
            for (int j = 0; j < depth; j++) sb.append("  ");
            sb.append("(").append(obj.toString()).append(")\n");
        }
        return 1;
    }

    @Override
    public void collisions(StringBuilder sb, int x, int y, int z,
                           int xWid, int yWid, int zWid, int depth) {
        // FIX 1: Print header unconditionally (outside the loop)
        sb.append("In leaf node (").append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ").append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ").append(depth).append("\n");

        for (int i = 0; i < data.size() - 1; i++) {
            for (int j = i + 1; j < data.size(); j++) {
                AirObject a = data.get(i);
                AirObject b = data.get(j);
                
                if (a.intersects(b)) {
                    int ix = Math.max(a.getXorig(), b.getXorig());
                    int iy = Math.max(a.getYorig(), b.getYorig());
                    int iz = Math.max(a.getZorig(), b.getZorig());
                    
                    if (ix >= x && ix < x + xWid &&
                        iy >= y && iy < y + yWid &&
                        iz >= z && iz < z + zWid) {
                        
                        if (a.compareTo(b) <= 0) {
                            sb.append("(").append(a.toString()).append(") and (").append(b.toString()).append(")\n");
                        } else {
                            sb.append("(").append(b.toString()).append(") and (").append(a.toString()).append(")\n");
                        }
                    }
                }
            }
        }
    }

    @Override
    public int intersect(StringBuilder sb, int qx, int qy, int qz,
                         int qxwid, int qywid, int qzwid,
                         int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
        
        // FIX 2: Print header unconditionally because InternalNode called us (so we are visited)
        sb.append("In leaf node (").append(x).append(", ").append(y).append(", ").append(z);
        sb.append(", ").append(xWid).append(", ").append(yWid).append(", ").append(zWid);
        sb.append(") ").append(depth).append("\n");

        AirObject[] matches = new AirObject[data.size()];
        int count = 0;
        
        for (int i = 0; i < data.size(); i++) {
            AirObject obj = data.get(i);
            
            // Check 1: Does object intersect the query?
            if (obj.intersects(qx, qy, qz, qxwid, qywid, qzwid)) {
                
                // FIX 3: Check 2 (Duplicate Avoidance)
                // Only print if the object's origin is strictly within THIS node
                // This prevents printing the same object multiple times if it spans leaves
                if (obj.getXorig() >= x && obj.getXorig() < x + xWid &&
                    obj.getYorig() >= y && obj.getYorig() < y + yWid &&
                    obj.getZorig() >= z && obj.getZorig() < z + zWid) {
                    
                    matches[count++] = obj;
                }
            }
        }
        
        if (count > 0) {
            // Sort Array (Alphabetical)
            for (int i = 0; i < count - 1; i++) {
                for (int j = 0; j < count - i - 1; j++) {
                    if (matches[j].compareTo(matches[j+1]) > 0) {
                        AirObject temp = matches[j];
                        matches[j] = matches[j+1];
                        matches[j+1] = temp;
                    }
                }
            }
            
            // Print Sorted Matches
            for (int i = 0; i < count; i++) {
                sb.append(matches[i].toString()).append("\n");
            }
        }
        
        return 1; 
    }
}