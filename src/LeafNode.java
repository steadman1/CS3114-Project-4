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

    @Override
    public BintreeNode insert(AirObject obj, int x, int y, int z,
                              int xWid, int yWid, int zWid, int depth) {
        data.add(obj);

        // Split condition: > 3 objects
        if (data.size() > 3) {
            // Exception: Do not split if ALL objects intersect each other's bounding boxes
            // We check if there is a common intersection area for ALL objects.
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
        
        // Start intersection box with the first object
        AirObject first = data.get(0);
        int ix = first.getXorig();
        int iy = first.getYorig();
        int iz = first.getZorig();
        int ixw = first.getXwidth();
        int iyw = first.getYwidth();
        int izw = first.getZwidth();

        for (int i = 1; i < data.size(); i++) {
            AirObject curr = data.get(i);
            
            // Calculate intersection between current 'intersection box' and 'curr'
            int maxX = Math.max(ix, curr.getXorig());
            int maxY = Math.max(iy, curr.getYorig());
            int maxZ = Math.max(iz, curr.getZorig());
            
            int minX2 = Math.min(ix + ixw, curr.getXorig() + curr.getXwidth());
            int minY2 = Math.min(iy + iyw, curr.getYorig() + curr.getYwidth());
            int minZ2 = Math.min(iz + izw, curr.getZorig() + curr.getZwidth());
            
            if (maxX >= minX2 || maxY >= minY2 || maxZ >= minZ2) {
                return false; // No overlap
            }
            
            // Update intersection box
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
        // Indentation
        for (int i = 0; i < depth; i++) sb.append("  ");
        
        sb.append("Leaf with ").append(data.size()).append(" objects (");
        sb.append(x).append(", ").append(y).append(", ").append(z).append(", ");
        sb.append(xWid).append(", ").append(yWid).append(", ").append(zWid).append(") ");
        sb.append(depth).append("\n");

        for (int i = 0; i < data.size(); i++) {
            AirObject obj = data.get(i);
            for (int j = 0; j < depth; j++) sb.append("  ");
            sb.append("(").append(obj.toString()).append(")\n");
        }
        return 1; // Counts as 1 node
    }

    @Override
    public void collisions(StringBuilder sb, int x, int y, int z,
                           int xWid, int yWid, int zWid, int depth) {
        // Check all pairs
        for (int i = 0; i < data.size() - 1; i++) {
            for (int j = i + 1; j < data.size(); j++) {
                AirObject a = data.get(i);
                AirObject b = data.get(j);
                
                if (a.intersects(b)) {
                    // Logic to report only if intersection origin is in this node
                    int ix = Math.max(a.getXorig(), b.getXorig());
                    int iy = Math.max(a.getYorig(), b.getYorig());
                    int iz = Math.max(a.getZorig(), b.getZorig());
                    
                    // Check if (ix, iy, iz) is inside this leaf's region
                    // Region: [x, x+xWid), [y, y+yWid), [z, z+zWid)
                    if (ix >= x && ix < x + xWid &&
                        iy >= y && iy < y + yWid &&
                        iz >= z && iz < z + zWid) {
                        
                        sb.append("In leaf node (").append(x).append(", ").append(y).append(", ").append(z);
                        sb.append(", ").append(xWid).append(", ").append(yWid).append(", ").append(zWid);
                        sb.append(") ").append(depth).append("\n");
                        sb.append("(").append(a.toString()).append(") and (").append(b.toString()).append(")\n");
                    }
                }
            }
        }
    }

    @Override
    public int intersect(StringBuilder sb, int qx, int qy, int qz,
                         int qxwid, int qywid, int qzwid,
                         int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
        
        // We are visiting this leaf
        // Check if objects intersect query
        int matches = 0;
        // We buffer matches to match format "In leaf node...\n obj1\n obj2\n"
        StringBuilder localSb = new StringBuilder();
        
        for (int i = 0; i < data.size(); i++) {
            AirObject obj = data.get(i);
            if (obj.intersects(qx, qy, qz, qxwid, qywid, qzwid)) {
                localSb.append(obj.toString()).append("\n");
                matches++;
            }
        }
        
        if (matches > 0) {
            sb.append("In leaf node (").append(x).append(", ").append(y).append(", ").append(z);
            sb.append(", ").append(xWid).append(", ").append(yWid).append(", ").append(zWid);
            sb.append(") ").append(depth).append("\n");
            sb.append(localSb);
        }
        
        return 1; // 1 node visited
    }
}