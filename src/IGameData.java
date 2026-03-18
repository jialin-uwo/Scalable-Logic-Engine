import java.util.List;
import java.util.Map;

/**
 * Defines the structure and access methods for all game data, including
 * locations, characters, objects, and rules.
 * This interface provides both read and write access for game setup and runtime
 * manipulation.
 * Defines the structure and access methods for all game data, including
 * locations, characters, objects, and rules.
 * This interface provides both read and write access for game setup and runtime
 * manipulation.
 * 
 * @author Junqi Zheng
 */

public interface IGameData {

    /** Getter methods for all main fields. */
    String getStartingMessage();

    String getStartingLocation();

    String getEndingMessage();

    String getEndingLocation();

    String getCurrentLocation();

    int getTurnLimit();

    Inventory getInventory();

    List<Location> getLocations();

    GameObjectCollection getObjects();

    List<Character> getCharacters();

    List<UseRule> getUseRules();

    List<GiveRule> getGiveRules();

    Map<String, String> getIcons();

    public String getCommandMessage(String command, boolean isSuccess);

    public Location getLocationByName(String name);

    public GameObject getObjectByName(String name);

    public Character getCharacterByName(String name);

    /**
     * Gets all connections in a specific location by name.
     *
     * @param locationName The name of the location.
     * @return List of Connection instances (may be empty)
     */
    public List<Connection> getConnectionsInLocation(String locationName);

    /* Object management */
    /**
     * Adds a game object to the data store.
     * 
     * @param object The game object to be added.
     * @return true if the object was successfully added; false otherwise.
     */
    public boolean addObject(GameObject object);

    /**
     * Removes a game object from the data store.
     * 
     * @param object The game object to be removed.
     * @return true if the object was successfully removed; false otherwise.
     */
    public boolean removeObject(String objectName);

    /**
     * Adds a game object to a specific location.
     * 
     * @param locationName The name of the location.
     * @param object       The game object to be added.
     * @return true if the object was successfully added; false otherwise.
     */
    public boolean addObjectToLocation(String locationName, GameObject object);

    /**
     * Removes a game object from a specific location.
     * 
     * @param locationName The name of the location.
     * @param object       The game object to be removed.
     * @return true if the object was successfully removed; false otherwise.
     */
    public boolean removeObjectFromLocation(String locationName, GameObject object);

    /**
     * Adds a game object to a specific character's inventory.
     * 
     * @param object
     * @return true if the object was successfully added; false otherwise.
     */
    public boolean addObjectToInventory(GameObject object);

    /**
     * Removes a game object from a specific character's inventory.
     * 
     * @param object The game object to be removed.
     * @return true if the object was successfully removed; false otherwise.
     */
    public boolean removeObjectFromInventory(String objectName);

    /**
     * Consumes a turn in the game, decrementing the turn counter.
     * 
     * @return true if a turn was successfully consumed; false if no turns remain.
     */
    public boolean consumeTurn();

    /**
     * Checks if the game is out of turns.
     * 
     * @return true if no turns remain; false otherwise.
     */
    public boolean isOutOfTurns();

    /**
     * Advances the character's dialogue and returns the next phrase to be spoken.
     * 
     * @return the next phrase; returns a default message if no phrases remain.
     */
    public String consumePhrase(Character cha);

    /**
     * Sets the current location of the player.
     * 
     * @param locationName The name of the new current location.
     */
    public void setCurrentLocation(String locationName);

}