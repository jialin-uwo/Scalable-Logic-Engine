import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Integration tests for GameEngine executing commands.
 * Tests the interaction between GameEngine, Commands, and GameData.
 * 
 * @author Jialin Li
 */
@DisplayName("GameEngine and Command Integration Tests")
public class GameEngineCommandIntegrationTest {

    private GameEngine engine;
    private GameData gameData;
    private TestEngineCallback callback;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        // Build simple game data used by these integration tests
        Location entrance = new Location("EntranceHall", "Entrance", "entrance.png", "You are at the entrance.",
                new java.util.ArrayList<>(), new java.util.ArrayList<>());
        Location upstairs = new Location("UpstairsLanding", "Upstairs", "upstairs.png", "You are upstairs.",
                new java.util.ArrayList<>(), new java.util.ArrayList<>());
        Location library = new Location("Library", "Library", "library.png", "You are in the library.",
                new java.util.ArrayList<>(), new java.util.ArrayList<>());

        // Connections between entrance and upstairs
        Connection upConn = new Connection("staircase up", "upstairs", "UpstairsLanding");
        Connection downConn = new Connection("stairs down", "downstairs", "EntranceHall");
        entrance.addConnection(upConn);
        upstairs.addConnection(downConn);

        // Objects
        GameObject brassKey = new GameObject("Brass Key", "A brass key", "brass.png", java.util.Arrays.asList("metal"),
                null, true);
        GameObject redDoor = new GameObject("Red Door", "A locked red door", "reddoor.png",
                java.util.Arrays.asList("locked"), null, false);
        GameObject pocketWatch = new GameObject("Pocket Watch", "An old watch", "watch.png", null, null, true);

        // Place objects
        upstairs.addObject(brassKey);
        entrance.addObject(redDoor);
        library.addObject(pocketWatch);

        // Character in library
        GiveRule watchRule = new GiveRule("givePocketWatchToCaretaker", "Caretaker", "Pocket Watch", "Thanks!",
                java.util.Arrays.asList("Library Key"), false);
        Character caretaker = new Character("Caretaker", "The caretaker", "caretaker.png",
                java.util.Arrays.asList("Welcome, stranger."), watchRule, "No more.", 0);
        library.addCharacter(caretaker);

        // UseRule: Brass Key + Red Door -> Open Door
        UseRule unlockRule = new UseRule(java.util.Arrays.asList("Brass Key", "Red Door"), null,
                java.util.Arrays.asList("Open Door"));

        // Objects map
        java.util.Map<String, GameObject> objMap = new java.util.HashMap<>();
        objMap.put("Brass Key", brassKey);
        objMap.put("Red Door", redDoor);
        objMap.put("Pocket Watch", pocketWatch);
        GameObjectCollection objects = new GameObjectCollection(objMap);

        java.util.List<Location> locations = java.util.Arrays.asList(entrance, upstairs, library);
        java.util.List<Character> characters = java.util.Arrays.asList(caretaker);
        java.util.List<UseRule> useRules = java.util.Arrays.asList(unlockRule);
        java.util.List<GiveRule> giveRules = java.util.Arrays.asList(watchRule);
        java.util.Map<String, String> icons = new java.util.HashMap<>();

        gameData = new GameData("StartMsg", "EntranceHall", "You have escaped!", "GardenExit", "EntranceHall", 50,
                locations, objects, characters, useRules, giveRules, icons);

        // Engine + Test UI that forwards to callback
        engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);

        callback = new TestEngineCallback();

        IGameUI testUi = new IGameUI() {
            @Override
            public void displayCurrentLocation(Location location) {
                if (location != null)
                    callback.updateLocation(location.getName(), location.getDescription(), location.getPicturePath(),
                            location.getMessage());
            }

            @Override
            public void displayMessage(String message) {
                callback.displayMessage(message);
            }

            @Override
            public void showError(String error) {
                callback.displayMessage("[Error] " + error);
            }

            @Override
            public void displayInventory(Inventory inventory) {
                callback.updateInventory(inventory.getAllObjects());
            }

            @Override
            public String getUserCommand() {
                return "";
            }

            @Override
            public void resetRender(GameData data) {
                if (data != null)
                    displayCurrentLocation(data.getLocationByName(data.getStartingLocation()));
            }

            @Override
            public void updateTurnCount(int count) {
                callback.updateTurnCount(count, gameData.getTurnLimit());
            }
        };

        engine.setUi(testUi instanceof GameUI ? (GameUI) testUi : null);
        // If the anonymous IGameUI is not a GameUI instance, call engine.setUi via
        // reflection to avoid GUI creation
        try {
            java.lang.reflect.Method m = engine.getClass().getMethod("setUi", GameUI.class);
            if (m != null) {
                // create a tiny adapter GameUI-like object to satisfy type
                GameUI adapter = new GameUI(new IEngineCallback() {
                    @Override
                    public void onCommand(String rawCommand) {
                    }

                    @Override
                    public void onResetRequested() {
                    }

                    @Override
                    public void onQuitRequested() {
                    }
                }, null) {
                    @Override
                    public void displayCurrentLocation(Location location) {
                        testUi.displayCurrentLocation(location);
                    }

                    @Override
                    public void displayMessage(String message) {
                        testUi.displayMessage(message);
                    }

                    @Override
                    public void showError(String error) {
                        testUi.showError(error);
                    }

                    @Override
                    public void displayInventory(Inventory inventory) {
                        testUi.displayInventory(inventory);
                    }

                    @Override
                    public String getUserCommand() {
                        return testUi.getUserCommand();
                    }

                    @Override
                    public void resetRender(GameData data) {
                        testUi.resetRender(data);
                    }

                    @Override
                    public void updateTurnCount(int count) {
                        testUi.updateTurnCount(count);
                    }
                };
                // Hide the UI window (do not show during tests)
                adapter.setVisible(false);
                m.invoke(engine, adapter);
            }
        } catch (Exception ex) {
            // best-effort: ignore and continue; tests will work with engine.setGameData
        }

        // Ensure the engine is marked as running so tests may call processCommand()
        // directly. startGame() is not safe to call in tests (it may attempt to load
        // data and show dialogs), so set the private 'running' field via
        // reflection.
        try {
            java.lang.reflect.Field runningField = engine.getClass().getDeclaredField("running");
            runningField.setAccessible(true);
            runningField.setBoolean(engine, true);
        } catch (Exception ex) {
            // best-effort: if reflection fails, continue and tests may fail.
        }
    }

    /**
     * Callback implementation to capture engine outputs for testing.
     */
    private static class TestEngineCallback implements IEngineCallback {

        private String lastMessage = "";
        private String lastImagePath = "";
        private int updateCount = 0;

        public String getLastMessage() {
            return lastMessage;
        }

        public String getLastImagePath() {
            return lastImagePath;
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public void reset() {
            lastMessage = "";
            lastImagePath = "";
            updateCount = 0;
        }

        @Override
        public void onCommand(String rawCommand) {
            // No-op for test
        }

        @Override
        public void onResetRequested() {
            // No-op for test
        }

        public void updateLocation(String locationName, String description, String imagePath, String message) {
            this.lastMessage = message;
            this.lastImagePath = imagePath;
            updateCount++;
        }

        public void updateInventory(List<GameObject> objects) {
            updateCount++;
        }

        public void displayMessage(String message) {
            this.lastMessage = message;
            updateCount++;
        }

        public void updateTurnCount(int currentTurn, int turnLimit) {
            updateCount++;
        }

        @Override
        public void onQuitRequested() {
            // No-op for test
        }

    }

    /**
     * Tests executing Drop command through engine (IT-EC-003).
     * Verifies that the engine correctly drops an object from inventory to
     * location.
     */
    @DisplayName("IT-EC-003: Execute Drop command through engine")
    void testExecuteDropCommandThroughEngine() {
        // Setup: Pick an object first
        gameData.setCurrentLocation("UpstairsLanding");
        Location location = gameData.getLocationByName("UpstairsLanding");
        GameObject brassKey = location.getObjectByName("Brass Key");
        gameData.addObjectToInventory(brassKey);
        location.removeObject("Brass Key");

        callback.reset();
        engine.processCommand("drop Brass Key");

        // Verify object is back in location
        GameObject inLocation = location.getObjectByName("Brass Key");
        assertNotNull(inLocation, "After dropping, the Brass Key should appear in the current location.");

        // Verify object removed from inventory
        GameObject inInventory = gameData.getInventory().getObjectByName("Brass Key");
        assertNull(inInventory, "After dropping, the Brass Key should no longer be in the inventory.");

        // Verify callback was invoked
        assertTrue(callback.getUpdateCount() > 0, "The callback should be triggered after executing the drop command.");
    }

    /**
     * Tests executing Examine command through engine (IT-EC-004).
     * Verifies that the engine correctly examines an object and displays its
     * description.
     */
    @DisplayName("IT-EC-004: Execute Examine command through engine")
    void testExecuteExamineCommandThroughEngine() {
        // Setup: Use a location with an object that has contained objects
        gameData.setCurrentLocation("EntranceHall");
        callback.reset();
        engine.processCommand("examine Red Door");

        // Verify callback displayed description
        assertFalse(callback.getLastMessage().isEmpty(), "Examining an object should display its description message.");
    }

    /**
     * Verifies that the engine correctly applies a UseRule when using objects
     * together.
     */
    @Test
    @DisplayName("IT-EC-005: Execute Use command through engine")
    // Duplicate methods removed; all logic is inside TestEngineCallback class
    // above.
    void testExecuteUseCommandThroughEngine() {
        // Setup: Get Brass Key and go to location with Red Door
        gameData.setCurrentLocation("UpstairsLanding");
        Location upstairs = gameData.getLocationByName("UpstairsLanding");
        GameObject brassKey = upstairs.getObjectByName("Brass Key");
        gameData.addObjectToInventory(brassKey);
        upstairs.removeObject("Brass Key");

        gameData.setCurrentLocation("EntranceHall");
        Location entranceHall = gameData.getLocationByName("EntranceHall");

        // Execute use command (Brass Key on Red Door)
        callback.reset();
        engine.processCommand("use Brass Key on Red Door");

        // Verify callback was invoked (UseRule should be applied)
        assertTrue(callback.getUpdateCount() > 0, "The callback should be triggered after using objects together.");
    }

    /**
     * Tests executing Talk command through engine (IT-EC-006).
     * Verifies that the engine correctly handles character dialogue.
     */
    @Test
    @DisplayName("IT-EC-006: Execute Talk command through engine")
    void testExecuteTalkCommandThroughEngine() {
        // Setup: Go to Library where Caretaker is
        gameData.setCurrentLocation("Library");

        callback.reset();
        engine.processCommand("talk Caretaker");

        // Verify callback displayed dialogue
        assertTrue(callback.getUpdateCount() > 0, "Talking to a character should trigger the callback.");
        assertFalse(callback.getLastMessage().isEmpty(), "Talking to a character should display dialogue text.");
    }

    /**
     * Tests executing Give command through engine (IT-EC-007).
     * Verifies that the engine correctly triggers GiveRules when giving objects to
     * characters.
     */
    @Test
    @DisplayName("IT-EC-007: Execute Give command through engine")
    void testExecuteGiveCommandThroughEngine() {
        // Setup: Get Pocket Watch and go to Library with Caretaker
        gameData.setCurrentLocation("Library");
        Location library = gameData.getLocationByName("Library");
        GameObject pocketWatch = library.getObjectByName("Pocket Watch");
        gameData.addObjectToInventory(pocketWatch);
        library.removeObject("Pocket Watch");

        callback.reset();
        engine.processCommand("give Pocket Watch Caretaker");

        // Verify callback was invoked (GiveRule should be triggered)
        assertTrue(callback.getUpdateCount() > 0, "Giving an object to a character should trigger the callback.");
    }

    /**
     * Tests that turn counter increments after each command (IT-EC-008).
     * Verifies that the engine correctly tracks turn count for each executed
     * command.
     */
    @Test
    @DisplayName("IT-EC-008: Turn counter increments after each command")
    void testTurnCounterIncrementsAfterCommand() {
        int initialTurn = gameData.getTurnLimit();

        // Execute several commands
        engine.processCommand("go staircase up");
        assertEquals(initialTurn - 1, gameData.getTurnLimit(),
                "After the first command, the turn count should decrease by one.");

        engine.processCommand("go stairs down");
        assertEquals(initialTurn - 2, gameData.getTurnLimit(),
                "After the second command, the turn count should decrease by one again.");

        engine.processCommand("examine Red Door");
        assertEquals(initialTurn - 3, gameData.getTurnLimit(),
                "After the third command, the turn count should decrease by one again.");
    }

    /**
     * Tests that invalid command does not change game state (IT-EC-009).
     * Verifies that unrecognized commands do not modify location or turn count.
     */
    @Test
    @DisplayName("IT-EC-009: Invalid command does not change game state")
    void testInvalidCommandDoesNotChangeState() {
        String initialLocation = gameData.getCurrentLocation();
        int initialTurn = gameData.getTurnLimit();

        // Execute invalid command
        callback.reset();
        engine.processCommand("fly north");

        // Verify state unchanged
        assertEquals(initialLocation, gameData.getCurrentLocation(),
                "An invalid command should not change the player's location.");
        assertEquals(initialTurn, gameData.getTurnLimit(), "An invalid command should not decrement the turn count.");
    }

    /**
     * Tests that command validation prevents invalid execution (IT-EC-010).
     * Verifies that commands fail validation and don't execute when prerequisites
     * aren't met.
     */
    @Test
    @DisplayName("IT-EC-010: Command validation prevents invalid execution")
    void testCommandValidationPreventsInvalidExecution() {
        // Try to pick object not in location
        gameData.setCurrentLocation("EntranceHall");
        Location location = gameData.getLocationByName("EntranceHall");
        int objectCountBefore = location.getAllObjects().size();
        int inventoryCountBefore = gameData.getInventory().getAllObjects().size();

        callback.reset();
        engine.processCommand("pick NonExistentObject");

        // Verify nothing changed
        assertEquals(objectCountBefore, location.getAllObjects().size(),
                "If picking a nonexistent object, the location's objects should remain unchanged.");
        assertEquals(inventoryCountBefore, gameData.getInventory().getAllObjects().size(),
                "If picking a nonexistent object, the inventory should remain unchanged.");
    }

    /**
     * Tests that multiple commands in sequence work correctly (IT-EC-011).
     * Verifies that a complex sequence of navigation, picking, and using commands
     * executes properly.
     */
    @Test
    @DisplayName("IT-EC-011: Multiple commands in sequence work correctly")
    void testMultipleCommandsInSequence() {
        int initialTurn = gameData.getTurnLimit();
        // Navigate to UpstairsLanding
        engine.processCommand("go staircase up");
        assertEquals("UpstairsLanding", gameData.getCurrentLocation(),
                "After going upstairs, the player should be in UpstairsLanding.");

        // Pick Brass Key
        engine.processCommand("pick Brass Key");
        assertNotNull(gameData.getInventory().getObjectByName("Brass Key"),
                "After picking up, the Brass Key should be in the inventory.");

        // Navigate back to EntranceHall
        engine.processCommand("go stairs down");
        assertEquals("EntranceHall", gameData.getCurrentLocation(),
                "After going back downstairs, the player should be in EntranceHall.");

        // Use Brass Key on Red Door
        engine.processCommand("use Brass Key on Red Door");

        // All commands should execute successfully
        assertTrue(gameData.getTurnLimit() <= (initialTurn - 4),
                "After a sequence of commands, the turn count should reflect all actions taken.");
    }

    /**
     * Tests that engine callback is invoked for each command (IT-EC-012).
     * Verifies that the callback receives notifications for every command
     * execution.
     */
    @Test
    @DisplayName("IT-EC-012: Engine callback invoked for each command")
    void testEngineCallbackInvokedForEachCommand() {
        callback.reset();

        engine.processCommand("examine Red Door");
        int count1 = callback.getUpdateCount();
        assertTrue(count1 > 0, "The first command should trigger the callback.");

        engine.processCommand("examine Old Map");
        int count2 = callback.getUpdateCount();
        assertTrue(count2 > count1, "The second command should trigger the callback and increase the count.");

        engine.processCommand("go staircase up");
        int count3 = callback.getUpdateCount();
        assertTrue(count3 > count2, "The third command should trigger the callback and further increase the count.");
    }
}
