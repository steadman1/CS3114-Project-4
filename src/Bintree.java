/**
 * The main Bintree class.
 * This class acts as the public interface for the
 * Bintree node system and manages the root node.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class Bintree {

    private BintreeNode root;
    private int worldSize;

    public Bintree(int worldSize) {
        this.worldSize = worldSize;
        this.root = EmptyNode.getInstance();
    }

    public void insert(AirObject obj) {
        root = root.insert(obj, 0, 0, 0,
            worldSize, worldSize, worldSize, 0);
    }

    public void remove(AirObject obj) {
        root = root.remove(obj, 0, 0, 0,
            worldSize, worldSize, worldSize, 0);
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        int nodeCount = root.print(sb, 0, 0, 0,
            worldSize, worldSize, worldSize, 0);
        sb.append(nodeCount);
        sb.append(" Bintree nodes printed\n");
        return sb.toString();
    }
    
    public String collisions() {
        StringBuilder sb = new StringBuilder();
        sb.append("The following collisions exist in the database:\n");
        root.collisions(sb, 0, 0, 0, worldSize, worldSize, worldSize, 0);
        return sb.toString();
    }
    
    public String intersect(int x, int y, int z, int xwid, int ywid, int zwid) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "The following objects intersect (%d %d %d %d %d %d):\n",
            x, y, z, xwid, ywid, zwid));
            
        int nodesVisited = root.intersect(
             sb, x, y, z, xwid, ywid, zwid,
             0, 0, 0, worldSize, worldSize, worldSize, 0);
            
        sb.append(nodesVisited);
        sb.append(" nodes were visited in the bintree\n");
        return sb.toString();
    }
}