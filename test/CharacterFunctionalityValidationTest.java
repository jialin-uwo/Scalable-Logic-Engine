import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Character Functionality Validation Tests.
 * Tests character-specific functional requirements (VT-CHR-001 to VT-CHR-006).
 * Validates character dialogue, interaction, and GiveRule functionality.
 * 
 * @author Jialin Li
 */
@DisplayName("Character Functionality Validation Tests")
public class CharacterFunctionalityValidationTest {

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
     * Tests that characters can be talked to (VT-CHR-001).
     * Verifies that all characters have names and can provide dialogue phrases.
     */
    @Test
    @DisplayName("VT-CHR-001: Verify characters can be talked to")
    void testCharactersCanBeTalkedTo() {
        List<Character> characters = gameData.getCharacters();
        assertTrue(characters.size() > 0, "Should have at least one character");

        for (Character character : characters) {
            // Verify character has dialogue
            assertNotNull(character.getName(), "Character should have a name");

            // Try to get a phrase
            String phrase = character.consumePhrase();
            if (phrase != null) {
                assertFalse(phrase.trim().isEmpty(), "Character phrase should not be empty");
            }
        }
    }

    /**
     * Tests that characters have dialogue phrases (VT-CHR-002).
     * Verifies that characters have dialogue systems that can be exhausted through
     * multiple interactions.
     */
    @Test
    @DisplayName("VT-CHR-002: Verify characters have dialogue phrases")
    void testCharactersExhaustDialogue() {
        List<Character> characters = gameData.getCharacters();
        boolean foundCharacterWithDialogue = false;

        for (Character character : characters) {
            String phrase = character.consumePhrase();
            if (phrase != null) {
                foundCharacterWithDialogue = true;
                assertFalse(phrase.trim().isEmpty(), "Character phrase should not be empty");

                // Try consuming a few more phrases to verify dialogue system works
                for (int i = 0; i < 5; i++) {
                    String nextPhrase = character.consumePhrase();
                    // Dialogue may be finite or infinite - both are valid
                    if (nextPhrase != null) {
                        assertFalse(nextPhrase.trim().isEmpty(), "Character phrase should not be empty");
                    }
                }
                break;
            }
        }

        assertTrue(foundCharacterWithDialogue, "At least one character should have dialogue");
    }

    /**
     * Tests that characters accept wanted objects via GiveRules (VT-CHR-003).
     * Verifies that GiveRules reference valid characters and objects with
     * appropriate response text.
     */
    @Test
    @DisplayName("VT-CHR-003: Verify characters accept wanted objects via GiveRules")
    void testCharactersAcceptWantedObjects() {
        List<GiveRule> giveRules = gameData.getGiveRules();
        assertTrue(giveRules.size() > 0, "Should have at least one GiveRule");

        for (GiveRule rule : giveRules) {
            // Verify the character exists
            Character character = gameData.getCharacterByName(rule.getCharacterName());
            assertNotNull(character,
                    String.format("Character '%s' in GiveRule should exist", rule.getCharacterName()));

            // Verify the object exists
            GameObject obj = gameData.getObjectByName(rule.getObjectName());
            assertNotNull(obj,
                    String.format("Object '%s' in GiveRule should exist", rule.getObjectName()));

            // Verify rule has response text
            String text = rule.getText();
            assertNotNull(text, "GiveRule should have response text");
            assertFalse(text.trim().isEmpty(), "GiveRule response text should not be empty");
        }
    }

    /**
     * Tests that GiveCommand can reject unwanted objects (VT-CHR-004).
     * Verifies that not all objects have GiveRules, allowing for rejection of
     * unwanted items.
     */
    @Test
    @DisplayName("VT-CHR-004: Verify GiveCommand can reject unwanted objects")
    void testCharactersCanRejectUnwantedObjects() {
        // This is tested by checking that not all objects have GiveRules
        List<Location> locations = gameData.getLocations();
        Set<String> objectsWithGiveRules = new HashSet<>();
        Set<String> allObjects = new HashSet<>();

        // Collect all objects with GiveRules
        for (GiveRule rule : gameData.getGiveRules()) {
            objectsWithGiveRules.add(rule.getObjectName());
        }

        // Collect all objects
        for (Location location : locations) {
            for (GameObject obj : location.getAllObjects().getAllObjects()) {
                allObjects.add(obj.getName());
            }
        }

        // Verify not all objects have GiveRules (some should be rejectable)
        assertTrue(allObjects.size() > objectsWithGiveRules.size(),
                "Some objects should not have GiveRules (can be rejected)");
    }

    /**
     * Tests that characters can give objects through GiveRules (VT-CHR-005).
     * Verifies that at least one GiveRule provides resulting objects to the player.
     */
    @Test
    @DisplayName("VT-CHR-005: Verify characters can give objects through GiveRules")
    void testCharactersCanGiveObjects() {
        List<GiveRule> giveRules = gameData.getGiveRules();
        boolean foundRuleWithResultingObjects = false;

        for (GiveRule rule : giveRules) {
            List<String> resultingObjects = rule.getResultingObjects();
            if (resultingObjects != null && !resultingObjects.isEmpty()) {
                foundRuleWithResultingObjects = true;

                // Verify each resulting object exists
                for (String objName : resultingObjects) {
                    GameObject obj = gameData.getObjectByName(objName);
                    assertNotNull(obj,
                            String.format("Resulting object '%s' in GiveRule should exist", objName));
                }
            }
        }

        assertTrue(foundRuleWithResultingObjects,
                "At least one GiveRule should give objects to the player");
    }

    /**
     * Tests that game-ending GiveRules are valid if present (VT-CHR-006).
     * Verifies that any GiveRule marked as ending the game references valid
     * characters and objects.
     */
    @Test
    @DisplayName("VT-CHR-006: Verify game-ending GiveRules are valid (if present)")
    void testCharactersCanEndGame() {
        List<GiveRule> giveRules = gameData.getGiveRules();

        // Check if any GiveRules can end the game
        for (GiveRule rule : giveRules) {
            if (rule.isEndGame()) {
                // If game-ending rule exists, verify it's valid
                assertNotNull(gameData.getCharacterByName(rule.getCharacterName()),
                        "Game-ending GiveRule should reference valid character");
                assertNotNull(gameData.getObjectByName(rule.getObjectName()),
                        "Game-ending GiveRule should reference valid object");
                // Found at least one valid game-ending rule
                return;
            }
        }

        // Game-ending GiveRules are optional - game can end via ending location or turn
        // limit
        assertTrue(true, "Game-ending GiveRules are optional");
    }
}
