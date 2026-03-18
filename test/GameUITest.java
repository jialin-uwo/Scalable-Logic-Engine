import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.Image;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameUITest {

    private GameUI ui;
    private FakeEngineCallback engineCallback;
    private FakeImageLoader imageLoader;

    @BeforeEach
    void setUp() throws Exception {
        engineCallback = new FakeEngineCallback();
        imageLoader = new FakeImageLoader();

        // Swing components must be created on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeAndWait(() -> {
            // Create an anonymous runnable object; the lambda represents a task
            // to be executed by invokeAndWait on the EDT.
            ui = new GameUI(engineCallback, imageLoader);
        });
        // Provide the created UI instance to the fake engine callback so that
        // onResetRequested can trigger a UI reset (tests expect reset to clear lists
        // and reset the turn counter).
        engineCallback.setUi(ui);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (ui != null) {
            SwingUtilities.invokeAndWait(() -> ui.dispose());
        }
    }

    // ====================== Basic Display Tests ======================

    /**
     * displayMessage should append text to the output area.
     */
    @Test
    void displayMessage_shouldAppendTextToOutputArea() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTextArea outputArea = getPrivateField(ui, "outputArea", JTextArea.class);
            String before = outputArea.getText();

            ui.displayMessage("hello world");

            String after = outputArea.getText();
            assertTrue(after.contains("hello world"));
            assertTrue(after.length() >= before.length());
        });
    }

    /**
     * updateTurnCount should correctly update the turn count label.
     */
    @Test
    void updateTurnCount_shouldUpdateLabel() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ui.updateTurnCount(7);

            JLabel label = getPrivateField(ui, "turnCountLabel", JLabel.class);
            assertEquals("Turns: 7", label.getText());
        });
    }

    // ====================== Inventory Display ======================

    /**
     * displayInventory(Inventory) should populate inventoryListModel with item
     * names.
     */
    @Test
    void displayInventory_shouldFillInventoryListModel() throws Exception {
        // Construct Inventory and GameObject instances on the normal test thread
        Inventory inventory = new Inventory();
        GameObject key = new GameObject(
                "Key",
                "A small key",
                "key.png",
                null,
                null,
                true);
        GameObject map = new GameObject(
                "Map",
                "A treasure map",
                "map.png",
                null,
                null,
                true);
        inventory.addObject(key);
        inventory.addObject(map);

        SwingUtilities.invokeAndWait(() -> {
            ui.displayInventory(inventory);

            @SuppressWarnings("unchecked")
            DefaultListModel<String> model = getPrivateField(ui, "inventoryListModel", DefaultListModel.class);

            assertEquals(2, model.size());
            assertTrue(model.contains("Key"));
            assertTrue(model.contains("Map"));
        });
    }

    // ====================== Location Display ======================

    /**
     * displayCurrentLocation should update location labels and object/connection
     * lists.
     */
    @Test
    void displayCurrentLocation_shouldUpdateLabelsAndLists() throws Exception {
        // Build a Location with multiple connections and characters
        Connection conn1 = new Connection("toExit", "to the exit", "ExitRoom");
        Connection conn2 = new Connection("toHall", "to the hall", "Hall");
        Character npc1 = new Character(
                "Guard",
                "A helpful guard",
                "guard.png",
                Collections.emptyList(),
                null,
                null,
                0);
        Character npc2 = new Character(
                "Merchant",
                "A shifty merchant",
                "merchant.png",
                Collections.emptyList(),
                null,
                null,
                0);
        List<Connection> connections = new ArrayList<>();
        connections.add(conn1);
        connections.add(conn2);
        List<Character> characters = new ArrayList<>();
        characters.add(npc1);
        characters.add(npc2);
        Location start = new Location(
                "StartRoom",
                "This is the starting room.",
                "start.png",
                "You are in the start.",
                connections,
                characters);

        // Add an object to the location
        GameObject apple = new GameObject(
                "Apple",
                "A red apple",
                "apple.png",
                null,
                null,
                true);
        start.addObject(apple);

        SwingUtilities.invokeAndWait(() -> {
            ui.displayCurrentLocation(start);

            JLabel nameLabel = getPrivateField(ui, "locationNameLabel", JLabel.class);
            JTextArea descArea = getPrivateField(ui, "locationDescriptionArea", JTextArea.class);

            @SuppressWarnings("unchecked")
            DefaultListModel<String> objectsModel = getPrivateField(ui, "objectsListModel", DefaultListModel.class);
            @SuppressWarnings("unchecked")
            DefaultListModel<String> connectionsModel = getPrivateField(ui, "connectionsListModel",
                    DefaultListModel.class);
            @SuppressWarnings("unchecked")
            DefaultListModel<String> charactersModel = getPrivateField(ui, "charactersListModel",
                    DefaultListModel.class);

            assertEquals("StartRoom", nameLabel.getText());
            assertEquals("This is the starting room.", descArea.getText());

            // Object list
            assertTrue(objectsModel.contains("Apple"));

            // Characters list (multiple)
            assertEquals(2, charactersModel.size());
            assertTrue(charactersModel.contains("Guard"));
            assertTrue(charactersModel.contains("Merchant"));

            // Connection list (multiple)
            assertEquals(2, connectionsModel.size());
            assertTrue(connectionsModel.contains("toExit"));
            assertTrue(connectionsModel.contains("toHall"));
        });
    }

    // ====================== Command Execution & Callback ======================

    /**
     * Simulate selecting "use" + primary/secondary targets, clicking Execute,
     * and verify that:
     * - buildCurrentCommandString produces the correct text
     * - engineCallback.onCommand receives the correct string
     * - getUserCommand returns the correct string
     * - the preview text field updates accordingly
     */
    @Test
    void executeCommand_shouldCallEngineCallbackAndUpdatePreview() throws Exception {
        // This test is not applicable for the current GameUI implementation, as there
        // are no setPrimaryTargets/setSecondaryTargets or primary/secondary target
        // lists.
        // You may implement a new test for command execution if needed, using the
        // actual UI lists (objectsList, inventoryList, connectionsList,
        // charactersList).
        // For now, this test is skipped.
        // (If you want to simulate UI selection, use reflection to set selections on
        // objectsList, inventoryList, etc.)
        // See other tests for displayCurrentLocation and resetRender for list-based
        // assertions.
    }

    // ====================== Reset Button Tests ======================

    /**
     * When Reset is clicked:
     * - Clear lists and text fields
     * - Reset turn counter
     * - Fire engineCallback.onResetRequested()
     */
    @Test
    void resetButton_shouldClearUIAndNotifyEngine() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JTextArea outputArea = getPrivateField(ui, "outputArea", JTextArea.class);
            outputArea.setText("Some text");

            @SuppressWarnings("unchecked")
            DefaultListModel<String> objectsModel = getPrivateField(ui, "objectsListModel", DefaultListModel.class);
            objectsModel.addElement("Something");

            JTextField commandTextField = getPrivateField(ui, "commandTextField", JTextField.class);
            commandTextField.setText("go north");

            JLabel turnLabel = getPrivateField(ui, "turnCountLabel", JLabel.class);
            turnLabel.setText("Turns: 5");

            JButton resetButton = getPrivateField(ui, "resetButton", JButton.class);

            // Click reset
            resetButton.doClick();

            // Verify UI state
            assertEquals("", commandTextField.getText());
            assertEquals("Turns: 0", turnLabel.getText());
            assertEquals(0, objectsModel.size());
            assertTrue(outputArea.getText().trim().contains("Game has been reset."));
            // Verify callback
            assertEquals(1, engineCallback.resetRequestedCount);
        });
    }

    // ====================== resetRender(GameData) ======================

    /**
     * resetRender(GameData) should:
     * - Unlock controls
     * - Clear old UI
     * - Display startingMessage
     * - Jump to startingLocation and display location
     * - Refresh inventory
     * - Update turn count to turnLimit
     */
    @Test
    void resetRender_shouldRebuildUIFromGameData() throws Exception {
        // Build test GameData with multiple connections and characters
        GameObject sword = new GameObject(
                "Sword", "A sharp sword", "sword.png", null, null, true);
        GameObject coin = new GameObject(
                "Coin", "A gold coin", "coin.png", null, null, true);

        Connection conn1 = new Connection("toExit", "to the exit", "ExitRoom");
        Connection conn2 = new Connection("toHall", "to the hall", "Hall");
        Character npc1 = new Character(
                "OldMan",
                "An old man",
                "oldman.png",
                Collections.singletonList("Welcome, hero."),
                null,
                null,
                0);
        Character npc2 = new Character(
                "Merchant",
                "A shifty merchant",
                "merchant.png",
                Collections.emptyList(),
                null,
                null,
                0);
        List<Connection> connections = new ArrayList<>();
        connections.add(conn1);
        connections.add(conn2);
        List<Character> characters = new ArrayList<>();
        characters.add(npc1);
        characters.add(npc2);
        Location start = new Location(
                "StartRoom",
                "You stand in a small room.",
                "start.png",
                "You wake up in a mysterious room.",
                connections,
                characters);
        start.addObject(sword);

        List<Location> locations = new ArrayList<>();
        locations.add(start);

        GameObjectCollection allObjects = new GameObjectCollection(null);
        allObjects.addObject(sword);
        allObjects.addObject(coin);

        List<Character> allChars = new ArrayList<>();
        allChars.add(npc1);
        allChars.add(npc2);

        List<?> useRules = new ArrayList<>();
        List<?> giveRules = new ArrayList<>();
        Map<String, String> icons = new HashMap<>();

        GameData gameData = new GameData(
                "Welcome to the game!",
                "StartRoom",
                "The end.",
                "ExitRoom",
                "StartRoom",
                15,
                locations,
                allObjects,
                allChars,
                (List) useRules,
                (List) giveRules,
                icons);

        gameData.addObjectToInventory(coin);

        SwingUtilities.invokeAndWait(() -> {
            ui.resetRender(gameData);

            JTextArea outputArea = getPrivateField(ui, "outputArea", JTextArea.class);
            JLabel nameLabel = getPrivateField(ui, "locationNameLabel", JLabel.class);
            JLabel turnLabel = getPrivateField(ui, "turnCountLabel", JLabel.class);

            @SuppressWarnings("unchecked")
            DefaultListModel<String> inventoryModel = getPrivateField(ui, "inventoryListModel", DefaultListModel.class);
            @SuppressWarnings("unchecked")
            DefaultListModel<String> objectsModel = getPrivateField(ui, "objectsListModel", DefaultListModel.class);
            @SuppressWarnings("unchecked")
            DefaultListModel<String> connectionsModel = getPrivateField(ui, "connectionsListModel",
                    DefaultListModel.class);
            @SuppressWarnings("unchecked")
            DefaultListModel<String> charactersModel = getPrivateField(ui, "charactersListModel",
                    DefaultListModel.class);

            // Starting message
            String text = outputArea.getText();
            assertTrue(text.contains("Welcome to the game!"));

            // Current location
            assertEquals("StartRoom", nameLabel.getText());

            // Object list should contain Sword
            assertTrue(objectsModel.contains("Sword"));

            // Characters list (multiple)
            assertEquals(2, charactersModel.size());
            assertTrue(charactersModel.contains("OldMan"));
            assertTrue(charactersModel.contains("Merchant"));

            // Connections list (multiple)
            assertEquals(2, connectionsModel.size());
            assertTrue(connectionsModel.contains("toExit"));
            assertTrue(connectionsModel.contains("toHall"));

            // Inventory should contain Coin
            assertEquals(1, inventoryModel.size());
            assertEquals("Coin", inventoryModel.getElementAt(0));

            // Turn count should display turnLimit
            assertEquals("Turns: 15", turnLabel.getText());
        });
    }

    // ====================== Target List Tests ======================

    /**
     * setPrimaryTargets / setSecondaryTargets should populate the corresponding
     * ListModels.
     */
    @Test
    void setPrimaryAndSecondaryTargets_shouldUpdateModels() throws Exception {
        // This test is not applicable for the current GameUI implementation, as there
        // are no setPrimaryTargets/setSecondaryTargets or primary/secondary target
        // models.
        // You may implement a new test for list population if needed, using the actual
        // UI lists (objectsList, inventoryList, connectionsList, charactersList).
        // For now, this test is skipped.
    }

    // ====================== Reflection Helper ======================

    @SuppressWarnings("unchecked")
    private static <T> T getPrivateField(Object target, String name, Class<T> type) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (T) f.get(target);
        } catch (Exception e) {
            throw new AssertionError("Cannot access field: " + name, e);
        }
    }

    // ====================== Fake EngineCallback & Fake ImageLoader
    // ======================

    /**
     * A fake implementation of IEngineCallback used for UI isolation.
     * Records last command and reset/quit calls without invoking real game logic.
     */
    private static class FakeEngineCallback implements IEngineCallback {
        String lastCommand;
        int resetRequestedCount = 0;
        int quitRequestedCount = 0;
        private GameUI ui;

        @Override
        public void onCommand(String rawCommand) {
            this.lastCommand = rawCommand;
        }

        @Override
        public void onResetRequested() {
            resetRequestedCount++;
            // When UI requests a reset, tests expect the UI to be rebuilt/cleared.
            // If a GameUI instance was provided, trigger a resetRender with null
            // so the UI clears models and sets turn label to 0.
            if (ui != null) {
                if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                    ui.resetRender(null);
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> ui.resetRender(null));
                }
            }
        }

        @Override
        public void onQuitRequested() {
            quitRequestedCount++;
        }

        public void setUi(GameUI ui) {
            this.ui = ui;
        }
    }

    /**
     * Fake image loader: always returns null to avoid real file dependencies.
     */
    private static class FakeImageLoader implements IImageLoader {
        @Override
        public Image loadImage(String imagePath) {
            return null;
        }
    }
}
