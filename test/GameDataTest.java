import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for GameData (Data Layer)
 * Tests game state management using Object Layer classes
 * 
 * @author Jialin Li
 */
public class GameDataTest {
    private GameData gameData;
    private Location hall;
    private Location room;
    private GameObject key;
    private GameObject sword;
    private Character guard;

    @BeforeEach
    public void setUp() {
        // Create Object Layer instances
        key = new GameObject("key", "A rusty key", "key.png",
                Arrays.asList("metal"), null, true);
        sword = new GameObject("sword", "A sharp sword", "sword.png",
                Arrays.asList("weapon"), null, true);

        Connection door = new Connection("door", "Wooden door", "room");
        Connection noConnection = new Connection("none", "No exit", "none");

        Character noCharacter = new Character("none", "Nobody", "",
                new ArrayList<>(), null, "Nothing to say", 0);

        List<Connection> hallConnections = new ArrayList<>();
        hallConnections.add(door);
        List<Character> hallCharacters = new ArrayList<>();
        hallCharacters.add(noCharacter);
        hall = new Location("hall", "A grand hall", "hall.png", "You are in the hall",
                hallConnections, hallCharacters);

        List<Connection> roomConnections = new ArrayList<>();
        roomConnections.add(noConnection);
        List<Character> roomCharacters = new ArrayList<>();
        roomCharacters.add(noCharacter);
        room = new Location("room", "A small room", "room.png", "You are in a room",
                roomConnections, roomCharacters);

        guard = new Character("guard", "A palace guard", "guard.png",
                Arrays.asList("Hello traveler"), null, "No more dialogue", 0);

        // Create Data Layer instance
        GameObjectCollection allObjects = new GameObjectCollection(
                Map.of("key", key, "sword", sword));

        gameData = new GameData(
                "Welcome to the game",
                "hall",
                "You won!",
                "exit",
                "hall",
                100,
                Arrays.asList(hall, room),
                allObjects,
                Arrays.asList(guard),
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>());
    }

    @Test
    public void testGetStartingLocation() {
        assertEquals("hall", gameData.getStartingLocation());
    }

    @Test
    public void testGetCurrentLocation() {
        assertEquals("hall", gameData.getCurrentLocation());
    }

    @Test
    public void testSetCurrentLocation() {
        gameData.setCurrentLocation("room");
        assertEquals("room", gameData.getCurrentLocation());
    }

    @Test
    public void testGetLocationByName() {
        Location loc = gameData.getLocationByName("hall");
        assertNotNull(loc);
        assertEquals("hall", loc.getName());
        assertEquals("A grand hall", loc.getDescription());
    }

    @Test
    public void testGetObjectByName() {
        GameObject obj = gameData.getObjectByName("key");
        assertNotNull(obj);
        assertEquals("key", obj.getName());
        assertTrue(obj.pickable());
    }

    @Test
    public void testGetCharacterByName() {
        Character ch = gameData.getCharacterByName("guard");
        assertNotNull(ch);
        assertEquals("guard", ch.getName());
    }

    @Test
    public void testInventoryOperations() {
        // Test adding to inventory
        gameData.addObjectToInventory(sword);
        assertTrue(gameData.getInventory().containsObject("sword"));

        // Test removing from inventory
        gameData.removeObjectFromInventory("sword");
        assertFalse(gameData.getInventory().containsObject("sword"));
    }

    @Test
    public void testGetTurnLimit() {
        assertEquals(100, gameData.getTurnLimit());
    }

    @Test
    public void testGetStartingMessage() {
        assertEquals("Welcome to the game", gameData.getStartingMessage());
    }

    @Test
    public void testGetEndingMessage() {
        assertEquals("You won!", gameData.getEndingMessage());
    }

    @Test
    public void testLocationObjectInteraction() {
        // Test Data Layer accessing Object Layer
        Location loc = gameData.getLocationByName("hall");
        assertNotNull(loc);

        // Test that we can get objects from GameData (not directly from Location)
        GameObject retrievedKey = gameData.getObjectByName("key");
        assertNotNull(retrievedKey);
        assertEquals("key", retrievedKey.getName());
    }
}
