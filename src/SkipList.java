import java.util.Random;

/**
 * Implements a Skip List data structure.
 * This class is generic, storing key-value pairs where the
 * key must be Comparable.
 *
 * @author adsleptsov
 * @version Fall 2025
 *
 * @param <K> The type of the key, must be Comparable.
 * @param <V> The type of the value being stored.
 */
public class SkipList<K extends Comparable<K>, V> {

    /**
     * The head node of the skip list. It has a null key/value
     * and the maximum level.
     */
    private SkipNode<K, V> head;
    
    /**
     * The current highest level (depth) of any node in the list.
     * Starts at 1.
     */
    private int level;
    
    /**
     * The number of key-value pairs stored in the skip list.
     */
    private int size;
    
    /**
     * The random number generator used for determining node levels.
     */
    private Random rnd;

    /**
     * The maximum possible level (depth) for any node, including the head.
     */
    private static final int MAX_LEVEL = 10;
    
    /**
     * The probability (0.5) used for random level generation.
     */
    private static final double P = 0.5;

    /**
     * Represents a node in the Skip List.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     */
    private static class SkipNode<K, V> {
        /**
         * The key for this node.
         */
        private K key;
        
        /**
         * The value for this node.
         */
        private V value;
        
        /**
         * An array of forward pointers. The length of this array
         * determines the node's level/depth.
         */
        private SkipNode<K, V>[] forward;

        /**
         * Creates a new SkipNode with a given key, value, and depth.
         *
         * @param key   The node's key.
         * @param value The node's value.
         * @param depth The depth (level) of the node,
         * which determines the size of its forward array.
         */
        @SuppressWarnings("unchecked")
        SkipNode(K key, V value, int depth) {
            this.key = key;
            this.value = value;
            // A node with depth 'd' has d pointers (indices 0 to d-1)
            this.forward = (SkipNode<K, V>[])new SkipNode[depth];
        }


        /**
         * Gets the key.
         * @return The key.
         */
        public K key() {
            return key;
        }


        /**
         * Gets the value.
         * @return The value.
         */
        public V value() {
            return value;
        }
    }

    /**
     * Creates a new, empty SkipList.
     *
     * @param r The random number generator to use.
     */
    public SkipList(Random r) {
        this.rnd = r;
        this.level = 1; // Start at level 1
        this.size = 0;
        // Head node has MAX_LEVEL pointers, all initially null
        this.head = new SkipNode<>(null, null, MAX_LEVEL);
    }
    
    /**
    * Gets the current level of the skip list.
    *
    * @return The current level.
    */
    public int level() {
        return level;
    }
    
    /**
     * Gets the size of the skip list.
     *
     * @return The size of the skip list.
     */
    public int size() {
        return size;
    }

    /**
     * Generates a random level (depth) for a new node.
     * The level will be at least 1 and at most MAX_LEVEL.
     *
     * @return The randomly determined level.
     */
    private int randomLevel() {
        int lev = 1;
        while (lev < MAX_LEVEL && rnd.nextDouble() < P) {
            lev++;
        }
        return lev;
    }


    /**
     * Inserts a new key-value pair into the skip list.
     * Assumes the key does not already exist.
     *
     * @param key   The key to insert.
     * @param value The value to associate with the key.
     */
    @SuppressWarnings("unchecked")
    public void insert(K key, V value) {
        // 1. Determine the level for the new node
        int newLevel = randomLevel();

        // 2. If new level is > current list level, update list level
        if (newLevel > level) {
            level = newLevel;
        }

        // 3. Create update array to store predecessors
        SkipNode<K, V>[] update =
            (SkipNode<K, V>[])new SkipNode[MAX_LEVEL];
        SkipNode<K, V> x = head;

        // 4. Find the insertion point and predecessors
        for (int i = level - 1; i >= 0; i--) {
            while ((x.forward[i] != null) &&
                   (x.forward[i].key().compareTo(key) < 0)) {
                x = x.forward[i];
            }
            update[i] = x; // Store predecessor at level i
        }

        // 5. Create the new node
        SkipNode<K, V> newNode = new SkipNode<>(key, value, newLevel);

        // 6. Splice the new node into the list
        for (int i = 0; i < newLevel; i++) {
            // new node's forward points to its successor
            newNode.forward[i] = update[i].forward[i];
            // predecessor's forward points to the new node
            update[i].forward[i] = newNode;
        }

        // 7. Increment size
        size++;
    }


    /**
     * Removes a key-value pair from the skip list.
     *
     * @param key The key to remove.
     * @return The value associated with the removed key,
     * or null if the key was not found.
     */
    @SuppressWarnings("unchecked")
    public V remove(K key) {
        // 1. Create update array
        SkipNode<K, V>[] update =
            (SkipNode<K, V>[])new SkipNode[MAX_LEVEL];
        SkipNode<K, V> x = head;

        // 2. Find predecessors of the node to remove
        for (int i = level - 1; i >= 0; i--) {
            while ((x.forward[i] != null) &&
                   (x.forward[i].key().compareTo(key) < 0)) {
                x = x.forward[i];
            }
            update[i] = x;
        }

        // 3. Get the node to be removed
        x = x.forward[0];

        // 4. Check if the node was found
        if ((x == null) || (x.key().compareTo(key) != 0)) {
            return null; // Key not found
        }

        // 5. Unlink the node from the list
        for (int i = 0; i < level; i++) {
            if (update[i].forward[i] == x) {
                update[i].forward[i] = x.forward[i];
            }
        }

        // 6. Decrement size
        size--;

        // 7. Adjust list level down if necessary
        while (level > 1 && head.forward[level - 1] == null) {
            level--;
        }

        // 8. Return the value of the removed node
        return x.value();
    }


    /**
     * Finds the value associated with a given key.
     *
     * @param key The key to search for.
     * @return The value associated with the key, or null if not found.
     */
    public V find(K key) {
        SkipNode<K, V> x = head;
        
        // 1. Search for the node
        for (int i = level - 1; i >= 0; i--) {
            while ((x.forward[i] != null) &&
                   (x.forward[i].key().compareTo(key) < 0)) {
                x = x.forward[i];
            }
        }

        // 2. Get the candidate node
        x = x.forward[0];

        // 3. Check if it's the correct node
        if ((x != null) && (x.key().compareTo(key) == 0)) {
            return x.value();
        }
        
        // 4. Not found
        return null;
    }


    /**
     * Generates a string representation of the skip list.
     *
     * @return A formatted string of all nodes and their depths.
     */
    public String print() {
        if (size == 0) {
            return "SkipList is empty";
        }

        StringBuilder sb = new StringBuilder();
        
        // 1. Print the head node with the current list level
        sb.append("Node has depth ").append(level);
        sb.append(", Value (null)\n");

        // 2. Iterate through the bottom level (level 0)
        SkipNode<K, V> curr = head.forward[0];
        int nodeCount = 0;
        
        while (curr != null) {
            // 3. Print each node's actual depth and value
            sb.append("Node has depth ").append(curr.forward.length);
            sb.append(", Value (").append(curr.value().toString());
            sb.append(")\n");
            
            curr = curr.forward[0];
            nodeCount++;
        }

        // 4. Append the total count
        sb.append(nodeCount).append(" skiplist nodes printed\n");
        return sb.toString();
    }


    /**
     * Finds and formats all values whose keys fall within a
     * specified range (inclusive).
     *
     * @param start The start of the range (inclusive).
     * @param end   The end of the range (inclusive).
     * @return A formatted string of all values in the range.
     */
    public String range(K start, K end) {
        StringBuilder sb = new StringBuilder();
        sb.append("Found these records in the range ");
        sb.append(start.toString()).append(" to ");
        sb.append(end.toString()).append("\n");

        // 1. Find the node *before* the start of the range
        SkipNode<K, V> x = head;
        for (int i = level - 1; i >= 0; i--) {
            while ((x.forward[i] != null) &&
                   (x.forward[i].key().compareTo(start) < 0)) {
                x = x.forward[i];
            }
        }

        // 2. Get the first candidate node (>= start)
        SkipNode<K, V> curr = x.forward[0];

        // 3. Iterate as long as we are in the range
        while (curr != null && curr.key().compareTo(end) <= 0) {
            // We already know curr.key() >= start
            sb.append(curr.value().toString()).append("\n");
            curr = curr.forward[0];
        }

        return sb.toString();
    }
}