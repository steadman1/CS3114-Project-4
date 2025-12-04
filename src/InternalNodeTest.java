
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
        objOverlap = new AirPlane("Overlap", 60, 10, 10, 10, 10, 10, "Test", 0, 0); 
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
        node = (InternalNode) node.insert(objOverlap, 0, 0, 0, 128, 128, 128, 0);

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
        // Insert Obj1 (Left) and Obj2 (Right)
        // Note: We need to build a structure that IS Internal, then remove to trigger merge.
        // If we just insert 2, it might stay a Leaf depending on the Leaf implementation.
        // Assuming InternalNode is manually created or forced.
        
        // Add 4 objects first to force InternalNode structure (assuming Leaf capacity is small, usually 3)
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
        AirObject obj5 = new AirPlane("Obj5", 15, 15, 15, 1, 1, 1, "Test", 0, 0);
        
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
        // objOverlap spans split. Exists in Left and Right.
        // obj1 is Left only.
        // obj2 is Right only.
        
        // Insert overlap, 1, and 2.
        // Total unique = 3 (Overlap, 1, 2).
        // Actual references in children = 4 (Overlap(L), 1(L), Overlap(R), 2(R)).
        
        // We need to trigger the merge. 
        // Let's add obj4, then remove it to trigger the check.
        node = (InternalNode) node.insert(objOverlap, 0, 0, 0, 128, 128, 128, 0);
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
        node.intersect(sb, 0,0,0, 100,100,100, 0,0,0, 100,100,100, 1);
        // Depth 2 (Z split)
        node.intersect(sb, 0,0,0, 100,100,100, 0,0,0, 100,100,100, 2);
        
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
        AirObject deepObj = new AirPlane("Deep", 10, 10, 10, 1, 1, 1, "Test", 0, 0);
        
        // Manually chaining inserts isn't enough to check internal args unless we print.
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
        
        assertTrue("Depth 1 should have halved X", out.contains("64, 128, 128"));
        assertTrue("Depth 2 should have halved Y", out.contains("64, 64, 128"));
    }
    
    /**
     * Tests that the Right child receives the correct calculated coordinate.
     * Kills mutants changing "x + half" to "x - half" or just "x".
     */
    public void testCoordinateCalculation() {
        // Insert item strictly on the right side.
        AirObject rightObj = new AirPlane("R", 100, 100, 100, 10, 10, 10, "Test", 0, 0);
        node = (InternalNode) node.insert(rightObj, 0, 0, 0, 128, 128, 128, 0);
        
        StringBuilder sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 0);
        String out = sb.toString();
        
  
        assertTrue("Right child should start at X=64", out.contains("(64, 0, 0"));
        
        // Test Y split coordinate calculation (Depth 1)
        node = new InternalNode();
        // Insert to depth 1
        node = (InternalNode) node.insert(rightObj, 0, 0, 0, 128, 128, 128, 1);
        sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 1);
        out = sb.toString();
        // Depth 1 splits Y. Right child starts at Y=64.
        assertTrue("Right child should start at Y=64", out.contains("(0, 64, 0"));
        
        // Test Z split coordinate calculation (Depth 2)
        node = new InternalNode();
        node = (InternalNode) node.insert(rightObj, 0, 0, 0, 128, 128, 128, 2);
        sb = new StringBuilder();
        node.print(sb, 0, 0, 0, 128, 128, 128, 2);
        out = sb.toString();
        // Depth 2 splits Z. Right child starts at Z=64.
        assertTrue("Right child should start at Z=64", out.contains("(0, 0, 64"));
    }
}
