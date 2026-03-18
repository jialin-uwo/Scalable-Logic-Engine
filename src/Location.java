
/**
 * Represents a location in the adventure game world.
 *
 * @author Junqi Zheng
 */
import java.util.ArrayList;
import java.util.List;

public class Location implements ILocation {
    private String name;
    private String description;
    private String imagePath;
    private String message;
    private GameObjectCollection objects;
    private List<Connection> connections;
    private List<Character> characters;

    /**
     * Constructs a new Location with the specified properties.
     * 
     * @param name        the name of the location (must not be null)
     * @param description the description of the location (must not be null)
     * @param imagePath   the path to the location's image (must not be null)
     * @param message     the message displayed when entering the location (must not
     *                    be null)
     * @param connection  the connection to another location (can be null)
     * @param character   the character present in this location (can be null)
     * @throws IllegalArgumentException if name, description, imagePath, or message
     *                                  is null
     */
    public Location(String name, String description, String imagePath, String message, List<Connection> connections,
            List<Character> characters) {
        if (name == null || description == null || imagePath == null || message == null) {
            throw new IllegalArgumentException("Some parameters cannot be null");
        }
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.message = message;
        this.objects = new GameObjectCollection(null);
        this.connections = (connections != null) ? connections : new ArrayList<>();
        this.characters = (characters != null) ? characters : new ArrayList<>();
    }

    /**
     * Gets the unique name of the location.
     *
     * @return The name of the location
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the detailed description of the location.
     *
     * @return The description of the location
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the file path for the location's picture.
     *
     * @return The string path to the image file
     */
    @Override
    public String getPicturePath() {
        return imagePath;
    }

    /**
     * Gets the message displayed when entering this location.
     *
     * @return The location's entry message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the single connection (exit) available from this location.
     * This method assumes there is a maximum of one connection defined for the
     * location.
     *
     * @return The Connection instance, or null if no connection is present
     */
    @Override

    /**
     * Gets all connections (exits) available from this location.
     *
     * @return List of Connection instances (may be empty)
     */
    public List<Connection> getConnections() {
        return connections;
    }

    /**
     * Adds a new connection to this location.
     * 
     * @param connection the Connection to add
     */
    public void addConnection(Connection connection) {
        if (connection != null) {
            connections.add(connection);
        }
    }

    /**
     * Removes a connection by name.
     * 
     * @param name the name of the connection to remove
     * @return true if removed
     */
    public boolean removeConnectionByName(String name) {
        return connections.removeIf(c -> c.getName().equalsIgnoreCase(name));
    }

    /**
     * Finds a connection by name (case-insensitive).
     * 
     * @param name the name to search
     * @return the Connection if found, else null
     */
    public Connection getConnectionByName(String name) {
        if (name == null)
            return null;
        for (Connection c : connections) {
            if (c.getName() != null && c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Retrieves the single character residing in this location.
     * Maximum one character per location.
     *
     * @return The Character instance, or null if no character is present
     */
    @Override

    /**
     * Gets all characters present in this location.
     * 
     * @return List of Character (may be empty)
     */
    public List<Character> getCharacters() {
        return characters;
    }

    /**
     * Adds a character to this location.
     * 
     * @param character the Character to add
     */
    public void addCharacter(Character character) {
        if (character != null) {
            characters.add(character);
        }
    }

    /**
     * Removes a character by name.
     * 
     * @param name the name of the character to remove
     * @return true if removed
     */
    public boolean removeCharacterByName(String name) {
        return characters.removeIf(c -> c.getName().equalsIgnoreCase(name));
    }

    /**
     * Finds a character by name (case-insensitive).
     * 
     * @param name the name to search
     * @return the Character if found, else null
     */
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

    /**
     * Adds a game object to this location.
     *
     * @param object The game object to be added
     * @return true if the object was successfully added; returns false otherwise
     */
    @Override
    public boolean addObject(GameObject object) {
        if (object == null) {
            return false;
        }
        return objects.addObject(object);
    }

    /**
     * Removes a game object with the specified name from this location.
     *
     * @param objectName The name of the object to be removed
     * @return true if the object was successfully removed; false otherwise
     */
    @Override
    public boolean removeObject(String objectName) {

        if (objectName == null) {
            return false;
        }
        return objects.removeObject(objectName);
    }

    /**
     * Finds and returns a game object in this location by its name.
     *
     * @param objectName The name of the object to search for
     * @return The found object instance, or null if it does not exist
     */
    @Override
    public GameObject getObjectByName(String objectName) {
        if (objectName == null) {
            return null;
        }
        return objects.getObjectByName(objectName);
    }

    /**
     * Retrieves the collection of all game objects currently in this location.
     *
     * @return The GameObjectCollection containing all objects in the location
     */
    @Override
    public GameObjectCollection getAllObjects() {
        return objects;
    }
}
