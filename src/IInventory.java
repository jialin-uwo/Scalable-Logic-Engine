
/**
 * Defines the contract for a container, typically the Player's inventory, used
 * to hold collectable Game Objects (items).
 * The Inventory must enforce a maximum weight or item count capacity.
 * 
 * @author Jialin Li
 */
import java.util.List;

public interface IInventory {

    /**
     * Gets the maximum number of items (Game Objects) that the inventory can hold.
     * 
     * @return The maximum capacity limit.
     */
    int getCapacity();

    /**
     * Checks if the inventory is full based on its capacity.
     * This is useful before calling addObject().
     * 
     * @return true if the inventory is at its maximum capacity; returns false
     *         otherwise.
     */
    boolean isFull();

    /**
     * Adds a game object to this inventory. Delegates to the internal collection.
     * The implementation must check against the capacity before adding.
     * 
     * @param object The game object to be added.
     * @return true if the object was successfully added (and capacity allows);
     *         returns false otherwise.
     */
    boolean addObject(GameObject object);

    /**
     * Removes a game object with the specified name from this inventory. Delegates
     * to the internal collection.
     * 
     * @param objectName The Name of the object to be removed.
     * @return true if the object was successfully removed; false otherwise.
     */
    boolean removeObject(String objectName);

    /**
     * Finds and returns a game object in this inventory by its name. Delegates to
     * the internal collection.
     * 
     * @param objectName The Name of the object to search for.
     * @return The found object instance, or null if it does not exist.
     */
    GameObject getObjectByName(String objectName);

    /**
     * Retrieves a list of all game objects currently in this inventory. Delegates
     * to the internal collection.
     * 
     * @return An unmodifiable List of all GameObject instances in the inventory.
     */
    List<GameObject> getAllObjects();

    /**
     * Gets the current number of objects in the inventory. Delegates to the
     * internal collection.
     * 
     * @return The total count of objects.
     */
    int size();

    /**
     * Checks if the inventory contains an object with the specified name.
     * 
     * @param objectName The name of the object to check
     * @return true if the object exists in the inventory, false otherwise
     */
    boolean containsObject(String objectName);

    /**
     * Checks if the inventory is empty.
     * 
     * @return true if the inventory has no objects, false otherwise
     */
    boolean isEmpty();
}