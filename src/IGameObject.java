import java.util.List;

/**
 * Defines the contract and capabilities for an interactive object within the
 * game world. This interface extends IEntity, adding functionality related to
 * object attributes and contained items, which are necessary for puzzle
 * solving.
 * 
 * @author Jialin Li
 */
public interface IGameObject extends IEntity {

    /**
     * Retrieves the list of descriptive attributes associated with the object.
     * 
     * @return a list of attributes, such as "sharp" or "heavy"
     */
    List<String> getAttributes();

    /**
     * Checks if this item has the specified attribute.
     * 
     * @param attribute the attribute to check for, e.g. "sharp"
     * @return true if the item has the attribute; false otherwise
     */
    boolean hasAttribute(String attribute);

    /**
     * Determines if the object can be picked up by the player.
     * 
     * @return true if the object is pickable; false otherwise
     */
    boolean pickable();

    /**
     * Provides access to objects contained within this item, if any.
     * 
     * @return a list of contained game items; may be empty if none are present
     */
    List<GameObject> getContainedItems();

    /**
     * Removes a contained item from this object.
     * 
     * @param item the game item to be removed
     * @return true if the item was successfully removed; false otherwise
     */
    boolean removeContainedItem(GameObject item);
}
