/**
 * IEngineCallback.java
 *
 * This interface defines the callback methods that the {@code GameUI}
 * uses to communicate user actions back to the game engine.
 * The UI layer depends only on this interface and does not need to know
 * about the concrete {@link GameEngine} implementation. This keeps the
 * coupling between UI and engine low and allows the engine to be replaced
 * or tested independently.
 *
 * @author Xinyan Cai
 */
public interface IEngineCallback {

    /**
     * Notifies the engine that the user issued a command.
     * The UI typically calls this when the user clicks an "Execute" button
     * after composing a command (for example, {@code "go to Hall"},
     * {@code "talk to Guard"}, or {@code "pick golden key"}).
     *
     * @param rawCommand the raw command string entered or constructed by the UI;
     *                   must not be {@code null} or empty for valid processing
     */
    void onCommand(String rawCommand); // CHANGED: new callback for commands

    /**
     * Notifies the engine that the user requested to reset the game.
     * The engine is expected to reload or re-initialize game data and
     * restore the game to its starting state when this method is called.
     * The UI itself does not perform the reset logic; it only forwards
     * the request through this callback.
     */
    void onResetRequested(); // CHANGED: new callback for reset

    /**
     * Notifies the engine that the user requested to quit the game.
     * The UI only signals the intent; the engine manages the actual shutdown.
     */
    void onQuitRequested(); // CHANGED: new callback for quit
}
