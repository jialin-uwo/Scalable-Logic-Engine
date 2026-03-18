import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Location Functionality Validation Tests.
 * Tests location-specific functional requirements (VT-LOC-001 to VT-LOC-007).
 * Validates that all locations have unique names, descriptions, objects,
 * connections, and proper starting/ending location designations.
 * 
 * @author Jialin Li
 */
@DisplayName("Location Functionality Validation Tests")
public class LocationFunctionalityValidationTest {

    private GameData gameData;
    private DataLoader dataLoader;

    /**
     * Sets up the test environment by loading game data from DataFile.json.
     * Initializes DataLoader and GameData instances for testing.
     * 
     * @throws Exception if data file cannot be loaded
     */
    @BeforeEach
    void setUp() throws Exception {
        dataLoader = new DataLoader();
        gameData = dataLoader.loadGameData("assets/data/DataFile.json");
        assertNotNull(gameData, "GameData should be loaded successfully");
    }

    /**
     * Tests that all location names are unique (VT-LOC-001).
     * Verifies no duplicate location names exist in the game data.
     */
    @Test
    @DisplayName("VT-LOC-001: Verify locations have unique names")
    void testLocationNamesAreUnique() {
        List<Location> locations = gameData.getLocations();
        Set<String> locationNames = new HashSet<>();

        for (Location location : locations) {
            String name = location.getName();
            assertFalse(locationNames.contains(name),
                    String.format("Duplicate location name found: %s", name));
            locationNames.add(name);
        }
    }

    /**
     * Tests that all locations have non-empty descriptions (VT-LOC-002).
     * Verifies each location has a valid description string.
     */
    @Test
    @DisplayName("VT-LOC-002: Verify all locations have descriptions")
    void testAllLocationsHaveDescriptions() {
        List<Location> locations = gameData.getLocations();

        for (Location location : locations) {
            String description = location.getDescription();
            assertNotNull(description,
                    String.format("Location '%s' has null description", location.getName()));
            assertFalse(description.trim().isEmpty(),
                    String.format("Location '%s' has empty description", location.getName()));
        }
    }

    /**
     * Tests that locations properly show their contained objects (VT-LOC-003).
     * Verifies at least one location contains objects and they can be retrieved.
     */
    @Test
    @DisplayName("VT-LOC-003: Verify locations show contained objects")
    void testLocationsShowContainedObjects() {
        List<Location> locations = gameData.getLocations();
        boolean foundLocationWithObjects = false;

        for (Location location : locations) {
            GameObjectCollection objects = location.getAllObjects();
            assertNotNull(objects,
                    String.format("Location '%s' has null objects collection", location.getName()));

            if (objects.size() > 0) {
                foundLocationWithObjects = true;
                // Verify we can retrieve objects
                List<GameObject> objectList = objects.getAllObjects();
                assertNotNull(objectList, "Object list should not be null");
                assertTrue(objectList.size() > 0, "Object list should contain objects");
            }
        }

        assertTrue(foundLocationWithObjects, "At least one location should contain objects");
    }

    /**
     * Tests that locations properly show their connections (VT-LOC-004).
     * Verifies at least one location has a valid connection with name and target.
     */
    @Test
    @DisplayName("VT-LOC-004: Verify locations show connections")
    void testLocationsShowConnections() {
        List<Location> locations = gameData.getLocations();
        boolean foundLocationWithConnection = false;

        for (Location location : locations) {
            List<Connection> connections = location.getConnections();
            if (connections != null && !connections.isEmpty()) {
                foundLocationWithConnection = true;
                for (Connection connection : connections) {
                    // Verify connection has valid properties
                    assertNotNull(connection.getName(),
                            String.format("Connection in location '%s' has null name", location.getName()));
                    assertNotNull(connection.getTargetLocationName(),
                            String.format("Connection in location '%s' has null target", location.getName()));
                }
            }
        }

        assertTrue(foundLocationWithConnection, "At least one location should have a connection");
    }

    /**
     * Tests that all locations have valid picture paths (VT-LOC-005).
     * Verifies each location has a non-empty picture path string.
     */
    @Test
    @DisplayName("VT-LOC-005: Verify locations have picture paths")
    void testLocationsHavePicturePaths() {
        List<Location> locations = gameData.getLocations();

        for (Location location : locations) {
            String picturePath = location.getPicturePath();
            assertNotNull(picturePath,
                    String.format("Location '%s' has null picture path", location.getName()));
            assertFalse(picturePath.trim().isEmpty(),
                    String.format("Location '%s' has empty picture path", location.getName()));
        }
    }

    /**
     * Tests that a valid starting location is designated (VT-LOC-006).
     * Verifies starting location is defined and exists in the locations list.
     */
    @Test
    @DisplayName("VT-LOC-006: Verify starting location designation")
    void testStartingLocationExists() {
        String startingLocationName = gameData.getStartingLocation();
        assertNotNull(startingLocationName, "Starting location should be defined");
        assertFalse(startingLocationName.trim().isEmpty(), "Starting location should have a name");

        // Verify starting location is in the locations list
        Location startingLocation = gameData.getLocationByName(startingLocationName);
        assertNotNull(startingLocation, "Starting location should exist in the locations list");
    }

    /**
     * Tests that a valid ending location is designated (VT-LOC-007).
     * Verifies ending location is defined and exists in the locations list.
     */
    @Test
    @DisplayName("VT-LOC-007: Verify ending location designation")
    void testEndingLocationExists() {
        String endingLocationName = gameData.getEndingLocation();
        assertNotNull(endingLocationName, "Ending location should be defined");
        assertFalse(endingLocationName.trim().isEmpty(), "Ending location should have a name");

        // Verify ending location is in the locations list
        Location endingLocation = gameData.getLocationByName(endingLocationName);
        assertNotNull(endingLocation, "Ending location should exist in the locations list");
    }
}
