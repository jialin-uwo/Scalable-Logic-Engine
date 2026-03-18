
import java.util.ArrayList;
import java.util.List;

/**
 * UseRule.java
 *
 * Data-driven rule describing how one or two objects interact when the
 * player issues the 'use' command. A UseRule defines which object names
 * and optional subject attribute are required, what resulting objects to
 * create, whether to place results into inventory, and whether triggering
 * the rule ends the game.
 *
 * The rule matching logic is case-insensitive and supports a wildcard
 * object name of '*'.
 *
 * Author: Junqi Zheng
 */
public class UseRule {

    private final List<String> objectNames;
    private final String subjectAttribute;
    private final List<String> resultingObjects;
    private final String message;
    private final String resultLocation;
    private final boolean placeInInventory;
    private final boolean endGame;

    // Constructor for standard use case (no location change)
    public UseRule(List<String> objectNames, String subjectAttribute, List<String> resultingObjects) {
        this(objectNames, subjectAttribute, resultingObjects, null, null, false);
    }

    // Constructor for use case with message and no location change
    public UseRule(List<String> objectNames, String subjectAttribute, List<String> resultingObjects, String message) {
        this(objectNames, subjectAttribute, resultingObjects, message, null, false);
    }

    // Constructor for end game rule (no location change)
    public UseRule(List<String> objectNames, String subjectAttribute, List<String> resultingObjects, boolean endGame) {
        this(objectNames, subjectAttribute, resultingObjects, null, null, endGame);
    }

    // Constructor for use case with message and result location
    public UseRule(List<String> objectNames, String subjectAttribute, List<String> resultingObjects, String message,
            String resultLocation) {
        this(objectNames, subjectAttribute, resultingObjects, message, resultLocation, false);
    }

    // Full Constructor (Base)
    public UseRule(List<String> objectNames, String subjectAttribute, List<String> resultingObjects, String message,
            String resultLocation, boolean endGame) {
        this(objectNames, subjectAttribute, resultingObjects, message, resultLocation, false, endGame);
    }

    // Full constructor with placeInInventory option
    public UseRule(List<String> objectNames, String subjectAttribute, List<String> resultingObjects, String message,
            String resultLocation, boolean placeInInventory, boolean endGame) {
        this.objectNames = new ArrayList<>(objectNames);
        this.subjectAttribute = subjectAttribute != null ? subjectAttribute.toLowerCase() : null;
        this.resultingObjects = resultingObjects != null ? new ArrayList<>(resultingObjects) : new ArrayList<>();
        this.message = message;
        this.resultLocation = resultLocation;
        this.placeInInventory = placeInInventory;
        this.endGame = endGame;
        // NOTE: The class likely requires a constructor matching the fields.
    }

    /**
     * Determines whether this UseRule is applicable to the given object names and
     * attribute set.
     * 
     * @param providedNames      The actual object names provided by the player
     *                           (e.g., ["Cup", "Faucet"])
     * @param providedAttributes The set of attributes of the provided objects
     *                           (e.g., ["container", "water_source"])
     * @return true if the rule matches; false otherwise
     */
    public boolean applicable(List<String> providedNames, List<String> providedAttributes) {
        if (providedNames == null || objectNames == null || providedNames.size() != objectNames.size())
            return false;
        // Allow unordered matching and support wildcard "*" in rule names.
        List<String> loweredProvided = new ArrayList<>();
        for (String p : providedNames) {
            loweredProvided.add(p == null ? null : p.toLowerCase());
        }
        for (String name : objectNames) {
            if (name == null)
                return false;
            String rn = name.toLowerCase();
            if ("*".equals(rn)) {
                // wildcard accepts any corresponding provided name
                continue;
            }
            if (!loweredProvided.contains(rn))
                return false;
        }
        if (subjectAttribute != null && !subjectAttribute.isEmpty()) {
            if (providedAttributes == null)
                return false;
            List<String> loweredAttrs = new ArrayList<>();
            for (String a : providedAttributes)
                loweredAttrs.add(a == null ? null : a.toLowerCase());
            if (!loweredAttrs.contains(subjectAttribute.toLowerCase()))
                return false;
        }
        return true;
    }

    /**
     * Generates a compact, deterministic identifier for this rule based on
     * the object names and optional subject attribute. This identifier is
     * used by the engine when commands request an end-by-rule.
     *
     * @param objectNames      list of object names declared in the rule
     * @param subjectAttribute optional subject attribute (may be null)
     * @return a generated rule identifier string
     */
    private String generateRuleName(List<String> objectNames, String subjectAttribute) {
        StringBuilder sb = new StringBuilder("use");
        for (String name : objectNames) {
            sb.append(":");
            sb.append(name.replaceAll("\\s+", ""));
        }
        if (subjectAttribute != null && !subjectAttribute.isEmpty()) {
            sb.append(":").append(subjectAttribute.replaceAll("\\s+", ""));
        }
        return sb.toString();
    }

    // Other methods
    public List<String> getObjectNames() {
        return objectNames;
    }

    public String getSubjectAttribute() {
        return subjectAttribute;
    }

    // ... (All other methods and properties must be present here) ...

    public String getMessage() {
        return message;
    }

    public List<String> getResultingObjects() {
        return resultingObjects;
    }

    public boolean isPlaceInInventory() {
        return placeInInventory;
    }

    public boolean isEndGame() {
        return endGame;
    }

    public String getResultLocation() {
        return resultLocation;
    }

    /**
     * Returns a stable name/identifier for this rule. This is used by the
     * engine to reference rules by name when commands request an end.
     */
    public String getName() {
        return generateRuleName(this.objectNames, this.subjectAttribute);
    }

}