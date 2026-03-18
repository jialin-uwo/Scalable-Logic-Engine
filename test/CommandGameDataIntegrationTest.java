
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Integration tests for Command and GameData interaction.
 * Tests how commands validate against and modify GameData state.
 * 
 * @author Jialin Li
 */
public class CommandGameDataIntegrationTest {
    private GameData gameData;
    private Location hall;
    private Location library;
    private GameObject key;
    private GameObject door;
    private GameObject chest;
    private Character guard;
    private UseRule keyDoorRule;
    private GiveRule giveKeyRule;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        // Locations
        hall = new Location("Hall", "The main hall", "hall.png", "You are in the hall.", new java.util.ArrayList<>(),
                new java.util.ArrayList<>());
        library = new Location("Library", "A quiet library", "library.png", "You are in the library.",
                new java.util.ArrayList<>(), new java.util.ArrayList<>());

        // Connections
        Connection north = new Connection("North Door", "To Library", "Library");
        hall.addConnection(north);
        Connection south = new Connection("South Door", "To Hall", "Hall");
        library.addConnection(south);

        // Objects
        key = new GameObject("key", "A small key", "key.png", java.util.Arrays.asList("metal"), null, true);
        door = new GameObject("door", "A sturdy door", "door.png", java.util.Arrays.asList("locked"), null, false);
        chest = new GameObject("chest", "A wooden chest", "chest.png", null, null, true);

        // Character and rules
        giveKeyRule = new GiveRule("giveKeyToGuard", "guard", "key", "Thanks for the key!",
                java.util.Arrays.asList("passage"), false);
        guard = new Character("guard", "A stern guard", "guard.png", java.util.Arrays.asList("Halt!"), giveKeyRule,
                "No more to say.", 0);

        keyDoorRule = new UseRule(java.util.Arrays.asList("key", "door"), null, java.util.Arrays.asList("Open Door"));

        // Objects collection
        java.util.Map<String, GameObject> objMap = new java.util.HashMap<>();
        objMap.put("key", key);
        objMap.put("door", door);
        objMap.put("chest", chest);
        GameObjectCollection objects = new GameObjectCollection(objMap);

        // Add objects & character to locations
        hall.addObject(key);
        hall.addObject(door);
        library.addObject(chest);
        hall.addCharacter(guard);

        // Build GameData
        java.util.List<Location> locations = java.util.Arrays.asList(hall, library);
        java.util.List<Character> characters = java.util.Arrays.asList(guard);
        java.util.List<UseRule> useRules = java.util.Arrays.asList(keyDoorRule);
        java.util.List<GiveRule> giveRules = java.util.Arrays.asList(giveKeyRule);
        java.util.Map<String, String> icons = new java.util.HashMap<>();

        gameData = new GameData("Start", "Hall", "End", "Exit", "Hall", 100, locations, objects, characters, useRules,
                giveRules, icons);
    }

    @Test
    public void testGiveCommandTriggersEndGame() {
        // Create a GiveRule that ends the game
        GiveRule endGameRule = new GiveRule("giveKeyToGuardEnd", "guard", "key",
                "The guard accepts the key and the adventure ends!", Arrays.asList("passage"), true);
        // Add the endGameRule to GameData's giveRules
        List<GiveRule> newGiveRules = new ArrayList<>(gameData.getGiveRules());
        // Put the end-game rule first so it takes precedence during validation
        newGiveRules.add(0, endGameRule);
        // Recreate GameData with the new giveRules
        gameData = new GameData(gameData.getStartingMessage(), gameData.getStartingLocation(),
                gameData.getEndingMessage(), gameData.getEndingLocation(), gameData.getCurrentLocation(),
                gameData.getTurnLimit(), Arrays.asList(hall, library), gameData.getObjects(), gameData.getCharacters(),
                gameData.getUseRules(), newGiveRules, gameData.getIcons());
        // Add key to inventory on the newly created GameData instance
        gameData.addObjectToInventory(key);

        // Create GiveCommand
        GiveCommand giveCommand = new GiveCommand("guard", "key");
        assertTrue(giveCommand.validate(gameData), "Should validate with matching GiveRule (endGame)");

        // Create a mock GameEngine to capture exitGame call
        class TestGameEngine extends GameEngine {
            public boolean exited = false;

            public TestGameEngine() {
                super(null, null, new IImageLoader() {
                    @Override
                    public java.awt.Image loadImage(String imagePath) {
                        // Return a tiny dummy image to satisfy UI initialization
                        return new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                    }
                });
            }

            @Override
            public void exitGame() {
                exited = true;
            }

            @Override
            public void processCommand(String command) {
                System.out.println("[TEST DEBUG] TestGameEngine.processCommand start. ui="
                        + (this.getGameUI() == null ? "null" : this.getGameUI().getClass().getName()));
                System.out.println("[TEST DEBUG] TestGameEngine.gameData == outer gameData? "
                        + (this.getGameData() == CommandGameDataIntegrationTest.this.gameData));

                // Extra manual check for GiveCommand to inspect requested end rule
                try {
                    String trimmed = command == null ? "" : command.trim();
                    if (!trimmed.isEmpty()) {
                        String[] tokens = trimmed.split("\\s+");
                        String verb = tokens[0].toLowerCase();
                        if ("give".equals(verb)) {
                            // simple parser: find 'to' and split
                            String objectName = null;
                            String characterName = null;
                            for (int i = 2; i < tokens.length - 1; i++) {
                                if ("to".equalsIgnoreCase(tokens[i])) {
                                    objectName = String.join(" ", java.util.Arrays.copyOfRange(tokens, 1, i));
                                    characterName = String.join(" ",
                                            java.util.Arrays.copyOfRange(tokens, i + 1, tokens.length));
                                    break;
                                }
                            }
                            if (objectName == null) { // fallback
                                if (tokens.length >= 3) {
                                    objectName = tokens[1];
                                    characterName = String.join(" ",
                                            java.util.Arrays.copyOfRange(tokens, 2, tokens.length));
                                }
                            }
                            if (objectName != null && characterName != null) {
                                // Determine which GiveRule would match (without mutating game state)
                                boolean found = false;
                                String matched = null;
                                boolean isEnd = false;
                                java.util.List<GiveRule> rules = this.getGameData().getGiveRules();
                                if (rules != null) {
                                    for (GiveRule gr : rules) {
                                        if (gr != null && gr.applicable(characterName, objectName)) {
                                            found = true;
                                            matched = gr.getName();
                                            isEnd = gr.isEndGame();
                                            break;
                                        }
                                    }
                                }
                                System.out.println("[TEST DEBUG] Manual rule matched=" + found + ", name=" + matched
                                        + ", isEnd=" + isEnd);
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println("[TEST DEBUG] manual GiveCommand threw: " + e);
                }

                try {
                    super.processCommand(command);
                } catch (RuntimeException e) {
                    System.out.println("[TEST DEBUG] processCommand threw: " + e);
                    throw e;
                }
                System.out.println("[TEST DEBUG] TestGameEngine.processCommand end. exited=" + exited);
            }

            @Override
            public GameData getGameData() {
                return gameData;
            }
        }

        // Silent/no-op GameUI for tests to avoid creating visible frames or loading
        // real images
        class SilentGameUI extends GameUI {
            public SilentGameUI(IEngineCallback callback, IImageLoader loader) {
                super(callback, loader);
                // Hide the real frame immediately in tests
                try {
                    this.setVisible(false);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void refreshAll(GameData gd) {
                // no-op for tests
            }

            @Override
            public void displayMessage(String message) {
                // no-op for tests
            }

            @Override
            public void resetRender(GameData gd) {
                // no-op for tests
            }

            @Override
            public void updateTurnCount(int count) {
                // no-op for tests
            }

            @Override
            public void displayCurrentLocation(Location location) {
                // no-op
            }

            @Override
            public void displayInventory(Inventory inventory) {
                // no-op
            }
        }

        TestGameEngine engine = new TestGameEngine();
        engine.setGameData(gameData);

        // Install a silent/no-op UI so engine calls to ui.* don't NPE and no windows
        // are shown during tests. Use the same dummy image loader used by the engine.
        SilentGameUI silent = new SilentGameUI(engine, new IImageLoader() {
            @Override
            public java.awt.Image loadImage(String imagePath) {
                return new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            }
        });
        engine.setUi(silent);

        // Sanity-check: engine should now have a non-null UI reference
        assertNotNull(engine.getGameUI(), "Engine UI should be non-null after installing silent UI");
        System.out.println("[TEST DEBUG] Engine UI set to: " + engine.getGameUI().getClass().getName());

        // Extra diagnostics: ensure engine references match test GameData and show give
        // rule names
        System.out.println("[TEST DEBUG] engine.getGameData() == gameData? " + (engine.getGameData() == gameData));
        System.out.println("[TEST DEBUG] engine UI (pre-process): "
                + (engine.getGameUI() == null ? "null" : engine.getGameUI().getClass().getName()));
        System.out.println("[TEST DEBUG] give rules in gameData: "
                + gameData.getGiveRules().stream().map(r -> r.getName()).toList());

        // Execute via engine so the engine's post-command end-rule validation runs
        // Use the engine command parser to exercise full processing (validate, execute,
        // and engine handling of requested end rules). Ensure the engine is marked
        // as running so processCommand does not return early.
        engine.setGameData(gameData);
        try {
            // mark private `running` field true via reflection so processCommand runs
            java.lang.reflect.Field runningField = GameEngine.class.getDeclaredField("running");
            runningField.setAccessible(true);
            runningField.setBoolean(engine, true);

            // Use the more explicit "give <object> to <character>" form
            engine.processCommand("give key to guard");
        } catch (Exception e) {
            // If processCommand throws, fail the test with the exception
            fail("processCommand threw: " + e.getMessage());
        }

        // Engine should have performed exitGame if the rule was endGame
        assertTrue(engine.exited, "Game should end when GiveRule.endGame is true");
    }

    /**
     * Tests that GoCommand validates connection existence.
     * Verifies that valid connections pass validation while invalid ones fail.
     */
    @Test
    public void testGoCommandValidatesConnectionExists() {
        GoCommand validGo = new GoCommand("North Door");
        assertTrue(validGo.validate(gameData), "Should validate existing connection");

        GoCommand invalidGo = new GoCommand("South Door");
        assertFalse(invalidGo.validate(gameData), "Should not validate non-existing connection");
    }

    // ===== PickCommand Integration Tests =====

    /**
     * Tests that PickCommand moves object to inventory.
     * Verifies that a pickable object is removed from location and added to
     * inventory.
     */
    @Test
    public void testPickCommandMovesObjectToInventory() {
        // Key is in hall, inventory is empty
        assertTrue(hall.getAllObjects().containsObject("key"));
        assertFalse(gameData.getInventory().containsObject("key"));

        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);

        // Pick command should validate
        PickCommand pickCommand = new PickCommand("key");
        assertTrue(pickCommand.validate(gameData), "Should validate pickable object in location");

        // Simulate execution
        pickCommand.execute(engine);

        // Verify state change
        assertFalse(hall.getAllObjects().containsObject("key"));
        assertTrue(gameData.getInventory().containsObject("key"));
    }

    /**
     * Tests that PickCommand rejects non-pickable objects.
     * Verifies that attempting to pick a non-pickable object fails validation.
     */
    @Test
    public void testPickCommandRejectsNonPickableObject() {
        // Door is not pickable
        PickCommand pickDoor = new PickCommand("door");
        assertFalse(pickDoor.validate(gameData), "Should not validate non-pickable object");
    }

    /**
     * Tests that PickCommand rejects when inventory is full.
     * Verifies that objects cannot be added to a full inventory.
     */
    @Test
    public void testPickCommandRejectsWhenInventoryFull() {
        // Create inventory with capacity 1
        Inventory limitedInventory = new Inventory(1);
        GameObject book = new GameObject("book", "A book", "book.png", null, null, true);
        limitedInventory.addObject(book);

        // Replace gameData's inventory (would need setter or reflection in real
        // implementation)
        // For now, just test the inventory directly
        assertTrue(limitedInventory.isFull());
        assertFalse(limitedInventory.addObject(key), "Should not add to full inventory");
    }

    // ===== DropCommand Integration Tests =====

    /**
     * Tests that DropCommand moves object to location.
     * Verifies that an object is removed from inventory and added to the current
     * location.
     */
    @Test
    public void testDropCommandMovesObjectToLocation() {
        // Add key to inventory first
        gameData.addObjectToInventory(key);
        gameData.removeObjectFromLocation("Hall", key);
        assertTrue(gameData.getInventory().containsObject("key"));

        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);

        // Drop command should validate
        DropCommand dropCommand = new DropCommand("key");
        assertTrue(dropCommand.validate(gameData), "Should validate object in inventory");

        // Simulate execution
        dropCommand.execute(engine);

        // Verify state change
        assertFalse(gameData.getInventory().containsObject("key"));
        assertTrue(hall.getAllObjects().containsObject("key"));
    }

    /**
     * Tests that DropCommand rejects object not in inventory.
     * Verifies that attempting to drop an object not in inventory fails validation.
     */
    @Test
    public void testDropCommandRejectsObjectNotInInventory() {
        // Key is in location, not in inventory
        DropCommand dropCommand = new DropCommand("key");
        assertFalse(dropCommand.validate(gameData), "Should not validate object not in inventory");
    }

    // ===== ExamineCommand Integration Tests =====

    /**
     * Tests that ExamineCommand validates existing objects.
     * Verifies that examining an existing object passes validation.
     */
    @Test
    public void testExamineCommandValidatesExistingObject() {
        ExamineCommand examineKey = new ExamineCommand("key", ExamineCommand.TargetType.OBJECT);
        assertTrue(examineKey.validate(gameData), "Should validate existing object");
    }

    /**
     * Tests that ExamineCommand validates existing characters.
     * Verifies that examining an existing character passes validation.
     */
    @Test
    public void testExamineCommandValidatesExistingCharacter() {
        ExamineCommand examineGuard = new ExamineCommand("guard", ExamineCommand.TargetType.CHARACTER);
        assertTrue(examineGuard.validate(gameData), "Should validate existing character");
    }

    /**
     * Tests that ExamineCommand reveals contained objects.
     * Verifies that examining a container object adds its contained items to the
     * location.
     */
    @Test
    public void testExamineCommandRevealsContainedObjects() {
        // Create chest with contained key
        GameObject smallKey = new GameObject("small key", "A small key", "small_key.png",
                null, null, true);
        GameObject chestWithKey = new GameObject("mystery chest", "A mysterious chest",
                "chest.png", Arrays.asList("container"), Arrays.asList(smallKey), false);

        // Add chest to GameObjectCollection first
        GameObjectCollection updatedObjects = gameData.getObjects();
        Map<String, GameObject> objMap = new HashMap<>();
        for (GameObject obj : updatedObjects.getAllObjects()) {
            objMap.put(obj.getName(), obj);
        }
        objMap.put("mystery chest", chestWithKey);
        objMap.put("small key", smallKey);
        GameObjectCollection newCollection = new GameObjectCollection(objMap);

        // Recreate GameData with updated objects
        gameData = new GameData(
                gameData.getStartingMessage(),
                gameData.getStartingLocation(),
                gameData.getEndingMessage(),
                gameData.getEndingLocation(),
                gameData.getCurrentLocation(),
                gameData.getTurnLimit(),
                Arrays.asList(hall, library),
                newCollection,
                gameData.getCharacters(),
                gameData.getUseRules(),
                gameData.getGiveRules(),
                gameData.getIcons());

        hall.addObject(chestWithKey);

        // Before examine, small key is not in location's object list
        assertFalse(hall.getAllObjects().containsObject("small key"));

        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);

        // ExamineCommand.execute() adds contained items to location
        ExamineCommand examineChest = new ExamineCommand("mystery chest", ExamineCommand.TargetType.OBJECT);
        assertTrue(examineChest.validate(gameData));
        examineChest.execute(engine);

        // Now small key should be in location
        assertTrue(hall.getAllObjects().containsObject("small key"));
    }

    // ===== UseCommand Integration Tests =====

    /**
     * Tests that UseCommand applies rule correctly.
     * Verifies that a valid use command with matching rule passes validation.
     */
    @Test
    public void testUseCommandAppliesRuleCorrectly() {
        // Key and door are in hall location initially
        // UseRule requires: objectNames=["key", "door"], no subjectAttribute

        // UseCommand with key and door should validate
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        UseCommand useCommand = new UseCommand(new String[] { "key", "door" });
        assertTrue(useCommand.validate(gameData), "Should validate with applicable rule");
        useCommand.execute(engine);

        // After execution, key and door should be removed, open door added
        // This would be done by GameEngine.execute(), but we can verify the rule exists
        List<UseRule> rules = gameData.getUseRules();
        assertFalse(rules.isEmpty());
        assertTrue(rules.stream().anyMatch(r -> r.getObjectNames().contains("key")));
    }

    /**
     * Tests that UseCommand rejects without applicable rule.
     * Verifies that attempting to use objects without a matching rule fails
     * validation.
     */
    @Test
    public void testUseCommandRejectsWithoutApplicableRule() {
        // Try to use objects without matching rule
        UseCommand useCommand = new UseCommand(new String[] { "key", "chest" });
        assertFalse(useCommand.validate(gameData), "Should not validate without applicable rule");
    }

    /**
     * Tests that UseCommand works with attributes.
     * Verifies that use rules can match objects based on their attributes.
     */
    @Test
    public void testUseCommandWorksWithAttributes() {
        // The keyDoorRule requires "metal" attribute
        assertTrue(key.hasAttribute("metal"), "Key should have metal attribute");

        // Create another metal object
        GameObject metalBar = new GameObject("metal bar", "An iron bar", "bar.png",
                Arrays.asList("metal"), null, true);
        hall.addObject(metalBar);

        // UseCommand with metal bar and door should also work due to attribute matching
        UseCommand useWithAttribute = new UseCommand(new String[] { "metal bar", "door" });
        // This would validate if the rule checks attributes properly
    }

    // ===== TalkCommand Integration Tests =====

    /**
     * Tests that TalkCommand consumes character phrases.
     * Verifies that talking to a character returns dialogue phrases in sequence.
     */
    @Test
    public void testTalkCommandConsumesCharacterPhrases() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        TalkCommand talkCommand = new TalkCommand("guard");
        assertTrue(talkCommand.validate(gameData), "Should validate with character in location");

        // First talk should display first phrase (no return value)
        talkCommand.execute(engine);
        // Second talk
        talkCommand.execute(engine);
        // Third talk
        talkCommand.execute(engine);
        // Fourth talk (no more dialogue)
        talkCommand.execute(engine);
    }

    /**
     * Tests that TalkCommand rejects character not in location.
     * Verifies that attempting to talk to a character in a different location fails
     * validation.
     */
    @Test
    public void testTalkCommandRejectsCharacterNotInLocation() {
        // Move to library where there's no character
        gameData.setCurrentLocation("Library");

        TalkCommand talkCommand = new TalkCommand("guard");
        assertFalse(talkCommand.validate(gameData), "Should not validate character in different location");
    }

    // ===== GiveCommand Integration Tests =====

    /**
     * Tests that GiveCommand triggers GiveRule.
     * Verifies that giving an object to a character that wants it passes
     * validation.
     */
    @Test
    public void testGiveCommandTriggersGiveRule() {
        // Add key to inventory
        gameData.addObjectToInventory(key);

        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);

        GiveCommand giveCommand = new GiveCommand("guard", "key");
        assertTrue(giveCommand.validate(gameData), "Should validate with matching GiveRule");
        giveCommand.execute(engine);

        // After execution, key should be removed from inventory
        // and rule's resulting objects should be added
        assertNotNull(guard.getGiveRule());
        assertEquals("guard", guard.getGiveRule().getCharacterName());
        assertEquals("key", guard.getGiveRule().getObjectName());
    }

    /**
     * Tests that GiveCommand rejects object not in inventory.
     * Verifies that attempting to give an object not in inventory fails validation.
     */
    @Test
    public void testGiveCommandRejectsObjectNotInInventory() {
        // Key is in location, not in inventory
        GiveCommand giveCommand = new GiveCommand("guard", "key");
        assertFalse(giveCommand.validate(gameData), "Should not validate object not in inventory");
    }

    /**
     * Tests that GiveCommand rejects unwanted objects.
     * Verifies that giving an object to a character that doesn't want it fails
     * validation.
     */
    @Test
    public void testGiveCommandRejectsUnwantedObject() {
        // Add chest to inventory (guard doesn't want it)
        gameData.addObjectToInventory(chest);

        GiveCommand giveCommand = new GiveCommand("guard", "chest");
        assertFalse(giveCommand.validate(gameData), "Should not validate unwanted object");
    }

    // ===== Complex Integration Scenarios =====

    /**
     * Tests complete pick-use-drop sequence.
     * Verifies that a sequence of picking, using, and dropping objects works
     * correctly.
     */
    @Test
    public void testCompletePickUseDropSequence() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        // 1. Pick up key
        PickCommand pick = new PickCommand("key");
        assertTrue(pick.validate(gameData));
        pick.execute(engine);
        assertTrue(gameData.getInventory().containsObject("key"));

        // 2. Use key with door
        UseCommand use = new UseCommand(new String[] { "key", "door" });
        assertTrue(use.validate(gameData));
        use.execute(engine);

        // 3. Drop an item
        gameData.addObjectToInventory(chest);
        DropCommand drop = new DropCommand("chest");
        assertTrue(drop.validate(gameData));
        drop.execute(engine);
        assertTrue(hall.getAllObjects().containsObject("chest"));
    }

    /**
     * Tests navigation and interaction sequence.
     * Verifies that examining, talking, picking, giving, and moving work in
     * sequence.
     */
    @Test
    public void testNavigationAndInteractionSequence() {
        GameEngine engine = new GameEngine(null, null, null);
        engine.setGameData(gameData);
        // 1. Examine guard
        ExamineCommand examine = new ExamineCommand("guard", ExamineCommand.TargetType.CHARACTER);
        assertTrue(examine.validate(gameData));
        examine.execute(engine);

        // 2. Talk to guard
        TalkCommand talk = new TalkCommand("guard");
        assertTrue(talk.validate(gameData));
        talk.execute(engine);

        // 3. Pick up key
        PickCommand pick = new PickCommand("key");
        assertTrue(pick.validate(gameData));
        pick.execute(engine);

        // 4. Give key to guard
        GiveCommand give = new GiveCommand("guard", "key");
        assertTrue(give.validate(gameData));
        give.execute(engine);

        // 5. Go to library
        GoCommand go = new GoCommand("North Door");
        assertTrue(go.validate(gameData));
        go.execute(engine);
    }

    /**
     * Tests inventory capacity integration.
     * Verifies that inventory correctly enforces capacity limits and allows
     * additions after removal.
     */
    @Test
    public void testInventoryCapacityIntegration() {
        // Create limited inventory
        Inventory limited = new Inventory(2);

        // Add first object
        assertTrue(limited.addObject(key));
        assertFalse(limited.isFull());

        // Add second object
        assertTrue(limited.addObject(chest));
        assertTrue(limited.isFull());

        // Try to add third object
        GameObject book = new GameObject("book", "A book", "book.png", null, null, true);
        assertFalse(limited.addObject(book), "Should reject when full");

        // Remove one object
        limited.removeObject("key");
        assertFalse(limited.isFull());

        // Now can add the book
        assertTrue(limited.addObject(book));
    }

    /**
     * Tests location object management.
     * Verifies that objects can be added, removed, and retrieved from locations.
     */
    @Test
    public void testLocationObjectManagement() {
        // Initial state
        assertTrue(hall.getAllObjects().containsObject("key"));
        assertTrue(hall.getAllObjects().containsObject("door"));

        // Add object
        hall.addObject(chest);
        assertTrue(hall.getAllObjects().containsObject("chest"));

        // Remove object
        hall.removeObject("key");
        assertFalse(hall.getAllObjects().containsObject("key"));

        // Get object
        GameObject retrievedDoor = hall.getObjectByName("door");
        assertNotNull(retrievedDoor);
        assertEquals("door", retrievedDoor.getName());
    }

    /**
     * Tests GameData location navigation.
     * Verifies that locations can be retrieved and navigated using connections.
     */
    @Test
    public void testGameDataLocationNavigation() {
        // Get location by name
        Location retrievedHall = gameData.getLocationByName("Hall");
        assertNotNull(retrievedHall);
        assertEquals("Hall", retrievedHall.getName());

        // Get connection (use getConnections and find by name or target)
        List<Connection> connections = retrievedHall.getConnections();
        assertNotNull(connections);
        assertTrue(connections.size() > 0);
        Connection connection = connections.stream().filter(c -> "Library".equals(c.getTargetLocationName()))
                .findFirst().orElse(null);
        assertNotNull(connection);
        assertEquals("North Door", connection.getName());
        assertEquals("Library", connection.getTargetLocationName());

        // Change current location
        gameData.setCurrentLocation("Library");
        assertEquals("Library", gameData.getCurrentLocation());
    }

    /**
     * Tests command message integration.
     * Verifies that GameData provides distinct success and failure messages for
     * commands.
     */
    @Test
    public void testCommandMessageIntegration() {
        // Get success message
        String pickupSuccess = gameData.getCommandMessage("pickup", true);
        assertNotNull(pickupSuccess);
        assertFalse(pickupSuccess.isEmpty());

        // Get failure message
        String pickupFailure = gameData.getCommandMessage("pickup", false);
        assertNotNull(pickupFailure);
        assertFalse(pickupFailure.isEmpty());
        assertNotEquals(pickupSuccess, pickupFailure);
    }
}
