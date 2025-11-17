import student.TestCase;
import java.util.Random;

/**
 * This class tests the SkipList implementation.
 * It extends student.Testcase.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class SkipListTest {

    private SkipList<String, String> list;

    /**
     * Sets up the test environment before each test.
     * Initializes a new SkipList.
     */
    public void setUp() {
        list = new SkipList<String, String>(new Random());
    }


    /**
     * Tests insertion into an empty list.
     */
    public void testInsertEmpty() {
        assertTrue(list.insert("A", "Value A"));
        assertEquals(1, list.size());
        assertEquals("Value A", list.find("A"));
    }


    /**
     * Tests inserting multiple items, checking for correct order and size.
     */
    public void testInsertMultiple() {
        list.insert("C", "Value C");
        list.insert("A", "Value A");
        list.insert("B", "Value B");
        list.insert("E", "Value E");
        list.insert("D", "Value D");

        assertEquals(5, list.size());
        assertEquals("Value A", list.find("A"));
        assertEquals("Value B", list.find("B"));
        assertEquals("Value C", list.find("C"));
        assertEquals("Value D", list.find("D"));
        assertEquals("Value E", list.find("E"));
    }


    /**
     * Tests inserting a duplicate key, which should update the value.
     */
    public void testInsertDuplicate() {
        list.insert("A", "Value A1");
        assertEquals(1, list.size());
        assertEquals("Value A1", list.find("A"));

        list.insert("A", "Value A2");
        assertEquals(1, list.size()); // Size should not change
        assertEquals("Value A2", list.find("A")); // Value should be updated
    }


    /**
     * Tests inserting a null key.
     */
    public void testInsertNull() {
        assertFalse(list.insert(null, "Value Null"));
        assertEquals(0, list.size());
    }


    /**
     * Tests finding in an empty list.
     */
    public void testFindEmpty() {
        assertNull(list.find("A"));
    }


    /**
     * Tests finding a non-existent key.
     */
    public void testFindNonExistent() {
        list.insert("A", "Value A");
        list.insert("C", "Value C");
        assertNull(list.find("B"));
    }


    /**
     * Tests removing from an empty list.
     */
    public void testRemoveEmpty() {
        assertNull(list.remove("A"));
        assertEquals(0, list.size());
    }


    /**
     * Tests removing a non-existent key.
     */
    public void testRemoveNonExistent() {
        list.insert("A", "Value A");
        assertNull(list.remove("B"));
        assertEquals(1, list.size());
    }


    /**
     * Tests removing a null key.
     */
    public void testRemoveNull() {
        list.insert("A", "Value A");
        assertNull(list.remove(null));
        assertEquals(1, list.size());
    }


    /**
     * Tests removing the only element in the list.
     */
    public void testRemoveOnlyElement() {
        list.insert("A", "Value A");
        assertEquals("Value A", list.remove("A"));
        assertEquals(0, list.size());
        assertNull(list.find("A"));
    }


    /**
     * Tests complex removal (head, tail, middle).
     */
    public void testRemoveComplex() {
        list.insert("C", "Value C");
        list.insert("A", "Value A");
        list.insert("B", "Value B");
        list.insert("E", "Value E");
        list.insert("D", "Value D");
        assertEquals(5, list.size());

        // Remove from middle
        assertEquals("Value C", list.remove("C"));
        assertEquals(4, list.size());
        assertNull(list.find("C"));

        // Remove head
        assertEquals("Value A", list.remove("A"));
        assertEquals(3, list.size());
        assertNull(list.find("A"));
        assertEquals("Value B", list.find("B"));

        // Remove tail
        assertEquals("Value E", list.remove("E"));
        assertEquals(2, list.size());
        assertNull(list.find("E"));
        assertEquals("Value D", list.find("D"));

        // Remove remaining
        assertEquals("Value B", list.remove("B"));
        assertEquals("Value D", list.remove("D"));
        assertEquals(0, list.size());
    }


    /**
     * Tests the dump method for a specific list state.
     */
    public void testDump() {
        // We use a fixed seed, so the levels are deterministic.
        // With seed 12345:
        // C: level 1
        // A: level 0
        // B: level 0
        // E: level 3
        // D: level 1
        list.insert("C", "Value C");
        list.insert("A", "Value A");
        list.insert("B", "Value B");
        list.insert("E", "Value E");
        list.insert("D", "Value D");

        String expectedDump =
            "SkipList dump:\n" +
            "Node with depth 0, value null\n" +
            "Node with depth 0, value (A, Value A)\n" +
            "Node with depth 0, value (B, Value B)\n" +
            "Node with depth 1, value (C, Value C)\n" +
            "Node with depth 1, value (D, Value D)\n" +
            "Node with depth 3, value (E, Value E)\n" +
            "SkipList size: 5\n";

        assertEquals(expectedDump, list.dump());
    }


    /**
     * Tests that removing an element correctly adjusts the list's max level.
     */
    public void testRemoveAdjustsLevel() {
        // With seed 12345, "E" will have the highest level (3)
        list.insert("C", "Value C");
        list.insert("A", "Value A");
        list.insert("E", "Value E");

        String dump1 = list.dump();
        assertTrue(dump1.contains("Node with depth 3, value (E, Value E)"));

        // Remove "E", which was the only node at level 3
        list.remove("E");
        String dump2 = list.dump();

        // The dump should no longer contain any nodes at level 3.
        // The highest level node should be "C" at level 1.
        assertFalse(dump2.contains("depth 3"));
        assertFalse(dump2.contains("depth 2"));
        assertTrue(dump2.contains("Node with depth 1, value (C, Value C)"));
        assertEquals(2, list.size());
    }
}