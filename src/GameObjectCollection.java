import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * A collection of game objects indexed by their names.
 * Provides methods to add, remove, retrieve, and query game objects.
 * 
 * @author Peiyong Wang
 */
public class GameObjectCollection implements IGameObjectCollection {

    private final Map<String, GameObject> objectsByName;

    /**
     * Constructs a new GameObjectCollection.
     * 
     * @param objectsByName a map of object names to GameObjects (can be null)
     */
    public GameObjectCollection(Map<String, GameObject> objectsByName) {
        // use provided map or create new one
        this.objectsByName = (objectsByName == null)
                ? new HashMap<>()
                : objectsByName;
    }

    /**
     * Adds a game object to the collection.
     * 
     * @param object the GameObject to add
     * @return true if the object was added successfully, false if it was null, had
     *         a null name, or already exists
     */
    @Override
    public boolean addObject(GameObject object) {
        if (object == null || object.getName() == null) {
            return false;
        }
        String name = object.getName();
        if (objectsByName.containsKey(name)) {
            return false; // already present
        }
        objectsByName.put(name, object);
        return true;
    }

    /**
     * Removes a game object from the collection by name.
     * 
     * @param objectName the name of the object to remove
     * @return true if the object was removed, false if objectName was null or not
     *         found
     */
    @Override
    public boolean removeObject(String objectName) {
        if (objectName == null) {
            return false;
        }
        return objectsByName.remove(objectName) != null;
    }

    /**
     * Retrieves a game object by its name.
     * 
     * @param objectName the name of the object to retrieve
     * @return the GameObject with the specified name, or null if not found or
     *         objectName is null
     */
    @Override
    public GameObject getObjectByName(String objectName) {
        if (objectName == null) {
            return null;
        }
        return objectsByName.get(objectName);
    }

    /**
     * Checks whether the collection contains an object with the specified name.
     * 
     * @param objectName the name of the object to check for
     * @return true if the collection contains the object, false otherwise
     */
    @Override
    public boolean containsObject(String objectName) {
        if (objectName == null) {
            return false;
        }
        return objectsByName.containsKey(objectName);
    }

    /**
     * Returns an unmodifiable list of all game objects in the collection.
     * 
     * @return an unmodifiable list of all GameObjects
     */
    @Override
    public List<GameObject> getAllObjects() {
        return Collections.unmodifiableList(new ArrayList<>(objectsByName.values()));
    }

    /**
     * Returns the number of objects in the collection.
     * 
     * @return the size of the collection
     */
    @Override
    public int size() {
        return objectsByName.size();
    }

    /**
     * Checks whether the collection is empty.
     * 
     * @return true if the collection contains no objects, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return objectsByName.isEmpty();
    }
}
