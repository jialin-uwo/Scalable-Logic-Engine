/**
 * Implements the "Drop" command for puzzle-solving gameplay.
 * 
 * This command allows players to drop objects from their inventory to the
 * current location.
 * 
 * @author Zhixian Wang
 */
public class DropCommand extends ICommand {
    private String objectName;

    /**
     * Constructs a DropCommand for the specified object.
     * 
     * @param objectName the name of the object to drop
     */
    public DropCommand(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Validates whether the drop command can be executed on the object.
     * This method is side-effect free.
     * 
     * @param data the game data
     * @return true if the object exists in the inventory; false otherwise
     */
    @Override
    public boolean validate(GameData data) {
        // Check if object exists in inventory
        if (objectName == null)
            return false;
        // Case-insensitive check against inventory items
        return data.getInventory().getAllObjects().stream()
                .anyMatch(o -> o.getName() != null && o.getName().equalsIgnoreCase(objectName));
    }

    /**
     * Executes the drop command by removing the object from the inventory
     * and adding it to the current location.
     * 
     * @param engine the game engine
     */
    @Override
    public void execute(GameEngine engine) {
        IGameData gameData = engine.getGameData();
        if (objectName == null)
            return;

        // Find the actual object instance in the inventory (case-insensitive)
        GameObject invObj = gameData.getInventory().getAllObjects().stream()
                .filter(o -> o.getName() != null && o.getName().equalsIgnoreCase(objectName))
                .findFirst().orElse(null);

        if (invObj != null) {
            String actualName = invObj.getName();
            String currentLocationName = gameData.getCurrentLocation();
            // Remove object from inventory using the correct name
            boolean removed = gameData.removeObjectFromInventory(actualName);
            if (removed) {
                // Add object instance back into the current location
                gameData.addObjectToLocation(currentLocationName, invObj);
            }
        }
    }

}