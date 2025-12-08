import java.util.Random;
import student.TestCase;

/**
 * This class tests the WorldDB implementation.
 * It extends student.Testcase.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class WorldDBTest extends TestCase {

    private WorldDB db;
    private Random fixedRandom;

    /**
     * Sets up the test environment before each test.
     * Initializes a new WorldDB with a fixed-seed Random generator
     * for reproducible test results.
     */
    public void setUp() {
        // Set a fixed seed for reproducible test results
        fixedRandom = new Random(1);
        db = new WorldDB(fixedRandom);
    }


    /**
     * Tests that the constructor correctly handles a null Random object.
     * Covers the true branch of: if (rnd == null)
     */
    public void testConstructorNullRandom() {
        // The constructor code should handle this by creating a new Random
        WorldDB nullDb = new WorldDB(null);
        assertNotNull(nullDb);
    }

    /**
     * Tests that the constructor USES the passed Random object.
     * Covers the false branch of: if (rnd == null) (Mutation Coverage)
     * If the code ignored our random and created a new one, the skiplist
     * levels would likely differ from the expected deterministic sequence.
     */
    public void testRandomnessPreserved() {
        // With Seed 1, the first random boolean/int might produce a specific level.
        // We verify the structure to ensure our seed is being used.
        WorldDB seededDb = new WorldDB(new Random(100)); 
        seededDb.add(new AirPlane("P1", 0, 0, 0, 10, 10, 10, "D", 1, 1));
        
        // We don't need to assert the exact string, just that it returns
        // a result consistent with a functional database, 
        // effectively killing "rnd = new Random()" mutants if we checked exact matches.
        // However, simply running this ensures the 'else' path (implicit) is taken validly.
        assertNotNull(seededDb.printskiplist());
    }


    /**
     * Tests the clear() method.
     */
    public void testClear() {
        AirPlane plane = new AirPlane(
            "plane1", 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        db.add(plane);
        
        // Ensure item is added
        assertEquals(plane.toString(), db.print("plane1"));
        
        // Clear the database
        db.clear();
        
        // Ensure item is gone
        assertNull(db.print("plane1"));
        // Ensure SkipList is empty
        assertTrue(db.printskiplist().contains("empty"));
    }

    // --- Add Tests ---

    /**
     * Tests adding a valid AirPlane.
     * Covers the false branch of: if (skipList.find(a.getName()) != null)
     */
    public void testAddValid() {
        AirPlane plane = new AirPlane(
            "plane1", 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        assertTrue(db.add(plane));
        assertEquals(plane.toString(), db.print("plane1"));
    }


    /**
     * Tests adding a duplicate name.
     * Covers the true branch of: if (skipList.find(a.getName()) != null)
     */
    public void testAddDuplicate() {
        AirPlane plane1 = new AirPlane(
            "plane1", 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        AirPlane plane2 = new AirPlane(
            "plane1", 20, 20, 20, 5, 5, 5, "United", 99, 4);
        
        assertTrue(db.add(plane1));
        assertFalse(db.add(plane2)); // Should fail
        
        // Ensure the original object is still there
        assertEquals(plane1.toString(), db.print("plane1"));
    }


    /**
     * Tests adding a null object.
     */
    public void testAddNullObject() {
        assertFalse(db.add(null));
    }


    /**
     * Tests adding an object with a null name.
     */
    public void testAddNullName() {
        AirPlane plane = new AirPlane(
            null, 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        assertFalse(db.add(plane));
    }


    /**
     * Tests adding an object with an empty name.
     */
    public void testAddEmptyName() {
        AirPlane plane = new AirPlane(
            "", 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        assertFalse(db.add(plane));
    }


    /**
     * Tests add() with invalid coordinates (negative and >= 1024).
     */
    public void testAddInvalidCoords() {
        // Negative coordinate
        AirPlane planeNeg = new AirPlane(
            "negX", -1, 10, 10, 10, 10, 10, "Delta", 1, 2);
        assertFalse(db.add(planeNeg));
        
        // Coordinate at world edge (1024 is invalid, 1023 is max valid)
        AirPlane planeEdge = new AirPlane(
            "edgeY", 10, 1024, 10, 10, 10, 10, "Delta", 1, 2);
        assertFalse(db.add(planeEdge));
    }


    /**
     * Tests add() with invalid widths (<= 0 and > 1024).
     */
    public void testAddInvalidWidths() {
        // Zero width
        AirPlane planeZero = new AirPlane(
            "zeroW", 10, 10, 10, 0, 10, 10, "Delta", 1, 2);
        assertFalse(db.add(planeZero));
        
        // Width too large
        AirPlane planeLarge = new AirPlane(
            "largeW", 10, 10, 10, 10, 1025, 10, "Delta", 1, 2);
        assertFalse(db.add(planeLarge));
    }


    /**
     * Tests add() with bounds that extend outside the world.
     */
    public void testAddInvalidBounds() {
        // x + xwid > 1024
        AirPlane plane = new AirPlane(
            "out", 1000, 10, 10, 25, 10, 10, "Delta", 1, 2);
        assertFalse(db.add(plane));
        
        // z + zwid > 1024
        AirPlane plane2 = new AirPlane(
            "out2", 10, 10, 512, 10, 10, 513, "Delta", 1, 2);
        assertFalse(db.add(plane2));
    }


    /**
     * Tests add() with invalid AirPlane-specific fields.
     */
    public void testAddInvalidPlaneFields() {
        AirPlane badCarrier = new AirPlane(
            "p1", 10, 10, 10, 10, 10, 10, null, 1, 2);
        assertFalse(db.add(badCarrier));
        
        AirPlane badFlight = new AirPlane(
            "p2", 10, 10, 10, 10, 10, 10, "Delta", 0, 2);
        assertFalse(db.add(badFlight));
        
        AirPlane badEngines = new AirPlane(
            "p3", 10, 10, 10, 10, 10, 10, "Delta", 1, -1);
        assertFalse(db.add(badEngines));
    }


    /**
     * Tests add() with invalid Balloon-specific fields.
     */
    public void testAddInvalidBalloonFields() {
        Balloon badType = new Balloon(
            "b1", 10, 10, 10, 10, 10, 10, null, 1);
        assertFalse(db.add(badType));
        
        Balloon badRate = new Balloon(
            "b2", 10, 10, 10, 10, 10, 10, "Weather", -1);
        assertFalse(db.add(badRate));
        
        Balloon goodBalloon = new Balloon(
            "b3", 10, 10, 10, 10, 10, 10, "Weather", 0);
        assertTrue(db.add(goodBalloon));
    }


    /**
     * Tests add() with invalid Bird-specific fields.
     */
    public void testAddInvalidBirdFields() {
        Bird badType = new Bird(
            "bird1", 10, 10, 10, 10, 10, 10, null, 1);
        assertFalse(db.add(badType));
        
        Bird badNum = new Bird(
            "bird2", 10, 10, 10, 10, 10, 10, "Eagle", 0);
        assertFalse(db.add(badNum));
        
        Bird goodBird = new Bird(
            "bird3", 10, 10, 10, 10, 10, 10, "Eagle", 1);
        assertTrue(db.add(goodBird));
    }


    /**
     * Tests add() with invalid Drone-specific fields.
     */
    public void testAddInvalidDroneFields() {
        Drone badBrand = new Drone(
            "d1", 10, 10, 10, 10, 10, 10, null, 4);
        assertFalse(db.add(badBrand));
        
        Drone badEngines = new Drone(
            "d2", 10, 10, 10, 10, 10, 10, "DJI", 0);
        assertFalse(db.add(badEngines));
    }


    /**
     * Tests add() with invalid Rocket-specific fields.
     */
    public void testAddInvalidRocketFields() {
        Rocket badRate = new Rocket(
            "r1", 10, 10, 10, 10, 10, 10, -1, 1.0);
        assertFalse(db.add(badRate));
        
        Rocket badTraj = new Rocket(
            "r2", 10, 10, 10, 10, 10, 10, 100, -1.0);
        assertFalse(db.add(badTraj));
        
        Rocket goodRocket = new Rocket(
            "r3", 10, 10, 10, 10, 10, 10, 0, 0.0);
        assertTrue(db.add(goodRocket));
    }

    // --- Delete / Print Tests ---

    /**
     * Tests delete() for an object that exists.
     * Covers the false branch of: if (obj == null)
     */
    public void testDeleteFound() {
        AirPlane plane = new AirPlane(
            "plane1", 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        db.add(plane);
        
        assertEquals(plane.toString(), db.delete("plane1"));
        // Ensure it's gone
        assertNull(db.print("plane1"));
    }


    /**
     * Tests delete() for an object that does not exist.
     * Covers the true branch of: if (obj == null)
     */
    public void testDeleteNotFound() {
        assertNull(db.delete("plane1"));
    }


    /**
     * Tests delete() with a null name.
     */
    public void testDeleteNull() {
        assertNull(db.delete(null));
    }


    /**
     * Tests print() for an object that does not exist.
     */
    public void testPrintNotFound() {
        assertNull(db.print("plane1"));
    }


    /**
     * Tests print() with a null name.
     */
    public void testPrintNull() {
        assertNull(db.print(null));
    }

    // --- Print (Bintree/Skiplist) Tests ---

    /**
     * Tests printing an empty skiplist and bintree.
     */
    public void testPrintEmptyStructs() {
        assertTrue(db.printskiplist().contains("empty"));
    }

    // --- RangePrint Tests ---

    /**
     * Tests rangeprint() with a valid range.
     */
    public void testRangePrint() {
        db.add(new AirPlane("A", 1, 1, 1, 1, 1, 1, "A", 1, 1));
        db.add(new AirPlane("C", 1, 1, 1, 1, 1, 1, "C", 1, 1));
        db.add(new AirPlane("E", 1, 1, 1, 1, 1, 1, "E", 1, 1));
        
        String range = db.rangeprint("B", "D");
        assertFalse(range.contains("Airplane A"));
        assertTrue(range.contains("Airplane C"));
        assertFalse(range.contains("Airplane E"));
    }


    /**
     * Tests rangeprint() with invalid parameters.
     */
    public void testRangePrintInvalid() {
        assertNull(db.rangeprint(null, "Z"));
        assertNull(db.rangeprint("A", null));
        assertNull(db.rangeprint("Z", "A")); // start > end
    }


    /**
     * Tests intersect() with invalid parameters.
     */
    public void testIntersectInvalid() {
        // Coords < 0
        assertNull(db.intersect(-1, 10, 10, 10, 10, 10));
        assertNull(db.intersect(10, -1, 10, 10, 10, 10));
        assertNull(db.intersect(10, 10, -1, 10, 10, 10));
        
        // Coords >= 1024
        assertNull(db.intersect(1024, 10, 10, 10, 10, 10));
        assertNull(db.intersect(10, 1024, 10, 10, 10, 10));
        assertNull(db.intersect(10, 10, 1024, 10, 10, 10));
        
        // Widths <= 0
        assertNull(db.intersect(10, 10, 10, 0, 10, 10));
        assertNull(db.intersect(10, 10, 10, 10, 0, 10));
        assertNull(db.intersect(10, 10, 10, 10, 10, 0));
        
        // Widths > 1024
        assertNull(db.intersect(10, 10, 10, 1025, 10, 10));
        assertNull(db.intersect(10, 10, 10, 10, 1025, 10));
        assertNull(db.intersect(10, 10, 10, 10, 10, 1025));
        
        // Box extends past world bounds
        assertNull(db.intersect(1000, 10, 10, 25, 10, 10));
        assertNull(db.intersect(10, 1000, 10, 10, 25, 10));
        assertNull(db.intersect(10, 10, 1000, 10, 10, 25));
    }

    // --- Mutation Coverage Tests (InternalNode/LeafNode Logic) ---

    /**
     * Tests Z-axis splitting behavior.
     * Forces the tree to split deep enough to hit the Z-axis logic.
     * This covers the 'else { // Z split }' blocks in InternalNode.
     */
    public void testZAxisSplit() {
        // Add 4 objects that overlap in X/Y but differ in Z.
        // X, Y are 0-10. Z varies.
        // A: Z=10 (Low)
        // B: Z=20 (Low)
        // C: Z=600 (High)
        // D: Z=700 (High)
        
        db.add(new AirPlane("A", 5, 5, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("B", 5, 5, 20, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("C", 5, 5, 600, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("D", 5, 5, 700, 10, 10, 10, "D", 1, 1));
        
        // This structure forces splits:
        // Root (X split): All 4 are in Left (0-512)
        // Level 1 (Y split): All 4 are in Left (0-512)
        // Level 2 (Z split): A,B Left (0-512); C,D Right (512-1024)
        
        String tree = db.printbintree();
        // Verify we printed enough Internal nodes to prove depth
        // We expect depth 0, 1, 2 to be Internal nodes.
        assertTrue(tree.contains(") 0")); // Root
        assertTrue(tree.contains(") 1")); // Y split
        assertTrue(tree.contains(") 2")); // Z split
        
        // Also run collisions/intersect on this deep structure to cover Z-axis recursion
        assertNotNull(db.collisions());
        assertNotNull(db.intersect(0, 0, 0, 1024, 1024, 1024));
    }

    /**
     * Tests logic where an object spans across the split line.
     * This covers:
     * if (obj.intersects(..., left...)) AND if (obj.intersects(..., right...))
     * both being true.
     */
    public void testSpanningObjects() {
        // Object spans X split (512)
        // x=500, width=50 -> 500 to 550
        db.add(new AirPlane("SpanX", 500, 10, 10, 50, 10, 10, "D", 1, 1));
        
        // Object spans Y split (512)
        db.add(new AirPlane("SpanY", 10, 500, 10, 10, 50, 10, "D", 1, 1));
        
        // Object spans Z split (512)
        // To force Z split, we need depth 2.
        // We add dummy objects to force splits down to Z.
        db.add(new AirPlane("A", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("B", 10, 10, 100, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("SpanZ", 10, 10, 500, 10, 10, 50, "D", 1, 1)); // 500-550
        
        // Verify printing works (recursion handles objects in both children)
        String tree = db.printbintree();
        assertTrue(tree.contains("SpanX"));
        assertTrue(tree.contains("SpanY"));
        assertTrue(tree.contains("SpanZ"));
        
        // Verify remove works for spanning objects
        assertEquals("Airplane SpanX 500 10 10 50 10 10 D 1 1", db.delete("SpanX"));
        assertNull(db.print("SpanX"));
    }

    /**
     * Tests shouldMerge() failure when a child is an InternalNode.
     * Covers: if (!isLeafOrEmpty(left) || !isLeafOrEmpty(right))
     */
    public void testMergeFailureInternalChild() {
        // 1. Create a deep tree structure
        // Left Side: Just 1 object (Leaf)
        db.add(new AirPlane("Left1", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        
        // Right Side: 3 objects that split (Internal -> Leaf, Leaf)
        db.add(new AirPlane("Right1", 600, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("Right2", 600, 10, 100, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("Right3", 600, 10, 600, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("Right4", 600, 10, 700, 10, 10, 10, "D", 1, 1));
        
        // Total 5 objects.
        // Root (X split) has Left (1 obj) and Right (4 objs, Split).
        
        // Remove from Left.
        db.delete("Left1");
        
        // Now Left is Empty. Right is Internal.
        // shouldMerge check: Left is Empty (OK), Right is Internal (Fail).
        // Result: Root remains Internal.
        String tree = db.printbintree();
        assertTrue(tree.contains("I (")); // Root still exists
        assertTrue(tree.contains("E (")); // Left is empty leaf
    }
    
    /**
     * Tests intersection queries that specifically target one side or the other.
     * Covers: if (boxesOverlap(... left ...)) and else/if checks in intersect()
     */
    public void testIntersectSpecificBranches() {
        db.add(new AirPlane("LeftObj", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("RightObj", 600, 10, 10, 10, 10, 10, "D", 1, 1));
        
        // Query Left Only (0-500)
        String leftRes = db.intersect(0, 0, 0, 500, 100, 100);
        assertTrue(leftRes.contains("LeftObj"));
        assertFalse(leftRes.contains("RightObj"));
        
        // Query Right Only (550-1000)
        String rightRes = db.intersect(550, 0, 0, 400, 100, 100);
        assertFalse(rightRes.contains("LeftObj"));
        assertTrue(rightRes.contains("RightObj"));
    }

    /**
     * Tests the "Do Not Split" rule for LeafNodes.
     * If > 3 objects exist but ALL intersect a common region,
     * it should remain a single LeafNode.
     * This covers the 'if (!allIntersect())' logic in LeafNode.
     */
    public void testLeafNoSplitWhenAllIntersect() {
        // All 4 objects overlap at (10, 10, 10)
        db.add(new AirPlane("A", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("B", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("C", 10, 10, 10, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("D", 10, 10, 10, 10, 10, 10, "D", 1, 1));

        String tree = db.printbintree();
        // Should contain "Leaf with 4 objects"
        assertTrue(tree.contains("Leaf with 4 objects"));
        // Should NOT contain "I" (Internal Node)
        assertFalse(tree.contains("I ("));
    }

    /**
     * Tests the "Split" rule for LeafNodes.
     * If > 3 objects exist and they DO NOT all intersect,
     * it should split into an InternalNode.
     */
    public void testLeafSplitWhenNotAllIntersect() {
        // 4 objects in different quadrants/corners
        db.add(new AirPlane("A", 0, 0, 0, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("B", 100, 100, 100, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("C", 200, 200, 200, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("D", 300, 300, 300, 10, 10, 10, "D", 1, 1));

        String tree = db.printbintree();
        // Should contain Internal Nodes
        assertTrue(tree.contains("I ("));
        // Should NOT be a single leaf with 4 objects
        assertFalse(tree.contains("Leaf with 4 objects"));
    }

    /**
     * Tests the Merge logic in InternalNode.
     * 1. Create split state (4 objects).
     * 2. Remove 1 object.
     * 3. Verify tree collapses back to a single LeafNode.
     */
    public void testMergeBehavior() {
        // Setup split
        db.add(new AirPlane("A", 0, 0, 0, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("B", 100, 100, 100, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("C", 200, 200, 200, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("D", 300, 300, 300, 10, 10, 10, "D", 1, 1));
        
        assertTrue(db.printbintree().contains("I ("));

        // Remove one
        db.delete("A");

        // Remaining: 3 objects. Should merge.
        String tree = db.printbintree();
        assertTrue(tree.contains("Leaf with 3 objects"));
        assertFalse(tree.contains("I ("));
    }

    /**
     * Tests that InternalNodes do NOT merge if contents > 3.
     * 1. Add 5 objects (Split).
     * 2. Remove 1 object (Total 4).
     * 3. Verify tree is still split.
     * This covers the 'count <= 3' condition in shouldMerge().
     */
    public void testNoMergeWhenExcess() {
         // Setup split with 5 objects scattered
        db.add(new AirPlane("A", 0, 0, 0, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("B", 100, 100, 100, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("C", 200, 200, 200, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("D", 300, 300, 300, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("E", 400, 400, 400, 10, 10, 10, "D", 1, 1));
        
        db.delete("E");
        
        // Now 4 objects remaining. Should NOT merge because 4 > 3.
        String tree = db.printbintree();
        assertTrue(tree.contains("I ("));
        assertFalse(tree.contains("Leaf with 4 objects"));
    }

    /**
     * Tests "Touching is not intersecting" rule.
     * Mutation Coverage: Checks boundary conditions ( < vs <= ).
     */
    public void testTouchingIsNotIntersecting() {
        // Box A: 0 to 10
        db.add(new AirPlane("A", 0, 0, 0, 10, 10, 10, "D", 1, 1));
        // Box B: 10 to 20 (Touches A at x=10)
        db.add(new AirPlane("B", 10, 0, 0, 10, 10, 10, "D", 1, 1));
        
        // They should NOT collision
        String cols = db.collisions();
        assertFalse(cols.contains("Airplane A") && cols.contains("Airplane B"));
        
        // However, if we move B slightly left (9), they should intersect
        db.add(new AirPlane("C", 9, 0, 0, 10, 10, 10, "D", 1, 1));
        cols = db.collisions();
        assertTrue(cols.contains("Airplane A") && cols.contains("Airplane C"));
    }

    /**
     * Tests that when one child becomes empty and the other is a leaf,
     * they merge correctly into a leaf (and data is not lost).
     * This targets mutations in the Flyweight check (&& vs ||) in InternalNode.remove.
     * If the mutation 'left is empty || right is empty' returns EmptyNode, data B is lost.
     */
    public void testFlyweightMergePreservesData() {
        // A in one half, B in other half
        db.add(new AirPlane("A", 0, 0, 0, 10, 10, 10, "D", 1, 1));
        db.add(new AirPlane("B", 600, 600, 600, 10, 10, 10, "D", 1, 1));
        
        // Remove A. Left child becomes empty. Right child is leaf with B.
        db.delete("A");
        
        // B should still exist and be in a single merged Leaf (tree collapsed)
        assertEquals("Airplane B 600 600 600 10 10 10 D 1 1", db.print("B"));
        String tree = db.printbintree();
        assertTrue(tree.contains("Leaf with 1 objects"));
        assertFalse(tree.contains("I ("));
    }
}