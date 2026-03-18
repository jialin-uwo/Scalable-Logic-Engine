import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

// For new TargetType enum

/**
 * Unit tests for ExamineCommand
 * Tests command validation and object/character examination
 * 
 * @author Jialin Li
 */
public class ExamineCommandTest {
    private ExamineCommand examineCommand;
    private GameData gameData;
    private GameObject chest;
    private GameObject key;
    private Character guard;
    private Connection testConnection;

    @BeforeEach
    public void setUp() {
        // Create test objects
        key = new GameObject("key", "A small key", "key.png",
                Arrays.asList("metal"), null, true);

        // Create a chest containing a key
        chest = new GameObject("chest", "A wooden chest", "chest.png",
                Arrays.asList("container"), Arrays.asList(key), false);

        // Create a character
        guard = new Character("guard", "A palace guard", "guard.png",
                Arrays.asList("Hello traveler", "Welcome"),
                null, "No more to say", 0);

        // Create a connection
        testConnection = new Connection("door", "A sturdy wooden door.", "exit");

        // Create game data
        List<Connection> connections = new ArrayList<>();
        connections.add(testConnection);
        List<Character> characters = new ArrayList<>();
        characters.add(guard);

        Location hall = new Location("hall", "A hall", "hall.png", "You are in a hall",
                connections, characters);

        GameObjectCollection allObjects = new GameObjectCollection(
                Map.of("chest", chest, "key", key));

        gameData = new GameData(
                "Start",
                "hall",
                "End",
                "exit",
                "hall",
                100,
                Arrays.asList(hall),
                allObjects,
                Arrays.asList(guard),
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>());
    }

    @Test
    public void testConstructorForObject() {
        examineCommand = new ExamineCommand("chest", ExamineCommand.TargetType.OBJECT);
        assertNotNull(examineCommand);
    }

    @Test
    public void testConstructorForCharacter() {
        examineCommand = new ExamineCommand("guard", ExamineCommand.TargetType.CHARACTER);
        assertNotNull(examineCommand);
    }

    @Test
    public void testConstructorForConnection() {
        examineCommand = new ExamineCommand("door", ExamineCommand.TargetType.CONNECTION);
        assertNotNull(examineCommand);
    }

    @Test
    public void testConstructorWithNullName() {
        examineCommand = new ExamineCommand(null, ExamineCommand.TargetType.OBJECT);
        assertNotNull(examineCommand);
    }

    @Test
    public void testConstructorWithEmptyName() {
        examineCommand = new ExamineCommand("", ExamineCommand.TargetType.OBJECT);
        assertNotNull(examineCommand);
    }

    @Test
    public void testExamineExistingObject() {
        examineCommand = new ExamineCommand("chest", ExamineCommand.TargetType.OBJECT);
        boolean result = examineCommand.validate(gameData);
        assertTrue(result, "Should validate when object exists");
    }

    @Test
    public void testExamineNonExistentObject() {
        examineCommand = new ExamineCommand("nonexistent", ExamineCommand.TargetType.OBJECT);
        boolean result = examineCommand.validate(gameData);
        assertFalse(result, "Should not validate when object doesn't exist");
    }

    @Test
    public void testExamineExistingCharacter() {
        examineCommand = new ExamineCommand("guard", ExamineCommand.TargetType.CHARACTER);
        boolean result = examineCommand.validate(gameData);
        assertTrue(result, "Should validate when character exists");
    }

    @Test
    public void testExamineNonExistentCharacter() {
        examineCommand = new ExamineCommand("unknown", ExamineCommand.TargetType.CHARACTER);
        boolean result = examineCommand.validate(gameData);
        assertFalse(result, "Should not validate when character doesn't exist");
    }

    @Test
    public void testExamineExistingConnection() {
        examineCommand = new ExamineCommand("door", ExamineCommand.TargetType.CONNECTION);
        boolean result = examineCommand.validate(gameData);
        assertTrue(result, "Should validate when connection exists");
    }

    @Test
    public void testExamineNonExistentConnection() {
        examineCommand = new ExamineCommand("window", ExamineCommand.TargetType.CONNECTION);
        boolean result = examineCommand.validate(gameData);
        assertFalse(result, "Should not validate when connection doesn't exist");
    }

    @Test
    public void testExamineSameTargetMultipleTimes() {
        ExamineCommand cmd1 = new ExamineCommand("chest", ExamineCommand.TargetType.OBJECT);
        ExamineCommand cmd2 = new ExamineCommand("chest", ExamineCommand.TargetType.OBJECT);
        assertNotNull(cmd1);
        assertNotNull(cmd2);
    }

    @Test
    public void testExamineWithCaseSensitiveName() {
        ExamineCommand cmd1 = new ExamineCommand("Chest", ExamineCommand.TargetType.OBJECT);
        ExamineCommand cmd2 = new ExamineCommand("CHEST", ExamineCommand.TargetType.OBJECT);
        assertNotNull(cmd1);
        assertNotNull(cmd2);
    }

    /*
     * Integration tests for validate() and execute() methods
     * These require GameEngine with GameUI, which is outside the scope of unit
     * testing
     * for Object Layer and Data Layer components.
     * 
     * Recommended integration test scenarios:
     * - testValidateObjectExists()
     * - testValidateObjectDoesNotExist()
     * - testValidateCharacterExists()
     * - testValidateCharacterDoesNotExist()
     * - testExecuteDisplaysObjectDescription() [Requires GameUI]
     * - testExecuteDisplaysCharacterDescription() [Requires GameUI]
     * - testExecuteAddsContainedObjectsToLocation()
     */
}
