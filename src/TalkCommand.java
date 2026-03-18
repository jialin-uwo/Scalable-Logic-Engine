/**
 * TalkCommand.java
 *
 * This class implements the "talk" command in the adventure game.
 * When executed, the command advances the character's dialogue by
 * consuming the next available phrase from {@link GameData} and
 * displaying it through {@link GameUI}. If there is no phrase left,
 * a fallback message is shown instead.
 *
 * @author Xinyan Cai
 */

/**
 * Command that lets the player talk to a character in the current location.
 * It advances the character's dialogue (if any) and shows the next phrase.
 */
public class TalkCommand extends ICommand {

    /** Name of the character the player choose. */
    private final String characterName;

    /**
     * Constructs a Talk command targeting the given character name.
     *
     * @param characterName name of the character to talk to (player's input);
     *                      used to match the character at the current location
     */
    public TalkCommand(String characterName) {
        this.characterName = characterName;
    }

    /**
     * Validates whether this {@code TalkCommand} can be executed
     * in the current game state.
     * 
     * @param data the {@link GameData} instance representing the current game state
     * @return {@code true} if the player can talk to the character,
     *         {@code false} otherwise
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

        String currentLocationName = data.getCurrentLocation();
        if (currentLocationName == null) {
            return false;
        }

        Location currentLocation = data.getLocationByName(currentLocationName);
        if (currentLocation == null) {
            return false;
        }

        if (characterName == null) {
            return false;
        }
        Character character = currentLocation.getCharacterByName(characterName.trim());
        if (character == null) {
            return false;
        }
        return true;
    }

    /**
     * Executes the {@code TalkCommand}, advancing the dialogue of the
     * character at the current location.
     * It assumes validation has already succeeded before execution.
     *
     * @param engine the {@link GameEngine} used to access {@link GameData}
     *               and {@link GameUI} for updating state and displaying text
     */
    @Override
    public void execute(GameEngine engine) {
        GameData data = engine.getGameData();
        GameUI ui = engine.getGameUI();

        String currentLocationName = data.getCurrentLocation();
        Location currentLocation = data.getLocationByName(currentLocationName);

        Character character = currentLocation.getCharacterByName(characterName == null ? null : characterName.trim());
        if (character == null) {
            ui.displayMessage("No such character here.");
            return;
        }
        String phrase = data.consumePhrase(character);
        if (phrase == null || phrase.trim().isEmpty()) {
            ui.displayMessage("No more response");
        } else {
            ui.displayMessage(phrase);
        }
    }
}
