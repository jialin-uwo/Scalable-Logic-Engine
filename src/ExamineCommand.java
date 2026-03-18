/**
 * Implements the "examine" command, allowing players to inspect objects or
 * characters.
 * When examining an object, its description is displayed. If the object
 * contains other objects, those contained objects are automatically added to
 * the current location, making them available for interaction in subsequent
 * commands.
 * When examining a character, only their description is displayed without
 * any side effects.
 * 
 * @author Jialin Li
 */
public class ExamineCommand extends ICommand {
    private String targetName;
    private TargetType targetType;

    public enum TargetType {
        OBJECT,
        CHARACTER,
        CONNECTION
    }

    /**
     * Constructs an ExamineCommand for the specified target.
     *
     * @param nameString the name of the object, character, or connection to examine
     * @param targetType the type of the target (OBJECT, CHARACTER, CONNECTION)
     */
    public ExamineCommand(String nameString, TargetType targetType) {
        this.targetName = nameString;
        this.targetType = targetType;
    }

    /**
     * Validates whether the examine command can be executed.
     *
     * @param data the game data
     * @return true if the target exists in the game; false otherwise
     */
    @Override
    public boolean validate(GameData data) {
        if (targetType == TargetType.OBJECT) {
            return data.getObjectByName(targetName) != null;
        } else if (targetType == TargetType.CHARACTER) {
            return data.getCharacterByName(targetName) != null;
        } else if (targetType == TargetType.CONNECTION) {
            Location currentLocation = data.getLocationByName(data.getCurrentLocation());
            if (currentLocation != null) {
                return currentLocation.getConnectionByName(targetName) != null;
            }
            return false;
        }
        return false;
    }

    /**
     * Executes the examine command, displaying the description of the target.
     * For objects, contained items are also added to the current location.
     *
     * @param engine the game engine
     */
    @Override
    public void execute(GameEngine engine) {
        IGameData gameData = engine.getGameData();
        IGameUI gameUI = engine.getGameUI();

        if (targetType == TargetType.OBJECT) {
            GameObject object = gameData.getObjectByName(targetName);
            if (object != null) {
                // Display the object's description
                String description = object.getDescription();
                if (description != null && !description.isEmpty()) {
                    gameUI.displayMessage(description);
                } else {
                    gameUI.displayMessage("You see nothing special about the " + targetName + ".");
                }

                // Add contained items to current location
                Location currentLocation = gameData.getLocationByName(gameData.getCurrentLocation());
                if (currentLocation != null) {
                    for (GameObject containedObject : object.getContainedItems()) {
                        currentLocation.addObject(containedObject);
                        gameUI.displayMessage("You notice a " + containedObject.getName() + " inside.");
                    }
                }
            } else {
                gameUI.displayMessage("You don't see a " + targetName + " here.");
            }
        } else if (targetType == TargetType.CHARACTER) {
            Character character = gameData.getCharacterByName(targetName);
            if (character != null) {
                String description = character.getDescription();
                if (description != null && !description.isEmpty()) {
                    gameUI.displayMessage(description);
                } else {
                    gameUI.displayMessage("You see " + targetName + ".");
                }
            } else {
                gameUI.displayMessage("You don't see " + targetName + " here.");
            }
        } else if (targetType == TargetType.CONNECTION) {
            Location currentLocation = gameData.getLocationByName(gameData.getCurrentLocation());
            if (currentLocation != null) {
                Connection connection = currentLocation.getConnectionByName(targetName);
                if (connection != null) {
                    String description = connection.getDescription();
                    if (description != null && !description.isEmpty()) {
                        gameUI.displayMessage(description);
                    } else {
                        gameUI.displayMessage("You see nothing special about the connection '" + targetName + "'.");
                    }
                } else {
                    gameUI.displayMessage("There is no connection named '" + targetName + "' here.");
                }
            } else {
                gameUI.displayMessage("You are not in a valid location.");
            }
        }
    }
}
