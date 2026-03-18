/**
 * Defines the contract for a user-issued command in the game.
 * A command typically validates first and executes if valid.
 * 
 * @author Xinyan Cai
 */
public abstract class ICommand {

    /**
     * Checks whether this command can be executed under the current game state.
     * This method must be side-effect free.
     *
     * @param data The GameData
     * @return true if the command is applicable now; false otherwise.
     */
    public abstract boolean validate(GameData data);

    /**
     * Executes the command’s effects. This is only called after validate(...)
     * has returned true. Implementations should route user-visible text via UI.
     *
     * @param engine The engine
     */
    public abstract void execute(GameEngine engine);

    // Optional: command may request the engine to consider ending the game.
    // Subclasses can call `requestEndForRule(String ruleName)` during execute(),
    // and the engine will validate the rule and perform the end if appropriate.
    protected String requestedEndRuleName = null;

    /**
     * Request the engine to consider ending the game for the given rule name.
     * The engine will validate the rule and only end if the rule is configured
     * with `endGame=true`.
     */
    public void requestEndForRule(String ruleName) {
        this.requestedEndRuleName = ruleName;
    }

    /**
     * Returns the rule name requested by this command to trigger an end, or null.
     */
    public String getRequestedEndRuleName() {
        return this.requestedEndRuleName;
    }
}
