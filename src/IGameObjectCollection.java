import java.util.List;

/**
 * IGameObjectCollection.java
 *
 * Defines the contract for a dedicated container responsible for managing a
 * collection of {@link GameObject} instances. This interface specifies
 * methods for adding, removing, and finding objects by their unique name,
 * as well as querying collection size and emptiness.
 *
 * Typical implementations may optimize for fast lookup by name and may
 * enforce uniqueness of object names within the collection.
 *
 * @author Jialin Li
 */
public interface IGameObjectCollection {

    /**
     * Adds a game object to the collection.
     *
     * @param object the game object to be added
     * @return {@code true} if the object was successfully added (e.g., if the
     *         collection accepts it and has capacity); {@code false} otherwise
     */
    boolean addObject(GameObject object);

    /**
     * Removes a game object from the collection by its name.
     *
     * @param objectName the name of the object to be removed
     * @return {@code true} if the object was successfully removed;
     *         {@code false} otherwise
     */
    boolean removeObject(String objectName);

    /**
     * Finds and returns a game object in the collection by its name.
     * <p>
     * This operation typically does not remove the object from the collection.
     *
     * @param objectName the name of the object to search for
     * @return the found {@link GameObject} instance, or {@code null} if it does not exist
     */
    GameObject getObjectByName(String objectName);

    /**
     * Checks if the collection contains a game object with a specific name.
     *
     * @param objectName the name of the object to check for
     * @return {@code true} if the collection contains an object with the given name;
     *         {@code false} otherwise
     */
    boolean containsObject(String objectName);

    /**
     * Retrieves a list of all game objects currently held in the collection.
     * <p>
     * The returned list should ideally be an unmodifiable view to prevent
     * external modification of the collection's internal state.
     *
     * @return a list of all {@link GameObject} instances in the collection;
     *         the list may be empty but should never be {@code null}
     */
    List<GameObject> getAllObjects();

    /**
     * Gets the current number of objects in the collection.
     *
     * @return the total count of objects
     */
    int size();

    /**
     * Checks if the collection is empty.
     *
     * @return {@code true} if there are no objects in the collection;
     *         {@code false} otherwise
     */
    boolean isEmpty();
}
