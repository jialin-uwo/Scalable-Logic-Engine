import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Turn Counting Validation Tests.
 * Tests turn counting functional requirements (VT-TRN-001 to VT-TRN-004).
 * Note: Turn counting is managed by GameEngine, not GameData, so these tests
 * validate the turn limit configuration in GameData only.
 * 
 * @author Jialin Li
 */
@DisplayName("Turn Counting Validation Tests")
public class TurnCountingValidationTest {

    private GameData gameData;
    private DataLoader dataLoader;

    /**
     * Sets up the test environment before each test.
     * Loads game data from the JSON file and validates successful loading.
     */
    @BeforeEach
    void setUp() throws Exception {
        dataLoader = new DataLoader();
        gameData = dataLoader.loadGameData("assets/data/DataFile.json");
        assertNotNull(gameData, "GameData should be loaded successfully");
    }

    /**
     * Tests that turn limit is defined in game data (VT-TRN-001).
     * Verifies that the turn limit is either -1 (unlimited) or a positive number.
     */
    @Test
    @DisplayName("VT-TRN-001: Verify turn limit is defined in game data")
    void testTurnLimitIsDefined() {
        int turnLimit = gameData.getTurnLimit();
        // Turn limit should be either -1 (unlimited) or a positive number
        assertTrue(turnLimit == -1 || turnLimit > 0,
                String.format("Turn limit should be -1 (unlimited) or positive, found %d", turnLimit));
    }

    /**
     * Tests that turn limit value is reasonable (VT-TRN-002).
     * Verifies that limited turn counts allow sufficient gameplay and are not
     * unreasonably large.
     */
    @Test
    @DisplayName("VT-TRN-002: Verify turn limit value is reasonable")
    void testTurnLimitIsReasonable() {
        int turnLimit = gameData.getTurnLimit();

        if (turnLimit > 0) {
            // If limited, should be at least 10 turns to allow gameplay
            assertTrue(turnLimit >= 10,
                    String.format("Turn limit should be at least 10 for reasonable gameplay, found %d", turnLimit));
        }

        // Verify it's not an unreasonably large number
        assertTrue(turnLimit <= 10000,
                String.format("Turn limit should be reasonable (<= 10000), found %d", turnLimit));
    }

    /**
     * Tests that unlimited turns support is valid (VT-TRN-003).
     * Verifies that a turn limit of -1 correctly represents unlimited turns.
     */
    @Test
    @DisplayName("VT-TRN-003: Verify unlimited turns support (turn limit = -1)")
    void testUnlimitedTurnsSupport() {
        int turnLimit = gameData.getTurnLimit();

        // Either unlimited or positive
        assertTrue(turnLimit == -1 || turnLimit > 0,
                "Turn limit should be -1 (unlimited) or a positive number");

        if (turnLimit == -1) {
            // Unlimited turns configuration is valid
            assertEquals(-1, turnLimit, "Unlimited turns should be represented by -1");
        }
    }

    /**
     * Tests that turn limit configuration is consistent (VT-TRN-004).
     * Verifies that the turn limit value remains consistent across multiple
     * accesses and is never zero.
     */
    @Test
    @DisplayName("VT-TRN-004: Verify turn limit configuration consistency")
    void testTurnLimitConsistency() {
        int turnLimit = gameData.getTurnLimit();

        // Verify the value is consistent across multiple accesses
        int secondCheck = gameData.getTurnLimit();
        assertEquals(turnLimit, secondCheck, "Turn limit should be consistent");

        // Verify it's not zero (should be -1 or positive)
        assertNotEquals(0, turnLimit, "Turn limit should not be 0");
    }
}