/**
 * GameEngine.java
 *
 * This file contains the concrete implementation of the main game engine for the
 * CS2212 adventure game. The engine coordinates the core game loop.
 * The engine also implements {@link IEngineCallback} so that the UI can notify
 * the engine of user actions (commands, reset, quit).
 *
 * @author Xinyan Cai, Jialin Li
 */

import java.util.Locale;

/**
 * GameEngine.java
 *
 * Central game engine coordinating the {@link GameData} state, the
 * {@link GameUI} and command execution lifecycle. The engine is
 * responsible for loading game data, processing user commands, applying
 * turn accounting, and ending the game when termination conditions are met.
 *
 * The engine implements {@link IEngineCallbackWithSaveLoad} so the UI can
 * request save/load operations.
 *
 * Author: Xinyan Cai
 */
public class GameEngine implements IGameEngine, IEngineCallbackWithSaveLoad {

    
    private static final String AUTOSAVE_PATH = "saves/autosave.json";

    /**
     * Save the current game data to a file (JSON).
     */
    @Override
    public void onSaveRequested(String filePath, GameData data) {
        try {
            DataSaver.saveGameData(filePath, data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save game: " + e.getMessage(), e);
        }
    }

    /**
     * Load game data from a file (JSON) and refresh the UI.
     */
    @Override
    public void onLoadRequested(String filePath) {
        try {
            this.gameData = dataLoader.loadGameData(filePath);
            if (ui != null) {
                ui.resetRender(gameData);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load game: " + e.getMessage(), e);
        }
    }

    /**
     * Loader responsible for reading external game data (JSON/XML) into GameData.
     */
    private final IDataLoader dataLoader;

    /** Path to the external game data file (e.g., "DataFile.json"). */
    private final String dataFilePath; // eg. "DataFile.json"

    /** Loader responsible for loading images used by the {@link GameUI}. */
    private final IImageLoader imageLoader; // CHANGED: store image loader to construct UI (may be used in future)

    /** The current in-memory game data model representing the game state. */
    private GameData gameData;

    /** The Swing-based user interface used to present the game visually. */
    private GameUI ui;

    /** Flag indicating whether the game is currently running. */
    private boolean running;
    // (removed previous pending end scheduling fields — commands now request an end
    // by name and the engine will validate immediately after execution)
    // private int turnsTaken; 

    /**
     * Constructs a new {@code GameEngine} instance.
     * The engine will store references to the provided loaders and immediately
     * create a {@link GameUI} instance, passing itself as the
     * {@link IEngineCallback}.
     *
     * @param dataLoader     loader used to read the external game data file into
     *                     {@link GameData}
     * @param dataFilePath path to the game data file (e.g., JSON/XML file)
     * @param imageLoader   loader used to load images for the UI
     */
    public GameEngine(IDataLoader dataLoader,
            String dataFilePath,
            IImageLoader imageLoader) { // CHANGED: constructor signature
        this.dataLoader = dataLoader;
        this.dataFilePath = dataFilePath;
        this.imageLoader = imageLoader; // CHANGED
        this.running = false;
        // this.turnsTaken = 0;

        
        java.io.File savesDir = new java.io.File("saves");
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }

        // CHANGED: Engine internally creates UI and passes itself as callback
        this.ui = new GameUI(this, imageLoader);
    }

    // ----------------------------------------------------
    // IGameEngine interface methods
    // ----------------------------------------------------

    /**
     * Starts a new game session.
     */
    @Override
    public void startGame() {

        java.io.File autosave = new java.io.File(AUTOSAVE_PATH);
        boolean loadedFromAutosave = false;

        if (autosave.exists() && ui != null) {
            int result = javax.swing.JOptionPane.showConfirmDialog(null,
                    "An autosave was detected. Do you want to load your saved progress?",
                    "Load autosave?",
                    javax.swing.JOptionPane.YES_NO_OPTION);

            if (result == javax.swing.JOptionPane.YES_OPTION) {
                try {

                    this.gameData = dataLoader.loadGameData(AUTOSAVE_PATH);
                    loadedFromAutosave = true;
                } catch (Exception e) {

                    // System.err.println("Failed to load autosave with exception: " +
                    // e.getMessage());
                    // e.printStackTrace();

                    ui.showError("Failed to load autosave, starting new game: " + e.getMessage());

                }
            }
        }

        if (!loadedFromAutosave) {
            try {
                this.gameData = dataLoader.loadGameData(dataFilePath);
            } catch (Exception e) {
                if (ui != null) {
                    ui.showError("Failed to load game data: " + e.getMessage());
                }
                exitGame("startup_fail", null);
                return;
            }
        }

        this.running = true;
        // Only initialize current location to the starting location when
        // we did not load an autosave. If an autosave was loaded, preserve
        // the saved currentLocation so the player returns to their saved spot.
        if (!loadedFromAutosave) {
            String startLocName = gameData.getStartingLocation();
            if (startLocName != null) {
                gameData.setCurrentLocation(startLocName);
            }
        }
        if (ui != null) {
            ui.resetRender(gameData);

            // String startMsg = gameData.getStartingMessage();
            // if (startMsg != null) {
            // ui.displayMessage(startMsg);
            // }

            ui.setVisible(true);
        }
    }

    /**
     * Processes a player-entered raw command string.
     * * @param input the raw command string entered by the player
     */
    @Override
    public void processCommand(String input) { // from UI
        // Exclude exceptional cases
        if (!running || gameData == null) {
            return;
        }

        // Empty input
        if (input == null) {
            if (ui != null) {
                ui.showError("Please enter a command.");
            }
            return;
        }

        // Check whether the game has already ended
        if (checkEnd()) {
            return;
        }

        // 1）Analyzing Command Verbs and Parameters
        String trimmed = input.trim(); // "go north"

        if (trimmed.isEmpty()) {
            if (ui != null) {
                ui.showError("Please enter a command.");
            }
            return;
        }

        String[] tokens = trimmed.split("\\s+"); // ["go", "north"]
        String verb = tokens[0].toLowerCase(Locale.ROOT); // "go"

        ICommand cmd = createCommandFromTokens(verb, tokens);
        if (cmd == null) {
            if (ui != null) {
                ui.showError("Unknown command: " + verb);
            }
            return;
        }

        // 2）Validate
        boolean ok = cmd.validate(gameData); // Pay attention to parameters.

        if (!ok) {
            
            if (ui != null) {
                String msg = gameData.getCommandMessage(verb, false);
                if (msg == null || msg.isEmpty()) {
                    msg = "You cannot do that right now.";
                }
                ui.displayMessage(msg);
            }
            return;
        }

        
        cmd.execute(this); 

        // After execution, allow commands to request an end by rule name.
        String requestedRule = cmd.getRequestedEndRuleName();

        
        /*
        System.out.println("[ENGINE DEBUG] command requested end rule: " + requestedRule);
        if (requestedRule != null) {
            System.out.println("[ENGINE DEBUG] useRules count="
                    + (gameData == null ? 0 : (gameData.getUseRules() == null ? 0 : gameData.getUseRules().size()))
                    + ", giveRules count="
                    + (gameData == null ? 0 : (gameData.getGiveRules() == null ? 0 : gameData.getGiveRules().size())));
        }
        */
        

        if (requestedRule != null && !requestedRule.isEmpty()) {
            boolean accepted = false;
            String endMsg = null;
            String reason = "useRule";
            // Check use rules first
            if (gameData != null && gameData.getUseRules() != null) {
                for (UseRule r : gameData.getUseRules()) {
                    if (r != null && r.getName() != null && r.getName().equalsIgnoreCase(requestedRule)
                            && r.isEndGame()) {
                        accepted = true;
                        endMsg = r.getMessage();
                        reason = "useRule";
                        break;
                    }
                }
            }
            // Then check give rules
            if (!accepted && gameData != null && gameData.getGiveRules() != null) {
                for (GiveRule g : gameData.getGiveRules()) {
                    if (g != null && g.getName() != null && g.getName().equalsIgnoreCase(requestedRule)
                            && g.isEndGame()) {
                        accepted = true;
                        endMsg = g.getText();
                        reason = "giveRule";
                        break;
                    }
                }
            }
            if (accepted) {
                exitGame(reason, endMsg);
                return;
            } else {
                if (ui != null) {
                    ui.displayMessage("Nothing special happens.");
                }
            }
        }

        
        gameData.consumeTurn();

        
        updateTurnCount();

        // Render Location & Inventory using the latest GameData
        ui.refreshAll(gameData);

        // Success prompt (If the command has already output special text, an additional
        // line "System prompt" will appear here)
        if (ui != null) {
            String successMsg = gameData.getCommandMessage(verb, true);
            if (successMsg != null && !successMsg.isEmpty()) {
                ui.displayMessage(successMsg);
            }
        }

        // No scheduled-end handling remains; commands request an end by name
        // and the engine validated/handled it earlier immediately after execution.

        // Check whether the termination condition is met (location/turns)
        checkEnd();
    }

    // ----------------------------------------------------
    // Helper: Simple command parsing to create concrete Command objects
    // ----------------------------------------------------

    /**
     * Very simple parser that converts raw user input into a concrete command.
     * The first token is treated as the verb, and the remaining tokens are used
     * as arguments. This method supports multiple verbs with multi-word names.
     * * @param verb   the lower-cased command verb (first token)
     * * @param tokens the full tokenized input array
     * @return a concrete {@link ICommand} implementation, or {@code null} if
     *         the input cannot be parsed into a valid command
     */
    private ICommand createCommandFromTokens(String verb, String[] tokens) {
        switch (verb) {
            // -------- go: "go to Entrance Hall" / "go Entrance Hall" --------
            case "go": {
                if (tokens.length < 2) {
                    return null;
                }

                String connectionLabel;
                // "go to A B C"
                if (tokens.length >= 3 && "to".equalsIgnoreCase(tokens[1])) {
                    connectionLabel = joinTokens(tokens, 2, tokens.length - 1);
                } else {
                    // "go A B C"
                    connectionLabel = joinTokens(tokens, 1, tokens.length - 1);
                }
                return new GoCommand(connectionLabel);
            }

            // -------- talk: "talk to Old Man" / "talk Old Man" --------
            case "talk": {
                if (tokens.length < 2) {
                    return null;
                }

                String characterName;
                if (tokens.length >= 3 && "to".equalsIgnoreCase(tokens[1])) {
                    // "talk to A B"
                    characterName = joinTokens(tokens, 2, tokens.length - 1);
                } else {
                    // "talk A B"
                    characterName = joinTokens(tokens, 1, tokens.length - 1);
                }
                return new TalkCommand(characterName);
            }

            // -------- give: "give somebody something" / "give A to B" --------
            case "give": {
                if (tokens.length < 3) {
                    return null;
                }

                // "give A A2 ... to B B2 ..."
                for (int i = 2; i < tokens.length - 1; i++) {
                    if ("to".equalsIgnoreCase(tokens[i])) {
                        // "give" [1..i-1] = left, [i+1..end] = right
                        String left = joinTokens(tokens, 1, i - 1);
                        String right = joinTokens(tokens, i + 1, tokens.length - 1);
                        // Default interpretation: left = objectName, right = characterName
                        // But the UI sometimes builds "give <character> to <object>",
                        // so detect and swap when left looks like a character and right looks like an
                        // object.
                        if (gameData != null) {
                            boolean leftIsCharacter = gameData.getCharacterByName(left) != null;
                            boolean rightIsObject = gameData.getObjectByName(right) != null;
                            if (leftIsCharacter && rightIsObject) {
                                // user likely used "give <character> to <object>", swap
                                String objectName = right;
                                String characterName = left;
                                return new GiveCommand(characterName, objectName);
                            }
                        }
                        String objectName = left;
                        String characterName = right;
                        return new GiveCommand(characterName, objectName);
                    }
                }

               
                if (gameData != null) {
                    int m = tokens.length - 1; 
                    for (int split = 1; split < m; split++) {
                        String characterName = joinTokens(tokens, 1, split);
                        String objectName = joinTokens(tokens, split + 1, tokens.length - 1);

                        if (gameData.getCharacterByName(characterName) != null
                                && gameData.getObjectByName(objectName) != null) {
                            return new GiveCommand(characterName, objectName);
                        }
                    }
                }

                
                String characterName = tokens[1];
                String objectName = joinTokens(tokens, 2, tokens.length - 1);
                return new GiveCommand(characterName, objectName);
            }

            // -------- examine: "examine A B C" --------
            case "examine": {
                if (tokens.length < 2) {
                    return null;
                }
                String nameString = joinTokens(tokens, 1, tokens.length - 1);

                if (gameData != null) {
                    // Check object first
                    if (gameData.getObjectByName(nameString) != null) {
                        return new ExamineCommand(nameString, ExamineCommand.TargetType.OBJECT);
                    }
                    // Then check character
                    if (gameData.getCharacterByName(nameString) != null) {
                        return new ExamineCommand(nameString, ExamineCommand.TargetType.CHARACTER);
                    }
                    // Then check connection in current location
                    Location currentLocation = gameData.getLocationByName(gameData.getCurrentLocation());
                    if (currentLocation != null && currentLocation.getConnectionByName(nameString) != null) {
                        return new ExamineCommand(nameString, ExamineCommand.TargetType.CONNECTION);
                    }
                }
                // If not found, default to object for error message compatibility
                return new ExamineCommand(nameString, ExamineCommand.TargetType.OBJECT);
            }

            // -------- use: "use A on B" / "use A with B" 或 "use A" --------
            case "use": {
                if (tokens.length < 2) {
                    return null;
                }

                int separatorIndex = -1;
                for (int i = 1; i < tokens.length; i++) {
                    if ("on".equalsIgnoreCase(tokens[i]) || "with".equalsIgnoreCase(tokens[i])) {
                        separatorIndex = i;
                        break;
                    }
                }

                String[] objectNames;
                if (separatorIndex != -1) {
                    String first = joinTokens(tokens, 1, separatorIndex - 1);
                    String second = joinTokens(tokens, separatorIndex + 1, tokens.length - 1);

                    if (second.isEmpty()) {
                        objectNames = new String[] { first };
                    } else {
                        objectNames = new String[] { first, second };
                    }
                } else {
                    String first = joinTokens(tokens, 1, tokens.length - 1);
                    objectNames = new String[] { first };
                }
                return new UseCommand(objectNames);
            }

            // -------- pick: "pick A" / "pick A B C" --------
            case "pick": {
                if (tokens.length < 2) {
                    return null;
                }
                String objectName = joinTokens(tokens, 1, tokens.length - 1);
                return new PickCommand(objectName);
            }

            // -------- drop: "drop A" / "drop A B C" --------
            case "drop": {
                if (tokens.length < 2) {
                    return null;
                }
                String objectName = joinTokens(tokens, 1, tokens.length - 1);
                return new DropCommand(objectName);
            }

            // 其它命令以后再扩展
            default:
                return null;
        }
    }

    /**
     * Joins tokens from index {@code from} to index {@code to} (inclusive)
     * into a single space-separated string.
     * This helper supports multi-word names such as {@code "red door"} or
     * {@code "old caretaker"}.
     *
     * @param tokens the array of tokens representing the input command
     * @param from     start index (inclusive)
     * @param to         end index (inclusive)
     * @return a trimmed string formed by joining tokens[from..to],
     *         or an empty string if the range is invalid
     */
    private String joinTokens(String[] tokens, int from, int to) {
        if (tokens == null || from > to || from < 0 || to >= tokens.length) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = from; i <= to; i++) {
            if (i > from) {
                sb.append(" ");
            }
            sb.append(tokens[i]);
        }
        return sb.toString().trim();
    }

    /**
     * Handles termination of the game.
     * Sets {@code running} to {@code false}, shows a final "Game over" message
     * and notifies the UI via {@link GameUI#onGameEnded()} so that the UI can
     * disable further inputs or close itself if desired.
     */
    @Override
    public void exitGame() {
        exitGame("win", null);
    }

    // Overloaded: allow passing reason and a custom end message
    public void exitGame(String reason, String endMessage) {
        running = false;
        if (ui != null) {
            if (endMessage != null && !endMessage.isEmpty()) {
                ui.displayMessage(endMessage);
            } else if ("quit".equals(reason)) {
                ui.displayMessage("Game exited. You have left the game.");
            } else if ("startup_fail".equals(reason)) {
                ui.displayMessage("Failed to start game due to data loading error.");
            } else if ("turns".equals(reason)) {
                ui.displayMessage("You have run out of turns!");
            } else if ("win".equals(reason)) {
                ui.displayMessage("Game over. Thanks for playing!");
            }
            ui.onGameEnded(reason, endMessage);
        }
        
    }

    // Removed scheduleEnd — engine now performs immediate validation of
    // command-requested ends after command execution.

    /**
     * Resets the game by starting a new session.
     * Internally this simply delegates to {@link #startGame()}, which reloads
     * the data file and reinitializes the game state and UI.
     */
    @Override
    public void resetGame() {
        try {
            this.gameData = dataLoader.loadGameData(dataFilePath);
            this.running = true;
            String startLocName = gameData.getStartingLocation();
            if (startLocName != null) {
                gameData.setCurrentLocation(startLocName);
            }
            if (ui != null) {
                ui.resetRender(gameData);
                
                // String startMsg = gameData.getStartingMessage();
                // if (startMsg != null) {
                // ui.displayMessage(startMsg);
                // }
                
            }
        } catch (Exception e) {
            this.running = false;
            if (ui != null) {
                ui.showError("Failed to reset game: " + e.getMessage());
            }
        }
    }

    /**
     * Replaces the current {@link GameData} instance.
     * This is typically used only in testing or special cases where the caller
     * wants to inject a different game state.
     *
     * @param data the new {@link GameData} to use, may be {@code null}
     */
    @Override
    public void setGameData(GameData data) {
        this.gameData = data;
        
    }

    /**
     * Returns the current {@link GameData} instance.
     * * @return the current game data, or {@code null} if the game has not been
     *         started
     */
    @Override
    public GameData getGameData() {
        return this.gameData;
    }

    /*
     * public void setGameUI() { 
     * if (ui == null || gameData == null) {
     * return;
     * }
     * * String currentLocName = gameData.getCurrentLocation();
     * Location currentLoc = gameData.getLocationByName(currentLocName);
     * if (currentLoc != null) {
     * ui.displayCurrentLocation(currentLoc);
     * }
     * * ui.displayInventory(gameData.getInventory());
     * // 回合显示交给 updateTurnCount()
     * }
     */

    /**
     * Returns the current {@link GameUI} instance used by this engine.
     * This method is primarily useful for integration tests or code that needs
     * to directly manipulate the UI.
     * * @return the current {@link GameUI}, or {@code null} if not yet created
     */
    @Override
    public GameUI getGameUI() {
        return ui;
    }

    /**
     * Updates the turn counter displayed in the UI.
     * The value shown is the remaining number of turns as maintained by
     * {@link GameData#getTurnLimit()}. If the UI or game data is not available,
     * or the game is not running, this method does nothing.
     */
    @Override
    public void updateTurnCount() {
        if (ui == null || !running || gameData == null) {
            return;
        }
        
        int remainingTurns = gameData.getTurnLimit();
        ui.updateTurnCount(remainingTurns);
    }

    /**
     * Checks whether the game has reached an end condition.
     * Two conditions are checked:
     * If an end condition is met, an appropriate message is shown via the UI
     * and {@link #exitGame()} is invoked.
     * * @return {@code true} if the game has ended, {@code false} otherwise
     */
    @Override
    public boolean checkEnd() {
        if (gameData == null) {
            return false;
        }

        boolean ended = false;

        // 1）Remaining Turns = 0
        if (gameData.isOutOfTurns()) {
            if (ui != null) {
                ui.displayMessage("You have run out of turns!");
            }
            ended = true;
        }

        // 2）Reach the final destination
        String current = gameData.getCurrentLocation();
        String ending = gameData.getEndingLocation();
        if (!ended && current != null && ending != null
                && current.equalsIgnoreCase(ending)) {
            if (ui != null) {
                ui.displayMessage(gameData.getEndingMessage());
            }
            ended = true;
        }

        if (ended) {
            exitGame();
        }
        return ended;
    }

    /**
     * Replaces the current {@link GameUI} instance with a new one.
     * This method is not part of the {@link IGameEngine} interface and is mainly
     * intended for testing or advanced scenarios where the UI needs to be swapped.
     *
     * @param ui the new {@link GameUI} to use
     */
    public void setUi(GameUI ui) {
        this.ui = ui;
    }

    // requestEndFromRule removed; command-requested ends are validated inline
    // immediately after command execution to keep control flow simple.

    // ----------------------------------------------------
    // IEngineCallback implementation (UI → Engine)
    // ----------------------------------------------------

    /**
     * Callback from the UI when a raw command string should be processed.
     * Delegates directly to {@link #processCommand(String)}.
     * * @param rawCommand the raw command string constructed by the UI
     */
    @Override
    public void onCommand(String rawCommand) { // CHANGED: callback from UI
        processCommand(rawCommand);
    }

    /**
     * Callback from the UI when a reset is requested by the player.
     * Delegates directly to {@link #resetGame()}.
     */
    @Override
    public void onResetRequested() { // CHANGED: callback from UI
        resetGame();
    }

    /**
     * Callback from the UI when the player requests to quit the game.
     * Delegates to {@link #exitGame()} and then terminates the process by
     * calling {@link System#exit(int)} with status code {@code 0}.
     */
    @Override
    public void onQuitRequested() { // CHANGED: callback from UI
        // Auto-save current game state to autosave.json before quitting
        try {
            if (gameData != null) {
                DataSaver.saveGameData(AUTOSAVE_PATH, gameData);
            }
        } catch (Exception e) {
            if (ui != null)
                ui.showError("Failed to autosave: " + e.getMessage());
        }

        
        try {
            Thread.sleep(100); 
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        
        System.exit(0);
    }
}
