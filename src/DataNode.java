/**
 * Represents a leaf node in the Bintree that contains data.
 * This class is part of the Composite design pattern.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class DataNode implements BintreeNode {

    /**
     * The list of AirObjects stored in this leaf node.
     * [cite_start]Using SimpleList as required, since ArrayList is not permitted. [cite: 75-76]
     */
    private SimpleList objects;

    /**
     * Creates a new, empty BintreeDataNode.
     */
    public DataNode() {
        this.objects = new SimpleList();
    }


    /**
     * {@inheritDoc}
     *
     * Inserts the object into this node's list. If the list
     * size exceeds 3, it checks the splitting criteria and
     * may split, returning a new BintreeInternalNode.
     */
    @Override
    public BintreeNode insert(
        AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {

        this.objects.add(obj);

        if (this.objects.size() > 3) {
            // Check the exception: "unless all of the boxes
            if (!this.checkAllIntersect()) {
                // SPLIT: Exception not met.
                return this.split(x, y, z, xWid, yWid, zWid, depth);
            }
            // else: Exception is met, do not split.
        }

        // No split, just return this node
        return this;
    }


    /**
     * Splits this data node into a new internal node.
     * It re-inserts all of its current objects into the new
     * internal node, which will filter them down to new children.
     *
     * @return The new BintreeInternalNode that replaces this node.
     */
    private BintreeNode split(
        int x, int y, int z, int xWid,
        int yWid, int zWid, int depth) {
        
        // Create the new internal node that will replace this data node
        BintreeNode newNode = new InternalNode(
            EmptyNode.getInstance(), EmptyNode.getInstance());

        // Re-insert all objects from this node into the new internal node.
        // The internal node's insert logic will handle
        // distributing them to the correct new children.
        for (int i = 0; i < this.objects.size(); i++) {
            AirObject obj = this.objects.get(i);
            newNode = newNode.insert(obj, x, y, z, xWid, yWid, zWid, depth);
        }

        return newNode;
    }


    /**
     * {@inheritDoc}
     *
     * Removes the object from this node's list. If the list
     * becomes empty, this node "collapses" and returns an
     * empty node.
     */
    @Override
    public BintreeNode remove(
        AirObject obj, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        this.objects.remove(obj);

        // If this was the last object, this node becomes empty
        if (this.objects.size() == 0) {
            // Use the Flyweight instance
            return EmptyNode.getInstance();
        }

        // Otherwise, this node remains
        return this;
    }


    /**
     * {@inheritDoc}
     *
     * Appends the string representation of this data node
     * and all objects it contains.
     */
    @Override
    public int print(
        StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // Append indentation
        String indent = "  ".repeat(depth);
        sb.append(indent);
        
        // Print node marker
        sb.append("Node at ");
        sb.append(x).append(" ");
        sb.append(y).append(" ");
        sb.append(z).append(", size ");
        sb.append(xWid).append(" ");
        sb.append(yWid).append(" ");
        sb.append(zWid).append(":\n");

        // Print all objects
        for (int i = 0; i < this.objects.size(); i++) {
            sb.append(indent).append("  "); // Extra indent for objects
            sb.append(this.objects.get(i).toString()); // Assumes a useful toString()
            sb.append("\n");
        }
        
        // This is one node
        return 1;
    }


    /**
     * {@inheritDoc}
     *
     * Checks for all pairwise collisions within this node's list.
     */
    @Override
    public void collisions(
        StringBuilder sb, int x, int y, int z,
        int xWid, int yWid, int zWid, int depth) {
        
        // Use a nested loop to check every unique pair
        for (int i = 0; i < this.objects.size(); i++) {
            AirObject obj1 = this.objects.get(i);
            
            for (int j = i + 1; j < this.objects.size(); j++) {
                AirObject obj2 = this.objects.get(j);
                
                // Check if the two objects intersect
                if (boxesIntersect(obj1, obj2)) {
                    // Append the collision pair
                    sb.append("Collision between ");
                    sb.append(obj1.getName()).append(" and ");
                    sb.append(obj2.getName()).append("\n");
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     *
     * Finds all objects in this node that intersect
     * with the given query box.
     */
    @Override
    public int intersect(
        StringBuilder sb,
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
        int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
        
        // Check every object in this node
        for (int i = 0; i < this.objects.size(); i++) {
            AirObject obj = this.objects.get(i);
            
            // Check if the object's box intersects the query box
            if (boxIntersectsQuery(obj, qx, qy, qz, qxwid, qywid, qzwid)) {
                // Append the object's info
                sb.append("Found: ").append(obj.toString()).append("\n");
            }
        }
        
        // This node was visited
        return 1;
    }

    // --- HELPER METHODS ---

    /**
     * Checks if all objects in this node have a common,
     * [cite_start]non-empty intersection box. [cite: 38]
     *
     * @return true if all objects intersect at a common point, false otherwise.
     */
    private boolean checkAllIntersect() {
        if (this.objects.size() == 0) {
            return true; // Vacuously true
        }

        // Start with the bounds of the first object
        AirObject first = this.objects.get(0);
        int ix = first.getXorig();
        int iy = first.getYorig();
        int iz = first.getZorig();
        int ixw = first.getXwidth();
        int iyw = first.getYwidth();
        int izw = first.getZwidth();

        // Loop through the rest of the objects
        for (int i = 1; i < this.objects.size(); i++) {
            AirObject current = this.objects.get(i);

            // Calculate the new intersection box
            int nx = Math.max(ix, current.getXorig());
            int ny = Math.max(iy, current.getYorig());
            int nz = Math.max(iz, current.getZorig());

            int nxw = Math.min(ix + ixw, current.getXorig() + current.getXwidth())
                - nx;
            int nyw = Math.min(iy + iyw, current.getYorig() + current.getYwidth())
                - ny;
            int nzw = Math.min(iz + izw, current.getZorig() + current.getZwidth())
                - nz;

            // Check if the intersection is empty
            if (nxw <= 0 || nyw <= 0 || nzw <= 0) {
                return false; // No common intersection
            }

            // Update the intersection box for the next iteration
            ix = nx;
            iy = ny;
            iz = nz;
            ixw = nxw;
            iyw = nyw;
            izw = nzw;
        }

        // If we got here, a non-empty intersection box exists for all objects
        return true;
    }


    /**
     * Helper method to check if two AirObjects intersect.
     * [cite_start]Per spec, adjacent faces are not an intersection. [cite: 40-41]
     */
    private boolean boxesIntersect(AirObject o1, AirObject o2) {
        // Check for non-overlap on any single axis
        boolean noOverlap =
            // o1 is to the left of o2
            (o1.getXorig() + o1.getXwidth() <= o2.getXorig()) ||
            // o1 is to the right of o2
            (o1.getXorig() >= o2.getXorig() + o2.getXwidth()) ||
            // o1 is below o2
            (o1.getYorig() + o1.getYwidth() <= o2.getYorig()) ||
            // o1 is above o2
            (o1.getYorig() >= o2.getYorig() + o2.getYwidth()) ||
            // o1 is behind o2
            (o1.getZorig() + o1.getZwidth() <= o2.getZorig()) ||
            // o1 is in front of o2
            (o1.getZorig() >= o2.getZorig() + o2.getZwidth());
        
        // If there is no-overlap, they don't intersect.
        // Otherwise, they must overlap.
        return !noOverlap;
    }


    /**
     * Helper method to check if an AirObject intersects a query box.
     */
    private boolean boxIntersectsQuery(
        AirObject obj,
        int qx, int qy, int qz, int qxwid, int qywid, int qzwid) {
        
        // Check for non-overlap on any single axis
        boolean noOverlap =
            // obj is to the left of query
            (obj.getXorig() + obj.getXwidth() <= qx) ||
            // obj is to the right of query
            (obj.getXorig() >= qx + qxwid) ||
            // obj is below query
            (obj.getYorig() + obj.getYwidth() <= qy) ||
            // obj is above query
            (obj.getYorig() >= qy + qywid) ||
            // obj is behind query
            (obj.getZorig() + obj.getZwidth() <= qz) ||
            // obj is in front of query
            (obj.getZorig() >= qz + qzwid);
        
        return !noOverlap;
    }
}