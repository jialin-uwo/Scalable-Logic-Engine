/**
 * Implements the "Pick" command for puzzle-solving gameplay.
 * 
 * This command allows players to pick objects from the current location into
 * their inventory.
 * 
 * @author Zhixian Wang
 */
public class PickCommand extends ICommand {
    private String objectName;

    /**
     * Constructs a PickCommand for the specified object.
     * 
     * @param objectName the name of the object to pick up
     */
    public PickCommand(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Validates whether the pick command can be executed on the object.
     * This method is side-effect free.
     * 
     * @param data the game data
     * @return true if the object exists, is pickable, and inventory is not full;
     *         false otherwise
     */
    @Override
    public boolean validate(GameData data) {
        // Check if inventory is full
        if (data.getInventory().isFull()) {
            return false;
        }

        // Check if object exists
        GameObject object = data.getObjectByName(objectName);
        if (object == null) {
            return false;
        }

        // Check if object is pickable
        return object.pickable();
    }

    /**
     * Executes the pick command by removing the object from the current location
     * and adding it to the player's inventory.
     * 
     * @param engine the game engine
     */
    @Override
    public void execute(GameEngine engine) {
        IGameData gameData = engine.getGameData();
        GameObject object = gameData.getObjectByName(objectName);

        if (object != null) {
            String currentLocationName = gameData.getCurrentLocation();
            // Remove object from current location
            gameData.removeObjectFromLocation(currentLocationName, object);
            // Add object to inventory
            gameData.addObjectToInventory(object);
        }
        // Success/failure messages are handled by GameEngine using CommandMessages
    }

}
