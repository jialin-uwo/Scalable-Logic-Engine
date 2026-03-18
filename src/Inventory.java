
/**
 * Represents the player's inventory in the adventure game.
 *
 * @author Junqi Zheng
 */
import java.util.List;

public class Inventory implements IInventory {
    private int capacity;
    private GameObjectCollection objects;
    private String image;

    /**
     * Constructs a new Inventory with unlimited capacity.
     */
    public Inventory() {
        this.capacity = -1; // -1 means unlimited capacity
        this.objects = new GameObjectCollection(null);
        this.image = "";
    }

    /**
     * Constructs a new Inventory with the specified capacity.
     * 
     * @param capacity the maximum number of items the inventory can hold
     */
    public Inventory(int capacity) {
        this.capacity = capacity;
        this.objects = new GameObjectCollection(null);
        this.image = "";
    }

    /**
     * Gets the current capacity of the inventory.
     *
     * @return The capacity (-1 for unlimited)
     */
    @Override
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the image path for the inventory icon.
     *
     * @return The image file path
     */
    public String getImage() {
        return image;
    }

    /**
     * Adds an object to the inventory.
     *
     * @param obj The GameObject to add
     * @return true if the object was added successfully, false if inventory is full
     */
    @Override
    public boolean addObject(GameObject obj) {
        if (obj == null) {
            return false;
        }

        // Check duplicates
        if (objects.containsObject(obj.getName())) {
            return false;
        }

        // Capacity check
        if (capacity != -1 && objects.size() >= capacity) {
            return false;
        }

        objects.addObject(obj);
        return true;
    }

    /**
     * Removes an object from the inventory by its name.
     *
     * @param objectName The name of the object to be removed
     * @return true if the object was successfully removed; false otherwise
     */
    @Override
    public boolean removeObject(String objectName) {
        return objects.removeObject(objectName);
    }

    /**
     * Gets an object from the inventory by name.
     *
     * @param objectName The name of the object to retrieve
     * @return The GameObject, or null if not found
     */
    @Override
    public GameObject getObjectByName(String objectName) {
        return objects.getObjectByName(objectName);
    }

    /**
     * Gets all objects in the inventory.
     *
     * @return List of all GameObjects in the inventory
     */
    @Override
    public List<GameObject> getAllObjects() {
        return objects.getAllObjects();
    }

    /**
     * Checks if the inventory contains an object with the specified name.
     *
     * @param objectName The name of the object to check
     * @return true if the object exists in the inventory, false otherwise
     */
    @Override
    public boolean containsObject(String objectName) {
        return objects.containsObject(objectName);
    }

    /**
     * Checks if the inventory is empty.
     *
     * @return true if the inventory has no objects, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    /**
     * Checks if the inventory is full.
     *
     * @return true if the inventory is at capacity, false otherwise
     *         (always returns false if capacity is unlimited)
     */
    @Override
    public boolean isFull() {
        if (capacity == -1) {
            return false; // Unlimited capacity
        }
        return objects.size() >= capacity;
    }

    /**
     * Gets the current number of objects in the inventory.
     *
     * @return The number of objects
     */
    @Override
    public int size() {
        return objects.size();
    }
}
