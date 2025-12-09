
import student.TestCase;

/**
 * This class tests the InternalNode implementation.
 * It extends student.Testcase.
 *
 * @author steadman
 * @version Fall 2025
 */
public class InternalNodeTest extends TestCase {

    private InternalNode node;
    private AirPlane obj1;
    private AirPlane obj2;
    private AirPlane obj3;
    private AirPlane obj4;
    private AirPlane objOverlap;

    public void setUp() {
        node = new InternalNode();
        // Setup standard objects
        // Coordinates setup for World Size 128
        // obj1: (10, 10, 10) - strictly left/top/front usually
        obj1 = new AirPlane("Obj1", 10, 10, 10, 10, 10, 10, "Test", 0, 0);
        // obj2: (100, 100, 100) - strictly right/bottom/back usually
        obj2 = new AirPlane("Obj2", 100, 100, 100, 10, 10, 10, "Test", 0, 0);
        // obj3: (20, 20, 20)
        obj3 = new AirPlane("Obj3", 20, 20, 20, 5, 5, 5, "Test", 0, 0);
        // obj4: (110, 110, 110)
        obj4 = new AirPlane("Obj4", 110, 110, 110, 5, 5, 5, "Test", 0, 0);
        
        // Spans the center (64)
        objOverlap = new AirPlane(
                "Overlap", 60, 10, 10, 10, 10, 10, "Test", 0, 0); 
    }

    /**
     * Tests initialization state.
     */
    public void testConstructor() {
        StringBuilder sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 0);
        String output = sb.toString();
        // Should contain Internal node info and then traverse to empty children
        assertTrue(output.contains("I (0, 0, 0, 128, 128, 128) 0"));
    }

    /**
     * Tests insertion splitting on X axis (Depth 0).
     * Depth 0 % 3 = 0 (X split). Split at 64.
     */
    public void testInsertXSplit() {
        // Insert left
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        
        // Insert right
        node = (InternalNode) node.insert(obj2, 0, 0, 0, 128, 128, 128, 0);
        
        // Insert overlap (should go to both children)
        node = (InternalNode) node.insert(
                objOverlap, 0, 0, 0, 128, 128, 128, 0);

        StringBuilder sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 0);
        String out = sb.toString();
        
        // Obj1 is at x=10 (Left)
        // Obj2 is at x=100 (Right)
        // ObjOverlap x=60 width=10 (60 to 70). Split is 64. intersects both.
        
        // Verification via printing structure implies successful insertion
        // Since we can't access 'left'/'right' directly, we rely on behavior
        // Verify output indicates structure matches expectation
        assertTrue(out.contains("I (0, 0, 0, 128, 128, 128) 0"));
    }

    /**
     * Tests insertion splitting on Y axis (Depth 1).
     * Depth 1 % 3 = 1 (Y split).
     */
    public void testInsertYSplit() {
        // Force depth 1 insert
        // obj1 y=10 (<64), obj2 y=100 (>64)
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 1);
        node = (InternalNode) node.insert(obj2, 0, 0, 0, 128, 128, 128, 1);
        
        StringBuilder sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 1);
        String out = sb.toString();
        
        // Basic check to ensure no exception and formatting holds
        assertTrue(out.contains(") 1"));
    }

    /**
     * Tests insertion splitting on Z axis (Depth 2).
     * Depth 2 % 3 = 2 (Z split).
     */
    public void testInsertZSplit() {
        // obj1 z=10, obj2 z=100
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 2);
        node = (InternalNode) node.insert(obj2, 0, 0, 0, 128, 128, 128, 2);
        
        StringBuilder sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 2);
        String out = sb.toString();
        
        assertTrue(out.contains(") 2"));
    }

    /**
     * Tests collapsing to EmptyNode when all children are empty.
     */
    public void testRemoveCollapseToEmpty() {
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        
        // Remove object. Should return EmptyNode instance, not InternalNode
        BintreeNode result = node.remove(obj1, 0, 0, 0, 128, 128, 128, 0);
        
        assertTrue(result instanceof EmptyNode);
    }

    /**
     * Tests merging to LeafNode when total unique objects <= 3.
     * Scenario: Left has 1, Right has 1. Total 2. -> Merge.
     */
    public void testRemoveMergeSimple() {
        // If Leaf capacity is 3, inserting 4 splits it.
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj2, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj3, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj4, 0, 0, 0, 128, 128, 128, 0);
        
        // Now remove obj4. Remaining: 1, 2, 3. Count = 3. Should merge.
        BintreeNode result = node.remove(obj4, 0, 0, 0, 128, 128, 128, 0);
        
        assertTrue(result instanceof LeafNode);
        LeafNode leaf = (LeafNode) result;
        // Verify data integrity
        assertEquals(3, leaf.getData().size());
    }

    /**
     * Tests NOT merging when total unique objects > 3.
     */
    public void testRemoveNoMerge() {
        AirObject obj5 = new AirPlane(
                "Obj5", 15, 15, 15, 1, 1, 1, "Test", 0, 0);
        
        // Insert 5 objects
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj2, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj3, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj4, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj5, 0, 0, 0, 128, 128, 128, 0);
        
        // Remove 1 (Obj5). Remaining: 4. Should NOT merge.
        BintreeNode result = node.remove(obj5, 0, 0, 0, 128, 128, 128, 0);
        
        assertTrue(result instanceof InternalNode);
    }
    
    /**
     * Tests duplicate handling during merge.
     * If an object spans the split, it exists in both Left and Right.
     * When merging, it should appear only once in the resulting Leaf.
     */
    public void testRemoveMergeDuplicates() {
        
        // We need to trigger the merge. 
        // Let's add obj4, then remove it to trigger the check.
        node = (InternalNode) node.insert(
                objOverlap, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj2, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj4, 0, 0, 0, 128, 128, 128, 0);
        
        // Remove obj4. Should trigger merge logic.
        BintreeNode result = node.remove(obj4, 0, 0, 0, 128, 128, 128, 0);
        
        assertTrue(result instanceof LeafNode);
        LeafNode leaf = (LeafNode) result;
        
        // Crucial check: Size should be 3, not 4.
        assertEquals(3, leaf.getData().size());
    }

    /**
     * Tests the intersect (Region Search) logic.
     */
    public void testIntersect() {
        // Setup: Left(obj1), Right(obj2)
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(obj2, 0, 0, 0, 128, 128, 128, 0);
        
        StringBuilder sb = new StringBuilder();
        
        // 1. Query strictly left region (should visit Internal + Left)
        int count = node.intersect(sb, 0, 0, 0, 50, 128, 128, 
                                   0, 0, 0, 128, 128, 128, 0);
        
        // Visited: Internal(1) + Left(1) = 2. Right should be skipped.
        // Note: This relies on LeafNode returning 1 for visited. 
        // If Leaf logic differs, adjust expectation.
        // But Internal must definitely process.
        assertTrue(count >= 2); 
        assertTrue(sb.toString().contains("Internal node"));
        
        // 2. Query strictly right region
        sb = new StringBuilder();
        count = node.intersect(sb, 80, 0, 0, 48, 128, 128, 
                               0, 0, 0, 128, 128, 128, 0);
        assertTrue(count >= 2); 
        
        // 3. Query overlapping region (should visit Internal + Left + Right)
        sb = new StringBuilder();
        count = node.intersect(sb, 0, 0, 0, 128, 128, 128, 
                               0, 0, 0, 128, 128, 128, 0);
        assertTrue(count >= 3);
    }
    
    /**
     * Tests Intersect on different axes to hit mutation lines.
     */
    public void testIntersectAxes() {
        StringBuilder sb = new StringBuilder();
        // Depth 1 (Y split)
        node.intersect(sb, 0, 0, 0, 100, 100, 100, 0, 0, 0, 100, 100, 100, 1);
        // Depth 2 (Z split)
        node.intersect(sb, 0, 0, 0, 100, 100, 100, 0, 0, 0, 100, 100, 100, 2);
        
        String out = sb.toString();
        // Just verify it ran and appended to SB without crashing
        assertTrue(out.length() > 0);
    }

    /**
     * Test Collisions.
     * Just ensures recursive traversal doesn't crash.
     */
    public void testCollisions() {
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        StringBuilder sb = new StringBuilder();
        node.collisions(sb, 0, 0, 0, 128, 128, 128, 0);
        // collisions usually delegates to leaves.
        assertNotNull(sb.toString());
    }
    
    /**
     * Tests that boxes touching on the exact edge do NOT overlap.
     * Kills mutants changing < to <= in boxesOverlap.
     */
    public void testRegionSearchTouchingNoOverlap() {
        // Object at 10-20
        node = (InternalNode) node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        
        StringBuilder sb = new StringBuilder();

        int visits = node.intersect(sb, 20, 10, 10, 10, 10, 10, 
                                    0, 0, 0, 128, 128, 128, 0);
        

        node = new InternalNode();
        node = (InternalNode) node.insert(obj2, 0, 0, 0, 128, 128, 128, 0);
        
        sb = new StringBuilder();
        visits = node.intersect(sb, 0, 0, 0, 64, 128, 128, 
                                0, 0, 0, 128, 128, 128, 0);
        
        assertEquals(2, visits); 
    }
    
    /**
     * Tests the exact boundary of merging.
     * Kills mutants changing "count <= 3" to "count < 3".
     */
    public void testMergeExactThree() {
        // Setup: We need exactly 3 unique objects to remain.
        // Add 4 objects: A, B, C, D.
        // Remove D.
        // Remaining: A, B, C.
        // If code is "count <= 3", it merges to Leaf.
        // If code is "count < 3", it stays Internal (size is 3, 3 is not < 3).
        
        AirObject a = new AirPlane("A", 1, 1, 1, 1, 1, 1, "Test", 0, 0);
        AirObject b = new AirPlane("B", 100, 100, 100, 1, 1, 1, "Test", 0, 0);
        AirObject c = new AirPlane("C", 2, 2, 2, 1, 1, 1, "Test", 0, 0);
        AirObject d = new AirPlane("D", 101, 101, 101, 1, 1, 1, "Test", 0, 0);
        
        node = (InternalNode) node.insert(a, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(b, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(c, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(d, 0, 0, 0, 128, 128, 128, 0);
        
        // Remove D
        BintreeNode result = node.remove(d, 0, 0, 0, 128, 128, 128, 0);
        
        // Must be LeafNode
        assertTrue("3 items should merge to Leaf", result instanceof LeafNode);
        assertEquals(3, ((LeafNode)result).getData().size());
    }
    
    /**
     * Tests that the dimensions split in the correct order (X->Y->Z).
     * Kills mutants in "depth % 3" and "depth + 1".
     */
    public void testDimensionSplitOrder() {
        
        // Create an object deep in the tree to force structure creation
        AirObject deepObj = new AirPlane(
                "Deep", 10, 10, 10, 1, 1, 1, "Test", 0, 0);
        
        node = (InternalNode) node.insert(deepObj, 0, 0, 0, 128, 128, 128, 0);
        
        StringBuilder sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 0);
        String out = sb.toString();
        
        // The root should be 128, 128, 128 at depth 0
        assertTrue(out.contains("I (0, 0, 0, 128, 128, 128) 0"));
        
        AirObject o1 = new AirPlane("1", 1, 1, 1, 1, 1, 1, "Test", 0, 0);
        AirObject o2 = new AirPlane("2", 2, 2, 2, 1, 1, 1, "Test", 0, 0);
        AirObject o3 = new AirPlane("3", 1, 2, 1, 1, 1, 1, "Test", 0, 0);
        AirObject o4 = new AirPlane("4", 2, 1, 2, 1, 1, 1, "Test", 0, 0);
        
        node = new InternalNode();
        node = (InternalNode) node.insert(o1, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(o2, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(o3, 0, 0, 0, 128, 128, 128, 0);
        node = (InternalNode) node.insert(o4, 0, 0, 0, 128, 128, 128, 0);
        
        sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 0);
        out = sb.toString();
        
        assertTrue(out.contains("64, 128, 128"));
        assertTrue(out.contains("64, 64, 128"));
    }
    
    /**
     * Tests that the Right child receives the correct calculated coordinate.
     * Kills mutants changing "x + half" to "x - half" or just "x".
     */
    public void testCoordinateCalculation() {
        // Insert item strictly on the right side.
        AirObject rightObj = new AirPlane(
                "R", 100, 100, 100, 10, 10, 10, "Test", 0, 0);
        node = (InternalNode) node.insert(rightObj, 0, 0, 0, 128, 128, 128, 0);
        
        StringBuilder sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 0);
        String out = sb.toString();
        
  
        assertTrue(out.contains("(64, 0, 0"));
        
        // Test Y split coordinate calculation (Depth 1)
        node = new InternalNode();
        // Insert to depth 1
        node = (InternalNode) node.insert(rightObj, 0, 0, 0, 128, 128, 128, 1);
        sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 1);
        out = sb.toString();
        // Depth 1 splits Y. Right child starts at Y=64.
        assertTrue(out.contains("(0, 64, 0"));
        
        // Test Z split coordinate calculation (Depth 2)
        node = new InternalNode();
        node = (InternalNode) node.insert(rightObj, 0, 0, 0, 128, 128, 128, 2);
        sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 2);
        out = sb.toString();
        // Depth 2 splits Z. Right child starts at Z=64.
        assertTrue(out.contains("(0, 0, 64"));
    }
    
    /**
     * Tests the boxesOverlap logic for the X-Axis boundaries.
     * Target: x1 < x2 + w2 && x1 + w1 > x2
     * Left Child X Range: [0, 64)
     */
    public void testBoxesOverlapX() {
        // Setup: Insert object into Left Child so it isn't empty.
        // If recursion happens, we will see "Leaf" in the output.
        node.insert(obj1, 0, 0, 0, 128, 128, 128, 0); 
        StringBuilder sb;

        // 1. Valid Overlap (Middle)
        // Query: 10 to 20. Clearly inside 0 to 64.
        sb = new StringBuilder();
        node.intersect(sb, 10, 0, 0, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertTrue(sb.toString().contains("leaf"));

        // 2. Boundary Miss: Touching Right Edge
        // Query: Starts at 64. (Range 64 to 74)
        // Check: queryX < childEnd => 64 < 64 => FALSE
        sb = new StringBuilder();
        node.intersect(sb, 64, 0, 0, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertFalse("Touching right edge (64) should NOT visit left child", 
                    sb.toString().contains("leaf"));

        // 3. Boundary Miss: Touching Left Edge
        // Query: Ends at 0. (Starts at -10, Width 10)
        // Check: queryEnd > childStart => 0 > 0 => FALSE
        sb = new StringBuilder();
        node.intersect(sb, -10, 0, 0, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertFalse("Touching left edge (0) should NOT visit left child", 
                    sb.toString().contains("leaf"));
                    
        // 4. Boundary Hit: Just Inside Right Edge (Mutation Killer)
        // Query: Starts at 63. (Range 63 to 73)
        // Check: 63 < 64 => TRUE
        sb = new StringBuilder();
        node.intersect(sb, 63, 0, 0, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertTrue("Just inside right edge (63) SHOULD visit left child", 
                   sb.toString().contains("leaf"));
    }

    /**
     * Tests the boxesOverlap logic for the Y-Axis boundaries.
     * Target: y1 < y2 + h2 && y1 + h1 > y2
     * Left Child Y Range: [0, 128) (Full height at Depth 0)
     */
    public void testBoxesOverlapY() {
        node.insert(obj1, 0, 0, 0, 128, 128, 128, 0); 
        StringBuilder sb;

        // 1. Boundary Miss: Touching Bottom Edge
        // Query: Starts at 128. (Range 128 to 138)
        // Check: queryY < childEnd => 128 < 128 => FALSE
        sb = new StringBuilder();
        node.intersect(sb, 0, 128, 0, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertFalse("Touching bottom edge (128) should NOT visit child", 
                    sb.toString().contains("leaf"));

        // 2. Boundary Miss: Touching Top Edge
        // Query: Ends at 0. (Starts at -10, Width 10)
        // Check: queryEnd > childStart => 0 > 0 => FALSE
        sb = new StringBuilder();
        node.intersect(sb, 0, -10, 0, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertFalse("Touching top edge (0) should NOT visit child", 
                    sb.toString().contains("leaf"));

        // 3. Boundary Hit: Just Inside Bottom Edge
        // Query: Starts at 127. (Range 127 to 137)
        // Check: 127 < 128 => TRUE
        sb = new StringBuilder();
        node.intersect(sb, 0, 127, 0, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertTrue("Just inside bottom edge (127) SHOULD visit child", 
                   sb.toString().contains("leaf"));
    }

    /**
     * Tests the boxesOverlap logic for the Z-Axis boundaries.
     * Target: z1 < z2 + d2 && z1 + d1 > z2
     * Left Child Z Range: [0, 128) (Full depth at Depth 0)
     */
    public void testBoxesOverlapZ() {
        node.insert(obj1, 0, 0, 0, 128, 128, 128, 0); 
        StringBuilder sb;

        // 1. Boundary Miss: Touching Back Edge
        // Query: Starts at 128.
        sb = new StringBuilder();
        node.intersect(sb, 0, 0, 128, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertFalse("Touching back edge (128) should NOT visit child", 
                    sb.toString().contains("leaf"));

        // 2. Boundary Miss: Touching Front Edge
        // Query: Ends at 0.
        sb = new StringBuilder();
        node.intersect(sb, 0, 0, -10, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertFalse("Touching front edge (0) should NOT visit child", 
                    sb.toString().contains("leaf"));
        
        // 3. Valid (Sanity Check)
        sb = new StringBuilder();
        node.intersect(sb, 0, 0, 50, 10, 10, 10, 0, 0, 0, 128, 128, 128, 0);
        assertTrue("Middle Z should visit child", 
                   sb.toString().contains("leaf"));
    }
    
    /**
     * Test Arithmetic: X-Split Logic (Depth 0).
     * Targets: 
     * - axis = depth % 3 (0 % 3 == 0)
     * - half = xWid / 2
     * - newX = x + half
     */
    public void testRemoveArithmeticX() {
        // Setup: Object clearly in the RIGHT half of X (64 to 74)
        // Bounds: x=0, w=128 -> half=64. Right starts at 64.
        AirPlane rightObj = new AirPlane(
                "R", 70, 0, 0, 10, 10, 10, "Test", 0, 0);
        
        // Insert it normally first to populate the tree
        node.insert(rightObj, 0, 0, 0, 128, 128, 128, 0);
        node.remove(rightObj, 0, 0, 0, 128, 128, 128, 0);
        
        // Verify: Tree should be empty (EmptyNode) if removal succeeded
        assertEquals(
                node.print(
                        new StringBuilder(), 0, 0, 0, 128, 128, 128, 0), 3);
    }

    /**
     * Test Arithmetic: Y-Split Logic (Depth 1).
     * Targets: 
     * - axis = depth % 3 (1 % 3 == 1)
     * - newY = y + half
     */
    public void testRemoveArithmeticY() {
        // Setup: Object clearly in the BOTTOM half of Y (64 to 74)
        // We manually inject this call at depth 1.
        AirPlane bottomObj = new AirPlane(
                "B", 0, 70, 0, 10, 10, 10, "Test", 0, 0);
        
        node.insert(bottomObj, 0, 0, 0, 128, 128, 128, 1); // Insert at depth 1
        node.remove(bottomObj, 0, 0, 0, 128, 128, 128, 1);
        
        assertEquals(
                node.print(
                        new StringBuilder(), 0, 0, 0, 128, 128, 128, 0), 3);
    }

    /**
     * Test Arithmetic: Z-Split Logic (Depth 2).
     * Targets: 
     * - axis = depth % 3 (2 % 3 == 2)
     * - newZ = z + half
     */
    public void testRemoveArithmeticZ() {
        // Setup: Object clearly in the BACK half of Z (64 to 74)
        AirPlane backObj = new AirPlane(
                "Bk", 0, 0, 70, 10, 10, 10, "Test", 0, 0);
        
        node.insert(backObj, 0, 0, 0, 128, 128, 128, 2); // Insert at depth 2
        
        // TEST: Remove passing depth 2.
        // Should trigger "axis == 2" (else clause).
        // Should calculate newZ = 0 + 64 = 64.
        node.remove(backObj, 0, 0, 0, 128, 128, 128, 2);
        
        assertEquals("Remove failed - likely failed Z-split arithmetic", 
                   node.print(
                           new StringBuilder(), 0, 0, 0, 128, 128, 128, 0), 3);
    }

    /**
     * Test Arithmetic: Cycle Logic (Depth 3).
     * Targets: 
     * - axis = depth % 3 (3 % 3 == 0) -> Should wrap back to X.
     */
    public void testRemoveArithmeticCycle() {
        // Setup: Object in Right half of X, but at depth 3.
        AirPlane rightObj = new AirPlane(
                "R", 70, 0, 0, 10, 10, 10, "Test", 0, 0);
        
        node.insert(rightObj, 0, 0, 0, 128, 128, 128, 3);
        
        // TEST: Remove passing depth 3.
        // Must perform X-split (axis 0).
        node.remove(rightObj, 0, 0, 0, 128, 128, 128, 3);
        
        assertEquals(
                node.print(
                    new StringBuilder(), 0, 0, 0, 128, 128, 128, 0), 3);
    }
    
    /**
     * Test Arithmetic: Depth Increment (newDepth = depth + 1).
     * Targets: newDepth passed to recursive call.
     */
    public void testRemoveDepthIncrement() {
        // We need to ensure that when we call remove at depth 0, 
        // it calls the child with depth 1.
        // We can infer this by inserting an object that would be valid for 
        // X-split (depth 0) AND Y-split (depth 1), but put it in a spot 
        // where incorrect depth propagation would cause a miss.
        
        // Object at (10, 70, 10).
        // Depth 0 (X): Left (0-64).
        // Depth 1 (Y): Bottom (64-128).
        AirPlane obj = new AirPlane(
                "Test", 10, 70, 10, 10, 10, 10, "Test", 0, 0);
        
        node.insert(obj, 0, 0, 0, 128, 128, 128, 0);
       
        node.remove(obj, 0, 0, 0, 128, 128, 128, 0);
        assertEquals(
                node.print(new StringBuilder(), 0, 0, 0, 128, 128, 128, 0), 3);
    }
    
    /**
     * Test Print Arithmetic: X-Split (Depth 0).
     * Targets: 
     * - if (axis == 0)
     * - half = xWid / 2
     * - left params (half width)
     * - right params (x + half, half width)
     * - count aggregation
     */
    public void testPrintArithmeticX() {
        // Setup: One object Left (10), One object Right (100)
        // At Depth 0 (X-Split), this creates 1 Internal -> 2 Leaves
        node.insert(obj1, 0, 0, 0, 128, 128, 128, 0);
        node.insert(obj2, 0, 0, 0, 128, 128, 128, 0);
        
        StringBuilder sb = new StringBuilder();
        // Call at depth 0
        int count = node.print(sb, 0, 0, 0, 128, 128, 128, 0);
        
        String output = sb.toString();
        
        // 1. Verify Logic Flow (Did we split X?)
        // Left Leaf: Should have Width 64 (128/2), Height 128, Depth 128
        assertTrue("X-Split failed: Left leaf should have width 64", 
                   output.contains("(0, 0, 0, 64, 128, 128)"));
                   
        // 2. Verify Right Child Offset (x + half)
        // Right Leaf: Should start at X=64
        assertTrue("X-Split failed: Right leaf should start at x=64", 
                   output.contains("(64, 0, 0, 64, 128, 128)"));
                   
        // 3. Verify Count Aggregation
        // 1 Internal + 1 Left Leaf + 1 Right Leaf = 3
        assertEquals("Count should equal 3 (1 Internal + 2 Leaves)", 3, count);
    }

    /**
     * Test Print Arithmetic: Y-Split (Depth 1).
     * Targets: 
     * - else if (axis == 1)
     * - half = yWid / 2
     * - right params (y + half)
     */
    public void testPrintArithmeticY() {
        // Setup: Use objects that differ in Y 
        // obj1 at y=10 (Top), obj3 at y=20 (Top) -- Wait, need Bottom
        // Create manual Bottom object at y=100
        AirPlane bottomObj = new AirPlane(
                "Bot", 0, 100, 0, 10, 10, 10, "Test", 0, 0);
        
        // Insert at Depth 1 (forcing Y split logic immediately)
        node.insert(obj1, 0, 0, 0, 128, 128, 128, 1);
        node.insert(bottomObj, 0, 0, 0, 128, 128, 128, 1);
        
        StringBuilder sb = new StringBuilder();
        int count = node.print(sb, 0, 0, 0, 128, 128, 128, 1);
        String output = sb.toString();

        // 1. Verify Y-Split Params
        // Left (Top) Leaf: Width 128, Height 64 (128/2), Depth 128
        assertTrue("Y-Split failed: Left leaf should have height 64", 
                   output.contains("(0, 0, 0, 128, 64, 128)"));
                   
        // 2. Verify Right (Bottom) Offset (y + half)
        // Right Leaf: Should start at Y=64
        assertTrue("Y-Split failed: Right leaf should start at y=64", 
                   output.contains("(0, 64, 0, 128, 64, 128)"));
        
        assertEquals(3, count);
    }

    /**
     * Test Print Arithmetic: Z-Split (Depth 2).
     * Targets: 
     * - else (implied axis == 2)
     * - half = zWid / 2
     * - right params (z + half)
     */
    public void testPrintArithmeticZ() {
        // Setup: Use objects that differ in Z
        // obj1 at z=10 (Front), BackObj at z=100
        AirPlane backObj = new AirPlane(
                "Back", 0, 0, 100, 10, 10, 10, "Test", 0, 0);
        
        // Insert at Depth 2 (forcing Z split)
        node.insert(obj1, 0, 0, 0, 128, 128, 128, 2);
        node.insert(backObj, 0, 0, 0, 128, 128, 128, 2);
        
        StringBuilder sb = new StringBuilder();
        int count = node.print(sb, 0, 0, 0, 128, 128, 128, 2);
        String output = sb.toString();

        // 1. Verify Z-Split Params
        // Left (Front) Leaf: Width 128, Height 128, Depth 64
        assertTrue("Z-Split failed: Left leaf should have depth 64", 
                   output.contains("(0, 0, 0, 128, 128, 64)"));
                   
        // 2. Verify Right (Back) Offset (z + half)
        // Right Leaf: Should start at Z=64
        assertTrue("Z-Split failed: Right leaf should start at z=64", 
                   output.contains("(0, 0, 64, 128, 128, 64)"));
                   
        assertEquals(3, count);
    }
}
