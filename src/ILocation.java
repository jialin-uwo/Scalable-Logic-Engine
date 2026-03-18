import java.util.List;

/**
 * Defines the contract for a specific area or location within the game world.
 * The Location holds Game Objects, a Character(optional), and a fixed
 * connection(optional) to another Location.
 * It is a core entity of the game map.
 *
 * @author Jialin Li
 */
public interface ILocation {

    /**
     * Gets the unique name of the location.
     * 
     * @return The name of the location.
     */
    String getName();

    /**
     * Gets the detailed description of the location.
     * 
     * @return The detailed description of the location.
     */
    String getDescription();

    /**
     * Gets the file path or identifier for the location's picture.
     * This path is used by the UI layer (ImageLoader) to display the background
     * image.
     * 
     * @return The string path to the image file.
     */
    String getPicturePath();

    /**
     * Gets the message displayed when entering this location.
     * 
     * @return The location's entry message.
     */
    String getMessage();

    /**
     * Gets all connections (exits) available from this location.
     *
     * @return List of Connection instances (may be empty)
     */
    List<Connection> getConnections();

    /**
     * Gets all characters present in this location.
     *
     * @return List of Character (may be empty)
     */
    List<Character> getCharacters();

    /**
     * Adds a game object to this location. Delegates to the internal collection.
     * 
     * @param object The game object to be added.
     * @return true if the object was successfully added; returns false otherwise.
     */
    boolean addObject(GameObject object);

    /**
     * Removes a game object with the specified name from this location. Delegates
     * to the internal collection.
     * 
     * @param objectName The Name of the object to be removed.
     * @return The removed object instance, or null if not found.
     */
    boolean removeObject(String objectName);

    /**
     * Finds and returns a game object in this location by its name. Delegates to
     * the internal collection.
     * 
     * @param objectName The Name of the object to search for.
     * @return The found object instance, or null if it does not exist.
     */
    GameObject getObjectByName(String objectName);

    /**
     * Retrieves a list of all game objects currently in this location. Delegates to
     * the internal collection.
     * 
     * @return An unmodifiable List of all GameObject instances in the location.
     */
    GameObjectCollection getAllObjects();
}