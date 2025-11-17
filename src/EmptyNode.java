/**
 * Represents an empty leaf node in the Bintree.
 * This is the Flyweight object. There should be only
 * one instance of this class.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
 /**
  * Represents an empty leaf node in the Bintree.
  * This class is an implementation of the Flyweight design pattern.
  *
  * @author adsleptsov
  * @version Fall 2025
  */
 public class EmptyNode implements BintreeNode {
 
     /**
      * The single, static instance of the empty node (Flyweight).
      */
     private static final EmptyNode INSTANCE = new EmptyNode();
 
     /**
      * Private constructor to enforce the Flyweight pattern.
      */
     private EmptyNode() {
         // Private constructor
     }
 
 
     /**
      * Gets the single instance of the empty node.
      *
      * @return The singleton BintreeEmptyNode.
      */
     public static EmptyNode getInstance() {
         return INSTANCE;
     }
 
 
     /**
      * {@inheritDoc}
      *
      * When an object is inserted into an empty node, this node
      * "becomes" a BintreeDataNode. It creates and returns a new
      * data node, which will then handle the insertion.
      */
     @Override
     public BintreeNode insert(
         AirObject obj, int x, int y, int z,
         int xWid, int yWid, int zWid, int depth) {
         
         // Create a new data node
         BintreeNode dataNode = new DataNode();
         
         // Delegate the insert to the new data node and return it
         return dataNode.insert(obj, x, y, z, xWid, yWid, zWid, depth);
     }
 
 
     /**
      * {@inheritDoc}
      *
      * Removing an object from an empty node does nothing,
      * as the object cannot be present.
      */
     @Override
     public BintreeNode remove(
         AirObject obj, int x, int y, int z,
         int xWid, int yWid, int zWid, int depth) {
         
         // The object isn't here, so just return the empty node (this)
         return this;
     }
 
 
     /**
      * {@inheritDoc}
      *
      * Appends the representation for an empty leaf ("E")
      * to the StringBuilder, indented by depth.
      */
     @Override
     public int print(
         StringBuilder sb, int x, int y, int z,
         int xWid, int yWid, int zWid, int depth) {
         
         // Append indentation
         for (int i = 0; i < depth; i++) {
             sb.append("  ");
         }
         
         // Append empty node marker
         sb.append("E\n");
         
         // This is one node
         return 1;
     }
 
 
     /**
      * {@inheritDoc}
      *
      * An empty node has no objects, so it cannot have any collisions.
      */
     @Override
     public void collisions(
         StringBuilder sb, int x, int y, int z,
         int xWid, int yWid, int zWid, int depth) {
         
         // No objects, no collisions. Do nothing.
     }
 
 
     /**
      * {@inheritDoc}
      *
      * An empty node has no objects, so it cannot intersect with
      * any query. This node itself is visited.
      */
     @Override
     public int intersect(
         StringBuilder sb,
         int qx, int qy, int qz, int qxwid, int qywid, int qzwid,
         int x, int y, int z, int xWid, int yWid, int zWid, int depth) {
         
         // No objects to find, but we "visited" this node.
         return 1;
     }
 }