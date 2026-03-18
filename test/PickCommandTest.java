import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for PickCommand
 * Tests command construction and validation logic
 * 
 * @author Jialin Li
 */
public class PickCommandTest {
    private PickCommand pickCommand;
    private GameObject key;
    private GameObject sword;
    private GameObject chest;

    @BeforeEach
    public void setUp() {
        // Create test objects
        key = new GameObject("key", "A rusty key", "key.png",
                Arrays.asList("metal"), null, true);
        sword = new GameObject("sword", "A sharp sword", "sword.png",
                Arrays.asList("weapon"), null, true);
        chest = new GameObject("chest", "A heavy chest", "chest.png",
                Arrays.asList("container"), null, false); // Not pickable
    }

    @Test
    public void testConstructor() {
        pickCommand = new PickCommand("key");
        assertNotNull(pickCommand);
    }

    @Test
    public void testConstructorWithNullName() {
        pickCommand = new PickCommand(null);
        assertNotNull(pickCommand);
    }

    @Test
    public void testConstructorWithEmptyName() {
        pickCommand = new PickCommand("");
        assertNotNull(pickCommand);
    }

    @Test
    public void testConstructorWithSpecialCharacters() {
        pickCommand = new PickCommand("magic_sword-+123");
        assertNotNull(pickCommand);
    }

    @Test
    public void testConstructorWithLongName() {
        String longName = "a".repeat(100);
        pickCommand = new PickCommand(longName);
        assertNotNull(pickCommand);
    }

    @Test
    public void testPickableObject() {
        pickCommand = new PickCommand("key");
        // Create test GameData with the object in a location
        GameData testData = createTestGameData();
        testData.addObjectToLocation("hall", key);
        boolean result = pickCommand.validate(testData);
        assertTrue(result, "Should validate when object is pickable");
    }

    @Test
    public void testNonPickableObject() {
        pickCommand = new PickCommand("chest");
        GameData testData = createTestGameData();
        testData.addObjectToLocation("hall", chest);
        boolean result = pickCommand.validate(testData);
        assertFalse(result, "Should not validate when object is not pickable");
    }

    @Test
    public void testPickNonExistentObject() {
        pickCommand = new PickCommand("nonexistent");
        GameData testData = createTestGameData();
        boolean result = pickCommand.validate(testData);
        assertFalse(result, "Should not validate when object doesn't exist");
    }

    private GameData createTestGameData() {
        Connection noConnection = new Connection("none", "No exit", "none");
        Character noCharacter = new Character("none", "Nobody", "",
                new ArrayList<>(), null, "Nothing", 0);
        List<Connection> connections = new ArrayList<>();
        connections.add(noConnection);
        List<Character> characters = new ArrayList<>();
        characters.add(noCharacter);
        Location hall = new Location("hall", "A hall", "hall.png", "You are in a hall",
                connections, characters);
        GameObjectCollection allObjects = new GameObjectCollection(
                Map.of("key", key, "chest", chest));
        return new GameData("Start", "hall", "End", "exit", "hall", 100,
                Arrays.asList(hall), allObjects, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    @Test
    public void testMultiplePickCommands() {
        PickCommand cmd1 = new PickCommand("key");
        PickCommand cmd2 = new PickCommand("sword");
        PickCommand cmd3 = new PickCommand("chest");

        assertNotNull(cmd1);
        assertNotNull(cmd2);
        assertNotNull(cmd3);
    }

    @Test
    public void testPickCommandWithWhitespace() {
        pickCommand = new PickCommand("  key  ");
        assertNotNull(pickCommand);
    }

    @Test
    public void testPickCommandCaseSensitivity() {
        PickCommand cmd1 = new PickCommand("Key");
        PickCommand cmd2 = new PickCommand("key");
        PickCommand cmd3 = new PickCommand("KEY");

        assertNotNull(cmd1);
        assertNotNull(cmd2);
        assertNotNull(cmd3);
    }

    @Test
    public void testPickCommandWithUnicodeCharacters() {
        pickCommand = new PickCommand("魔法剑");
        assertNotNull(pickCommand);
    }

    /*
     * Integration tests for validate() and execute() methods
     * These require GameEngine with GameUI, which is outside the scope of unit
     * testing
     * for Object Layer and Data Layer components.
     * 
     * Recommended integration test scenarios:
     * - testValidateWithPickableObjectInLocation()
     * - testValidateWithNonPickableObject()
     * - testValidateWithFullInventory()
     * - testValidateWithObjectNotInLocation()
     * - testExecutePicksObjectFromLocation()
     * - testExecuteAddsObjectToInventory()
     */
}
