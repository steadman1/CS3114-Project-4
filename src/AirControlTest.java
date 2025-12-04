import java.util.Random;
import student.TestCase;

/**
 * @author adsleptsov
 * @version Fall 2025
 */
public class AirControlTest extends TestCase {

    private WorldDB db;

    /**
     * Sets up the tests that follow. In general, used for initialization
     */
    public void setUp() {
        // Initialize a standard DB with a fixed seed for the new mutation tests
        // Existing tests (testSampleInput etc) create their own instances, which is fine.
        db = new WorldDB(new Random(1));
    }


    /**
     * Get code coverage of the class declaration.
     *
     * @throws Exception
     */
    public void testRInit() throws Exception {
        AirControl recstore = new AirControl();
        assertNotNull(recstore);
    }


    // ----------------------------------------------------------
    /**
     * Test syntax: Sample Input/Output
     *
     * @throws Exception
     */
    public void testSampleInput() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0xCAFEBEEF);
        WorldDB w = new WorldDB(rnd);

        assertTrue(w.add(new Balloon("B1",
            10, 11, 11, 21, 12, 31, "hot_air", 15)));
        assertTrue(w.add(new AirPlane("Air1",
            0, 10, 1, 20, 2, 30, "USAir", 717, 4)));
        assertTrue(w.add(new Drone("Air2",
            100, 1010, 101, 924, 2, 900, "Droners", 3)));
        assertTrue(w.add(new Bird("pterodactyl",
            0, 100, 20, 10, 50, 50, "Dinosaur", 1)));
        assertFalse(w.add(new Bird("pterodactyl",
            0, 100, 20, 10, 50, 50, "Dinosaur", 1)));
        assertTrue(w.add(new Rocket("Enterprise",
            0, 100, 20, 10, 50, 50, 5000, 99.29)));

        assertFuzzyEquals(
            "Rocket Enterprise 0 100 20 10 50 50 5000 99.29",
            w.delete("Enterprise"));

        assertFuzzyEquals("Airplane Air1 0 10 1 20 2 30 USAir 717 4",
            w.print("Air1"));
        assertNull(w.print("air1"));

        assertFuzzyEquals(
            "I (0, 0, 0, 1024, 1024, 1024) 0\r\n"
                + "  I (0, 0, 0, 512, 1024, 1024) 1\r\n"
                + "    Leaf with 3 objects (0, 0, 0, 512, 512, 1024) 2\r\n"
                + "    (Airplane Air1 0 10 1 20 2 30 USAir 717 4)\r\n"
                + "    (Balloon B1 10 11 11 21 12 31 hot_air 15)\r\n"
                + "    (Bird pterodactyl 0 100 20 10 50 50 Dinosaur 1)\r\n"
                + "    Leaf with 1 objects (0, 512, 0, 512, 512, 1024) 2\r\n"
                + "    (Drone Air2 100 1010 101 924 2 900 Droners 3)\r\n"
                + "  Leaf with 1 objects (512, 0, 0, 512, 1024, 1024) 1\r\n"
                + "  (Drone Air2 100 1010 101 924 2 900 Droners 3)\r\n"
                + "5 Bintree nodes printed\r\n",
                w.printbintree());

        assertFuzzyEquals(
            "Node has depth 3, Value (null)\r\n"
                + "Node has depth 3, "
                + "Value (Airplane Air1 0 10 1 20 2 30 USAir 717 4)\r\n"
                + "Node has depth 1, "
                + "Value (Drone Air2 100 1010 101 924 2 900 Droners 3)\r\n"
                + "Node has depth 2, "
                + "Value (Balloon B1 10 11 11 21 12 31 hot_air 15)\r\n"
                + "Node has depth 2, "
                + "Value (Bird pterodactyl 0 100 20 10 50 50 Dinosaur 1)\r\n"
                + "4 skiplist nodes printed\r\n",
                w.printskiplist());

        assertFuzzyEquals(
            "Found these records in the range a to z\r\n"
                + "Bird pterodactyl 0 100 20 10 50 50 Dinosaur 1\r\n",
                w.rangeprint("a", "z"));
        assertFuzzyEquals(
            "Found these records in the range a to l\r\n",
            w.rangeprint("a", "l"));
        assertNull(w.rangeprint("z", "a"));

        assertFuzzyEquals(
            "The following collisions exist in the database:\r\n"
                + "In leaf node (0, 0, 0, 512, 512, 1024) 2\r\n"
                + "(Airplane Air1 0 10 1 20 2 30 USAir 717 4) "
                + "and (Balloon B1 10 11 11 21 12 31 hot_air 15)\r\n"
                + "In leaf node (0, 512, 0, 512, 512, 1024) 2\r\n"
                + "In leaf node (512, 0, 0, 512, 1024, 1024) 1\r\n",
                w.collisions());

        assertFuzzyEquals(
            "The following objects intersect (0 0 0 1024 1024 1024):\r\n"
                + "In Internal node (0, 0, 0, 1024, 1024, 1024) 0\r\n"
                + "In Internal node (0, 0, 0, 512, 1024, 1024) 1\r\n"
                + "In leaf node (0, 0, 0, 512, 512, 1024) 2\r\n"
                + "Airplane Air1 0 10 1 20 2 30 USAir 717 4\r\n"
                + "Balloon B1 10 11 11 21 12 31 hot_air 15\r\n"
                + "Bird pterodactyl 0 100 20 10 50 50 Dinosaur 1\r\n"
                + "In leaf node (0, 512, 0, 512, 512, 1024) 2\r\n"
                + "Drone Air2 100 1010 101 924 2 900 Droners 3\r\n"
                + "In leaf node (512, 0, 0, 512, 1024, 1024) 1\r\n"
                + "5 nodes were visited in the bintree\r\n",
                w.intersect(0, 0, 0, 1024, 1024, 1024));
    }



    // ----------------------------------------------------------
    /**
     * Test syntax: Check various forms of bad input parameters
     *
     * @throws Exception
     */
    public void testBadInput() throws Exception {
        Random rnd = new Random();
        rnd.setSeed(0xCAFEBEEF);
        WorldDB w = new WorldDB(rnd);
        assertFalse(w.add(new AirPlane("a", 1, 1, 1, 1, 1, 1, null, 1, 1)));
        assertFalse(w.add(new AirPlane("a", 1, 1, 1, 1, 1, 1, "Alaska", 0, 1)));
        assertFalse(w.add(new AirPlane("a", 1, 1, 1, 1, 1, 1, "Alaska", 1, 0)));
        assertFalse(w.add(new Balloon(null, 1, 1, 1, 1, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", -1, 1, 1, 1, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, -1, 1, 1, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, -1, 1, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 0, 1, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 1, 0, 1, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 1, 1, 0, "hot", 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 1, 1, 1, null, 5)));
        assertFalse(w.add(new Balloon("b", 1, 1, 1, 1, 1, 1, "hot", -1)));
        assertFalse(w.add(new Bird("b", 1, 1, 1, 1, 1, 1, null, 5)));
        assertFalse(w.add(new Bird("b", 1, 1, 1, 1, 1, 1, "Ostrich", 0)));
        assertFalse(w.add(new Drone("d", 1, 1, 1, 1, 1, 1, null, 5)));
        assertFalse(w.add(new Drone("d", 1, 1, 1, 1, 1, 1, "Droner", 0)));
        assertFalse(w.add(new Rocket("r", 1, 1, 1, 1, 1, 1, -1, 1.1)));
        assertFalse(w.add(new Rocket("r", 1, 1, 1, 1, 1, 1, 1, -1.1)));
        assertFalse(w.add(
            new AirPlane("a", 2000, 1, 1, 1, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 2000, 1, 1, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 2000, 1, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 1, 2000, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 1, 1, 2000, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 1, 1, 1, 2000, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1000, 1, 1, 1000, 1, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1000, 1, 1, 1000, 1, "Alaska", 1, 1)));
        assertFalse(w.add(
            new AirPlane("a", 1, 1, 1000, 1, 1, 1000, "Alaska", 1, 1)));
        assertNull(w.delete(null));
        assertNull(w.print(null));
        assertNull(w.rangeprint(null, "a"));
        assertNull(w.rangeprint("a", null));
        assertNull(w.intersect(-1, 1, 1, 1, 1, 1));
        assertNull(w.intersect(1, -1, 1, 1, 1, 1));
        assertNull(w.intersect(1, 1, -1, 1, 1, 1));
        assertNull(w.intersect(1, 1, 1, -1, 1, 1));
        assertNull(w.intersect(1, 1, 1, 1, -1, 1));
        assertNull(w.intersect(1, 1, 1, 1, 1, -1));
        assertNull(w.intersect(2000, 1, 1, 1, 1, 1));
        assertNull(w.intersect(1, 2000, 1, 1, 1, 1));
        assertNull(w.intersect(1, 1, 2000, 1, 1, 1));
        assertNull(w.intersect(1, 1, 1, 2000, 1, 1));
        assertNull(w.intersect(1, 1, 1, 1, 2000, 1));
        assertNull(w.intersect(1, 1, 1, 1, 1, 2000));
        assertNull(w.intersect(1000, 1, 1, 1000, 1, 1));
        assertNull(w.intersect(1, 1000, 1, 1, 1000, 1));
        assertNull(w.intersect(1, 1, 1000, 1, 1, 1000));
    }


    // ----------------------------------------------------------
    /**
     * Test empty: Check various returns from commands on empty database
     *
     * @throws Exception
     */
    public void testEmpty() throws Exception {
        WorldDB w = new WorldDB(null);
        assertNull(w.delete("hello"));
        assertFuzzyEquals("SkipList is empty", w.printskiplist());
        assertFuzzyEquals(
            "E (0, 0, 0, 1024, 1024, 1024) 0\r\n"
                + "1 Bintree nodes printed\r\n",
                w.printbintree());
        assertNull(w.print("hello"));
        assertFuzzyEquals("Found these records in the range begin to end\n",
            w.rangeprint("begin", "end"));
        assertFuzzyEquals("The following collisions exist in the database:\n",
            w.collisions());
        assertFuzzyEquals(
            "The following objects intersect (1, 1, 1, 1, 1, 1)\n" +
                "1 nodes were visited in the bintree\n",
                w.intersect(1, 1, 1, 1, 1, 1));
    }

    // ----------------------------------------------------------
    // NEW MUTATION COVERAGE TESTS
    // ----------------------------------------------------------

    /**
     * This test targets InternalNode line-by-line coverage.
     * It forces the tree to split into X, Y, and Z levels, and then
     * operations are performed that target SPECIFIC child branches.
     */
    public void testCompleteGeometricCoverage() {
        // 1. SETUP: Force a 3-level split (Root->X, Child->Y, Grandchild->Z)
        // Piling 4 objects at (10, 10, 10) causes a "No Split" (all intersect).
        // We need them to NOT all intersect to force a split.
        db.add(new AirPlane("Base1", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("Base2", 10, 10, 100, 10, 10, 10, "D", 1, 1)); 
        db.add(new AirPlane("Base3", 10, 100, 10, 10, 10, 10, "D", 1, 1)); 
        db.add(new AirPlane("Base4", 100, 10, 10, 10, 10, 10, "D", 1, 1));
        
        // Root (X split at 512). All objects are in Left (<512). Right is Empty.
        
        // 2. TEST X-AXIS BRANCHING (Level 0)
        // Case A: Insert Right Only (X > 512). Covers "Left=False, Right=True"
        db.add(new AirPlane("XRight", 600, 10, 10, 10, 10, 10, "D", 1, 1));
        
        // Case B: Insert Spanning (X spans 512). Covers "Left=True, Right=True"
        db.add(new AirPlane("XSpan", 500, 10, 10, 50, 10, 10, "D", 1, 1)); // 500 to 550
        
        // 3. TEST Y-AXIS BRANCHING (Level 1)
        // Focus on Left X-Node (0-512), which splits on Y (at 512).
        
        // Case A: Insert Y-Right Only (Y > 512).
        db.add(new AirPlane("YRight", 10, 600, 10, 10, 10, 10, "D", 1, 1));
        
        // Case B: Insert Y-Spanning (Y spans 512).
        db.add(new AirPlane("YSpan", 10, 500, 10, 10, 50, 10, "D", 1, 1));
        
        // 4. TEST Z-AXIS BRANCHING (Level 2)
        // Focus on Left X -> Left Y. This node splits on Z (at 512).
        
        // Case A: Insert Z-Right Only (Z > 512).
        db.add(new AirPlane("ZRight", 10, 10, 600, 10, 10, 10, "D", 1, 1));
        
        // Case B: Insert Z-Spanning (Z spans 512).
        db.add(new AirPlane("ZSpan", 10, 10, 500, 10, 10, 50, "D", 1, 1));
        
        // 5. VERIFY & DELETE (Covers "remove" logic branches)
        String tree = db.printbintree();
        assertTrue(tree.contains("XRight"));
        assertTrue(tree.contains("XSpan"));
        assertTrue(tree.contains("YRight"));
        assertTrue(tree.contains("YSpan"));
        assertTrue(tree.contains("ZRight"));
        assertTrue(tree.contains("ZSpan"));
        
        // Delete items to trigger specific remove() branches
        db.delete("XRight"); // Removes from X-Right child
        db.delete("XSpan");  // Removes from Both X children
        db.delete("YRight"); // Removes from Y-Right child (nested)
        
        // 6. INTERSECT COVERAGE
        // Hit X-Right only
        String res = db.intersect(600, 0, 0, 100, 100, 100);
        assertFalse(res.contains("Base1")); // Base1 is in X-Left
        
        // Hit Y-Right only (inside X-Left)
        res = db.intersect(0, 600, 0, 100, 100, 100);
        assertFalse(res.contains("Base1")); // Base1 is at y=10
        
        // Hit Z-Right only (inside X-Left, Y-Left)
        res = db.intersect(0, 0, 600, 100, 100, 100);
        assertTrue(res.contains("ZRight")); 
        assertFalse(res.contains("Base1")); 
    }

    /**
     * Tests the boundary condition where an object is EXACTLY on the split line.
     */
    public void testBoundaryConditions() {
        // Object ending exactly at 512 (Left Only)
        db.add(new AirPlane("LeftEdge", 502, 10, 10, 10, 10, 10, "D", 1, 1));
        
        // Object starting exactly at 512 (Right Only)
        db.add(new AirPlane("RightEdge", 512, 10, 10, 10, 10, 10, "D", 1, 1));
        
        // Force split
        db.add(new AirPlane("Filler1", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("Filler2", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        
        String tree = db.printbintree();
        assertTrue(tree.contains("LeftEdge"));
        assertTrue(tree.contains("RightEdge"));
        
        // Ensure "Touching is not intersecting" logic holds (collisions)
        String cols = db.collisions();
        assertFalse(cols.contains("LeftEdge") && cols.contains("RightEdge"));
    }

    /**
     * Tests logic for "Should Merge" when one child is Internal.
     * Hits: if (!isLeafOrEmpty(left) || !isLeafOrEmpty(right))
     */
    public void testMergeFailureOnInternalChild() {
        // Build a tree where Right child splits again.
        db.add(new AirPlane("L1", 10, 10, 10, 10, 10, 10, "D", 1, 1)); // Left
        
        db.add(new AirPlane("R1", 600, 10, 10, 10, 10, 10, "D", 1, 1)); // Right
        db.add(new AirPlane("R2", 700, 10, 10, 10, 10, 10, "D", 1, 1)); // Right
        db.add(new AirPlane("R3", 800, 10, 10, 10, 10, 10, "D", 1, 1)); // Right
        db.add(new AirPlane("R4", 900, 10, 10, 10, 10, 10, "D", 1, 1)); // Right
        
        // Delete L1. Left becomes Empty. Right is Internal.
        // Merge should fail (Root remains Internal).
        db.delete("L1");
        
        String tree = db.printbintree();
        assertTrue(tree.contains("I (")); 
    }

    /**
     * Test 1: Verify Bubble Sort Logic in LeafNode.print.
     * Inserts items in Reverse Order: Z, M, A.
     * Requires sorting loops to swap "A" from index 2 to index 0.
     * Kills mutants like: j < length - i - 2 (stop early) or skipping loops.
     */
    public void testLeafSortOrder() {
        // All items at (10,10,10) to stay in one leaf
        db.add(new AirPlane("Zebra", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("Mike", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("Alpha", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        
        String output = db.printbintree();
        
        // Find indices to ensure Alpha comes before Mike, Mike before Zebra
        int iA = output.indexOf("Alpha");
        int iM = output.indexOf("Mike");
        int iZ = output.indexOf("Zebra");
        
        assertTrue("Alpha should be present", iA != -1);
        assertTrue("Mike should be present", iM != -1);
        assertTrue("Zebra should be present", iZ != -1);
        assertTrue("Sort Order Failed", iA < iM && iM < iZ);
    }
    
    /**
     * Test 2: Verify Geometry Math Logic for Intersection box.
     * Specifically kills "Addition instead of Subtraction" mutants in calculating ixw/iyw/izw.
     * * Scenario:
     * A: (0,0,0) w10. Range 0-10.
     * B: (5,0,0) w10. Range 5-15.
     * Intersection A&B: Start 5, End 10. Width = 5. (ixw = minX2(10) - ix(5) = 5).
     * * C: (12,0,0) w10. Range 12-22.
     * * Correct Logic:
     * C (12) is outside A&B Intersection (5-10). Returns False (No common overlap).
     * Result: Tree Splits.
     * * Mutated Logic (ixw = minX2 + ix):
     * ixw = 10 + 5 = 15.
     * Next Iteration Box: Start 5, End 5+15=20.
     * C (12) fits inside (5-20). Returns True (Overlap detected).
     * Result: Tree Does NOT Split.
     */
    public void testLeafSplitMathMutation() {
        // A
        db.add(new AirPlane("A", 0, 0, 0, 10, 10, 10, "D", 1, 1));
        // B overlaps A
        db.add(new AirPlane("B", 5, 0, 0, 10, 10, 10, "D", 1, 1));
        // C is outside A&B intersection, but inside "Mutated Large" intersection
        db.add(new AirPlane("C", 12, 0, 0, 10, 10, 10, "D", 1, 1));
        // D (dummy) to trigger >3 check
        db.add(new AirPlane("D", 200, 200, 200, 10, 10, 10, "D", 1, 1));
        
        String tree = db.printbintree();
        // Should contain Internal Node "I (" because they don't all overlap
        // If mutation exists, it would be a single Leaf
        assertTrue("Tree should split", tree.contains("I ("));
    }
    
    /**
     * Test 3: Verify positive case for allIntersect math.
     * Ensures we didn't break valid logic while fixing mutation.
     * A, B intersect (5-20). C (10-15) fits inside.
     * Should be a single Leaf.
     */
    public void testLeafNoSplitMathLogic() {
        // A: 0-20
        db.add(new AirPlane("A", 0, 0, 0, 20, 10, 10, "D", 1, 1));
        // B: 5-25. Intersect A&B is 5-20.
        db.add(new AirPlane("B", 5, 0, 0, 20, 10, 10, "D", 1, 1));
        // C: 10-15. Fits inside 5-20.
        db.add(new AirPlane("C", 10, 0, 0, 5, 10, 10, "D", 1, 1));
        // D: 11-12. Fits inside.
        db.add(new AirPlane("D", 11, 0, 0, 1, 10, 10, "D", 1, 1));
        
        String tree = db.printbintree();
        // Should be one leaf
        assertTrue("Tree should NOT split", tree.contains("Leaf with 4 objects"));
        assertFalse(tree.contains("I ("));
    }
}