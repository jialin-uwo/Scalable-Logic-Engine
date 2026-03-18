import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Unit tests for DataLoader (Data Layer)
 * Tests JSON loading and deserialization into Object Layer classes
 * 
 * @author Jialin Li
 */
public class DataLoaderTest {
    private DataLoader dataLoader;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        dataLoader = new DataLoader();
    }

    @Test
    public void testLoadGameData() throws IOException {
        // Create a simple test JSON file
        String jsonContent = """
                {
                    "startingMessage": "Welcome!",
                    "startingLocation": "hall",
                    "endingMessage": "You won!",
                    "endingLocation": "exit",
                    "turnLimit": 100,
                    "locations": [
                        {
                            "name": "hall",
                            "description": "A grand hall",
                            "imagePath": "hall.png",
                            "message": "You are in a hall",
                            "objects": [],
                            "connection": {
                                "name": "door",
                                "description": "A wooden door",
                                "targetLocationName": "room"
                            },
                            "character": null
                        }
                    ],
                    "objects": [
                        {
                            "name": "key",
                            "description": "A rusty key",
                            "image": "key.png",
                            "attributes": ["metal"],
                            "containedObjects": [],
                            "pickable": true
                        }
                    ],
                    "characters": [],
                    "useRules": [],
                    "giveRules": [],
                    "icons": {}
                }
                """;

        Path testFile = tempDir.resolve("test_data.json");
        Files.writeString(testFile, jsonContent);

        // Test loading
        GameData gameData = dataLoader.loadGameData(testFile.toString());

        assertNotNull(gameData);
        assertEquals("Welcome!", gameData.getStartingMessage());
        assertEquals("hall", gameData.getStartingLocation());
        assertEquals(100, gameData.getTurnLimit());
    }

    @Test
    public void testLoadGameDataWithObjects() throws IOException {
        String jsonContent = """
                {
                    "startingMessage": "Start",
                    "startingLocation": "room",
                    "endingMessage": "End",
                    "endingLocation": "exit",
                    "turnLimit": 50,
                    "locations": [],
                    "objects": [
                        {
                            "name": "sword",
                            "description": "A sharp sword",
                            "image": "sword.png",
                            "attributes": ["weapon", "metal"],
                            "containedObjects": [],
                            "pickable": true
                        },
                        {
                            "name": "chest",
                            "description": "A locked chest",
                            "image": "chest.png",
                            "attributes": ["container"],
                            "containedObjects": [],
                            "pickable": false
                        }
                    ],
                    "characters": [],
                    "useRules": [],
                    "giveRules": [],
                    "icons": {}
                }
                """;

        Path testFile = tempDir.resolve("test_objects.json");
        Files.writeString(testFile, jsonContent);

        GameData gameData = dataLoader.loadGameData(testFile.toString());

        // Verify Object Layer instances were created correctly
        GameObject sword = gameData.getObjectByName("sword");
        assertNotNull(sword);
        assertEquals("sword", sword.getName());
        assertTrue(sword.pickable());
        assertTrue(sword.hasAttribute("weapon"));

        GameObject chest = gameData.getObjectByName("chest");
        assertNotNull(chest);
        assertFalse(chest.pickable());
    }

    @Test
    public void testLoadInvalidFile() {
        assertThrows(IOException.class, () -> {
            dataLoader.loadGameData("nonexistent.json");
        });
    }
}
