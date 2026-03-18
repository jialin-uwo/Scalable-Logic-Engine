import java.util.ArrayList;
import java.util.List;

/**
 * Represents a non-player character (NPC) in the adventure game.
 * Characters have dialogue that can be consumed through interactions,
 * and may have associated give rules for item exchanges.
 * 
 * @author Peiyong Wang
 */
public class Character implements ICharacter {

    // IEntity fields
    private final String name;
    private final String description;
    private final String imagePath;

    // ICharacter specific fields
    private final List<String> dialoguePhrases;
    private final GiveRule giveRule;
    private final String noMoreDialogueMessage;
    private int currentPhraseIndex;

    /**
     * Constructs a new Character with the specified properties.
     * 
     * @param name                  the name of the character
     * @param description           the description of the character
     * @param imagePath             the path to the character's image
     * @param dialoguePhrases       the list of dialogue phrases (can be null)
     * @param giveRule              the give rule for item exchanges (can be null)
     * @param noMoreDialogueMessage the message when dialogue is exhausted (can be
     *                              null)
     * @param currentPhraseIndex    the starting index for dialogue phrases
     */
    public Character(String name,
            String description,
            String imagePath,
            List<String> dialoguePhrases,
            GiveRule giveRule,
            String noMoreDialogueMessage,
            int currentPhraseIndex) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        // defensive copy to avoid external modification
        this.dialoguePhrases = (dialoguePhrases == null)
                ? new ArrayList<>()
                : new ArrayList<>(dialoguePhrases);
        this.giveRule = giveRule;
        this.noMoreDialogueMessage = (noMoreDialogueMessage != null)
                ? noMoreDialogueMessage
                : "They have nothing more to say.";
        this.currentPhraseIndex = Math.max(0, currentPhraseIndex);
    }

    // ===== IEntity methods =====

    /**
     * Gets the name of this character.
     * 
     * @return the character's name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this character.
     * 
     * @return the character's description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the image path for this character.
     * 
     * @return the path to the character's image
     */
    @Override
    public String getImagePath() {
        return imagePath;
    }

    // ===== ICharacter methods =====

    /**
     * Advances the character's dialogue and returns the next phrase.
     * 
     * @return the next dialogue phrase, or noMoreDialogueMessage if exhausted
     */
    @Override
    public String consumePhrase() {
        if (currentPhraseIndex < dialoguePhrases.size()) {
            String phrase = dialoguePhrases.get(currentPhraseIndex);
            currentPhraseIndex++;
            return phrase;
        }
        return noMoreDialogueMessage;
    }

    /**
     * Gets the give rule associated with this character.
     * 
     * @return the GiveRule for item exchanges, or null if none exists
     */
    @Override
    public GiveRule getGiveRule() {
        return giveRule;
    }
}
