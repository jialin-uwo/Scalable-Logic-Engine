import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GoCommandTest.java
 *
 * Unit tests for the {@link GoCommand} class.
 * 
 * @author Xinyan Cai
 */
public class GoCommandTest {

    /**
     * Shared {@link GoCommand} instance initialized before each test.
     * Individual test methods may overwrite this field as needed.
     */
    private GoCommand goCommand;

    /**
     * Initializes a default {@link GoCommand} before each test method.
     * This ensures that there is always at least one valid-looking
     * command instance available and that construction does not throw
     * exceptions in a typical case.
     */
    @BeforeEach
    public void setUp() {
        // 默认构造一个命令，后面每个 @Test 会根据需要重新赋值
        goCommand = new GoCommand("staircase up");
        // If Location setup is needed in future, use:
        // List<Connection> connections = new ArrayList<>();
        // List<Character> characters = new ArrayList<>();
        // Location loc = new Location("test", "desc", "img.png", "long desc",
        // connections, characters);
    }

    /**
     * Verifies that the constructor works with a normal, non-null
     * connection label.
     * The resulting {@link GoCommand} should not be {@code null}.
     */
    @Test
    public void testConstructorWithNormalLabel() {
        goCommand = new GoCommand("staircase up");
        assertNotNull(goCommand);
    }

    /**
     * Verifies that the constructor accepts a {@code null} connection
     * label without throwing an exception.
     */
    @Test
    public void testConstructorWithNullLabel() {
        goCommand = new GoCommand(null);
        assertNotNull(goCommand);
    }

    /**
     * Verifies that the constructor accepts an empty string for the
     * connection label without throwing an exception.
     */
    @Test
    public void testConstructorWithEmptyLabel() {
        goCommand = new GoCommand("");
        assertNotNull(goCommand);
    }

    /**
     * Verifies that the constructor accepts a connection label that
     * consists only of whitespace.
     */
    @Test
    public void testConstructorWithWhitespaceLabel() {
        goCommand = new GoCommand("   ");
        assertNotNull(goCommand);
    }

    /**
     * Verifies that the constructor can handle a very long connection
     * label (for example, 100 characters) without failing.
     */
    @Test
    public void testConstructorWithLongLabel() {
        String longLabel = "a".repeat(100);
        goCommand = new GoCommand(longLabel);
        assertNotNull(goCommand);
    }

    /**
     * Verifies that the constructor accepts a connection label
     * containing special characters (such as dashes and digits).
     */
    @Test
    public void testConstructorWithSpecialCharacters() {
        goCommand = new GoCommand("north-door#1");
        assertNotNull(goCommand);
    }

    /**
     * Verifies that multiple {@link GoCommand} instances with different
     * labels can be created independently, and none of them is
     * {@code null}.
     */
    @Test
    public void testMultipleGoCommands() {
        GoCommand cmd1 = new GoCommand("staircase up");
        GoCommand cmd2 = new GoCommand("stairs down");
        GoCommand cmd3 = new GoCommand("hidden passage");

        assertNotNull(cmd1);
        assertNotNull(cmd2);
        assertNotNull(cmd3);
    }

    /**
     * Verifies that the constructor accepts different case variations
     * of the same logical label (for example, lower case, mixed case,
     * and upper case).
     * This test only checks that construction succeeds; it does not
     * verify any case-insensitive behavior in {@code validate(...)}.
     */
    @Test
    public void testGoCommandCaseVariations() {
        GoCommand cmd1 = new GoCommand("staircase Up");
        GoCommand cmd2 = new GoCommand("staircAse up");
        GoCommand cmd3 = new GoCommand("STAIRCASE UP");

        assertNotNull(cmd1);
        assertNotNull(cmd2);
        assertNotNull(cmd3);
    }

    /**
     * Verifies that the constructor accepts Unicode characters
     * (for example, Chinese) in the connection label without
     * throwing an exception.
     */
    @Test
    public void testGoCommandWithUnicodeCharacters() {
        goCommand = new GoCommand("楼梯");
        assertNotNull(goCommand);
    }

    /*
     * Integration tests for validate() and execute() methods
     * 这些需要真正的 GameEngine + GameData + Location + Connection 环境，
     * 超出了命令类的“纯单元测试”范围。
     *
     * 推荐后续的集成测试场景（在专门的集成测试类里实现）：
     * - testValidateWithMatchingConnection()
     * - testValidateFailsWhenNoConnection()
     * - testValidateFailsWhenOutOfTurns()
     * - testExecuteUpdatesCurrentLocation()
     * - testExecuteDoesNothingIfTargetLocationMissing()
     */
}
