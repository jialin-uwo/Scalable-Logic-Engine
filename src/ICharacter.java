/**
 * Defines the contract for Non-Player Characters (NPCs) that the player can
 * interact with. This interface extends IEntity and
 * specifies unique behaviors required for the Talk and Give commands.
 * 
 * @author Jialin Li
 */
public interface ICharacter extends IEntity {
    /**
     * Advances the character's dialogue and returns the next phrase to be spoken.
     * 
     * @return the next phrase; returns a default message if no phrases remain
     */
    String consumePhrase();

    /**
     * Retrieves the give rule associated with this character, if any.
     * 
     * @return the give rule for this character, or null if none exists
     */
    GiveRule getGiveRule();
}
