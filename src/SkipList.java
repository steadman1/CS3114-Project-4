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

    private SkipNode<K, V> head;
    private int level;
    private int size;
    private Random rnd;
    private static final int MAX_LEVEL = 10;

    /**
     * Represents a node in the Skip List.
     */
    private static class SkipNode<K, V> {
        private K key;
        private V value;
        private SkipNode<K, V>[] forward;

        @SuppressWarnings("unchecked")
        SkipNode(K key, V value, int depth) {
            this.key = key;
            this.value = value;
            this.forward = (SkipNode<K, V>[])new SkipNode[depth];
        }

        public K key() { return key; }
        public V value() { return value; }
    }

    public SkipList(Random r) {
        this.rnd = r;
        this.level = 1;
        this.size = 0;
        this.head = new SkipNode<>(null, null, MAX_LEVEL);
    }
    
    public int level() { return level; }
    public int size() { return size; }

    /**
     * Generates a random level for a new node.
     * Uses the integer parity check (nextInt() % 2 == 0) to match
     * the standard reference implementation behavior.
     * @return The random level (1 to MAX_LEVEL).
     */
    private int randomLevel() {
        int lev = 1;
        // Using (rnd.nextInt() % 2 == 0) is the standard OpenDSA/Textbook
        // way to simulate a coin flip for Skip Lists in Java.
        while (lev < MAX_LEVEL && (rnd.nextInt() % 2) == 0) {
            lev++;
        }
        return lev;
    }

    @SuppressWarnings("unchecked")
    public void insert(K key, V value) {
        int newLevel = randomLevel();
        
        SkipNode<K, V>[] update = (SkipNode<K, V>[])new SkipNode[MAX_LEVEL];
        SkipNode<K, V> x = head;

        // Fill update array for existing levels
        for (int i = level - 1; i >= 0; i--) {
            while ((x.forward[i] != null) &&
                   (x.forward[i].key().compareTo(key) < 0)) {
                x = x.forward[i];
            }
            update[i] = x;
        }

        // Adjust head if new node is taller than current max level
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) {
                update[i] = head;
            }
            level = newLevel;
        }

        SkipNode<K, V> newNode = new SkipNode<>(key, value, newLevel);

        for (int i = 0; i < newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }

        size++;
    }

    @SuppressWarnings("unchecked")
    public V remove(K key) {
        SkipNode<K, V>[] update = (SkipNode<K, V>[])new SkipNode[MAX_LEVEL];
        SkipNode<K, V> x = head;

        for (int i = level - 1; i >= 0; i--) {
            while ((x.forward[i] != null) &&
                   (x.forward[i].key().compareTo(key) < 0)) {
                x = x.forward[i];
            }
            update[i] = x;
        }

        x = x.forward[0];

        if ((x == null) || (x.key().compareTo(key) != 0)) {
            return null;
        }

        for (int i = 0; i < level; i++) {
            if (update[i].forward[i] == x) {
                update[i].forward[i] = x.forward[i];
            }
        }

        size--;

        // Decrease level if the highest level is now empty
        while (level > 1 && head.forward[level - 1] == null) {
            level--;
        }

        return x.value();
    }

    public V find(K key) {
        SkipNode<K, V> x = head;
        for (int i = level - 1; i >= 0; i--) {
            while ((x.forward[i] != null) &&
                   (x.forward[i].key().compareTo(key) < 0)) {
                x = x.forward[i];
            }
        }
        x = x.forward[0];
        if ((x != null) && (x.key().compareTo(key) == 0)) {
            return x.value();
        }
        return null;
    }

    public String print() {
        if (size == 0) {
            return "SkipList is empty";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Node has depth ").append(level).append(", Value (null)\n");
        SkipNode<K, V> curr = head.forward[0];
        int nodeCount = 0;
        while (curr != null) {
            sb.append("Node has depth ").append(curr.forward.length);
            sb.append(", Value (").append(
                curr.value().toString()).append(")\n");
            curr = curr.forward[0];
            nodeCount++;
        }
        sb.append(nodeCount).append(" skiplist nodes printed\n");
        return sb.toString();
    }

    public String range(K start, K end) {
        StringBuilder sb = new StringBuilder();
        sb.append("Found these records in the range ");
        sb.append(start.toString()).append(" to ").append(
            end.toString()).append("\n");
        SkipNode<K, V> x = head;
        for (int i = level - 1; i >= 0; i--) {
            while ((x.forward[i] != null) &&
                   (x.forward[i].key().compareTo(start) < 0)) {
                x = x.forward[i];
            }
        }
        SkipNode<K, V> curr = x.forward[0];
        while (curr != null && curr.key().compareTo(end) <= 0) {
            sb.append(curr.value().toString()).append("\n");
            curr = curr.forward[0];
        }
        return sb.toString();
    }
}