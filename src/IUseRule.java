
import java.util.List;

/**
 * Specialized rule that reacts to using specific objects in the game.
 * UseRules define validation logic for object usage and resulting outcomes.
 * @author Xinyan Cai 
 */
public interface IUseRule {

    /**
     * Gets the list of object names required for this rule.
     *
     * @return List of object names
     */
    List<String> getObjectNames();

    /**
     * Gets the unique name/identifier for this rule.
     *
     * @return the rule name (should be unique for each rule)
     */
    String getName();

    /**
     * Gets the subject attribute required for validation.
     *
     * @return The attribute name, or empty string if no attribute check is needed
     */
    String getSubjectAttribute();

    /**
     * Gets the list of resulting object names produced by this rule.
     *
     * @return List of resulting object names
     */
    List<String> getResultingObjects();

    /**
     * Determines if this rule should trigger by matching the provided objects.
     * Validation has two modes:
     * 1. If subjectAttribute is empty or null, only match object names
     * 2. If subjectAttribute is specified, match both names and verify the
     * attribute
     *
     * @param gameData The game data containing all objects
     * @param objects  The collection of objects to validate
     * @return true if validation passes; false otherwise
     */
    boolean applicable(GameData gameData, GameObjectCollection objects);
}
