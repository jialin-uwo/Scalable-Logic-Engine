/**
 * GameUI.java
 *
 * This class provides a Swing-based implementation of the {@link IGameUI}
 * interface to render and control the visual user interface for the adventure game.
 * It displays the current location, inventory, available commands, output messages,
 * and handles user interactions such as executing commands, resetting the game,
 * and quitting.
 *
 * @author Zhixian Wang
 */

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.IOException;
import java.awt.Graphics;
import java.util.Map;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 * The {@code GameUI} class is a concrete implementation of {@link IGameUI}
 * using Java Swing. It manages all UI components (labels, lists, buttons,
 * text areas, etc.), displays the current game state, and forwards user
 * commands back to the engine via an {@link IEngineCallback}.
 */
public class GameUI extends JFrame implements IGameUI {

    // Top: location information + turn count
    private JLabel locationNameLabel;
    private JTextArea locationDescriptionArea;
    private JLabel turnCountLabel;

    // Middle-left: location image
    private JLabel locationImageLabel;

    // Lower middle-left: objects and connections in the current location
    private JList<String> objectsList;
    private DefaultListModel<String> objectsListModel; // items on map
    private JList<String> connectionsList;
    private DefaultListModel<String> connectionsListModel; // connections
    private JList<String> charactersList;
    private DefaultListModel<String> charactersListModel; // characters

    // Upper-right: inventory
    private JList<String> inventoryList;
    private DefaultListModel<String> inventoryListModel;

    // Middle-right: command selection area (buttons)
    private JButton goButton;
    private JButton pickButton;
    private JButton dropButton;
    private JButton examineButton;
    private JButton useButton;
    private JButton talkButton;
    private JButton giveButton;
    private JButton executeCommandButton;

    // Currently selected verb (set when a command button is pressed)
    private String selectedVerb = "";

    // Lower-right: output area + control buttons
    private JTextArea outputArea;
    private JButton resetButton;
    private JButton quitButton;
    private JButton clearCommandButton;

    // Command preview text field
    private JTextField commandTextField;
    private String lastBuiltCommand = "";

    Location currentLocation;
    GameData gameData; // Store game data for command building

    // Engine callback + image loader
    private final IEngineCallback engineCallback;
    private final IImageLoader imageLoader;

    // Shared fonts for UI components (so multiple methods can access them)
    private final Font headerFont = new Font("SansSerif", Font.BOLD, 18); // larger bold header
    private final Font titleFont = new Font("SansSerif", Font.BOLD, 14); // bold for titled borders/labels
    private final Font bodyFont = new Font("SansSerif", Font.PLAIN, 14);
    private final Font smallFont = new Font("SansSerif", Font.PLAIN, 12);
    private final Font smallBoldFont = new Font("SansSerif", Font.BOLD, 12);
    private final Font dialogFont = new Font("SansSerif", Font.PLAIN, 16); // New font for dialog messages

    // Map of command icons loaded from GameData (keyed by command name)
    private final Map<String, ImageIcon> commandIconMap = new HashMap<>();

    /**
     * Constructs a new {@code GameUI} instance and initializes the main window.
     *
     * @param engineCallback the callback interface used to communicate user actions
     *                       back to the game engine
     * @param imageLoader    the image loader used to load images for locations and
     *                       objects
     */
    public GameUI(IEngineCallback engineCallback, IImageLoader imageLoader) {
        this.engineCallback = engineCallback;
        this.imageLoader = imageLoader;

        initComponents();
        layoutComponents();

        setTitle("The House That Remembers");
        setPreferredSize(new Dimension(1000, 700)); // larger window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    /* ====================== Component initialization ====================== */

    /**
     * Initializes all Swing components used in the game UI.
     * This includes labels, lists, buttons, text areas, and other controls,
     * but does not lay them out on the frame.
     */
    private void initComponents() {
        // Top
        locationNameLabel = new JLabel("Location Name");
        // Use shared fonts declared at class level
        locationNameLabel.setFont(headerFont);

        locationDescriptionArea = new JTextArea(4, 40); // multiple lines text
        locationDescriptionArea.setEditable(false);
        locationDescriptionArea.setLineWrap(true); // automatically newline
        locationDescriptionArea.setWrapStyleWord(true); // newline before/after a whole word.
        locationDescriptionArea.setFont(bodyFont);

        turnCountLabel = new JLabel("Turns: 0");
        turnCountLabel.setFont(smallFont);

        // Middle: image
        locationImageLabel = new AspectImageLabel();
        locationImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        locationImageLabel.setPreferredSize(new Dimension(480, 360)); // 4:3 aspect ratio area
        locationImageLabel.setMinimumSize(new Dimension(240, 180));
        locationImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Objects and connections in the current location
        objectsListModel = new DefaultListModel<>(); // data container
        objectsList = new JList<>(objectsListModel); // dynamically refresh UI from DefaultListModel
        javax.swing.border.TitledBorder tbObjects = BorderFactory.createTitledBorder("Objects in Location");
        tbObjects.setTitleFont(titleFont);
        objectsList.setBorder(tbObjects);
        objectsList.setFont(bodyFont);

        connectionsListModel = new DefaultListModel<>();
        connectionsList = new JList<>(connectionsListModel);
        javax.swing.border.TitledBorder tbConns = BorderFactory.createTitledBorder("Connections");
        tbConns.setTitleFont(titleFont);
        connectionsList.setBorder(tbConns);
        connectionsList.setFont(bodyFont);

        // Characters in the current location
        charactersListModel = new DefaultListModel<>();
        charactersList = new JList<>(charactersListModel);
        javax.swing.border.TitledBorder tbChars = BorderFactory.createTitledBorder("Characters");
        tbChars.setTitleFont(titleFont);
        charactersList.setBorder(tbChars);
        charactersList.setFont(bodyFont);

        // Inventory
        inventoryListModel = new DefaultListModel<>();
        inventoryList = new JList<>(inventoryListModel);
        javax.swing.border.TitledBorder tbInv = BorderFactory.createTitledBorder("Inventory");
        tbInv.setTitleFont(titleFont);
        inventoryList.setBorder(tbInv);
        inventoryList.setFont(bodyFont);

        // Command selection: create individual buttons like original UI
        goButton = new JButton("Go");
        pickButton = new JButton("Pick");
        dropButton = new JButton("Drop");
        examineButton = new JButton("Examine");
        useButton = new JButton("Use");
        talkButton = new JButton("Talk");
        giveButton = new JButton("Give");

        // Set fonts for command buttons
        goButton.setFont(smallBoldFont);
        pickButton.setFont(smallBoldFont);
        dropButton.setFont(smallBoldFont);
        examineButton.setFont(smallBoldFont);
        useButton.setFont(smallBoldFont);
        talkButton.setFont(smallBoldFont);
        giveButton.setFont(smallBoldFont);

        // Execute button (runs the currently selected built command)
        executeCommandButton = new JButton("Execute");
        executeCommandButton.setFont(smallBoldFont);

        // Wire command buttons to update the preview/selected verb
        goButton.addActionListener(e -> onCommandButton("go"));
        pickButton.addActionListener(e -> onCommandButton("pick"));
        dropButton.addActionListener(e -> onCommandButton("drop"));
        examineButton.addActionListener(e -> onCommandButton("examine"));
        useButton.addActionListener(e -> onCommandButton("use"));
        talkButton.addActionListener(e -> onCommandButton("talk"));
        giveButton.addActionListener(e -> onCommandButton("give"));

        // Output area
        outputArea = new JTextArea(8, 30);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(bodyFont);

        // Reset/quit buttons
        resetButton = new JButton("Reset Game");
        quitButton = new JButton("Quit");
        clearCommandButton = new JButton("Clear");

        // Optional text-based command display
        commandTextField = new JTextField();
        javax.swing.border.TitledBorder tbPrompt = BorderFactory.createTitledBorder("prompt area");
        tbPrompt.setTitleFont(titleFont);
        commandTextField.setBorder(tbPrompt);
        commandTextField.setEditable(false);
        commandTextField.setFocusable(false);
        commandTextField.setFont(bodyFont);

        // Event listeners
        executeCommandButton.addActionListener(e -> onExecuteCommand());
        resetButton.addActionListener(e -> onResetGame());
        clearCommandButton.addActionListener(e -> clearCommandSelection());
        quitButton.addActionListener(e -> onQuit());
        // note: command buttons call updateCommandPreview via onCommandButton()

        objectsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Avoid repeated triggers while dragging; only fire once when selection settles
                updateCommandPreview();
            }
        });

        inventoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCommandPreview();
            }
        });

        connectionsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCommandPreview();
            }
        });

        charactersList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCommandPreview();
            }
        });
    }

    /* ====================== Layout ====================== */

    /**
     * Lays out all UI components on the main frame using layout managers.
     * This method arranges panels, split panes, and other components but
     * assumes that {@link #initComponents()} has already been called.
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(8, 8));

        // Top: location info + turn count
        JPanel topPanel = new JPanel(new BorderLayout(4, 4));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

        topPanel.add(locationNameLabel, BorderLayout.NORTH);
        topPanel.add(new JScrollPane(locationDescriptionArea), BorderLayout.CENTER);

        JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        turnPanel.add(turnCountLabel);
        topPanel.add(turnPanel, BorderLayout.SOUTH);

        this.add(topPanel, BorderLayout.NORTH);

        // Middle-left: image + object/connection lists
        JPanel leftCenterPanel = new JPanel(new BorderLayout(4, 4));
        leftCenterPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 4));

        leftCenterPanel.add(locationImageLabel, BorderLayout.CENTER);

        JPanel listsPanel = new JPanel(new GridLayout(1, 3, 4, 4));
        listsPanel.add(new JScrollPane(objectsList));
        listsPanel.add(new JScrollPane(charactersList));
        listsPanel.add(new JScrollPane(connectionsList));
        leftCenterPanel.add(listsPanel, BorderLayout.SOUTH);

        // Middle-right: inventory + commands + output
        JPanel rightCenterPanel = new JPanel();
        rightCenterPanel.setLayout(new BorderLayout(4, 4));
        rightCenterPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 8));

        // Upper part: inventory + command area
        JPanel topRightPanel = new JPanel(new BorderLayout(4, 4));

        // Inventory
        JScrollPane inventoryScroll = new JScrollPane(inventoryList);
        inventoryScroll.setPreferredSize(new Dimension(200, 120));
        topRightPanel.add(inventoryScroll, BorderLayout.NORTH);

        // Command area
        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout(4, 4));
        javax.swing.border.TitledBorder tbCommand = BorderFactory.createTitledBorder("Command");
        tbCommand.setTitleFont(titleFont);
        commandPanel.setBorder(tbCommand);

        JPanel commandTop = new JPanel(new BorderLayout(4, 4));
        javax.swing.JLabel cmdLabel = new JLabel("Command:");
        cmdLabel.setFont(titleFont);
        commandTop.add(cmdLabel, BorderLayout.WEST);

        // Create a vertically stacked panel with two FlowLayout rows so buttons
        // size to their preferred widths instead of forcing equal widths.
        JPanel cmdButtonsPanel = new JPanel();
        cmdButtonsPanel.setLayout(new BoxLayout(cmdButtonsPanel, BoxLayout.Y_AXIS));

        JPanel cmdRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        cmdRow1.add(goButton);
        cmdRow1.add(pickButton);
        cmdRow1.add(dropButton);
        cmdRow1.add(examineButton);

        JPanel cmdRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        cmdRow2.add(useButton);
        cmdRow2.add(talkButton);
        cmdRow2.add(giveButton);

        cmdButtonsPanel.add(cmdRow1);
        cmdButtonsPanel.add(cmdRow2);

        commandPanel.add(cmdButtonsPanel, BorderLayout.NORTH);

        // (clear/execute will be placed next to the prompt area in the bottom-right)

        topRightPanel.add(commandPanel, BorderLayout.CENTER);

        rightCenterPanel.add(topRightPanel, BorderLayout.NORTH);

        // Lower-right: output + control buttons + command text display
        JPanel bottomRightPanel = new JPanel(new BorderLayout(4, 4));
        javax.swing.border.TitledBorder tbOutput = BorderFactory.createTitledBorder("Output");
        tbOutput.setTitleFont(titleFont);
        bottomRightPanel.setBorder(tbOutput);

        // Prompt area with vertical Execute/Clear buttons on the right
        JPanel promptRow = new JPanel(new BorderLayout(4, 4));
        promptRow.add(commandTextField, BorderLayout.CENTER);

        JPanel rightButtons = new JPanel();
        rightButtons.setLayout(new BoxLayout(rightButtons, BoxLayout.Y_AXIS));
        // Make Execute and Clear equal width: compute max preferred width and apply
        Dimension execPref = executeCommandButton.getPreferredSize();
        Dimension clearPref = clearCommandButton.getPreferredSize();
        int maxW = Math.max(execPref.width, clearPref.width);
        executeCommandButton.setPreferredSize(new Dimension(maxW, execPref.height));
        clearCommandButton.setPreferredSize(new Dimension(maxW, clearPref.height));
        executeCommandButton.setMaximumSize(new Dimension(maxW, Math.max(execPref.height, clearPref.height)));
        clearCommandButton.setMaximumSize(new Dimension(maxW, Math.max(execPref.height, clearPref.height)));
        executeCommandButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearCommandButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightButtons.add(executeCommandButton);
        rightButtons.add(Box.createVerticalStrut(6));
        rightButtons.add(clearCommandButton);

        // Place the Execute/Clear vertical stack to the LEFT of the prompt field
        promptRow.add(rightButtons, BorderLayout.WEST);

        bottomRightPanel.add(promptRow, BorderLayout.NORTH);
        bottomRightPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Set fonts for control buttons
        resetButton.setFont(smallBoldFont);
        quitButton.setFont(smallBoldFont);
        clearCommandButton.setFont(smallBoldFont);
        // commandComboBox removed; buttons use smallFont already

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(resetButton);
        controlPanel.add(quitButton);
        bottomRightPanel.add(controlPanel, BorderLayout.SOUTH);

        rightCenterPanel.add(bottomRightPanel, BorderLayout.CENTER);

        // Overall middle: split left/right
        JSplitPane centerSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftCenterPanel,
                rightCenterPanel);
        centerSplit.setResizeWeight(0.6); // Left side takes ~60%
        this.add(centerSplit, BorderLayout.CENTER);
    }

    /* ====================== IGameUI implementation ====================== */

    /**
     * Displays the inventory items in the inventory list UI component.
     *
     * @param inventory the current inventory to display; if {@code null}, the list
     *                  is cleared
     */
    @Override
    public void displayInventory(Inventory inventory) {
        inventoryListModel.clear();

        if (inventory == null) {
            return;
        }

        List<GameObject> objects = inventory.getAllObjects();
        if (objects != null) {
            for (IGameObject obj : objects) {
                inventoryListModel.addElement(obj.getName());
            }
        }
    }

    /**
     * Displays information for the current location, including its name,
     * description, image, objects, characters, and connections.
     *
     * @param location the location to display; if {@code null}, no changes are made
     */
    @Override
    public void displayCurrentLocation(Location location) {
        if (location == null) {
            return;
        }

        locationNameLabel.setText(location.getName());
        locationDescriptionArea.setText(location.getDescription());

        // Update image
        try {
            String picturePath = location.getPicturePath();
            if (picturePath != null && !picturePath.isEmpty()) {
                Image img = imageLoader.loadImage(picturePath);
                if (img != null) {
                    ((AspectImageLabel) locationImageLabel).setImage(img);
                    locationImageLabel.setText(null);
                }
            }
        } catch (IOException e) {
            // Show fallback when image loading fails
            ((AspectImageLabel) locationImageLabel).setImage(null);
            locationImageLabel.setText("Image not available");
        }

        // Update map objects list
        objectsListModel.clear();
        for (GameObject obj : location.getAllObjects().getAllObjects()) {
            objectsListModel.addElement(obj.getName());
        }

        // Update characters list
        charactersListModel.clear();
        java.util.List<Character> chars = location.getCharacters();
        if (chars != null && !chars.isEmpty()) {
            for (Character c : chars) {
                if (c != null && c.getName() != null) {
                    charactersListModel.addElement(c.getName());
                }
            }
        }

        // Update connections list
        connectionsListModel.clear();
        java.util.List<Connection> conns = location.getConnections();
        if (conns != null && !conns.isEmpty()) {
            for (Connection c : conns) {
                if (c != null && c.getName() != null) {
                    connectionsListModel.addElement(c.getName());
                }
            }
        }
    }

    /**
     * Displays a general text message (e.g., command result) in the output area.
     *
     * @param message the message to display; ignored if {@code null} or empty
     */
    @Override
    public void displayMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        // Helper method to append text to the output area
        appendOutput(message);
    }

    /**
     * Displays an error message: both as a dialog and as a line in the output area.
     *
     * @param error the error message; ignored if {@code null} or empty
     */
    @Override
    public void showError(String error) {
        if (error == null || error.isEmpty()) {
            return;
        }

        // Pop up an error dialog (original simple style)
        JOptionPane.showMessageDialog(
                this,
                error,
                "Error",
                JOptionPane.ERROR_MESSAGE);

        // Also record a line in the output area
        appendOutput("[Error] " + error);
    }

    /**
     * Returns the last built command string.
     *
     * @return the last command string built and executed by the UI
     */
    @Override
    public String getUserCommand() {
        return lastBuiltCommand;
    }

    /**
     * Resets the entire UI display state using the provided {@link GameData}.
     * Reloads the starting message, starting location, command icons, and clears
     * any previous output or selections.
     *
     * @param gameData the latest snapshot of the game state to render
     */
    @Override
    public void resetRender(GameData gameData) {
        // Store game data reference
        this.gameData = gameData;

        // Load command icons (if available) so buttons show icons matching GameData
        loadCommandIcons(gameData);

        // First unlock all controls (start a new round)
        executeCommandButton.setEnabled(true);
        // enable command buttons
        goButton.setEnabled(true);
        pickButton.setEnabled(true);
        dropButton.setEnabled(true);
        examineButton.setEnabled(true);
        useButton.setEnabled(true);
        talkButton.setEnabled(true);
        giveButton.setEnabled(true);

        objectsList.setEnabled(true);
        connectionsList.setEnabled(true);
        charactersList.setEnabled(true);
        inventoryList.setEnabled(true);

        // Clear UI
        outputArea.setText("");
        objectsListModel.clear();
        connectionsListModel.clear();
        charactersListModel.clear();
        inventoryListModel.clear();
        turnCountLabel.setText("Turns: 0");

        // Starting message
        if (gameData == null) {
            outputArea.append("Game data is null, failed to start game.\n");
            return;
        }

        String startingMessage = gameData.getStartingMessage();
        if (startingMessage != null && !startingMessage.isEmpty()) {
            outputArea.append(startingMessage + "\n\n");
        }

        // Find starting Location by startingLocation name, then call
        // displayCurrentLocation
        String startingLocationName = gameData.getStartingLocation();
        Location startingLocation = null;

        for (Location loc : gameData.getLocations()) {
            if (loc.getName().equalsIgnoreCase(startingLocationName)) {
                startingLocation = loc;
                break;
            }
        }

        // Record current location and call display method
        this.currentLocation = startingLocation;
        displayCurrentLocation(startingLocation);
        refreshAll(gameData);
    }

    /**
     * Updates the turn count display label.
     *
     * @param count the remaining number of turns to display
     */
    @Override
    public void updateTurnCount(int count) {
        turnCountLabel.setText("Turns: " + count);
    }

    /**
     * Builds a user-friendly command string representing the current
     * selected verb and selected targets (objects, characters, connections).
     *
     * @return the textual representation of the currently selected command and
     *         its targets, or an empty string if no verb is selected
     */
    private String buildCurrentCommandString() {
        StringBuilder sb = new StringBuilder();

        // Use the verb set by the command buttons (selectedVerb)
        String command = (selectedVerb != null) ? selectedVerb.trim().toLowerCase() : "";
        if (command.isEmpty()) {
            return "";
        }
        sb.append(command);

        // Get selections from the main lists
        List<String> objectSelections = objectsList.getSelectedValuesList();
        List<String> inventorySelections = inventoryList.getSelectedValuesList();
        List<String> connectionSelections = connectionsList.getSelectedValuesList();
        List<String> characterSelections = charactersList.getSelectedValuesList();

        boolean isTalk = "talk".equalsIgnoreCase(command);
        boolean isGive = "give".equalsIgnoreCase(command);
        boolean isUse = "use".equalsIgnoreCase(command);

        List<String> primaries = new java.util.ArrayList<>();

        // For give/use commands: combine objects and inventory selections
        if (isGive || isUse) {
            if (objectSelections != null)
                primaries.addAll(objectSelections);
            if (inventorySelections != null)
                primaries.addAll(inventorySelections);
            if (characterSelections != null)
                primaries.addAll(characterSelections);
        } else {
            // For other commands: use first non-empty list
            if (characterSelections != null && !characterSelections.isEmpty()) {
                primaries = characterSelections;
            } else if (objectSelections != null && !objectSelections.isEmpty()) {
                primaries = objectSelections;
            } else if (inventorySelections != null && !inventorySelections.isEmpty()) {
                primaries = inventorySelections;
            } else if (connectionSelections != null && !connectionSelections.isEmpty()) {
                primaries = connectionSelections;
            }
        }

        // ===== Special command handling =====

        // talk <character>
        if (isTalk) {
            if (characterSelections != null && !characterSelections.isEmpty()) {
                sb.append(" ").append(characterSelections.get(0));
            } else if (primaries != null && !primaries.isEmpty()) {
                sb.append(" ").append(primaries.get(0));
            }
            return sb.toString().trim();
        }

        // give <character> <item>
        if (isGive) {
            String characterName = null;
            String itemName = null;

            if (primaries != null && !primaries.isEmpty()) {
                if (primaries.size() >= 2) {
                    // Two items selected: determine which is character and which is item
                    String first = primaries.get(0);
                    String second = primaries.get(1);

                    // Check if first is a character
                    if (gameData != null && gameData.getCharacterByName(first) != null) {
                        characterName = first;
                        itemName = second;
                    } else if (gameData != null && gameData.getCharacterByName(second) != null) {
                        // Reversed order: item first, character second
                        characterName = second;
                        itemName = first;
                    } else {
                        // Default: assume first is character, second is item
                        characterName = first;
                        itemName = second;
                    }
                } else {
                    // Only one selected
                    characterName = primaries.get(0);
                }
            }

            if (characterName != null) {
                sb.append(" ").append(characterName);
            }
            if (itemName != null) {
                sb.append(" ").append(itemName);
            }

            return sb.toString().trim();
        }

        // use A with B
        if (isUse) {
            if (primaries != null && !primaries.isEmpty()) {
                sb.append(" ").append(primaries.get(0));
                if (primaries.size() >= 2) {
                    // Two items selected: use first with second
                    sb.append(" with ").append(primaries.get(1));
                }
            }
            return sb.toString().trim();
        }

        // ===== Other common commands (go / pick / drop / examine ...) =====

        // Default: first primary is main target
        if (primaries != null && !primaries.isEmpty()) {
            sb.append(" ").append(primaries.get(0));
        }

        return sb.toString().trim();
    }

    /**
     * Rebuilds the current command string and updates the command preview field.
     * Also caches the built command into {@code lastBuiltCommand}.
     */
    private void updateCommandPreview() {
        String cmd = this.buildCurrentCommandString();

        // Show current built command in the command text field
        commandTextField.setText(cmd);

        // Also store it for getUserCommand()
        lastBuiltCommand = cmd;
    }

    /**
     * Clears all current command selections and resets the command controls.
     * This includes list selections, the selected command, the preview field,
     * and the cached {@code lastBuiltCommand}.
     */
    private void clearCommandSelection() {
        objectsList.clearSelection();
        connectionsList.clearSelection();
        charactersList.clearSelection();
        inventoryList.clearSelection();
        // clear selected verb (buttons)
        selectedVerb = "";
        commandTextField.setText("");
        lastBuiltCommand = "";
        updateCommandPreview();
    }

    /**
     * Handler for the Execute button click.
     * Builds the current command string and sends it to the engine callback, if any.
     */
    private void onExecuteCommand() {
        String rawCommand = buildCurrentCommandString();
        commandTextField.setText(rawCommand);
        lastBuiltCommand = rawCommand;

        if (!rawCommand.isEmpty() && engineCallback != null) {
            engineCallback.onCommand(rawCommand);
        }
    }

    /**
     * Handler for individual command button clicks.
     * Sets the current verb and updates the command preview.
     *
     * @param verb the command verb selected by the user (e.g., {@code "go"}, {@code "pick"})
     */
    private void onCommandButton(String verb) {
        if (verb == null) {
            return;
        }
        selectedVerb = verb.trim().toLowerCase();
        commandTextField.setText(selectedVerb);
        lastBuiltCommand = selectedVerb;
        updateCommandPreview();
    }

    /**
     * Handler for the "Reset Game" button click.
     * Clears UI state and notifies the engine that a reset is requested.
     */
    private void onResetGame() {
        try {
            // 1. Clear current UI data
            lastBuiltCommand = "";
            if (commandTextField != null) {
                commandTextField.setText("");
            }
            outputArea.setText("");
            objectsListModel.clear();
            connectionsListModel.clear();
            inventoryListModel.clear();
            turnCountLabel.setText("Turns: 0");

            // 2. Notify engine to restart the game (reload data)
            engineCallback.onResetRequested();

            appendOutput("Game has been reset.");
        } catch (Exception ex) {
            appendOutput("Error resetting game: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handler for the "Quit" button click.
     * Shows a confirmation dialog; if confirmed, notifies the engine to quit.
     */
    private void onQuit() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to quit?",
                "Quit Game",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            engineCallback.onQuitRequested();
        }
    }

    /**
     * Helper method to set a larger font for JOptionPane messages temporarily.
     * This customizes the font for message and button components in option panes.
     *
     * @param font the font to apply to option pane messages and buttons
     */
    private void setDialogFont(Font font) {
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);
        UIManager.put("Label.font", font); // Also impacts component fonts in complex dialogs
        // Note: For full consistency, we might also set OptionPane.font, but
        // messageFont and buttonFont cover the main text/buttons.
    }

    /**
     * Called by the game engine when the game has ended and no custom end message
     * is provided. Uses the ending message from {@link GameData}, if available,
     * or a generic message based on the provided reason.
     *
     * @param reason the reason for game end, such as {@code "win"} for normal
     *               completion or {@code "turns"} for running out of turns
     */
    public void onGameEnded(String reason) {
        // Determine an appropriate ending message. Prefer GameData's ending
        // message when present (this covers the "end location" type).
        String effective;
        if (gameData != null && gameData.getEndingMessage() != null && !gameData.getEndingMessage().isEmpty()) {
            effective = gameData.getEndingMessage();
        } else if ("turns".equals(reason)) {
            effective = "Game Over! You have run out of turns.\nNo more actions are possible.";
        } else {
            effective = "Congratulations! You have reached the ending location.\nNo more actions are possible.";
        }

        // Append to output and scroll
        appendOutput(effective);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());

        // Disable interactive controls
        executeCommandButton.setEnabled(false);
        goButton.setEnabled(false);
        pickButton.setEnabled(false);
        dropButton.setEnabled(false);
        examineButton.setEnabled(false);
        useButton.setEnabled(false);
        talkButton.setEnabled(false);
        giveButton.setEnabled(false);
        objectsList.setEnabled(false);
        connectionsList.setEnabled(false);
        charactersList.setEnabled(false);
        inventoryList.setEnabled(false);

        // Keep Reset / Quit buttons enabled
        resetButton.setEnabled(true);
        quitButton.setEnabled(true);

        // Show a modal dialog using the effective (possibly GameData) message.
        String title = "Game Completed";
        if ("turns".equals(reason))
            title = "Game Over";

        // 1. Set larger font for dialog message
        setDialogFont(dialogFont);

        // 2. Display the dialog
        JOptionPane.showMessageDialog(this, effective + "\nYou may Reset or Quit.", title,
                JOptionPane.INFORMATION_MESSAGE);

        // 3. Restore default fonts (optional but good practice)
        setDialogFont(new Font("SansSerif", Font.PLAIN, 12)); // Restoring to a common default size
    }

    /**
     * Overloaded helper to support engine calls that pass a custom end message.
     * If a non-empty {@code endMessage} is provided, it is used instead of
     * any message from {@link GameData}.
     *
     * @param reason     the reason for game end, such as {@code "win"} or
     *                   {@code "turns"}
     * @param endMessage the custom end message provided by the engine; if
     *                   {@code null} or empty, falls back to
     *                   {@link #onGameEnded(String)}
     */
    public void onGameEnded(String reason, String endMessage) {
        // If engine provided an explicit endMessage (rule-based ending),
        // display that message both in the output and in the dialog and
        // disable play controls. Do NOT fall through to the single-arg
        // handler in this case (that would show gameData's ending message
        // and cause mismatch).
        if (endMessage != null && !endMessage.isEmpty()) {
            // Append provided message to output
            appendOutput(endMessage);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());

            // Disable interactive controls
            executeCommandButton.setEnabled(false);
            goButton.setEnabled(false);
            pickButton.setEnabled(false);
            dropButton.setEnabled(false);
            examineButton.setEnabled(false);
            useButton.setEnabled(false);
            talkButton.setEnabled(false);
            giveButton.setEnabled(false);
            objectsList.setEnabled(false);
            connectionsList.setEnabled(false);
            charactersList.setEnabled(false);
            inventoryList.setEnabled(false);

            // Keep Reset / Quit enabled
            resetButton.setEnabled(true);
            quitButton.setEnabled(true);

            // Show dialog using the engine-provided message
            String title = "Game Completed";
            if ("turns".equals(reason))
                title = "Game Over";

            // 1. Set larger font for dialog message
            setDialogFont(dialogFont);

            // 2. Display the dialog
            JOptionPane.showMessageDialog(this, endMessage + "\nYou may Reset or Quit.", title,
                    JOptionPane.INFORMATION_MESSAGE);

            // 3. Restore default fonts (optional but good practice)
            setDialogFont(new Font("SansSerif", Font.PLAIN, 12)); // Restoring to a common default size
            return;
        }

        // No explicit endMessage provided by engine: fall back to existing
        // behavior which prefers GameData's ending message or generic text.
        onGameEnded(reason);
    }

    /**
     * Appends a line of text to the output area, followed by a newline, and
     * scrolls to the end.
     *
     * @param message the text to append; ignored if {@code null}
     */
    public void appendOutput(String message) {
        if (message == null) {
            return;
        }
        outputArea.append(message + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    // Dialog helpers removed — using original JOptionPane string-based dialogs

    /**
     * Convenience method to refresh all UI sections at once based on the
     * given game data.
     *
     * @param gameData the game data used to refresh the UI components
     */
    public void refreshAll(GameData gameData) {
        // Store game data reference
        this.gameData = gameData;

        if (gameData == null) {
            return;
        }

        // 1. Refresh "current location" area
        String currentLocName = gameData.getCurrentLocation(); // current location name
        if (currentLocName != null) {
            Location currentLoc = gameData.getLocationByName(currentLocName);
            if (currentLoc != null) {
                displayCurrentLocation(currentLoc);
            }
        }

        // 2. Refresh inventory
        Inventory inventory = gameData.getInventory();
        displayInventory(inventory);

        // 3. Show remaining turns
        int remainingTurns = gameData.getTurnLimit();
        updateTurnCount(remainingTurns);
    }

    /**
     * Loads icons for command buttons from the provided {@link GameData} icon map.
     * Uses the injected {@link IImageLoader} to load and scale images to 20x20
     * before assigning them to the appropriate command buttons.
     *
     * @param data the game data that provides the mapping from command names to
     *             icon paths
     */
    private void loadCommandIcons(GameData data) {
        commandIconMap.clear();
        if (data == null) {
            return;
        }

        Map<String, String> icons = data.getIcons();
        if (icons == null || icons.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> e : icons.entrySet()) {
            String key = e.getKey();
            String path = e.getValue();
            try {
                Image img = imageLoader.loadImage(path);
                if (img != null) {
                    Image scaled = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    commandIconMap.put(key.toLowerCase(), new ImageIcon(scaled));
                }
            } catch (IOException ex) {
                // ignore failed icon loads
            }
        }

        // Apply icons to buttons (if they exist)
        setButtonIcons();
    }

    /**
     * Assigns icons from {@code commandIconMap} to the command buttons
     * where applicable. If no specific icon exists for a verb, the button
     * remains without an icon.
     */
    private void setButtonIcons() {
        if (goButton != null) {
            ImageIcon ico = commandIconMap.get("go");
            if (ico == null)
                ico = commandIconMap.get("north");
            if (ico != null)
                goButton.setIcon(ico);
            goButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        if (pickButton != null) {
            ImageIcon ico = commandIconMap.get("pick");
            if (ico == null)
                ico = commandIconMap.get("pickup");
            if (ico != null)
                pickButton.setIcon(ico);
            pickButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        if (dropButton != null) {
            ImageIcon ico = commandIconMap.get("drop");
            if (ico != null)
                dropButton.setIcon(ico);
            dropButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        if (examineButton != null) {
            ImageIcon ico = commandIconMap.get("examine");
            if (ico != null)
                examineButton.setIcon(ico);
            examineButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        if (useButton != null) {
            ImageIcon ico = commandIconMap.get("use");
            if (ico != null)
                useButton.setIcon(ico);
            useButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        if (talkButton != null) {
            ImageIcon ico = commandIconMap.get("talk");
            if (ico != null)
                talkButton.setIcon(ico);
            talkButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        if (giveButton != null) {
            ImageIcon ico = commandIconMap.get("give");
            if (ico != null)
                giveButton.setIcon(ico);
            giveButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
    }

}

/**
 * A custom {@link JLabel} that draws an {@link Image} while preserving its
 * aspect ratio and centering it within the available label area. The image
 * is scaled to fill the component while maintaining its proportions.
 */
class AspectImageLabel extends JLabel {
    private Image img;

    /**
     * Sets the image to be rendered by this label and repaints the component.
     *
     * @param img the image to display; may be {@code null} to clear the current image
     */
    public void setImage(Image img) {
        this.img = img;
        repaint();
    }

    /**
     * Paints the component, drawing the stored image (if any) with preserved
     * aspect ratio, centered within the label's bounds.
     *
     * @param g the {@link Graphics} context used for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            int w = getWidth(), h = getHeight();
            int iw = img.getWidth(null), ih = img.getHeight(null);
            if (iw > 0 && ih > 0) {
                double scale = Math.max(w * 1.0 / iw, h * 1.0 / ih);
                int nw = (int) (iw * scale), nh = (int) (ih * scale);
                int x = (w - nw) / 2, y = (h - nh) / 2;
                g.drawImage(img, x, y, nw, nh, this);

            }
        }
    }
}
