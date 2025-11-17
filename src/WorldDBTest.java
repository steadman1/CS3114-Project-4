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
     */
    public void testConstructorNullRandom() {
        // The constructor code should handle this by creating a new Random
        WorldDB nullDb = new WorldDB(null);
        assertNotNull(nullDb);
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
     */
    public void testAddValid() {
        AirPlane plane = new AirPlane(
            "plane1", 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        assertTrue(db.add(plane));
        assertEquals(plane.toString(), db.print("plane1"));
    }


    /**
     * Tests adding a duplicate name.
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
        
        // An empty Bintree should have 1 node (the flyweight)
        assertTrue(db.printbintree().contains("E\n"));
        assertTrue(db.printbintree().contains("1 nodes printed"));
    }


    /**
     * Tests printing non-empty skiplist and bintree.
     */
    public void testPrintNonEmptyStructs() {
        AirPlane plane = new AirPlane(
            "plane1", 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        db.add(plane);
        
        assertFalse(db.printskiplist().contains("empty"));
        assertTrue(db.printskiplist().contains("Value (Airplane plane1"));
        
        assertFalse(db.printbintree().contains("E\n"));
        assertTrue(db.printbintree().contains("Node at"));
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

    // --- Intersect Tests ---

    /**
     * Tests intersect() with a valid query box.
     */
    public void testIntersectValid() {
        // This just checks that the call is delegated.
        // Bintree tests are responsible for correctness.
        String result = db.intersect(10, 10, 10, 10, 10, 10);
        assertNotNull(result);
        assertTrue(result.contains("nodes visited"));
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
    
    // --- Collisions Test ---
    
    /**
     * Tests collisions() method.
     */
    public void testCollisions() {
        AirPlane plane1 = new AirPlane(
            "p1", 10, 10, 10, 10, 10, 10, "Delta", 1, 2);
        AirPlane plane2 = new AirPlane(
            "p2", 15, 15, 15, 10, 10, 10, "Delta", 2, 2);
        AirPlane plane3 = new AirPlane(
            "p3", 50, 50, 50, 10, 10, 10, "Delta", 3, 2);
            
        db.add(plane1);
        db.add(plane2);
        db.add(plane3);
        
        String collisions = db.collisions();
        // Bintree implementation determines the exact output,
        // but we expect p1 and p2 to be mentioned.
        assertTrue(collisions.contains("p1") && collisions.contains("p2"));
        assertFalse(collisions.contains("p3"));
    }
}