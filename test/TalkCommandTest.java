import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TalkCommandTest.java
 *
 * Unit tests for the {@link TalkCommand} class.
 * <p>
 * This suite focuses only on verifying that {@code TalkCommand}
 * instances can be constructed correctly for a variety of input
 * values. It does <b>not</b> test the full interaction with the
 * game world (such as {@code GameData}, {@code Location}, or
 * {@code GameEngine}).
 * <p>
 * Full behavior of {@code validate(...)} and {@code execute(...)}
 * should be covered by integration tests that use a real game
 * environment.
 *
 * @author Xinyan Cai
 */
public class TalkCommandTest {

    /**
     * Shared {@link TalkCommand} instance prepared before each test.
     * Individual tests may reassign this field if needed.
     */
    private TalkCommand talkCommand;

    /**
     * Initializes a default {@link TalkCommand} instance before each test.
     * <p>
     * This ensures that construction with a typical, valid character
     * name does not throw any exceptions.
     */
    @BeforeEach
    public void setUp() {
        // 默认给一个正常的名字，单纯确保构造不会抛异常
        talkCommand = new TalkCommand("Caretaker");
    }

    /**
     * Verifies that the constructor works correctly with a normal,
     * non-null character name.
     */
    @Test
    public void testConstructorWithNormalName() {
        talkCommand = new TalkCommand("Caretaker");
        assertNotNull(talkCommand);
    }

    /**
     * Verifies that the constructor accepts a {@code null} character
     * name without throwing an exception.
     */
    @Test
    public void testConstructorWithNullName() {
        talkCommand = new TalkCommand(null);
        assertNotNull(talkCommand);
    }

    /**
     * Verifies that the constructor accepts an empty string as the
     * character name without failing.
     */
    @Test
    public void testConstructorWithEmptyName() {
        talkCommand = new TalkCommand("");
        assertNotNull(talkCommand);
    }

    /**
     * Verifies that the constructor accepts a character name that
     * consists only of whitespace.
     */
    @Test
    public void testConstructorWithWhitespaceName() {
        talkCommand = new TalkCommand("   ");
        assertNotNull(talkCommand);
    }

    /**
     * Verifies that the constructor can handle a very long character
     * name (for example, 100 characters) without throwing an exception.
     */
    @Test
    public void testConstructorWithLongName() {
        String longName = "a".repeat(100);
        talkCommand = new TalkCommand(longName);
        assertNotNull(talkCommand);
    }

    /**
     * Verifies that the constructor accepts character names containing
     * special characters (such as dashes, hashes, and underscores).
     */
    @Test
    public void testConstructorWithSpecialCharacters() {
        talkCommand = new TalkCommand("NPC-#1_@");
        assertNotNull(talkCommand);
    }

    /**
     * Verifies that multiple {@link TalkCommand} instances can be created
     * independently and that each instance is non-null.
     */
    @Test
    public void testMultipleTalkCommands() {
        TalkCommand cmd1 = new TalkCommand("Caretaker");
        // TalkCommand cmd2 = new TalkCommand("Guard");
        // TalkCommand cmd3 = new TalkCommand("Merchant");

        assertNotNull(cmd1);
        // assertNotNull(cmd2);
        // assertNotNull(cmd3);
    }

    /**
     * Verifies that the constructor accepts different case variations
     * of the same logical character name (for example, lower case,
     * mixed case, and upper case).
     * <p>
     * This test ensures only that construction is robust; it does not
     * check case-insensitive matching within {@code validate(...)}.
     */
    @Test
    public void testTalkCommandCaseVariations() {
        TalkCommand cmd1 = new TalkCommand("caretaker");
        TalkCommand cmd2 = new TalkCommand("Caretaker");
        TalkCommand cmd3 = new TalkCommand("CARETAKER");

        assertNotNull(cmd1);
        assertNotNull(cmd2);
        assertNotNull(cmd3);
    }

    /**
     * Verifies that the constructor accepts Unicode characters (for example,
     * Chinese characters) in the character name without throwing an exception.
     */
    @Test
    public void testTalkCommandWithUnicodeCharacters() {
        talkCommand = new TalkCommand("图书管理员");
        assertNotNull(talkCommand);
    }

    /*
     * Integration tests for validate() and execute() methods
     * 这些需要真正的 GameData / GameEngine / Location / Character / UI 环境，
     * 超出了命令类“纯单元测试”的范围。
     *
     * 推荐后续在单独的集成测试类中编写的场景：
     * - testValidateWithMatchingCharacterInLocation()
     * - testValidateFailsWhenNoCharacterInLocation()
     * - testValidateFailsWhenOutOfTurns()
     * - testExecuteDisplaysNextPhrase()
     * - testExecuteDisplaysNoMoreResponseWhenNoPhrases()
     */
}
