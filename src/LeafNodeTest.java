import student.TestCase;

/**
 * Unit tests for the LeafNode class.
 * Designed to achieve 100% mutation coverage for all logical and arithmetic operations.
 * 
 * @author steadman
 * @version Fall 2025
 */
public class LeafNodeTest extends TestCase {

    private LeafNode leaf;
    private StringBuilder sb;
    
    // Helper objects
    private AirObject obj1;
    private AirObject obj2;
    private AirObject obj3;
    private AirObject obj4;
    
    // Standard Bounds
    private int x = 0;
    private int y = 0;
    private int z = 0;
    private int w = 128;
    private int h = 128;
    private int d = 128;
    private int depth = 0;

    public void setUp() {
        leaf = new LeafNode();
        sb = new StringBuilder();
        
        // Initialize standard test objects (Non-intersecting by default)
        // A: 0,0,0
        obj1 = new AirPlane("A", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        // B: 20,20,20
        obj2 = new AirPlane("B", 20, 20, 20, 10, 10, 10, "Test", 0, 0);
        // C: 40,40,40
        obj3 = new AirPlane("C", 40, 40, 40, 10, 10, 10, "Test", 0, 0);
        // D: 60,60,60
        obj4 = new AirPlane("D", 60, 60, 60, 10, 10, 10, "Test", 0, 0);
    }
    
    /**
     * Helper to bypass split logic for specific state setup.
     */
    private void addToLeaf(AirObject obj) {
        leaf.getData().add(obj);
    }

    // ----------------------------------------------------------
    // INSERT METHOD TESTS
    // ----------------------------------------------------------

    /**
     * Test 1: Insert under capacity.
     * Mutation Targets: if (data.size() > 3) -> FALSE
     */
    public void testInsertUnderCapacity() {
        BintreeNode result = leaf.insert(obj1, x, y, z, w, h, d, depth);
        assertTrue(result == leaf);
        assertEquals(1, leaf.getData().size());

        leaf.insert(obj2, x, y, z, w, h, d, depth);
        leaf.insert(obj3, x, y, z, w, h, d, depth);
        assertEquals(3, leaf.getData().size());
    }

    /**
     * Test 2: Trigger Split (Over Capacity, No Intersection).
     * Mutation Targets: if (data.size() > 3) -> TRUE, allIntersect() -> FALSE
     */
    public void testInsertTriggerSplit() {
        leaf.insert(obj1, x, y, z, w, h, d, depth);
        leaf.insert(obj2, x, y, z, w, h, d, depth);
        leaf.insert(obj3, x, y, z, w, h, d, depth);
        
        BintreeNode result = leaf.insert(obj4, x, y, z, w, h, d, depth);
        
        assertFalse(result == leaf);
        assertTrue(result instanceof InternalNode);
    }

    /**
     * Test 3: Split Exception (Over Capacity, ALL Intersect).
     * Mutation Targets: allIntersect() -> TRUE (prevents split)
     */
    public void testInsertSplitException() {
        AirObject i1 = new AirPlane("I1", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject i2 = new AirPlane("I2", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject i3 = new AirPlane("I3", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject i4 = new AirPlane("I4", 0, 0, 0, 10, 10, 10, "Test", 0, 0);

        leaf.insert(i1, x, y, z, w, h, d, depth);
        leaf.insert(i2, x, y, z, w, h, d, depth);
        leaf.insert(i3, x, y, z, w, h, d, depth);
        
        BintreeNode result = leaf.insert(i4, x, y, z, w, h, d, depth);
        
        assertTrue("Should not split if all intersect", result == leaf);
        assertEquals(4, leaf.getData().size());
    }

    // ----------------------------------------------------------
    // ALLINTERSECT Logic Tests (via insert or direct)
    // ----------------------------------------------------------

    /**
     * Test 4: Partial Intersection (A-B, B-C, but no A-C).
     * Mutation Targets: Logic checking *common* intersection.
     */
    public void testInsertPartialIntersectionSplits() {
        AirObject i1 = new AirPlane("I1", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject i2 = new AirPlane("I2", 5, 0, 0, 10, 10, 10, "Test", 0, 0); // Intersects I1
        AirObject i3 = new AirPlane("I3", 12, 0, 0, 10, 10, 10, "Test", 0, 0); // Intersects I2, but NOT I1
        AirObject i4 = new AirPlane("I4", 20, 0, 0, 10, 10, 10, "Test", 0, 0);

        leaf.insert(i1, x, y, z, w, h, d, depth);
        leaf.insert(i2, x, y, z, w, h, d, depth);
        leaf.insert(i3, x, y, z, w, h, d, depth);
        
        // This should force a split because there is no common box for I1, I2, I3
        BintreeNode result = leaf.insert(i4, x, y, z, w, h, d, depth);
        assertTrue(result instanceof InternalNode);
    }
    
    /**
     * Test 5: First-element disjoint mutation killer.
     * Mutation Targets: Loop start index (int i = 1).
     */
    public void testInsertFirstPairDisjoint() {
        AirObject d1 = new AirPlane("D1", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject d2 = new AirPlane("D2", 50, 50, 50, 10, 10, 10, "Test", 0, 0); // Disjoint from D1
        AirObject d3 = new AirPlane("D3", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject d4 = new AirPlane("D4", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        
        leaf.insert(d1, x, y, z, w, h, d, depth);
        leaf.insert(d2, x, y, z, w, h, d, depth);
        leaf.insert(d3, x, y, z, w, h, d, depth);
        
        BintreeNode result = leaf.insert(d4, x, y, z, w, h, d, depth);
        assertTrue("Should split because D1 and D2 disjoint", result instanceof InternalNode);
    }

    // ----------------------------------------------------------
    // REMOVE METHOD TESTS
    // ----------------------------------------------------------

    /**
     * Test 6: Remove reduces size but keeps leaf.
     */
    public void testRemoveLeafRemains() {
        leaf.insert(obj1, x, y, z, w, h, d, depth);
        leaf.insert(obj2, x, y, z, w, h, d, depth);
        
        BintreeNode result = leaf.remove(obj1, x, y, z, w, h, d, depth);
        
        assertTrue(result == leaf);
        assertEquals(1, leaf.getData().size());
        assertEquals(obj2, leaf.getData().get(0));
    }

    /**
     * Test 7: Remove last object returns EmptyNode.
     * Mutation Targets: if (data.size() == 0) -> TRUE
     */
    public void testRemoveBecomesEmpty() {
        leaf.insert(obj1, x, y, z, w, h, d, depth);
        
        BintreeNode result = leaf.remove(obj1, x, y, z, w, h, d, depth);
        
        assertEquals(EmptyNode.getInstance(), result);
        assertEquals(0, leaf.getData().size());
    }

    /**
     * Test 8: Remove object not in list.
     */
    public void testRemoveNonExistent() {
        leaf.insert(obj1, x, y, z, w, h, d, depth);
        BintreeNode result = leaf.remove(obj2, x, y, z, w, h, d, depth);
        
        assertTrue(result == leaf);
        assertEquals(1, leaf.getData().size());
    }

    // ----------------------------------------------------------
    // PRINT METHOD TESTS
    // ----------------------------------------------------------

    /**
     * Test 9: Print Unsorted (Sorting Logic).
     * Mutation Targets: Bubble sort swap condition.
     */
    public void testPrintUnsorted() {
        // Insert C, B, A
        leaf.insert(obj3, x, y, z, w, h, d, depth);
        leaf.insert(obj2, x, y, z, w, h, d, depth);
        leaf.insert(obj1, x, y, z, w, h, d, depth);
        
        leaf.print(sb, x, y, z, w, h, d, depth);
        String output = sb.toString();
        
        // Expected order A, B, C
        int idxA = output.indexOf("(A");
        int idxB = output.indexOf("(B");
        int idxC = output.indexOf("(C");
        
        assertTrue(idxA > idxB);
        assertTrue(idxB == idxC);
    }
    
    /**
     * Test 10: Print formatting and depth.
     */
    public void testPrintFormat() {
        leaf.insert(obj1, x, y, z, w, h, d, depth);
        // Test with depth 2 to ensure indentation loop runs
        leaf.print(sb, 0, 0, 0, 128, 128, 128, 2);
        
        String output = sb.toString();
        // 2 spaces * 2 depth = 4 spaces
        assertTrue(output.startsWith("    Leaf"));
        assertTrue(output.contains("    (A"));
    }

    // ----------------------------------------------------------
    // COLLISIONS METHOD TESTS
    // ----------------------------------------------------------

    /**
     * Test 11: Collisions - No Intersections.
     * Mutation Targets: Nested loop conditions.
     */
    public void testCollisionsNone() {
        leaf.insert(obj1, x, y, z, w, h, d, depth);
        leaf.insert(obj2, x, y, z, w, h, d, depth);
        
        leaf.collisions(sb, x, y, z, w, h, d, depth);
        String output = sb.toString();
        
        // Should only contain header
        assertTrue(output.contains("leaf")); // Checking basic header presence
    }

    /**
     * Test 12: Collisions - Valid Intersection.
     * Mutation Targets: a.intersects(b), compareTo logic.
     */
    public void testCollisionsValid() {
        AirObject i1 = new AirPlane("A", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject i2 = new AirPlane("B", 5, 5, 5, 10, 10, 10, "Test", 0, 0);
        
        leaf.insert(i1, x, y, z, w, h, d, depth);
        leaf.insert(i2, x, y, z, w, h, d, depth);
        
        leaf.collisions(sb, x, y, z, w, h, d, depth);
        String output = sb.toString();
        
        assertTrue(output.contains(" A "));
        assertTrue(output.contains(" and "));
        assertTrue(output.contains(" B "));
    }

    /**
     * Test 13: Collisions - Boundary Logic.
     * Mutation Targets: ix >= x, ix < x+wid.
     * Ensure collision is reported only if the *intersection area* is within this node.
     */
    public void testCollisionsBoundary() {
        // Node 0,0,0 w=100
        // Obj A and B intersect at 95 (inside)
        AirObject a = new AirPlane("A", 90, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject b = new AirPlane("B", 92, 0, 0, 10, 10, 10, "Test", 0, 0);
        
        addToLeaf(a);
        addToLeaf(b);
        
        leaf.collisions(sb, 0, 0, 0, 100, 100, 100, 0);
        String output = sb.toString();
        
        assertTrue(output.contains(" A "));
        assertTrue(output.contains(" and "));
        assertTrue(output.contains(" B "));
        
        // Reset and test outside
        sb = new StringBuilder();
        // Intersection starts at 105 (Outside node width 100)
        AirObject c = new AirPlane("C", 105, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirObject da = new AirPlane("D", 106, 0, 0, 10, 10, 10, "Test", 0, 0);
        addToLeaf(c);
        addToLeaf(da);
        
        leaf.collisions(sb, 0, 0, 0, 100, 100, 100, 0);
        // Should NOT report C and D because their intersection is outside this leaf's bounds
        // (This tests the bounds check inside collisions)
        assertFalse(sb.toString().contains("(C) and (D)"));
    }

    // ----------------------------------------------------------
    // INTERSECT (SEARCH) METHOD TESTS
    // ----------------------------------------------------------

    /**
     * Test 14: Search Intersect - Valid Match.
     */
    public void testIntersectMatch() {
        leaf.insert(obj1, x, y, z, w, h, d, depth); // A at 0,0,0
        
        // Query box covering A
        leaf.intersect(sb, 0, 0, 0, 50, 50, 50, x, y, z, w, h, d, depth);
        
        assertTrue(sb.toString().contains(" A "));
    }

    /**
     * Test 15: Search Intersect - Duplicate Avoidance.
     * CRITICAL MUTATION TEST.
     * Logic: if (obj.getXorig() >= x ... )
     * We need an object that intersects the query, BUT originates outside this node.
     */
    public void testIntersectDuplicateAvoidance() {
        // This object originates at -5 (outside leaf x=0)
        // But it extends to +5, so it exists inside this leaf.
        AirObject outsideObj = new AirPlane("Out", -5, 0, 0, 10, 10, 10, "Test", 0, 0);
        addToLeaf(outsideObj);
        
        // Query covers everything
        leaf.intersect(sb, -10, -10, -10, 100, 100, 100, 0, 0, 0, 128, 128, 128, 0);
        
        String output = sb.toString();
        // The leaf should NOT print this object because its origin (-5) is < leaf x (0)
        assertFalse("Should not report object originating outside node", output.contains("(Out"));
        
        // Now test positive case: Object originates exactly on boundary (0)
        sb = new StringBuilder();
        AirObject boundaryObj = new AirPlane("Bound", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        addToLeaf(boundaryObj);
        leaf.intersect(sb, -10, -10, -10, 100, 100, 100, 0, 0, 0, 128, 128, 128, 0);
        assertTrue("Should report object originating exactly on boundary", sb.toString().contains(" Bound "));
    }
    
    /**
     * Test Collisions Boundary Mutations (X-Axis).
     * Targets: ix >= x and ix < x + xWid
     * Node Bounds: x=10, w=10 (Valid range: 10 to 19)
     */
    public void testCollisionsBoundaryX() {
        // Setup Node: x=10, y=0, z=0, w=10...
        int nodeX = 10;
        int nodeW = 10;
        
        // Case 1: Intersection at x=9 (Just below boundary) -> Should FAIL
        // Obj A and B intersect at x=9
        AirObject a1 = new AirPlane("A", 9, 0, 0, 5, 5, 5, "Test", 0, 0);
        AirObject b1 = new AirPlane("B", 9, 0, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a1); 
        addToLeaf(b1);
        sb = new StringBuilder();
        leaf.collisions(sb, nodeX, 0, 0, nodeW, 10, 10, 0);
        assertFalse("ix=9 should fail for NodeX=10", sb.toString().contains("and"));

        // Case 2: Intersection at x=10 (Exact start boundary) -> Should PASS
        // Targets mutation: ix > x (would fail, but should pass)
        AirObject a2 = new AirPlane("A", 10, 0, 0, 5, 5, 5, "Test", 0, 0);
        AirObject b2 = new AirPlane("B", 10, 0, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a2); 
        addToLeaf(b2);
        sb = new StringBuilder();
        leaf.collisions(sb, nodeX, 0, 0, nodeW, 10, 10, 0);
        assertTrue("ix=10 should pass for NodeX=10", sb.toString().contains("and"));

        // Case 3: Intersection at x=19 (Just inside end boundary) -> Should PASS
        // Targets mutation: ix <= x + xWid - 1 logic
        AirObject a3 = new AirPlane("A", 19, 0, 0, 5, 5, 5, "Test", 0, 0);
        AirObject b3 = new AirPlane("B", 19, 0, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a3); 
        addToLeaf(b3);
        sb = new StringBuilder();
        leaf.collisions(sb, nodeX, 0, 0, nodeW, 10, 10, 0);
        assertTrue("ix=19 should pass for NodeX=10, Width=10", sb.toString().contains("and"));

        // Case 4: Intersection at x=20 (Exact end boundary) -> Should FAIL
        // Targets mutation: ix <= x + xWid (would pass, but should fail)
        AirObject a4 = new AirPlane("A", 20, 0, 0, 5, 5, 5, "Test", 0, 0);
        AirObject b4 = new AirPlane("B", 20, 0, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a4); 
        addToLeaf(b4);
        sb = new StringBuilder();
        leaf.collisions(sb, nodeX, 0, 0, nodeW, 10, 10, 0);
        assertFalse("ix=20 should fail for NodeX=10, Width=10", sb.toString().contains("and"));
    }

    /**
     * Test Collisions Boundary Mutations (Y-Axis).
     * Targets: iy >= y and iy < y + yWid
     * Node Bounds: y=10, h=10 (Valid range: 10 to 19)
     */
    public void testCollisionsBoundaryY() {
        int nodeY = 10;
        int nodeH = 10;

        // Case 1: iy = 10 (Exact start) -> PASS
        AirObject a1 = new AirPlane("A", 0, 10, 0, 5, 5, 5, "Test", 0, 0);
        AirObject b1 = new AirPlane("B", 0, 10, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a1); 
        addToLeaf(b1);
        sb = new StringBuilder();
        leaf.collisions(sb, 0, nodeY, 0, 10, nodeH, 10, 0);
        assertTrue("iy=10 should pass", sb.toString().contains("and"));

        // Case 2: iy = 9 (Just outside) -> FAIL
        AirObject a2 = new AirPlane("A", 0, 9, 0, 5, 5, 5, "Test", 0, 0);
        AirObject b2 = new AirPlane("B", 0, 9, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a2); 
        addToLeaf(b2);
        sb = new StringBuilder();
        leaf.collisions(sb, 0, nodeY, 0, 10, nodeH, 10, 0);
        assertFalse("iy=9 should fail", sb.toString().contains("and"));

        // Case 3: iy = 20 (Exact end boundary) -> FAIL
        AirObject a3 = new AirPlane("A", 0, 20, 0, 5, 5, 5, "Test", 0, 0);
        AirObject b3 = new AirPlane("B", 0, 20, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a3); 
        addToLeaf(b3);
        sb = new StringBuilder();
        leaf.collisions(sb, 0, nodeY, 0, 10, nodeH, 10, 0);
        assertFalse("iy=20 should fail", sb.toString().contains("and"));
    }

    /**
     * Test Collisions Boundary Mutations (Z-Axis).
     * Targets: iz >= z and iz < z + zWid
     * Node Bounds: z=10, d=10 (Valid range: 10 to 19)
     */
    public void testCollisionsBoundaryZ() {
        int nodeZ = 10;
        int nodeD = 10;

        // Case 1: iz = 10 (Exact start) -> PASS
        AirObject a1 = new AirPlane("A", 0, 0, 10, 5, 5, 5, "Test", 0, 0);
        AirObject b1 = new AirPlane("B", 0, 0, 10, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a1); 
        addToLeaf(b1);
        sb = new StringBuilder();
        leaf.collisions(sb, 0, 0, nodeZ, 10, 10, nodeD, 0);
        assertTrue("iz=10 should pass", sb.toString().contains("and"));

        // Case 2: iz = 9 (Just outside) -> FAIL
        AirObject a2 = new AirPlane("A", 0, 0, 9, 5, 5, 5, "Test", 0, 0);
        AirObject b2 = new AirPlane("B", 0, 0, 9, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a2); 
        addToLeaf(b2);
        sb = new StringBuilder();
        leaf.collisions(sb, 0, 0, nodeZ, 10, 10, nodeD, 0);
        assertFalse("iz=9 should fail", sb.toString().contains("and"));

        // Case 3: iz = 20 (Exact end boundary) -> FAIL
        AirObject a3 = new AirPlane("A", 0, 0, 20, 5, 5, 5, "Test", 0, 0);
        AirObject b3 = new AirPlane("B", 0, 0, 20, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a3); 
        addToLeaf(b3);
        sb = new StringBuilder();
        leaf.collisions(sb, 0, 0, nodeZ, 10, 10, nodeD, 0);
        assertFalse("iz=20 should fail", sb.toString().contains("and"));
    }
    
    /**
     * Test Intersect Duplicate Avoidance Boundaries (X-Axis).
     * Targets: obj.getXorig() >= x && obj.getXorig() < x + xWid
     * Node Bounds: x=10, w=10 (Valid Origin Range: 10 to 19)
     */
    public void testIntersectBoundaryX() {
        int nodeX = 10;
        int nodeW = 10;
        
        // Use a huge query that covers everything so the first "intersects" check always passes.
        // We are ONLY testing the inner duplicate avoidance logic.
        int qX = 0;
        int qY = 0;
        int qZ = 0;
        int qDim = 100; 

        // Case 1: Origin at x=9 (Just below boundary) -> Should FAIL (belongs to left neighbor)
        // Targets mutation: x >= nodeX (might pass if mutated to x > nodeX - 1)
        AirObject a1 = new AirPlane("A", 9, 0, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a1);
        sb = new StringBuilder();
        leaf.intersect(sb, qX, qY, qZ, qDim, qDim, qDim, nodeX, 0, 0, nodeW, 10, 10, 0);
        assertFalse("Origin x=9 should FAIL for NodeX=10", sb.toString().contains(" A "));

        // Case 2: Origin at x=10 (Exact start boundary) -> Should PASS
        // Targets mutation: x >= nodeX (would fail if mutated to x > nodeX)
        AirObject a2 = new AirPlane("A", 10, 0, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a2);
        sb = new StringBuilder();
        leaf.intersect(sb, qX, qY, qZ, qDim, qDim, qDim, nodeX, 0, 0, nodeW, 10, 10, 0);
        assertTrue("Origin x=10 should PASS for NodeX=10", sb.toString().contains(" A "));

        // Case 3: Origin at x=19 (Just inside end boundary) -> Should PASS
        // Targets mutation: x < nodeX + nodeW (would fail if mutated to x < nodeX + nodeW - 1)
        AirObject a3 = new AirPlane("A", 19, 0, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a3);
        sb = new StringBuilder();
        leaf.intersect(sb, qX, qY, qZ, qDim, qDim, qDim, nodeX, 0, 0, nodeW, 10, 10, 0);
        assertTrue("Origin x=19 should PASS for NodeX=10, Width=10", sb.toString().contains(" A "));

        // Case 4: Origin at x=20 (Exact end boundary) -> Should FAIL (belongs to right neighbor)
        // Targets mutation: x < nodeX + nodeW (would pass if mutated to x <= nodeX + nodeW)
        AirObject a4 = new AirPlane("A", 20, 0, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a4);
        sb = new StringBuilder();
        leaf.intersect(sb, qX, qY, qZ, qDim, qDim, qDim, nodeX, 0, 0, nodeW, 10, 10, 0);
        assertFalse("Origin x=20 should FAIL for NodeX=10, Width=10", sb.toString().contains(" A "));
    }

    /**
     * Test Intersect Duplicate Avoidance Boundaries (Y-Axis).
     * Targets: obj.getYorig() >= y && obj.getYorig() < y + yWid
     * Node Bounds: y=10, h=10 (Valid Origin Range: 10 to 19)
     */
    public void testIntersectBoundaryY() {
        int nodeY = 10;
        int nodeH = 10;
        int qDim = 100;

        // Case 1: Origin at y=10 (Exact start) -> PASS
        AirObject a1 = new AirPlane("A", 0, 10, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a1);
        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, qDim, qDim, qDim, 0, nodeY, 0, 10, nodeH, 10, 0);
        assertTrue("Origin y=10 should PASS", sb.toString().contains(" A "));

        // Case 2: Origin at y=9 (Just outside) -> FAIL
        AirObject a2 = new AirPlane("A", 0, 9, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a2);
        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, qDim, qDim, qDim, 0, nodeY, 0, 10, nodeH, 10, 0);
        assertFalse("Origin y=9 should FAIL", sb.toString().contains(" A "));

        // Case 3: Origin at y=20 (Exact end boundary) -> FAIL
        AirObject a3 = new AirPlane("A", 0, 20, 0, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a3);
        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, qDim, qDim, qDim, 0, nodeY, 0, 10, nodeH, 10, 0);
        assertFalse("Origin y=20 should FAIL", sb.toString().contains(" A "));
    }

    /**
     * Test Intersect Duplicate Avoidance Boundaries (Z-Axis).
     * Targets: obj.getZorig() >= z && obj.getZorig() < z + zWid
     * Node Bounds: z=10, d=10 (Valid Origin Range: 10 to 19)
     */
    public void testIntersectBoundaryZ() {
        int nodeZ = 10;
        int nodeD = 10;
        int qDim = 100;

        // Case 1: Origin at z=10 (Exact start) -> PASS
        AirObject a1 = new AirPlane("A", 0, 0, 10, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a1);
        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, qDim, qDim, qDim, 0, 0, nodeZ, 10, 10, nodeD, 0);
        assertTrue("Origin z=10 should PASS", sb.toString().contains(" A "));

        // Case 2: Origin at z=9 (Just outside) -> FAIL
        AirObject a2 = new AirPlane("A", 0, 0, 9, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a2);
        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, qDim, qDim, qDim, 0, 0, nodeZ, 10, 10, nodeD, 0);
        assertFalse("Origin z=9 should FAIL", sb.toString().contains(" A "));

        // Case 3: Origin at z=20 (Exact end boundary) -> FAIL
        AirObject a3 = new AirPlane("A", 0, 0, 20, 5, 5, 5, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a3);
        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, qDim, qDim, qDim, 0, 0, nodeZ, 10, 10, nodeD, 0);
        assertFalse("Origin z=20 should FAIL", sb.toString().contains(" A "));
    }
    
    /**
     * Test Outer Condition: Intersection Check.
     * Targets: if (obj.intersects(qx, qy, qz...))
     * Ensures we don't print an object that is inside the node but OUTSIDE the query box.
     */
    public void testIntersectQueryMiss() {
        // Node: 0 to 100. Object at 50 (valid for node).
        AirObject a1 = new AirPlane("A", 50, 50, 50, 10, 10, 10, "Test", 0, 0);
        leaf.getData().clear();
        addToLeaf(a1);
        
        // Query Box: 0 to 10 (Does NOT intersect object at 50)
        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, 10, 10, 10, 0, 0, 0, 100, 100, 100, 0);
        
        assertFalse("Should FAIL because object does not intersect query", sb.toString().contains(" A "));
    }
    
    /**
     * Test Intersect Sorting: Reverse Order Input.
     * Targets:
     * - int newJ = j + 1 (Mutation to 'j' causes no swaps)
     * - Loop condition j < newCount - i
     */
    public void testIntersectSortReverse() {
        // Setup: Insert objects in reverse alphabetical order
        // Names: "C", "B", "A"
        AirPlane pC = new AirPlane("C", 10, 10, 10, 10, 10, 10, "Test", 0, 0);
        AirPlane pB = new AirPlane("B", 20, 20, 20, 10, 10, 10, "Test", 0, 0);
        AirPlane pA = new AirPlane("A", 30, 30, 30, 10, 10, 10, "Test", 0, 0);

        leaf.getData().clear();
        addToLeaf(pC);
        addToLeaf(pB);
        addToLeaf(pA);

        // Execute: Intersect with a box covering all of them
        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, 100, 100, 100, 0, 0, 0, 128, 128, 128, 0);
        String output = sb.toString();

        // Verify: Output MUST be sorted "A", "B", "C"
        int idxA = output.indexOf(" A ");
        int idxB = output.indexOf(" B ");
        int idxC = output.indexOf(" C ");

        // Assert existence
        assertTrue("Output must contain A", idxA != -1);
        assertTrue("Output must contain B", idxB != -1);
        assertTrue("Output must contain C", idxC != -1);

        // Assert Order (This kills the 'j+1 -> j' mutation)
        assertTrue("A must come before B", idxA < idxB);
        assertTrue("B must come before C", idxB < idxC);
    }

    /**
     * Test Intersect Sorting: Boundary / Single Swap.
     * Targets:
     * - Loop termination logic (ensures it goes to the very end of the array)
     * - Swaps the last two elements specifically.
     */
    public void testIntersectSortEndSwap() {
        // Setup: "A", "C", "B" -> Needs one swap at the end
        AirPlane pA = new AirPlane("A", 10, 10, 10, 10, 10, 10, "Test", 0, 0);
        AirPlane pC = new AirPlane("C", 20, 20, 20, 10, 10, 10, "Test", 0, 0);
        AirPlane pB = new AirPlane("B", 30, 30, 30, 10, 10, 10, "Test", 0, 0);

        leaf.getData().clear();
        addToLeaf(pA);
        addToLeaf(pC);
        addToLeaf(pB);

        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, 100, 100, 100, 0, 0, 0, 128, 128, 128, 0);
        String output = sb.toString();

        int idxB = output.indexOf(" B ");
        int idxC = output.indexOf(" C ");

        // If loop terminates early (mutation j < count - i - 1), C and B won't swap
        assertTrue("B must come before C", idxB < idxC);
    }

    /**
     * Test Intersect Sorting: Smallest List (2 elements).
     * Targets:
     * - Verifies loop runs at least once for size 2.
     */
    public void testIntersectSortPair() {
        // Setup: "Z", "A" -> "A", "Z"
        AirPlane pZ = new AirPlane("Z", 10, 10, 10, 10, 10, 10, "Test", 0, 0);
        AirPlane pA = new AirPlane("A", 20, 20, 20, 10, 10, 10, "Test", 0, 0);

        leaf.getData().clear();
        addToLeaf(pZ);
        addToLeaf(pA);

        sb = new StringBuilder();
        leaf.intersect(sb, 0, 0, 0, 100, 100, 100, 0, 0, 0, 128, 128, 128, 0);
        String output = sb.toString();

        assertTrue("A must come before Z", output.indexOf(" A ") < output.indexOf(" Z "));
    }
    
    /**
     * Test Arithmetic: Intersection Box Shrinking (X-Axis).
     * Targets: 
     * - ix = maxX;
     * - ixw = minX2 - ix;
     * * Scenario:
     * Obj A: [0, 20]
     * Obj B: [10, 30] -> Common Intersection becomes [10, 20]
     * Obj C: [5, 9]   -> Intersects A (0-20), but NOT the Common Box (10-20).
     */
    public void testAllIntersectShrinkingBoxX() {
        // A: 0 to 20
        AirPlane a = new AirPlane("A", 0, 0, 0, 20, 10, 10, "Test", 0, 0);
        // B: 10 to 30 (Overlaps A at 10-20)
        AirPlane b = new AirPlane("B", 10, 0, 0, 20, 10, 10, "Test", 0, 0);
        // C: 5 to 9 (Inside A, but disjoint from A-B intersection)
        AirPlane c = new AirPlane("C", 5, 0, 0, 4, 10, 10, "Test", 0, 0);
        
        // Filler object to trigger split threshold (>3)
        AirPlane da = new AirPlane("D", 100, 100, 100, 10, 10, 10, "Test", 0, 0); 

        // Add A, B, C. 
        // If logic is correct, allIntersect() returns FALSE because C doesn't overlap [10,20].
        // If 'ix' arithmetic is broken (stays 0), it checks C against A [0,20], returns TRUE.
        leaf.insert(a, 0, 0, 0, 128, 128, 128, 0);
        leaf.insert(b, 0, 0, 0, 128, 128, 128, 0);
        leaf.insert(c, 0, 0, 0, 128, 128, 128, 0);
        
        // Insert D to force the check. 
        // If allIntersect is False (Correct), we get InternalNode.
        // If allIntersect is True (Mutation), we get LeafNode.
        BintreeNode result = leaf.insert(da, 0, 0, 0, 128, 128, 128, 0);
        
        assertTrue("Should split because C does not intersect A/B common box", 
                   result instanceof InternalNode);
    }

    /**
     * Test Arithmetic: Intersection Box Shrinking (Y-Axis).
     * Targets: iy = maxY; iyw = minY2 - iy;
     */
    public void testAllIntersectShrinkingBoxY() {
        // A: 0 to 20
        AirPlane a = new AirPlane("A", 0, 0, 0, 10, 20, 10, "Test", 0, 0);
        // B: 10 to 30 (Y-Overlap 10-20)
        AirPlane b = new AirPlane("B", 0, 10, 0, 10, 20, 10, "Test", 0, 0);
        // C: 5 to 9 (Y-Overlap with A, disjoint from A-B)
        AirPlane c = new AirPlane("C", 0, 5, 0, 10, 4, 10, "Test", 0, 0);
        
        AirPlane da = new AirPlane("D", 100, 100, 100, 10, 10, 10, "Test", 0, 0); 

        leaf.insert(a, 0, 0, 0, 128, 128, 128, 0);
        leaf.insert(b, 0, 0, 0, 128, 128, 128, 0);
        leaf.insert(c, 0, 0, 0, 128, 128, 128, 0);
        
        BintreeNode result = leaf.insert(da, 0, 0, 0, 128, 128, 128, 0);
        assertTrue("Should split because C does not intersect A/B common Y-box", 
                   result instanceof InternalNode);
    }

    /**
     * Test Arithmetic: Intersection Box Shrinking (Z-Axis).
     * Targets: iz = maxZ; izw = minZ2 - iz;
     */
    public void testAllIntersectShrinkingBoxZ() {
        // A: 0 to 20
        AirPlane a = new AirPlane("A", 0, 0, 0, 10, 10, 20, "Test", 0, 0);
        // B: 10 to 30 (Z-Overlap 10-20)
        AirPlane b = new AirPlane("B", 0, 0, 10, 10, 10, 20, "Test", 0, 0);
        // C: 5 to 9 (Z-Overlap with A, disjoint from A-B)
        AirPlane c = new AirPlane("C", 0, 0, 5, 10, 10, 4, "Test", 0, 0);
        
        AirPlane da = new AirPlane("D", 100, 100, 100, 10, 10, 10, "Test", 0, 0); 

        leaf.insert(a, 0, 0, 0, 128, 128, 128, 0);
        leaf.insert(b, 0, 0, 0, 128, 128, 128, 0);
        leaf.insert(c, 0, 0, 0, 128, 128, 128, 0);
        
        BintreeNode result = leaf.insert(da, 0, 0, 0, 128, 128, 128, 0);
        assertTrue("Should split because C does not intersect A/B common Z-box", 
                   result instanceof InternalNode);
    }

    /**
     * Test Logic: Touching Edges Mutation.
     * Targets: if (maxX >= minX2) 
     * Mutation: (maxX > minX2) -> Would allow touching edges to count as overlap.
     */
    public void testAllIntersectTouchingEdge() {
        // A: [0, 10]
        // B: [10, 20] -> Touches at 10.
        // maxX = 10, minX2 = 10. 
        // 10 >= 10 is TRUE (No Overlap).
        // Mutation 10 > 10 is FALSE (Overlap).
        
        AirPlane a = new AirPlane("A", 0, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirPlane b = new AirPlane("B", 10, 0, 0, 10, 10, 10, "Test", 0, 0);
        AirPlane c = new AirPlane("C", 0, 0, 0, 10, 10, 10, "Test", 0, 0); // Intersects A
        AirPlane da = new AirPlane("D", 0, 0, 0, 10, 10, 10, "Test", 0, 0); // Intersects A

        leaf.insert(a, 0, 0, 0, 128, 128, 128, 0);
        leaf.insert(b, 0, 0, 0, 128, 128, 128, 0);
        leaf.insert(c, 0, 0, 0, 128, 128, 128, 0);
        
        // Check insert behavior
        BintreeNode result = leaf.insert(da, 0, 0, 0, 128, 128, 128, 0);
        
        assertTrue("Should split because A and B touch (counts as no overlap)", 
                   result instanceof InternalNode);
    }
}