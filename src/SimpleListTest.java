import student.TestCase;

/**
 * This class tests the SimpleList implementation.
 * It extends student.Testcase.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class SimpleListTest extends TestCase {

    private SimpleList list;
    // Changed type from String to AirObject
    private AirObject obj1;
    private AirObject obj2;
    private AirObject obj3;
    private AirObject obj4;
    private AirObject obj5;

    /**
     * Sets up the test environment before each test.
     * Initializes a new SimpleList and several AirPlane objects.
     */
    public void setUp() {
        list = new SimpleList();
        
        // Create AirPlane objects to use in tests
        // (SimpleList expects AirObjects)
        obj1 = new AirPlane("obj1", 0, 0, 0, 1, 1, 1, "Delta", 1, 2);
        obj2 = new AirPlane("obj2", 1, 1, 1, 1, 1, 1, "Delta", 2, 2);
        obj3 = new AirPlane("obj3", 2, 2, 2, 1, 1, 1, "Delta", 3, 2);
        obj4 = new AirPlane("obj4", 3, 3, 3, 1, 1, 1, "Delta", 4, 2);
        obj5 = new AirPlane("obj5", 4, 4, 4, 1, 1, 1, "Delta", 5, 2);
    }


    /**
     * Tests adding a single element to an empty list.
     */
    public void testAddAndGet() {
        assertEquals(0, list.size());
        list.add(obj1);
        assertEquals(1, list.size());
        assertSame(obj1, list.get(0));
    }


    /**
     * Tests the get() method with out-of-bounds indices.
     */
    public void testGetOutOfBounds() {
        list.add(obj1);
        
        // Test negative index
        Exception exception = null;
        try {
            list.get(-1);
        }
        catch (IndexOutOfBoundsException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Index: -1"));

        // Test index equal to size
        exception = null;
        try {
            list.get(1);
        }
        catch (IndexOutOfBoundsException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Index: 1"));
    }


    /**
     * Tests that the internal array resizes when adding more
     * elements than the default capacity (4).
     */
    public void testResize() {
        list.add(obj1); // 1
        list.add(obj2); // 2
        list.add(obj3); // 3
        list.add(obj4); // 4
        
        // At capacity. Next add should trigger resize.
        assertEquals(4, list.size());
        
        list.add(obj5); // 5 - Triggers resize
        
        // Check that all elements are still present and in order
        assertEquals(5, list.size());
        assertSame(obj1, list.get(0));
        assertSame(obj2, list.get(1));
        assertSame(obj3, list.get(2));
        assertSame(obj4, list.get(3));
        assertSame(obj5, list.get(4));
    }


    /**
     * Tests removing an element from the middle of the list.
     * This ensures System.arraycopy is called correctly.
     */
    public void testRemoveMiddle() {
        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        assertEquals(3, list.size());
        
        // Remove obj2 (from the middle)
        // Changed type from String to AirObject
        AirObject removed = list.remove(obj2);
        
        assertSame(obj2, removed);
        assertEquals(2, list.size());
        assertSame(obj1, list.get(0)); // obj1 should still be at index 0
        assertSame(obj3, list.get(1)); // obj3 should be shifted to index 1
    }


    /**
     * Tests removing the last element from the list.
     * This is an edge case for the remove logic (numToMove = 0).
     */
    public void testRemoveLast() {
        list.add(obj1);
        list.add(obj2);
        assertEquals(2, list.size());
        
        // Remove obj2 (from the end)
        // Changed type from String to AirObject
        AirObject removed = list.remove(obj2);
        
        assertSame(obj2, removed);
        assertEquals(1, list.size());
        assertSame(obj1, list.get(0)); // obj1 should still be at index 0
    }
    
    
    /**
     * Tests removing the first element from the list.
     */
    public void testRemoveFirst() {
        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        assertEquals(3, list.size());
        
        // Remove obj1 (from the start)
        // Changed type from String to AirObject
        AirObject removed = list.remove(obj1);
        
        assertSame(obj1, removed);
        assertEquals(2, list.size());
        assertSame(obj2, list.get(0)); // obj2 shifted to index 0
        assertSame(obj3, list.get(1)); // obj3 shifted to index 1
    }


    /**
     * Tests removing a non-existent element from the list.
     */
    public void testRemoveNonExistent() {
        list.add(obj1);
        list.add(obj2);
        assertEquals(2, list.size());
        
        // Try to remove obj3, which was never added
        // Changed type from String to AirObject
        AirObject removed = list.remove(obj3);
        
        assertNull(removed);
        assertEquals(2, list.size()); // Size should be unchanged
        assertSame(obj1, list.get(0));
        assertSame(obj2, list.get(1));
    }


    /**
     * Tests removing the only element in the list.
     */
    public void testRemoveOnlyElement() {
        list.add(obj1);
        assertEquals(1, list.size());
        
        // Changed type from String to AirObject
        AirObject removed = list.remove(obj1);
        
        assertSame(obj1, removed);
        assertEquals(0, list.size());
        
        // Getting from an empty list should now fail
        Exception exception = null;
        try {
            list.get(0);
        }
        catch (IndexOutOfBoundsException e) {
            exception = e;
        }
        assertNotNull(exception);
    }
    
    
    /**
     * Tests removing an object from an empty list.
     */
    public void testRemoveFromEmpty() {
        assertEquals(0, list.size());
        // Changed type from String to AirObject
        AirObject removed = list.remove(obj1);
        assertNull(removed);
        assertEquals(0, list.size());
    }
}