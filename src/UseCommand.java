import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * UseCommand.java
 *
 * Implements the player 'use' command which attempts to apply a
 * data-driven {@link UseRule} to one or two objects. The command
 * performs case-insensitive matching, gathers object attributes
 * for attribute-based rules, removes used objects and creates
 * resulting objects/connections as defined by the rule.
 *
 * This class follows the ICommand contract: {@link #validate(GameData)}
 * is called prior to {@link #execute(GameEngine)} by the engine.
 *
 * Author: Jialin Li
 */
public class UseCommand extends ICommand {
    private final List<String> objectNames;
    private UseRule rule;

    /**
     * Constructs a new UseCommand.
     *
     * @param objectNames array of object names provided by the player
     *                    (one or two objects, multi-word names allowed)
     */
    public UseCommand(String[] objectNames) {
        if (objectNames != null) {
            this.objectNames = new ArrayList<>(Arrays.asList(objectNames));
        } else {
            this.objectNames = Collections.emptyList();
        }
    }

    /**
     * Returns the matched {@link UseRule} after {@link #validate(GameData)}
     * has been called. May be {@code null} if validation has not been run
     * or failed.
     *
     * @return the matched UseRule or {@code null}
     */
    public UseRule getRule() {
        return rule;
    }

    /**
     * Validates the command against the provided {@link GameData}.
     * This method builds a case-insensitive view of the provided object
     * names and collects attributes from the objects found in the
     * inventory and current location. Matching is delegated to
     * {@link UseRule#applicable(List, List)}.
     *
     * @param data current game state
     * @return {@code true} if a UseRule applies; {@code false} otherwise
     */
    @Override
    public boolean validate(GameData data) {
        if (data == null)
            return false;
        List<UseRule> rules = data.getUseRules();
        // Create a lowercase copy for case-insensitive matching so we don't
        // mutate the original `objectNames` (which are needed later during
        // execution to remove the correct items with original casing).
        List<String> inputNames = new ArrayList<>();
        for (String n : objectNames) {
            inputNames.add(n == null ? null : n.toLowerCase());
        }
        List<String> inputAttrs = new ArrayList<>();
        // Collect all attributes from inventory and current location for input objects
        String currentLocName = data.getCurrentLocation();
        Location currentLoc = data.getLocationByName(currentLocName);
        for (String lname : inputNames) {
            if (lname == null)
                continue;
            String lower = lname.toLowerCase();
            GameObject found = null;
            // search inventory (case-insensitive)
            for (GameObject o : data.getInventory().getAllObjects()) {
                if (o != null && o.getName() != null && o.getName().equalsIgnoreCase(lower)) {
                    found = o;
                    break;
                }
            }
            // search current location
            if (found == null && currentLoc != null) {
                for (GameObject o : currentLoc.getAllObjects().getAllObjects()) {
                    if (o != null && o.getName() != null && o.getName().equalsIgnoreCase(lower)) {
                        found = o;
                        break;
                    }
                }
            }
            // fallback to global definition
            if (found == null) {
                found = data.getObjectByName(lname);
            }
            if (found != null) {
                for (String a : found.getAttributes()) {
                    if (a != null)
                        inputAttrs.add(a.toLowerCase());
                }
            }
        }
        // Lowercase attributes for case-insensitive matching
        for (int i = 0; i < inputAttrs.size(); i++) {
            inputAttrs.set(i, inputAttrs.get(i).toLowerCase());
        }
        for (UseRule currentRule : rules) {
            // Delegate matching logic to UseRule.applicable to keep behavior centralized
            if (currentRule.applicable(inputNames, inputAttrs)) {
                this.rule = currentRule;
                return true;
            }
        }
        return false;
    }

    /**
     * Executes the use action. If {@link #validate(GameData)} was not
     * previously called, this method attempts a validation first and
     * reports failure via the UI if no rule matches.
     *
     * @param engine the game engine used to access data and UI
     */
    @Override
    public void execute(GameEngine engine) {
        if (engine == null)
            return;
        GameData data = engine.getGameData();
        // If execute is called directly without prior validate(), attempt to validate
        // now
        if (this.rule == null) {
            boolean ok = validate(data);
            if (!ok) {
                if (engine.getGameUI() != null) {
                    engine.getGameUI().displayMessage("That doesn't work.");
                }
                return;
            }
        }
        // Rule matched; proceed to execute the rule (no debug output).
        String currentLocName = data.getCurrentLocation();
        // Remove used objects (case-insensitive search in inventory then location)
        for (String objName : objectNames) {
            if (objName == null)
                continue;
            GameObject foundInInventory = null;
            for (GameObject o : data.getInventory().getAllObjects()) {
                if (o != null && o.getName() != null && o.getName().equalsIgnoreCase(objName)) {
                    foundInInventory = o;
                    break;
                }
            }
            if (foundInInventory != null) {
                data.removeObjectFromInventory(foundInInventory.getName());
                continue;
            }

            // search in current location (case-insensitive)
            GameObject foundInLoc = null;
            Location loc = data.getLocationByName(currentLocName);
            if (loc != null) {
                for (GameObject o : loc.getAllObjects().getAllObjects()) {
                    if (o != null && o.getName() != null && o.getName().equalsIgnoreCase(objName)) {
                        foundInLoc = o;
                        break;
                    }
                }
            }
            if (foundInLoc != null) {
                data.removeObjectFromLocation(currentLocName, foundInLoc);
            }
        }
        // Add resulting objects (log additions). Place into inventory if rule requests
        // it,
        // otherwise add to current location. Connections are still created when the
        // resulting object has the 'connection' attribute and rule specifies a
        // resultLocation.
        for (String resultName : rule.getResultingObjects()) {
            GameObject resultObj = data.getObjectByName(resultName);
            if (resultObj != null) {
                if (rule.isPlaceInInventory()) {
                    data.addObjectToInventory(resultObj);
                } else {
                    data.addObjectToLocation(currentLocName, resultObj);
                }

                // If resultObj is a connection, and rule specifies a resultLocation, add
                // connection
                if (resultObj.hasAttribute("connection") && rule.getResultLocation() != null) {
                    Location loc = data.getLocationByName(currentLocName);
                    if (loc != null) {
                        Connection conn = new Connection(resultObj.getName(), resultObj.getDescription(),
                                rule.getResultLocation());
                        loc.addConnection(conn);
                    }
                }
            }
        }
        // Show rule message to the player if present
        if (rule.getMessage() != null && !rule.getMessage().isEmpty()) {
            String msg = rule.getMessage();
            if (engine.getGameUI() != null) {
                engine.getGameUI().displayMessage(msg);
            }
        }
        // Do not automatically change player's current location when a use rule
        // creates a connection. The connection is added to the current location
        // and the player must explicitly 'go' to it.
        // Handle end game: request engine to validate the rule and perform the end
        if (rule.isEndGame()) {
            // Request the engine to validate and schedule the end by rule name
            this.requestEndForRule(rule.getName());
        }
    }

    // No persistent debug logging in production code.
}