/**
 * Defines the core functionality of the Game Engine, which manages the overall
 * game flow, state transitions, and command execution.
 * The Game Engine acts as the central controller that coordinates between the
 * UI, data, and command logic.
 * 
 * @author Junqi Zheng
 */
public interface IGameEngine {
    /**
     * Starts the game, initializing game data and displaying the opening scene.
     */
    void startGame();

    /**
     * Processes a command input from the player.
     * 
     * @param command the raw text command to process
     */
    void processCommand(String command);

    /**
     * Exits the game.
     */
    void exitGame();

    /**
     * Resets the game
     */
    void resetGame();

    /**
     * Updates the current game state with the provided data.
     * 
     * @param data the game data to apply
     */
    void setGameData(GameData data);

    /**
     * Returns the current game data.
     * 
     * @return the active GameData object
     */
    GameData getGameData();

    /**
     * Refreshes the user interface to reflect the current game state.
     */
    //void setGameUI();

    /**
     * Returns the UI instance currently attached to the engine.
     * 
     * @return the game UI
     */
    GameUI getGameUI();

    /**
     * Updates the count of turn
     */
    void updateTurnCount();

    /**
     * Check if it is the end of game
     * 
     * @return true if it is the end
     */
    boolean checkEnd();
}