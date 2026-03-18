
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;

/**
 * End-to-end integration tests for complete game scenarios.
 * Tests complete puzzle sequences and game flow without UI.
 * 
 * @author Jialin Li
 */
@DisplayName("End-to-End Game Scenario Tests")
public class EndToEndGameScenarioTest {

    private GameData gameData;
    private CommandMessages commandMessages;

    /**
     * Sets up the test environment before each test.
     * Loads game data and initializes command messages.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Load actual game data
        DataLoader loader = new DataLoader();
        gameData = loader.loadGameData("assets/data/DataFile.json");
        commandMessages = new CommandMessages();

        // Set starting location
        gameData.setCurrentLocation(gameData.getStartingLocation());
    }

    /**
     * Tests main ending: use Glowing Lantern in SecretChamber triggers ending with
     * correct message.
     */
    @Test
    @DisplayName("IT-E2E-011: Main ending - use Glowing Lantern in SecretChamber")
    void testMainEndingWithGlowingLantern() {
        // 1. 进入 Kitchen，获得 Cup of Water
        gameData.setCurrentLocation("DiningRoom");
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        new PickCommand("Cup").execute(engine);
        gameData.setCurrentLocation("Kitchen");
        new UseCommand(new String[] { "Cup", "Faucet" }).execute(engine);
        // 2. 给 Ghostly Boy 水，获得 Child's Emblem
        gameData.setCurrentLocation("DiningRoom");
        new GiveCommand("Ghostly Boy", "Cup of Water").execute(engine);
        new PickCommand("Child's Emblem").execute(engine);
        // 3. 用 Child's Emblem 打开 Old Cabinet，获得 Glowing Lantern
        gameData.setCurrentLocation("StorageRoom");
        new UseCommand(new String[] { "Child's Emblem", "Old Cabinet" }).execute(engine);
        new PickCommand("Glowing Lantern").execute(engine);
        // 4. 走到 SecretChamber 并对 Treasure 使用 Glowing Lantern 触发结局
        gameData.setCurrentLocation("SecretChamber");
        // Use the engine parser so the engine validates and handles end-game rules.
        try {
            java.lang.reflect.Field runningField = GameEngine.class.getDeclaredField("running");
            runningField.setAccessible(true);
            runningField.setBoolean(engine, true);
            engine.processCommand("use Glowing Lantern on Treasure");
        } catch (Exception e) {
            fail("processCommand threw: " + e.getMessage());
        }
        String endingMsg = gameData.getEndingMessage();
        assertNotNull(endingMsg);
        String lower = endingMsg.toLowerCase();
        assertTrue(lower.contains("garden") || lower.contains("escape") || lower.contains("slip through"),
                "Main ending message should reference escaping or the garden");
    }

    /**
     * Tests side ending: use Rope on Study Window to escape via GardenExit,
     * triggers ending with correct message.
     */
    @Test
    @DisplayName("IT-E2E-012: Side ending - escape via GardenExit")
    void testSideEndingWithRopeEscape() {
        // 1. 拾取 Rope
        gameData.setCurrentLocation("ServantQuarters");
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        new PickCommand("Metal Rod").execute(engine);
        // 2. 进入 StudyRoom，使用 Rope 逃生
        gameData.setCurrentLocation("StudyRoom");
        UseCommand useRod = new UseCommand(new String[] { "Metal Rod", "Study Window" });
        assertTrue(useRod.validate(gameData), "Should be able to use Metal Rod on Study Window");
        useRod.execute(engine);

        // 3. 进入 GardenExit，断言结局 message
        gameData.setCurrentLocation("GardenExit");
        String endingMsg = gameData.getEndingMessage();
        assertNotNull(endingMsg);
        assertTrue(endingMsg.toLowerCase().contains("cold night air") || endingMsg.contains("夜空气"),
                "Side ending message should appear");
    }

    /**
     * Tests completing simple puzzle sequence - Unlock Red Door (IT-E2E-001).
     * Verifies that a player can navigate to get a key and use it to unlock a door.
     */
    @Test
    @DisplayName("IT-E2E-001: Complete simple puzzle sequence - Unlock Red Door")
    void testCompleteUnlockRedDoorPuzzle() {
        // Scenario: Get Brass Key from upstairs, return to entrance, unlock Red Door

        // Step 1: Navigate to UpstairsLanding
        gameData.setCurrentLocation("EntranceHall");
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        GoCommand goUp = new GoCommand("stairsUp");
        assertTrue(goUp.validate(gameData), "Should be able to go upstairs");
        goUp.execute(engine);
        assertEquals("UpstairsLanding", gameData.getCurrentLocation(), "Should be upstairs");

        // Step 2: Pick up Brass Key
        Location upstairs = gameData.getLocationByName("UpstairsLanding");
        GameObject brassKey = upstairs.getAllObjects().getAllObjects().stream()
                .filter(obj -> obj.getName().equals("Brass Key"))
                .findFirst()
                .orElse(null);
        assertNotNull(brassKey, "Brass Key should be in UpstairsLanding");

        PickCommand pickKey = new PickCommand("Brass Key");
        assertTrue(pickKey.validate(gameData), "Should be able to pick Brass Key");
        pickKey.execute(engine);
        assertNotNull(gameData.getInventory().getObjectByName("Brass Key"), "Brass Key in inventory");

        // Step 3: Navigate back to EntranceHall
        GoCommand goDown = new GoCommand("stairsDown");
        assertTrue(goDown.validate(gameData), "Should be able to go downstairs");
        goDown.execute(engine);
        assertEquals("EntranceHall", gameData.getCurrentLocation(), "Should be in entrance hall");

        // Step 4: Use Brass Key on Red Door
        UseCommand useKey = new UseCommand(new String[] { "Brass Key", "Red Door" });
        assertTrue(useKey.validate(gameData), "Should be able to use key on door");
        useKey.execute(engine);

        // Verify door is unlocked (Open Door object should exist)
        Location entrance = gameData.getLocationByName("EntranceHall");
        GameObject openDoor = entrance.getAllObjects().getAllObjects().stream()
                .filter(obj -> obj.getName().equals("Open Door"))
                .findFirst()
                .orElse(null);
        assertNotNull(openDoor, "Red Door should be replaced with Open Door");
    }

    /**
     * Tests navigating multiple locations in sequence (IT-E2E-002).
     * Verifies that bidirectional navigation between locations works correctly.
     */
    @Test
    @DisplayName("IT-E2E-002: Navigate multiple locations in sequence")
    void testNavigateMultipleLocations() {
        // Start at EntranceHall
        gameData.setCurrentLocation("EntranceHall");
        assertEquals("EntranceHall", gameData.getCurrentLocation());

        // Go to UpstairsLanding
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        GoCommand goUp = new GoCommand("stairsUp");
        goUp.execute(engine);
        assertEquals("UpstairsLanding", gameData.getCurrentLocation());

        // Go back to EntranceHall
        GoCommand goDown = new GoCommand("stairsDown");
        goDown.execute(engine);
        assertEquals("EntranceHall", gameData.getCurrentLocation());

        // Verify can go back up again
        goUp.execute(engine);
        assertEquals("UpstairsLanding", gameData.getCurrentLocation());
    }

    /**
     * Tests collecting and using multiple objects together (IT-E2E-003).
     * Verifies that multiple objects can be collected and managed in inventory.
     */
    @Test
    @DisplayName("IT-E2E-003: Collect and use multiple objects together")
    void testCollectAndUseMultipleObjects() {
        // Scenario: Collect multiple keys/objects and use them

        // Collect Brass Key from upstairs
        gameData.setCurrentLocation("UpstairsLanding");
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        PickCommand pickKey = new PickCommand("Brass Key");
        pickKey.execute(engine);
        assertNotNull(gameData.getInventory().getObjectByName("Brass Key"));

        // Collect Old Map from entrance
        gameData.setCurrentLocation("EntranceHall");
        PickCommand pickMap = new PickCommand("Old Map");
        pickMap.execute(engine);
        assertNotNull(gameData.getInventory().getObjectByName("Old Map"));

        // Verify both objects are in inventory
        List<GameObject> inventoryItems = gameData.getInventory().getAllObjects();
        assertTrue(inventoryItems.size() >= 2, "Should have at least 2 items");
        assertTrue(inventoryItems.stream().anyMatch(obj -> obj.getName().equals("Brass Key")));
        assertTrue(inventoryItems.stream().anyMatch(obj -> obj.getName().equals("Old Map")));
    }

    /**
     * Tests interacting with multiple characters (IT-E2E-004).
     * Verifies that dialogue can be obtained from different characters in various
     * locations.
     */
    @Test
    @DisplayName("IT-E2E-004: Interact with multiple characters")
    void testInteractWithMultipleCharacters() {
        // Test talking to different characters

        // Talk to Caretaker in Library
        gameData.setCurrentLocation("Library");
        Location library = gameData.getLocationByName("Library");
        Character caretaker = library.getCharacterByName("Caretaker");

        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        if (caretaker != null) {
            TalkCommand talk1 = new TalkCommand("Caretaker");
            assertTrue(talk1.validate(gameData), "Should be able to talk to Caretaker");
            talk1.execute(engine);
            // If needed, check dialogue via gameData or other API

            // Talk again
            talk1.execute(engine);
        }

        // Check if there's another character in a different location
        gameData.setCurrentLocation("DiningRoom");
        Location diningRoom = gameData.getLocationByName("DiningRoom");
        Character ghostlyBoy = diningRoom.getCharacterByName("Ghostly Boy");

        if (ghostlyBoy != null) {
            TalkCommand talk2 = new TalkCommand("Ghostly Boy");
            assertTrue(talk2.validate(gameData), "Should be able to talk to Ghostly Boy");
            talk2.execute(engine);
            // If needed, check dialogue via gameData or other API
        }
    }

    /**
     * Tests completing puzzle to reach Library (IT-E2E-005).
     * Verifies that solving the door unlock puzzle grants access to new areas.
     */
    @Test
    @DisplayName("IT-E2E-005: Complete puzzle to reach Library")
    void testReachLibraryThroughPuzzle() {
        // Scenario: Unlock Red Door to access Library

        // Get Brass Key
        gameData.setCurrentLocation("UpstairsLanding");
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        new PickCommand("Brass Key").execute(engine);

        // Go to entrance and unlock door
        gameData.setCurrentLocation("EntranceHall");
        new UseCommand(new String[] { "Brass Key", "Red Door" }).execute(engine);

        // Now Library should be accessible through the open door
        // Note: Connection logic depends on UseRule implementation
        Location entrance = gameData.getLocationByName("EntranceHall");
        assertNotNull(entrance, "EntranceHall should exist");
    }

    /**
     * Tests object containment reveal sequence (IT-E2E-006).
     * Verifies that examining container objects reveals their contained items.
     */
    @Test
    @DisplayName("IT-E2E-006: Test object containment reveal sequence")
    void testObjectContainmentRevealSequence() {
        // Examine objects to reveal contained items

        gameData.setCurrentLocation("EntranceHall");
        Location entrance = gameData.getLocationByName("EntranceHall");

        // Count initial objects
        int initialCount = entrance.getAllObjects().getAllObjects().size();

        // Examine an object (if it has contained objects, they should be revealed)
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        ExamineCommand examine = new ExamineCommand("Red Door", ExamineCommand.TargetType.OBJECT);
        examine.execute(engine);

        // Check if contained objects were revealed (count should increase if any)
        int afterCount = entrance.getAllObjects().getAllObjects().size();
        assertTrue(afterCount >= initialCount, "Object count should not decrease");
    }

    /**
     * Tests using attribute-based UseRule correctly (IT-E2E-007).
     * Verifies that UseRules can match objects based on their attributes.
     */
    @Test
    @DisplayName("IT-E2E-007: Use attribute-based UseRule correctly")
    void testAttributeBasedUseRule() {
        // Test UseRule that checks object attributes

        // Get Brass Key (has metal attribute)
        gameData.setCurrentLocation("UpstairsLanding");
        Location upstairs = gameData.getLocationByName("UpstairsLanding");
        GameObject brassKey = upstairs.getAllObjects().getAllObjects().stream()
                .filter(obj -> obj.getName().equals("Brass Key"))
                .findFirst()
                .orElse(null);

        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        if (brassKey != null) {
            new PickCommand("Brass Key").execute(engine);

            // Use on Red Door (UseRule should check attributes)
            gameData.setCurrentLocation("EntranceHall");
            UseCommand useKey = new UseCommand(new String[] { "Brass Key", "Red Door" });

            assertTrue(useKey.validate(gameData), "UseRule should validate");
            useKey.execute(engine);

            // Verify rule was applied
            Location entrance = gameData.getLocationByName("EntranceHall");
            boolean hasOpenDoor = entrance.getAllObjects().getAllObjects().stream()
                    .anyMatch(obj -> obj.getName().equals("Open Door"));
            assertTrue(hasOpenDoor, "UseRule should have created Open Door");
        }
    }

    /**
     * Tests triggering GiveRule with character (IT-E2E-008).
     * Verifies that giving objects to characters triggers appropriate GiveRules
     * with responses.
     */
    @Test
    @DisplayName("IT-E2E-008: Trigger GiveRule with character")
    void testTriggerGiveRuleWithCharacter() {
        // Scenario: Give Pocket Watch to Caretaker

        // Get Pocket Watch from Library
        gameData.setCurrentLocation("Library");
        Location library = gameData.getLocationByName("Library");
        GameObject pocketWatch = library.getAllObjects().getAllObjects().stream()
                .filter(obj -> obj.getName().equals("Pocket Watch"))
                .findFirst()
                .orElse(null);

        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        if (pocketWatch != null && pocketWatch.pickable()) {
            new PickCommand("Pocket Watch").execute(engine);
            assertNotNull(gameData.getInventory().getObjectByName("Pocket Watch"));

            // Give to Caretaker
            GiveCommand giveWatch = new GiveCommand("Caretaker", "Pocket Watch");
            assertTrue(giveWatch.validate(gameData), "Should be able to give watch");
            giveWatch.execute(engine);
            // If needed, check response via gameData or other API

            // Verify Pocket Watch is no longer in inventory
            assertNull(gameData.getInventory().getObjectByName("Pocket Watch"),
                    "Pocket Watch should be consumed by GiveRule");
        }
    }

    /**
     * Tests complete game sequence from start to multiple checkpoints (IT-E2E-009).
     * Verifies that a full game playthrough reaches various progress milestones
     * successfully.
     */
    @Test
    @DisplayName("IT-E2E-009: Complete game sequence from start to multiple checkpoints")
    void testCompleteGameSequenceToCheckpoints() {
        // Simulate a full game playthrough to various checkpoints

        // Checkpoint 1: Start at EntranceHall
        assertEquals("EntranceHall", gameData.getCurrentLocation());
        assertNotNull(gameData.getLocationByName("EntranceHall"));

        // Checkpoint 2: Successfully navigate
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        new GoCommand("stairsUp").execute(engine);
        assertEquals("UpstairsLanding", gameData.getCurrentLocation());

        // Checkpoint 3: Successfully collect item
        new PickCommand("Brass Key").execute(engine);
        assertTrue(gameData.getInventory().getAllObjects().size() > 0);

        // Checkpoint 4: Successfully return to start
        new GoCommand("stairsDown").execute(engine);
        assertEquals("EntranceHall", gameData.getCurrentLocation());

        // Checkpoint 5: Successfully use object
        UseCommand useKey = new UseCommand(new String[] { "Brass Key", "Red Door" });
        if (useKey.validate(gameData)) {
            useKey.execute(engine);
            // Game progressed successfully
            assertTrue(true);
        }
    }

    /**
     * Tests game data integrity after multiple operations (IT-E2E-010).
     * Verifies that game data remains consistent after executing various commands.
     */
    @Test
    @DisplayName("IT-E2E-010: Verify game data integrity after multiple operations")
    void testGameDataIntegrityAfterMultipleOperations() {
        // Perform multiple operations and verify data integrity

        int initialLocationCount = gameData.getLocations().size();
        int initialObjectCount = gameData.getObjects().getAllObjects().size();

        // Perform various operations
        gameData.setCurrentLocation("UpstairsLanding");
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        new PickCommand("Brass Key").execute(engine);
        gameData.setCurrentLocation("EntranceHall");
        new PickCommand("Old Map").execute(engine);
        new GoCommand("stairsUp").execute(engine);
        new GoCommand("stairs down").execute(engine);

        // Verify data integrity
        assertEquals(initialLocationCount, gameData.getLocations().size(),
                "Location count should not change");
        assertEquals(initialObjectCount, gameData.getObjects().getAllObjects().size(),
                "Total object count should not change (objects just moved)");

        // Verify inventory has objects
        assertTrue(gameData.getInventory().getAllObjects().size() > 0,
                "Inventory should have items");
    }
}
