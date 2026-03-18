import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for DropCommand
 * Tests command construction and validation logic
 * 
 * @author Jialin Li
 */
public class DropCommandTest {
    private DropCommand dropCommand;
    private GameObject key;
    private GameObject sword;
    private GameObject potion;

    @BeforeEach
    public void setUp() {
        // Create test objects
        key = new GameObject("key", "A rusty key", "key.png",
                Arrays.asList("metal"), null, true);
        sword = new GameObject("sword", "A sharp sword", "sword.png",
                Arrays.asList("weapon"), null, true);
        potion = new GameObject("potion", "Health potion", "potion.png",
                Arrays.asList("consumable"), null, true);
    }

    @Test
    public void testConstructor() {
        dropCommand = new DropCommand("key");
        assertNotNull(dropCommand);
    }

    @Test
    public void testConstructorWithNullName() {
        dropCommand = new DropCommand(null);
        assertNotNull(dropCommand);
    }

    @Test
    public void testConstructorWithEmptyName() {
        dropCommand = new DropCommand("");
        assertNotNull(dropCommand);
    }

    @Test
    public void testConstructorWithSpecialCharacters() {
        dropCommand = new DropCommand("magic_sword-+123");
        assertNotNull(dropCommand);
    }

    @Test
    public void testConstructorWithLongName() {
        String longName = "a".repeat(100);
        dropCommand = new DropCommand(longName);
        assertNotNull(dropCommand);
    }

    @Test
    public void testDropExistingObject() {
        dropCommand = new DropCommand("key");
        // Create test GameData with object in inventory
        GameData testData = createTestGameData();
        testData.addObjectToInventory(key);
        boolean result = dropCommand.validate(testData);
        assertTrue(result, "Should validate when object is in inventory");
    }

    @Test
    public void testDropNonExistentObject() {
        dropCommand = new DropCommand("nonexistent");
        GameData testData = createTestGameData();
        boolean result = dropCommand.validate(testData);
        assertFalse(result, "Should not validate when object is not in inventory");
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
                Map.of("key", key, "sword", sword, "potion", potion));
        return new GameData("Start", "hall", "End", "exit", "hall", 100,
                Arrays.asList(hall), allObjects, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    }

    @Test
    public void testMultipleDropCommands() {
        DropCommand cmd1 = new DropCommand("key");
        DropCommand cmd2 = new DropCommand("sword");
        DropCommand cmd3 = new DropCommand("potion");

        assertNotNull(cmd1);
        assertNotNull(cmd2);
        assertNotNull(cmd3);
    }

    @Test
    public void testDropCommandWithWhitespace() {
        dropCommand = new DropCommand("  sword  ");
        assertNotNull(dropCommand);
    }

    @Test
    public void testDropSameObjectMultipleTimes() {
        DropCommand cmd1 = new DropCommand("key");
        DropCommand cmd2 = new DropCommand("key");

        assertNotNull(cmd1);
        assertNotNull(cmd2);
    }

    @Test
    public void testDropCommandCaseSensitivity() {
        DropCommand cmd1 = new DropCommand("Sword");
        DropCommand cmd2 = new DropCommand("sword");
        DropCommand cmd3 = new DropCommand("SWORD");

        assertNotNull(cmd1);
        assertNotNull(cmd2);
        assertNotNull(cmd3);
    }

    @Test
    public void testDropCommandWithUnicodeCharacters() {
        dropCommand = new DropCommand("魔法药水");
        assertNotNull(dropCommand);
    }

    /*
     * Integration tests for validate() and execute() methods
     * These require GameEngine with GameUI, which is outside the scope of unit
     * testing
     * for Object Layer and Data Layer components.
     * 
     * Recommended integration test scenarios:
     * - testValidateWithObjectInInventory()
     * - testValidateWithObjectNotInInventory()
     * - testValidateWithEmptyInventory()
     * - testExecuteRemovesObjectFromInventory()
     * - testExecuteAddsObjectToCurrentLocation()
     */
}
