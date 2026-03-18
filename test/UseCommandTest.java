import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for UseCommand
 * Tests command validation and execution logic
 * 
 * @author Jialin Li
 */
public class UseCommandTest {
    private UseCommand useCommand;
    private GameEngine mockEngine;
    private GameData gameData;
    private GameObject key;
    private GameObject door;
    private GameObject unlockedDoor;
    private UseRule useRule;

    @BeforeEach
    public void setUp() {
        // Create test objects
        key = new GameObject("key", "A rusty key", "key.png",
                Arrays.asList("metal"), null, true);
        door = new GameObject("door", "A locked door", "door.png",
                Arrays.asList("locked"), null, false);
        unlockedDoor = new GameObject("unlocked door", "An open door", "door_open.png",
                Arrays.asList("open"), null, false);

        // Create a use rule: door + any metal -> unlocked door
        useRule = new UseRule(
                Arrays.asList("door", "*"),
                "metal",
                Arrays.asList("unlocked door"));

        // Create game data
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
                Map.of("key", key, "door", door, "unlocked door", unlockedDoor));

        gameData = new GameData(
                "Start",
                "hall",
                "End",
                "exit",
                "hall",
                100,
                Arrays.asList(hall),
                allObjects,
                new ArrayList<>(),
                Arrays.asList(useRule),
                new ArrayList<>(),
                new HashMap<>());

        // Add key and door to current location
        gameData.addObjectToLocation("hall", key);
        gameData.addObjectToLocation("hall", door);
    }

    @Test
    public void testConstructor() {
        useCommand = new UseCommand(new String[] { "key", "door" });
        assertNotNull(useCommand);
    }

    @Test
    public void testConstructorWithNullArray() {
        useCommand = new UseCommand(null);
        assertNotNull(useCommand);
    }

    @Test
    public void testConstructorWithEmptyArray() {
        useCommand = new UseCommand(new String[] {});
        assertNotNull(useCommand);
    }

    @Test
    public void testValidateWithApplicableRule() {
        useCommand = new UseCommand(new String[] { "door", "key" });
        // Now we can test validate with GameData directly
        boolean result = useCommand.validate(gameData);
        assertTrue(result, "Should validate when rule exists");
    }

    @Test
    public void testValidateWithNoApplicableRule() {
        useCommand = new UseCommand(new String[] { "invalid", "objects" });
        boolean result = useCommand.validate(gameData);
        assertFalse(result, "Should not validate when no rule exists");
    }

    @Test
    public void testSingleObjectCommand() {
        useCommand = new UseCommand(new String[] { "key" });
        assertNotNull(useCommand);
    }

    @Test
    public void testMultipleObjectsCommand() {
        useCommand = new UseCommand(new String[] { "key", "door", "sword" });
        assertNotNull(useCommand);
    }

    @Test
    public void testUseCommandWithDuplicateObjects() {
        useCommand = new UseCommand(new String[] { "key", "key" });
        assertNotNull(useCommand);
    }

    @Test
    public void testUseCommandWithManyObjects() {
        String[] manyObjects = new String[10];
        for (int i = 0; i < 10; i++) {
            manyObjects[i] = "object" + i;
        }
        useCommand = new UseCommand(manyObjects);
        assertNotNull(useCommand);
    }

    /*
     * Integration tests for validate() and execute() methods
     * These require GameEngine with GameUI, which is outside the scope of unit
     * testing
     * for Object Layer and Data Layer components.
     * 
     * Recommended integration test scenarios:
     * - testValidateWithApplicableUseRule()
     * - testValidateWithNoApplicableRule()
     * - testValidateWithMissingObjects()
     * - testExecuteRemovesUsedObjects()
     * - testExecuteAddsResultObjects()
     * - testExecuteAppliesCorrectRule()
     * - testExecuteDisplaysRuleText() [Requires GameUI]
     */
}
