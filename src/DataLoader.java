
/**
 * DataLoader.java implements the IDataLoader interface to load game data from a JSON file.
 * * @author Jialin Li
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class DataLoader implements IDataLoader {
    /**
     * Constructs a new DataLoader instance.
     */
    public DataLoader() {
    }

    /**
     * Loads and parses game data from the specified file path.
     * * @param filePath the path to the game data file
     * 
     * @return the loaded GameData instance
     */
    public GameData loadGameData(String filePath) throws IOException {
        String json = new String(
                java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)),
                java.nio.charset.StandardCharsets.UTF_8);

        Gson gson = new Gson();

        
        
        if (filePath.toLowerCase().endsWith("autosave.json")) {
            
            return gson.fromJson(json, GameData.class);
        } else {
            
            RawGameData rawData = gson.fromJson(json, RawGameData.class);
            return convertToGameData(rawData);
        }
    }

    /**
     * Converts raw JSON data structure to GameData with proper object references.
     */
    private GameData convertToGameData(RawGameData raw) {
        // Build object map for quick lookup
        Map<String, RawGameObject> objectMap = new HashMap<>();
        if (raw.objects != null) {
            for (RawGameObject obj : raw.objects) {
                objectMap.put(obj.name, obj);
            }
        }

        // Build character map for quick lookup
        Map<String, RawCharacter> characterMap = new HashMap<>();
        if (raw.characters != null) {
            for (RawCharacter ch : raw.characters) {
                characterMap.put(ch.name, ch);
            }
        }

        // Convert objects to GameObject instances
        Map<String, GameObject> gameObjectMap = new HashMap<>();
        for (RawGameObject rawObj : raw.objects) {
            List<GameObject> containedItems = new ArrayList<>();
            if (rawObj.containedObjects != null) {
                for (String containedName : rawObj.containedObjects) {
                    RawGameObject containedRaw = objectMap.get(containedName);
                    if (containedRaw != null) {
                        GameObject contained = new GameObject(
                                containedRaw.name,
                                containedRaw.description,
                                containedRaw.image,
                                containedRaw.attributes,
                                new ArrayList<>(), // Nested containment not supported yet
                                containedRaw.pickable);
                        containedItems.add(contained);
                        gameObjectMap.put(containedName, contained);
                    }
                }
            }

            GameObject gameObj = new GameObject(
                    rawObj.name,
                    rawObj.description,
                    rawObj.image,
                    rawObj.attributes,
                    containedItems,
                    rawObj.pickable);
            gameObjectMap.put(rawObj.name, gameObj);
        }

        GameObjectCollection allObjects = new GameObjectCollection(gameObjectMap);

        // Convert characters
        List<Character> characters = new ArrayList<>();
        Map<String, GiveRule> giveRuleMap = new HashMap<>();

        // First pass: create give rules
        if (raw.giveRules != null) {
            for (RawGiveRule rawRule : raw.giveRules) {
                GiveRule rule = new GiveRule(
                        rawRule.name,
                        rawRule.character,
                        rawRule.object,
                        rawRule.text,
                        rawRule.resultingObjects != null ? rawRule.resultingObjects : new ArrayList<>(),
                        rawRule.endGame);
                giveRuleMap.put(rawRule.name, rule);
            }
        }

        // Second pass: create characters with give rules
        for (RawCharacter rawChar : raw.characters) {
            GiveRule giveRule = null;
            if (rawChar.giveRule != null && !rawChar.giveRule.isEmpty()) {
                giveRule = giveRuleMap.get(rawChar.giveRule);
            }

            Character character = new Character(
                    rawChar.name,
                    rawChar.description,
                    rawChar.image,
                    rawChar.dialoguePhrases != null ? rawChar.dialoguePhrases : new ArrayList<>(),
                    giveRule,
                    rawChar.noMoreDialogueMessage != null ? rawChar.noMoreDialogueMessage : "",
                    rawChar.currentPhraseIndex);
            characters.add(character);
            characterMap.put(rawChar.name, rawChar);
        }

        // Convert locations
        List<Location> locations = new ArrayList<>();

        for (RawLocation rawLoc : raw.locations) {
            // Create connections
            List<Connection> connections = new ArrayList<>();
            if (rawLoc.connections != null) {
                for (RawConnection rc : rawLoc.connections) {
                    if (rc != null) {
                        connections.add(new Connection(rc.name, rc.description, rc.targetLocationName));
                    }
                }
            } else if (rawLoc.connection != null) { 
                connections.add(new Connection(
                        rawLoc.connection.name,
                        rawLoc.connection.description,
                        rawLoc.connection.targetLocationName));
            }

            // Find characters by name
            List<Character> locCharacters = new ArrayList<>();
            if (rawLoc.characters != null) {
                for (String cname : rawLoc.characters) {
                    for (Character ch : characters) {
                        if (ch.getName().equals(cname)) {
                            locCharacters.add(ch);
                            break;
                        }
                    }
                }
            } else if (rawLoc.character != null && !rawLoc.character.isEmpty()) { 
                for (Character ch : characters) {
                    if (ch.getName().equals(rawLoc.character)) {
                        locCharacters.add(ch);
                        break;
                    }
                }
            }

            // Create location
            Location location = new Location(
                    rawLoc.name,
                    rawLoc.description,
                    rawLoc.image != null ? rawLoc.image : "",
                    rawLoc.message != null ? rawLoc.message : "",
                    connections,
                    locCharacters);

            // Add objects to location
            if (rawLoc.objects != null) {
                for (String objName : rawLoc.objects) {
                    GameObject obj = gameObjectMap.get(objName);
                    if (obj != null) {
                        location.addObject(obj);
                    }
                }
            }

            locations.add(location);
        }

        // Convert use rules
        List<UseRule> useRules = new ArrayList<>();
        if (raw.useRules != null) {
            for (RawUseRule rawRule : raw.useRules) {
                UseRule rule = new UseRule(
                        rawRule.objectNames != null ? rawRule.objectNames : new ArrayList<>(),
                        rawRule.subjectAttribute,
                        rawRule.resultingObjects != null ? rawRule.resultingObjects : new ArrayList<>(),
                        rawRule.text != null ? rawRule.text : null,
                        rawRule.resultLocation != null ? rawRule.resultLocation : null,
                        rawRule.placeInInventory,
                        rawRule.endGame);
                useRules.add(rule);
            }
        }

        // Convert give rules to list
        List<GiveRule> giveRules = new ArrayList<>(giveRuleMap.values());

        // Create GameData
        return new GameData(
                raw.startingMessage,
                raw.startingLocation,
                raw.endingMessage,
                raw.endingLocation,
                raw.startingLocation, // currentLocation starts as startingLocation
                raw.turnLimit,
                locations,
                allObjects,
                characters,
                useRules,
                giveRules,
                raw.icons != null ? raw.icons : new HashMap<>());
    }

    // Inner classes for JSON deserialization
    private static class RawGameData {
        String startingMessage;
        String startingLocation;
        String endingMessage;
        String endingLocation;
        int turnLimit;
        List<String> startingInventory;
        Map<String, String> icons;
        List<RawLocation> locations;
        List<RawGameObject> objects;
        List<RawCharacter> characters;
        List<RawUseRule> useRules;
        List<RawGiveRule> giveRules;
    }

    private static class RawLocation {
        String name;
        String description;
        String image;
        String message;
        List<String> objects;
        List<RawConnection> connections; 
        RawConnection connection; 
        List<String> characters; 
        String character; 
    }

    private static class RawConnection {
        String name;
        String description;
        String targetLocationName;
    }

    private static class RawGameObject {
        String name;
        String description;
        String image;
        List<String> attributes;
        List<String> containedObjects;
        boolean pickable;
        List<String> useRuleNames;
    }

    private static class RawCharacter {
        String name;
        String description;
        String image;
        List<String> dialoguePhrases;
        String giveRule; // GiveRule name reference
        String noMoreDialogueMessage;
        int currentPhraseIndex;
    }

    private static class RawUseRule {
        String name;
        List<String> objectNames;
        String subjectAttribute;
        String text;
        List<String> resultingObjects;
        String resultLocation;
        boolean placeInInventory;
        boolean endGame;
    }

    private static class RawGiveRule {
        String name;
        String character;
        String object;
        String text;
        List<String> resultingObjects;
        boolean endGame;
    }
}