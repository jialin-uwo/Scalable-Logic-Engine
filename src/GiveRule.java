import java.util.ArrayList;
import java.util.List;

/**
 * GiveRule.java
 *
 * Represents a rule that defines what happens when the player gives a
 * specified object to a particular character. Rules may provide dialogue
 * text, award resulting objects, and optionally mark the action as
 * ending the game.
 *
 * Rules are loaded from the JSON data file and evaluated by
 * {@link GiveCommand#validate(GameData)}.
 *
 * *  @author Junqi Zheng
 */
public class GiveRule implements IGiveRule {
    /**
     * The unique name/identifier of this rule (e.g., "givePocketWatchToCaretaker")
     */
    private String name;

    /** The name of the character who receives the object (e.g., "Caretaker") */
    private String characterName;

    /** The name of the object that the player must give (e.g., "Pocket Watch") */
    private String objectName;

    /** The dialogue text spoken by the character after receiving the object */
    private String text;

    /** List of reward object names given to the player after the exchange */
    private List<String> resultingObjects;

    /** Whether triggering this rule ends the game (checked by GameEngine) */
    private boolean endGame;

    /**
     * Constructs a new GiveRule with the specified properties.
     *
     * @param name             the unique name of this rule (e.g.,
     *                         "givePocketWatchToCaretaker")
     * @param characterName    the name of the character who receives the object
     * @param objectName       the name of the object that must be given
     * @param text             the dialogue text shown when this rule is triggered
     * @param resultingObjects the list of reward object names given to the player
     *                         (can be null)
     * @param endGame          whether triggering this rule ends the game
     */
    public GiveRule(String name,
            String characterName,
            String objectName,
            String text,
            List<String> resultingObjects,
            boolean endGame) {
        this.name = name;
        this.characterName = characterName;
        this.objectName = objectName;
        this.text = text;
        this.resultingObjects = (resultingObjects != null) ? new ArrayList<>(resultingObjects)
                : new ArrayList<>();
        this.endGame = endGame;
    }

    /**
     * Returns the rule identifier.
     */
    public String getName() {
        return this.name;
    }
    // ----------------------
    // Getters / Setters
    // ----------------------

    /**
     * Gets the character name.
     *
     * @return The name of the character
     */
    public String getCharacterName() {
        return characterName;
    }

    /**
     * Sets the character name.
     *
     * @param characterName The name of the character
     */
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    /**
     * Gets the object name.
     *
     * @return The name of the object
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets the object name.
     *
     * @param objectName The name of the object
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Gets the rule text (dialogue).
     *
     * @return The dialogue text shown when this rule triggers
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the rule text.
     *
     * @param text the dialogue to show
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the list of resulting object names.
     *
     * @return List of objects given to the player in return
     */
    public List<String> getResultingObjects() {
        return new ArrayList<>(resultingObjects);
    }

    /**
     * Sets the list of resulting object names.
     *
     * @param resultingObjects List of objects to give to the player
     */
    public void setResultingObjects(List<String> resultingObjects) {
        this.resultingObjects = (resultingObjects != null) ? new ArrayList<>(resultingObjects)
                : new ArrayList<>();
    }

    /**
     * Checks if this rule ends the game.
     *
     * @return true if giving this object ends the game, false otherwise
     */
    public boolean isEndGame() {
        return endGame;
    }

    /**
     * Sets whether this rule ends the game.
     *
     * @param endGame true to end the game, false otherwise
     */
    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    // ----------------------
    // Core logic
    // ----------------------

    /**
     * Determines if this rule should trigger for the given character and object.
     *
     * @param givenCharacterName 
     * @param givenObjectName    
     * @return true if this rule applies; false otherwise
     */
    @Override
    public boolean applicable(String givenCharacterName, String givenObjectName) {
        if (givenCharacterName == null || givenObjectName == null) {
            return false;
        }
        if (this.characterName == null || this.objectName == null) {
            return false;
        }

        
        String chInput = givenCharacterName.trim();
        String objInput = givenObjectName.trim();

        return this.characterName.equalsIgnoreCase(chInput)
                && this.objectName.equalsIgnoreCase(objInput);
    }
}
