
/**
 * This class implements the IGameData interface to manage game data.
 * 
 * @author Jialin Li
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData implements IGameData {
    /**
     * Gets all connections in a specific location by name.
     *
     * @param locationName The name of the location.
     * @return List of Connection instances (may be empty)
     */
    @Override
    public List<Connection> getConnectionsInLocation(String locationName) {
        Location loc = getLocationByName(locationName);
        if (loc == null)
            return new ArrayList<>();
        return loc.getConnections();
    }

    @Override
    public Character getCharacterByName(String name) {
        if (name == null)
            return null;
        for (Character c : characters) {
            if (c.getName() != null && c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    private String startingMessage;
    private String startingLocation;
    private String endingMessage;
    private String endingLocation;
    private String currentLocation;
    private int turnLimit;
    private Inventory inventory = new Inventory();
    private List<Location> locations = new ArrayList<>();
    private GameObjectCollection objects = new GameObjectCollection(null);
    private List<Character> characters = new ArrayList<>();
    private List<UseRule> useRules = new ArrayList<>();
    private List<GiveRule> giveRules = new ArrayList<>();
    private Map<String, String> icons = new HashMap<>();
    private CommandMessages commandMessages = new CommandMessages();

    /**
     * Constructs a new GameData instance with all game configuration and state.
     * 
     * @param startingMessage  the message displayed when the game starts
     * @param startingLocation the name of the initial location
     * @param endingMessage    the message displayed when the game ends
     * @param endingLocation   the name of the location that triggers game end
     * @param currentLocation  the name of the current location
     * @param turnLimit        the maximum number of turns allowed (-1 for
     *                         unlimited)
     * @param locations        the list of all locations in the game
     * @param objects          the collection of all game objects
     * @param characters       the list of all characters in the game
     * @param useRules         the list of all use rules for object interactions
     * @param giveRules        the list of all give rules for character interactions
     * @param icons            the map of icon names to their file paths
     */
    public GameData(String startingMessage, String startingLocation, String endingMessage,
            String endingLocation, String currentLocation, int turnLimit, List<Location> locations,
            GameObjectCollection objects,
            List<Character> characters, List<UseRule> useRules, List<GiveRule> giveRules,
            Map<String, String> icons) {
        this.startingMessage = startingMessage;
        this.startingLocation = startingLocation;
        this.currentLocation = currentLocation;
        this.endingMessage = endingMessage;
        this.endingLocation = endingLocation;
        this.turnLimit = turnLimit;
        this.locations = locations;
        this.objects = objects;
        this.characters = characters;
        this.useRules = useRules;
        this.giveRules = giveRules;
        this.icons = icons;

    }

    /**
     * Gets the starting message displayed when the game begins.
     * 
     * @return the starting message
     */
    public String getStartingMessage() {
        return this.startingMessage;

    }

    /**
     * Gets the name of the starting location.
     * 
     * @return the starting location name
     */
    public String getStartingLocation() {
        return this.startingLocation;
    }

    /**
     * Gets the ending message displayed when the game ends.
     * 
     * @return the ending message
     */
    public String getEndingMessage() {
        return this.endingMessage;
    }

    /**
     * Gets the name of the ending location.
     * 
     * @return the ending location name
     */
    public String getEndingLocation() {
        return this.endingLocation;
    }

    /**
     * Gets the name of the current location.
     * 
     * @return the current location name
     */
    public String getCurrentLocation() {
        return this.currentLocation;
    }

    /**
     * Gets the maximum number of turns allowed in the game.
     * 
     * @return the turn limit (-1 for unlimited)
     */
    public int getTurnLimit() {
        return this.turnLimit;
    }

    /**
     * Gets the list of all locations in the game.
     * 
     * @return the list of locations
     */
    public List<Location> getLocations() {
        return this.locations;
    }

    /**
     * Gets the collection of all game objects.
     * 
     * @return the game object collection
     */
    public GameObjectCollection getObjects() {
        return this.objects;
    }

    /**
     * Gets the list of all characters in the game.
     * 
     * @return the list of characters
     */
    public List<Character> getCharacters() {
        return this.characters;
    }

    /**
     * Gets the list of all use rules for object interactions.
     * 
     * @return the list of use rules
     */
    public List<UseRule> getUseRules() {
        return this.useRules;
    }

    /**
     * Gets the list of all give rules for character interactions.
     * 
     * @return the list of give rules
     */
    public List<GiveRule> getGiveRules() {
        return this.giveRules;
    }

    /**
     * Gets the player's inventory.
     * 
     * @return the inventory
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets the command message for the specified command and success status.
     * 
     * @param command   the command name
     * @param isSuccess whether the command was successful
     * @return the command message
     */
    public String getCommandMessage(String command, boolean isSuccess) {
        return commandMessages.get(command, isSuccess);
    }

    /**
     * Finds a location by its name (case-insensitive).
     * 
     * @param name the name of the location to find
     * @return the Location object, or null if not found
     */
    public Location getLocationByName(String name) {
        for (Location loc : locations) {
            if (loc.getName().equalsIgnoreCase(name)) {
                return loc;
            }
        }
        return null;
    }

    /**
     * Finds a game object by its name (case-insensitive).
     * 
     * @param name the name of the object to find
     * @return the GameObject, or null if not found
     */
    public GameObject getObjectByName(String name) {
        for (GameObject obj : objects.getAllObjects()) {
            if (obj.getName().equalsIgnoreCase(name)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Finds a character by its name (case-insensitive).
     * 
     * 
     * @Override
     *           public List<Connection> getConnectionsInLocation(String
     *           locationName) {
     *           Location loc = getLocationByName(locationName);
     *           if (loc == null) return new ArrayList<>();
     *           return loc.getConnections();
     *           }
     *           }
     *           }
     *           return null;
     *           }
     * 
     *           /**
     *           Gets the connection in the specified location.
     * 
     * @param locationName the name of the location
     * @return the Connection in that location, or null if location not found
     */
    public Connection getConnectionInLocation(String locationName) {
        Location loc = getLocationByName(locationName);
        if (loc != null) {
            // return loc.getConnection(); // Removed: now use getConnections()
            // If you need a single connection, use loc.getConnections().isEmpty() ? null :
            // loc.getConnections().get(0);
            return null; // Placeholder, as single-connection logic is deprecated
        }
        return null;
    }

    /**
     * Gets the map of icon names to their file paths.
     * 
     * @return the icons map
     */
    public Map<String, String> getIcons() {
        return this.icons;
    }

    /* Object management */

    /**
     * Adds a game object to the data store.
     * 
     * @param object The game object to be added.
     * @return true if the object was successfully added; false otherwise.
     */
    public boolean addObject(GameObject object) {
        return objects.addObject(object);
    }

    /**
     * Removes a game object from the data store.
     * 
     * @param object The game object to be removed.
     * @return true if the object was successfully removed; false otherwise.
     */
    public boolean removeObject(String objectName) {
        return objects.removeObject(objectName);
    }

    /**
     * Adds a game object to a specific location.
     * 
     * @param locationName The name of the location.
     * @param object       The game object to be added.
     * @return true if the object was successfully added; false otherwise.
     */
    public boolean addObjectToLocation(String locationName, GameObject object) {
        Location loc = getLocationByName(locationName);
        if (loc != null) {
            return loc.addObject(object);
        }
        return false;
    }

    /**
     * Removes a game object from a specific location.
     * 
     * @param locationName The name of the location.
     * @param object       The game object to be removed.
     * @return true if the object was successfully removed; false otherwise.
     */
    public boolean removeObjectFromLocation(String locationName, GameObject object) {
        Location loc = getLocationByName(locationName);
        if (loc != null) {
            return loc.removeObject(object.getName());
        }
        return false;
    }

    /**
     * Adds a game object to a specific character's inventory.
     * 
     * @param object
     * @return true if the object was successfully added; false otherwise.
     */
    public boolean addObjectToInventory(GameObject object) {
        return inventory.addObject(object);
    }

    /**
     * Removes a game object from a specific character's inventory.
     * 
     * @param object The game object to be removed.
     * @return true if the object was successfully removed; false otherwise.
     */
    public boolean removeObjectFromInventory(String objectName) {
        return inventory.removeObject(objectName);
    }

    /**
     * Consumes a turn in the game, decrementing the turn counter.
     * 
     * @return true if a turn was successfully consumed; false if no turns remain.
     */
    public boolean consumeTurn() {
        if (turnLimit > 0) {
            turnLimit--;
            return true;
        }
        return false;
    }

    /**
     * Checks if the game is out of turns.
     * 
     * @return true if no turns remain; false otherwise.
     */
    public boolean isOutOfTurns() {
        return turnLimit <= 0;
    }

    /**
     * Advances the character's dialogue and returns the next phrase to be spoken.
     * 
     * @return the next phrase; returns a default message if no phrases remain.
     */
    public String consumePhrase(Character cha) {
        return cha.consumePhrase();
    }

    /**
     * Sets the current location of the player.
     * 
     * @param locationName The name of the new current location.
     */
    public void setCurrentLocation(String locationName) {
        this.currentLocation = locationName;
    }

}
