import java.util.List;

/**
 * GiveCommand.java
 *
 * This class implements the "give" command in the adventure game.
 * The command allows the player to give an object from their inventory to a
 * character located in the current location.
 * If a matching rule is found, the object is removed from the player's
 * inventory, an optional line of dialogue is displayed, and any resulting
 * reward objects defined by the rule are added to the inventory.
 *
 * @author Xinyan Cai
 */

/**
 * Command that gives an object from the player's inventory to a character
 * in the current location. If the character wants the object (according to
 * its GiveRule), the rule text is shown and resulting objects are added
 * to the player's inventory.
 */
public class GiveCommand extends ICommand {

    /** The target character's name that the player wants to give to (input). */
    private final String characterName;

    /** The name of the object being given (from the player's inventory). */
    private final String objectName;

    /**
     * The GiveRule that matched during validate().
     * This cached rule is later used during {@link #execute(GameEngine)}.
     */
    private GiveRule matchedRule;
    /**
     * The actual GameObject instance found in the player's inventory
     * (case-insensitive match).
     */
    private GameObject matchedItem;

    /**
     * Constructs a new {@code GiveCommand} with the specified character and object.
     *
     * @param characterName name of the character to give an item to;
     *                      this is the player-typed target character at the current
     *                      location
     * @param objectName    name of the object from the player's inventory
     *                      that the player wants to give
     */
    public GiveCommand(String characterName, String objectName) { // player's input
        this.characterName = characterName;
        this.objectName = objectName;
    }

    /**
     * Validates whether this give action is allowed in the current game state.
     * If a rule is found, it is stored in {@link #matchedRule} for later use
     * in {@link #execute(GameEngine)}.
     *
     * @param data the current {@link GameData} instance representing the game state
     * @return {@code true} if a matching give rule exists and all preconditions
     *         are satisfied; {@code false} otherwise
     */
    @Override
    public boolean validate(GameData data) {
        // clear previous state
        matchedRule = null;

        if (data == null) {
            return false;
        }

        // no turns left => cannot execute
        if (data.isOutOfTurns()) {
            return false;
        }

        // current location
        String currentLocationName = data.getCurrentLocation();
        if (currentLocationName == null) {
            return false;
        }

        Location currentLocation = data.getLocationByName(currentLocationName);
        if (currentLocation == null) {
            return false;
        }

        
        if (characterName == null || objectName == null) {
            return false;
        }
        Character character = currentLocation.getCharacterByName(characterName.trim());
        if (character == null) {
            return false;
        }

        // inventory must contain the item
        Inventory inventory = data.getInventory();
        if (inventory == null) {
            return false;
        }
        matchedItem = null;
        for (GameObject obj : inventory.getAllObjects()) {
            if (obj != null && obj.getName() != null && obj.getName().equalsIgnoreCase(objectName.trim())) {
                matchedItem = obj;
                break;
            }
        }
        if (matchedItem == null) {
            return false;
        }

        // from all GiveRules, find one applicable to (characterName, objectName)
        List<GiveRule> rules = data.getGiveRules();
        if (rules == null) {
            return false;
        }

        for (GiveRule rule : rules) {
            if (rule != null && rule.applicable(characterName, objectName)) {
                matchedRule = rule; // reserved for execute()
                return true;
            }
        }

        // no rule accepts this give action
        return false;
    }

    /**
     * Executes the give action after a successful validation.
     * This method assumes that {@link #validate(GameData)} has already been
     * called and returned {@code true} for the same command instance.
     *
     * @param engine the {@link GameEngine} used to access {@link GameData} and
     *               {@link GameUI}
     */
    @Override
    public void execute(GameEngine engine) {
        GameData data = engine.getGameData();
        GameUI ui = engine.getGameUI();

        if (data == null) {
            return;
        }

        
        if (matchedRule == null) {
            if (ui != null) {
                String failMsg = data.getCommandMessage("give", false);
                if (failMsg == null) {
                    failMsg = "It has no need for that object.";
                }
                ui.displayMessage(failMsg);
            }
            return;
        }

        
        if (matchedItem != null) {
            data.removeObjectFromInventory(matchedItem.getName());
        } else {
            data.removeObjectFromInventory(objectName);
        }

        
        String line = matchedRule.getText();
        if (ui != null && line != null && !line.trim().isEmpty()) {
            ui.displayMessage(line);
        }

        
        List<String> resultNames = matchedRule.getResultingObjects();
        if (resultNames != null) {
            for (String name : resultNames) {
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }
                GameObject reward = data.getObjectByName(name);
                if (reward != null) {
                    data.addObjectToInventory(reward);
                }
            }
        }

        
        if (matchedRule.isEndGame()) {
            this.requestEndForRule(matchedRule.getName());
        }
    }
}
