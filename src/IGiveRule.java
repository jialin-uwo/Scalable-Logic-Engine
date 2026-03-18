

/**
 * Specialized rule that reacts to giving an item to a character.
 * @Xinyan Cai
 */
public interface IGiveRule {

    /**
     * Checks whether this rule is applicable given the character name and object name.
     * @param givenCharacterName the character to whom the object is given
     * @param givenObjectName the object being given
     * @return true if this rule applies
     */
    boolean applicable(String givenCharacterName, String givenObjectName);
}

