/**
 * GoCommand.java
 *
 * This class implements the "go" command in the adventure game.
 * When executed, the command updates the current location in
 * {@link GameData}, without directly handling turn consumption or
 * end-of-game logic (these are managed by {@link GameEngine}).
 *
 * @author Xinyan Cai
 */

/**
 * Concrete command that moves the player from the current location
 * through a connection (e.g., "north door", "elevator", etc.).
 * It uses the GameEngine as a façade to access GameData, and only
 * mutates GameData (currentLocation, turn counter).
 */
public class GoCommand extends ICommand {

    /** Label chosen by the player, e.g. "north" or "red door". */
    private final String connectionLabel;

    /**
     * Creates a new Go command for the given connection label.
     *
     * @param connectionLabel the label of the connection the player wants to use
     *                        (input);
     *                        this is typically typed by the player to match a
     *                        connection name
     */
    public GoCommand(String connectionLabel) {
        this.connectionLabel = connectionLabel;
    }

    /**
     * Validates whether this {@code GoCommand} can be executed in the current game
     * state.
     * If all checks pass, the command is considered valid and can be executed.
     *
     * @param data the current {@link GameData} instance representing the game state
     * @return {@code true} if the command can be executed, {@code false} otherwise
     */
    @Override
    public boolean validate(GameData data) {

        if (data == null) {
            return false;
        }

        // Checks if the game is out of turns.
        if (data.isOutOfTurns()) {
            return false;
        }

        // current Location
        String currentLocationName = data.getCurrentLocation();
        if (currentLocationName == null) {
            return false;
        }

        Location currentLocation = data.getLocationByName(currentLocationName);
        if (currentLocation == null) {
            return false;
        }

        // 多connection支持：查找是否有匹配的connection
        if (connectionLabel == null) {
            return false;
        }
        Connection matched = currentLocation.getConnectionByName(connectionLabel.trim());
        return matched != null;
    }

    /**
     * Executes the {@code GoCommand} to move the player to the target location.
     * If the target location cannot be resolved, no state change occurs and the
     * method returns silently.
     *
     * @param engine the {@link GameEngine} used to access and modify
     *               {@link GameData}
     */
    @Override
    public void execute(GameEngine engine) { // validated
        GameData data = engine.getGameData();

        String currentLocationName = data.getCurrentLocation();
        Location currentLocation = data.getLocationByName(currentLocationName);
        Connection matched = currentLocation
                .getConnectionByName(connectionLabel == null ? null : connectionLabel.trim());
        if (matched == null) {
            return;
        }
        String targetLocationName = matched.getTargetLocationName();
        Location targetLocation = data.getLocationByName(targetLocationName);
        if (targetLocation == null) {
            // Fallback protection for data errors: No state change
            return;
        }
        // update current location
        data.setCurrentLocation(targetLocationName);

    }
}
