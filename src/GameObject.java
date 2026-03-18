import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a game object in the adventure game world.
 * Game objects can have attributes, contain other objects, and may be pickable
 * by the player.
 * This class extends Entity to inherit basic properties like name, description,
 * and image path.
 * 
 * @author Peiyong Wang
 */
public class GameObject extends Entity implements IGameObject {

    private final List<String> attributes;
    private final List<GameObject> containedItems;
    private final boolean pickable;

    /**
     * Constructs a new GameObject with the specified properties.
     * 
     * @param name           the name of the game object
     * @param description    the description of the game object
     * @param imagePath      the path to the image representing this object
     * @param attributes     the list of attributes for this object (can be null)
     * @param containedItems the list of game objects contained within this object
     *                       (can be null)
     * @param pickable       whether this object can be picked up by the player
     */
    public GameObject(String name,
            String description,
            String imagePath,
            List<String> attributes,
            List<GameObject> containedItems,
            boolean pickable) {
        super(name, description, imagePath);
        // avoid null lists
        this.attributes = attributes == null
                ? new ArrayList<>()
                : new ArrayList<>(attributes);
        this.containedItems = containedItems == null
                ? new ArrayList<>()
                : new ArrayList<>(containedItems);
        this.pickable = pickable;
    }

    /**
     * Returns an unmodifiable list of all attributes associated with this game
     * object.
     * 
     * @return an unmodifiable list of attribute strings
     */
    @Override
    public List<String> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    /**
     * Checks whether this game object has a specific attribute.
     * The comparison is case-insensitive.
     * 
     * @param attribute the attribute to check for
     * @return true if the object has the specified attribute, false otherwise
     */
    @Override
    public boolean hasAttribute(String attribute) {
        if (attribute == null) {
            return false;
        }
        for (String a : attributes) {
            if (attribute.equalsIgnoreCase(a)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether this game object can be picked up by the player.
     * 
     * @return true if this object is pickable, false otherwise
     */
    @Override
    public boolean pickable() {
        return this.pickable;
    }

    /**
     * Returns an unmodifiable list of all game objects contained within this
     * object.
     * 
     * @return an unmodifiable list of contained GameObjects
     */
    @Override
    public List<GameObject> getContainedItems() {
        return Collections.unmodifiableList(containedItems);
    }

    /**
     * Removes a specified item from this object's contained items.
     * 
     * @param item the GameObject to remove
     * @return true if the item was successfully removed, false if the item was null
     *         or not found
     */
    @Override
    public boolean removeContainedItem(GameObject item) {
        if (item == null) {
            return false;
        }
        return containedItems.remove(item);
    }
}
