/**
 * A simple dynamic list implementation, as ArrayList is not permitted.
 * This list is specifically for storing AirObjects.
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class SimpleList {

    /**
     * The underlying array storing the objects.
     */
    private AirObject[] data;
    
    /**
     * The number of objects currently in the list.
     */
    private int size;
    
    /**
     * The default initial capacity of the list.
     */
    private static final int DEFAULT_CAPACITY = 4;

    /**
     * Creates a new SimpleList with default capacity.
     */
    public SimpleList() {
        this.data = new AirObject[DEFAULT_CAPACITY];
        this.size = 0;
    }


    /**
     * Gets the number of elements in the list.
     * @return The size of the list.
     */
    public int size() {
        return size;
    }


    /**
     * Gets the element at a specific index.
     * @param index The index.
     * @return The AirObject at that index.
     */
    public AirObject get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
                + size);
        }
        return data[index];
    }


    /**
     * Adds an AirObject to the end of the list.
     * @param obj The object to add.
     */
    public void add(AirObject obj) {
        // If the array is full, resize it
        if (size == data.length) {
            resize();
        }
        data[size] = obj;
        size++;
    }
    
    /**
     * Clears all objects from list
     */
    public void clear() {
    	this.data = new AirObject[DEFAULT_CAPACITY];
        this.size = 0;
    }


    /**
     * Removes a specific AirObject from the list.
     *
     * @param obj The object to remove.
     * @return The removed object, or null if not found.
     */
    public AirObject remove(AirObject obj) {
        for (int i = 0; i < size; i++) {
            // Use reference equality, as these are the exact
            // objects passed around.
            if (data[i] == obj) {
                AirObject removed = data[i];
                // Shift elements left
                int numToMove = size - i - 1;
                if (numToMove > 0) {
                    System.arraycopy(data, i + 1, data, i, numToMove);
                }
                size--;
                data[size] = null; // Help garbage collector
                return removed;
            }
        }
        return null; // Not found
    }


    /**
     * Doubles the capacity of the internal array.
     */
    private void resize() {
        int newCapacity = data.length * 2;
        AirObject[] newData = new AirObject[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }
}