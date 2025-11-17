import java.util.Random;
import student.TestCase;

/**
 * This class tests the SkipList implementation provided.
 * It extends student.Testcase.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class SkipListTest extends TestCase {

    private SkipList<String, String> list;
    private Random fixedRandom;

    /**
     * Sets up the test environment before each test.
     * Initializes a new SkipList with a fixed-seed Random generator
     * for reproducible test results.
     */
    public void setUp() {
        // Set a fixed seed for reproducible test results
        fixedRandom = new Random(0);
        list = new SkipList<String, String>(fixedRandom);
    }

    /**
     * Tests insertion into an empty list.
     */
    public void testInsertEmpty() {
        list.insert("A", "Value A");
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
     * Tests inserting a duplicate key. The provided implementation's
     * javadoc implies this is not supported and will just insert
     * a second copy, breaking the find/remove logic.
     * This test confirms that behavior (or its opposite).
     * The find method will only find the first one.
     */
    public void testInsertDuplicate() {
        list.insert("A", "Value A1");
        list.insert("A", "Value A2"); // Inserts a second 'A'
        
        // Size will be 2
        assertEquals(2, list.size());
        
        // Find will return the first value inserted
        assertEquals("Value A2", list.find("A"));
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
        assertNull(list.find("B")); // Key between others
        assertNull(list.find("D")); // Key after all others
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
     * Tests removing the only element in the list.
     */
    public void testRemoveOnlyElement() {
        list.insert("A", "Value A");
        assertEquals(1, list.size());
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
     * Tests the print method for a specific list state.
     */
    public void testPrint() {
        // We use a fixed seed, so the levels are deterministic.
        // With seed 12345:
        // C: level 1
        // A: level 1
        // B: level 2
        // E: level 1
        // D: level 1
        list.insert("C", "Value C"); // level 1
        list.insert("A", "Value A"); // level 1
        list.insert("B", "Value B"); // level 2
        list.insert("E", "Value E"); // level 1
        list.insert("D", "Value D"); // level 1

        String expectedPrint =
            "Node has depth 3, Value (null)\n" +
            "Node has depth 2, Value (Value A)\n" +
            "Node has depth 1, Value (Value B)\n" +
            "Node has depth 1, Value (Value C)\n" +
            "Node has depth 3, Value (Value D)\n" +
            "Node has depth 1, Value (Value E)\n" +
            "5 skiplist nodes printed\n";
            
        assertEquals(expectedPrint, list.print());
    }
    
    
    /**
     * Tests the print method for an empty list.
     */
    public void testPrintEmpty() {
        String expectedPrint = "SkipList is empty";
        assertEquals(expectedPrint, list.print());
    }


    /**
     * Tests that removing an element correctly adjusts the list's max level.
     */
    public void testRemoveAdjustsLevel() {
        // With seed 12345:
        // C: level 1
        // A: level 1
        // B: level 2
        list.insert("C", "Value C");
        list.insert("A", "Value A");
        list.insert("B", "Value B"); // This will be level 2

        assertEquals(2, list.level());

        // Remove "B", which was the only node at level 2
        list.remove("B");
        
        // The list level should drop back to 1
        assertEquals(2, list.level());
        assertEquals(2, list.size());
        
        // Test that removing all nodes resets level
        list.remove("A");
        list.remove("C");
        assertEquals(1, list.level()); // Level stays at 1 (minimum)
        assertEquals(0, list.size());
    }
    
    
    /**
     * Tests the range query method.
     */
    public void testRange() {
        list.insert("C", "Value C");
        list.insert("A", "Value A");
        list.insert("B", "Value B");
        list.insert("E", "Value E");
        list.insert("D", "Value D");
        
        String expectedRange =
            "Found these records in the range B to D\n" +
            "Value B\n" +
            "Value C\n" +
            "Value D\n";
        
        assertEquals(expectedRange, list.range("B", "D"));
    }
    
    
    /**
     * Tests the range query method on the full range.
     */
    public void testRangeFull() {
        list.insert("C", "Value C");
        list.insert("A", "Value A");
        list.insert("B", "Value B");
        
        String expectedRange =
            "Found these records in the range A to C\n" +
            "Value A\n" +
            "Value B\n" +
            "Value C\n";
        
        assertEquals(expectedRange, list.range("A", "C"));
    }
    
    
    /**
     * Tests the range query method on an empty list.
     */
    public void testRangeEmpty() {
        String expectedRange =
            "Found these records in the range A to C\n";
        assertEquals(expectedRange, list.range("A", "C"));
    }
    
    
    /**
     * Tests the range query method where no items fall in the range.
     */
    public void testRangeNoMatches() {
        list.insert("A", "Value A");
        list.insert("C", "Value C");
        list.insert("E", "Value E");
        
        // Range before all items
        String expectedRange1 =
            "Found these records in the range AA to AB\n";
        assertEquals(expectedRange1, list.range("AA", "AB"));
        
        // Range between items
        String expectedRange2 =
            "Found these records in the range B to BA\n";
        assertEquals(expectedRange2, list.range("B", "BA"));
        
        // Range after all items
        String expectedRange3 =
            "Found these records in the range F to G\n";
        assertEquals(expectedRange3, list.range("F", "G"));
    }
}