import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Validation tests for game data loading requirements.
 * Verifies that loaded game data meets all functional requirements.
 * 
 * @author Jialin Li
 */
@DisplayName("Game Data Loading Validation Tests")
public class GameDataLoadingValidationTest {

    private GameData gameData;

    /**
     * Sets up the test environment before each test.
     * Loads game data and sets the starting location.
     */
    @BeforeEach
    void setUp() throws IOException {
        DataLoader loader = new DataLoader();
        gameData = loader.loadGameData("assets/data/DataFile.json");
        gameData.setCurrentLocation(gameData.getStartingLocation());
    }

    /**
     * Tests that JSON parsing works correctly (VT-GDL-001).
     * Verifies that game data is successfully loaded and basic fields are
     * populated.
     */
    @Test
    @DisplayName("VT-GDL-001: Verify JSON parsing works correctly")
    void testJsonParsingWorks() {
        assertNotNull(gameData, "GameData should be loaded from JSON");
        assertNotNull(gameData.getStartingLocation(), "Starting location should be defined");
        assertNotNull(gameData.getEndingLocation(), "Ending location should be defined");
    }

    /**
     * Tests that minimum 10 locations are loaded (VT-GDL-002).
     * Verifies that at least 10 unique locations exist in the game data.
     */
    @Test
    @DisplayName("VT-GDL-002: Verify minimum 10 locations loaded")
    void testMinimum10Locations() {
        List<Location> locations = gameData.getLocations();
        assertNotNull(locations, "Locations list should not be null");
        assertTrue(locations.size() >= 10,
                String.format("Should have at least 10 locations, found %d", locations.size()));

        // Verify location names are unique
        Set<String> locationNames = new HashSet<>();
        for (Location loc : locations) {
            assertNotNull(loc.getName(), "Location name should not be null");
            assertTrue(locationNames.add(loc.getName()),
                    "Location name should be unique: " + loc.getName());
        }
    }

    /**
     * Tests that minimum 10 objects are loaded (VT-GDL-003).
     * Verifies that at least 10 unique objects exist in the game data.
     */
    @Test
    @DisplayName("VT-GDL-003: Verify minimum 10 objects loaded")
    void testMinimum10Objects() {
        GameObjectCollection objects = gameData.getObjects();
        assertNotNull(objects, "Objects collection should not be null");

        List<GameObject> allObjects = objects.getAllObjects();
        assertNotNull(allObjects, "Objects list should not be null");
        assertTrue(allObjects.size() >= 10,
                String.format("Should have at least 10 objects, found %d", allObjects.size()));

        // Verify object names are unique
        Set<String> objectNames = new HashSet<>();
        for (GameObject obj : allObjects) {
            assertNotNull(obj.getName(), "Object name should not be null");
            assertTrue(objectNames.add(obj.getName()),
                    "Object name should be unique: " + obj.getName());
        }
    }

    /**
     * Tests that minimum 3 characters are loaded (VT-GDL-004).
     * Verifies that at least 3 unique characters exist in the game data.
     */
    @Test
    @DisplayName("VT-GDL-004: Verify minimum 3 characters loaded")
    void testMinimum3Characters() {
        List<Character> characters = gameData.getCharacters();
        assertNotNull(characters, "Characters list should not be null");
        assertTrue(characters.size() >= 3,
                String.format("Should have at least 3 characters, found %d", characters.size()));

        // Verify character names are unique
        Set<String> characterNames = new HashSet<>();
        for (Character ch : characters) {
            assertNotNull(ch.getName(), "Character name should not be null");
            assertTrue(characterNames.add(ch.getName()),
                    "Character name should be unique: " + ch.getName());
        }
    }

    /**
     * Tests that UseRules are loaded correctly (VT-GDL-005).
     * Verifies that at least one UseRule exists with required fields.
     */
    @Test
    @DisplayName("VT-GDL-005: Verify UseRules loaded correctly")
    void testUseRulesLoaded() {
        List<UseRule> useRules = gameData.getUseRules();
        assertNotNull(useRules, "UseRules list should not be null");
        assertTrue(useRules.size() > 0, "Should have at least one UseRule");

        // Verify each UseRule has required fields
        for (UseRule rule : useRules) {
            assertNotNull(rule.getObjectNames(), "UseRule object names should not be null");
            assertTrue(rule.getObjectNames().size() > 0,
                    "UseRule should have at least one object");
            assertNotNull(rule.getResultingObjects(), "UseRule resulting objects should not be null");
        }
    }

    /**
     * Tests that GiveRules are loaded correctly (VT-GDL-006).
     * Verifies that at least one GiveRule exists with required fields.
     */
    @Test
    @DisplayName("VT-GDL-006: Verify GiveRules loaded correctly")
    void testGiveRulesLoaded() {
        List<GiveRule> giveRules = gameData.getGiveRules();
        assertNotNull(giveRules, "GiveRules list should not be null");
        assertTrue(giveRules.size() > 0, "Should have at least one GiveRule");

        // Verify each GiveRule has required fields
        for (GiveRule rule : giveRules) {
            assertNotNull(rule.getCharacterName(), "GiveRule character name should not be null");
            assertNotNull(rule.getObjectName(), "GiveRule object name should not be null");
            assertNotNull(rule.getText(), "GiveRule text should not be null");
        }
    }

    /**
     * Tests that starting and ending locations exist (VT-GDL-007).
     * Verifies that the designated starting and ending locations are present and
     * different.
     */
    @Test
    @DisplayName("VT-GDL-007: Verify starting and ending locations exist")
    void testStartingAndEndingLocationsExist() {
        String startingLocationName = gameData.getStartingLocation();
        assertNotNull(startingLocationName, "Starting location name should be defined");

        Location startingLocation = gameData.getLocationByName(startingLocationName);
        assertNotNull(startingLocation,
                "Starting location should exist: " + startingLocationName);

        String endingLocationName = gameData.getEndingLocation();
        assertNotNull(endingLocationName, "Ending location name should be defined");

        Location endingLocation = gameData.getLocationByName(endingLocationName);
        assertNotNull(endingLocation,
                "Ending location should exist: " + endingLocationName);

        // Verify starting and ending locations are different
        assertNotEquals(startingLocationName, endingLocationName,
                "Starting and ending locations should be different");
    }

    /**
     * Tests that turn limit is set (VT-GDL-008).
     * Verifies that the turn limit is a positive value.
     */
    @Test
    @DisplayName("VT-GDL-008: Verify turn limit is set")
    void testTurnLimitIsSet() {
        int turnLimit = gameData.getTurnLimit();
        assertTrue(turnLimit > 0,
                String.format("Turn limit should be positive, found %d", turnLimit));
    }

    /**
     * Tests that all locations have descriptions (VT-GDL-009).
     * Verifies that every location has a non-null, non-empty description.
     */
    @Test
    @DisplayName("VT-GDL-009: Verify all locations have descriptions")
    void testAllLocationsHaveDescriptions() {
        List<Location> locations = gameData.getLocations();

        for (Location location : locations) {
            assertNotNull(location.getDescription(),
                    "Location should have description: " + location.getName());
            assertFalse(location.getDescription().trim().isEmpty(),
                    "Location description should not be empty: " + location.getName());
        }
    }

    /**
     * Tests that all objects have descriptions (VT-GDL-010).
     * Verifies that every object has a non-null, non-empty description.
     */
    @Test
    @DisplayName("VT-GDL-010: Verify all objects have descriptions")
    void testAllObjectsHaveDescriptions() {
        List<GameObject> objects = gameData.getObjects().getAllObjects();

        for (GameObject obj : objects) {
            assertNotNull(obj.getDescription(),
                    "Object should have description: " + obj.getName());
            assertFalse(obj.getDescription().trim().isEmpty(),
                    "Object description should not be empty: " + obj.getName());
        }
    }

    /**
     * Tests that all characters have descriptions (VT-GDL-011).
     * Verifies that every character has a non-null, non-empty description.
     */
    @Test
    @DisplayName("VT-GDL-011: Verify all characters have descriptions")
    void testAllCharactersHaveDescriptions() {
        List<Character> characters = gameData.getCharacters();

        for (Character ch : characters) {
            assertNotNull(ch.getDescription(),
                    "Character should have description: " + ch.getName());
            assertFalse(ch.getDescription().trim().isEmpty(),
                    "Character description should not be empty: " + ch.getName());
        }
    }

    /**
     * Tests that location connections reference valid locations (VT-GDL-012).
     * Verifies that all connection targets exist in the game data.
     */
    @Test
    @DisplayName("VT-GDL-012: Verify location connections reference valid locations")
    void testLocationConnectionsValid() {
        List<Location> locations = gameData.getLocations();

        for (Location location : locations) {
            List<Connection> connections = location.getConnections();
            if (connections != null && !connections.isEmpty()) {
                for (Connection connection : connections) {
                    String targetName = connection.getTargetLocationName();
                    if (targetName != null && !targetName.equals("none")) {
                        Location targetLocation = gameData.getLocationByName(targetName);
                        assertNotNull(targetLocation,
                                String.format("Connection target should exist: %s -> %s",
                                        location.getName(), targetName));
                    }
                }
            }
        }
    }

    /**
     * Tests that starting message and ending message exist (VT-GDL-013).
     * Verifies that both messages are defined and non-empty.
     */
    @Test
    @DisplayName("VT-GDL-013: Verify starting message and ending message exist")
    void testStartingAndEndingMessagesExist() {
        String startingMessage = gameData.getStartingMessage();
        assertNotNull(startingMessage, "Starting message should be defined");
        assertFalse(startingMessage.trim().isEmpty(), "Starting message should not be empty");

        String endingMessage = gameData.getEndingMessage();
        assertNotNull(endingMessage, "Ending message should be defined");
        assertFalse(endingMessage.trim().isEmpty(), "Ending message should not be empty");
    }

    /**
     * Tests that UseRules reference valid objects (VT-GDL-014).
     * Verifies that all objects referenced in UseRules exist in the game data.
     */
    @Test
    @DisplayName("VT-GDL-014: Verify UseRules reference valid objects")
    void testUseRulesReferenceValidObjects() {
        List<UseRule> useRules = gameData.getUseRules();

        for (UseRule rule : useRules) {
            for (String objName : rule.getObjectNames()) {
                if ("*".equals(objName)) {
                    // Wildcard: skip object name validation, attribute logic is handled in engine
                    continue;
                }
                GameObject obj = gameData.getObjectByName(objName);
                assertNotNull(obj,
                        String.format("UseRule references non-existent object: %s", objName));
            }

            // Verify resulting objects if any
            if (rule.getResultingObjects() != null && rule.getResultingObjects().size() > 0) {
                for (String resultObjName : rule.getResultingObjects()) {
                    GameObject resultObj = gameData.getObjectByName(resultObjName);
                    assertNotNull(resultObj,
                            String.format("UseRule resulting object should exist: %s", resultObjName));
                }
            }
        }
    }

    /**
     * Tests that GiveRules reference valid characters and objects (VT-GDL-015).
     * Verifies that all characters and objects referenced in GiveRules exist in the
     * game data.
     */
    @Test
    @DisplayName("VT-GDL-015: Verify GiveRules reference valid characters and objects")
    void testGiveRulesReferenceValidCharactersAndObjects() {
        List<GiveRule> giveRules = gameData.getGiveRules();

        for (GiveRule rule : giveRules) {
            // Verify character exists
            Character character = gameData.getCharacterByName(rule.getCharacterName());
            assertNotNull(character,
                    String.format("GiveRule references non-existent character: %s",
                            rule.getCharacterName()));

            // Verify object exists
            GameObject obj = gameData.getObjectByName(rule.getObjectName());
            assertNotNull(obj,
                    String.format("GiveRule references non-existent object: %s",
                            rule.getObjectName()));

            // Verify resulting objects if any
            if (rule.getResultingObjects() != null && rule.getResultingObjects().size() > 0) {
                for (String resultObjName : rule.getResultingObjects()) {
                    GameObject resultObj = gameData.getObjectByName(resultObjName);
                    assertNotNull(resultObj,
                            String.format("GiveRule resulting object should exist: %s",
                                    resultObjName));
                }
            }
        }
    }

    /**
     * Tests that characters are placed in valid locations (VT-GDL-016).
     * Verifies that all characters in locations exist in the master character list.
     */
    @Test
    @DisplayName("VT-GDL-016: Verify characters are placed in valid locations")
    void testCharactersPlacedInValidLocations() {
        List<Character> characters = gameData.getCharacters();
        List<Location> locations = gameData.getLocations();

        // Build set of all characters
        Set<String> allCharacterNames = new HashSet<>();
        for (Character ch : characters) {
            allCharacterNames.add(ch.getName());
        }

        // Check each location's character (if any)
        for (Location location : locations) {
            List<Character> chars = location.getCharacters();
            Character locationChar = (chars != null && !chars.isEmpty()) ? chars.get(0) : null;
            if (locationChar != null) {
                assertTrue(allCharacterNames.contains(locationChar.getName()),
                        String.format("Location '%s' has character '%s' that is not in characters list",
                                location.getName(), locationChar.getName()));
            }
        }
    }

    /**
     * Tests that inventory capacity is reasonable (VT-GDL-017).
     * Verifies that inventory capacity is either unlimited or allows adequate
     * gameplay.
     */
    @Test
    @DisplayName("VT-GDL-017: Verify inventory capacity is reasonable")
    void testInventoryCapacityReasonable() {
        Inventory inventory = gameData.getInventory();
        assertNotNull(inventory, "Inventory should exist");

        int capacity = inventory.getCapacity();
        // Capacity -1 means unlimited, which is valid
        // Otherwise it should be at least 3 to allow reasonable gameplay
        assertTrue(capacity == -1 || capacity >= 3,
                String.format("Inventory capacity should be -1 (unlimited) or >= 3, found %d",
                        capacity));
    }
}
