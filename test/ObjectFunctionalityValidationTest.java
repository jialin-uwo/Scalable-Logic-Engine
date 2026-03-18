import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Object Functionality Validation Tests.
 * Tests object-specific functional requirements (VT-OBJ-001 to VT-OBJ-006).
 * Validates object attributes, pickability, descriptions, and containment
 * functionality.
 * 
 * @author Jialin Li
 */
@DisplayName("Object Functionality Validation Tests")
public class ObjectFunctionalityValidationTest {

    private GameData gameData;
    private DataLoader dataLoader;

    /**
     * Sets up the test environment before each test.
     * Loads game data from the JSON file and validates successful loading.
     */
    @BeforeEach
    void setUp() throws Exception {
        dataLoader = new DataLoader();
        gameData = dataLoader.loadGameData("assets/data/DataFile.json");
        assertNotNull(gameData, "GameData should be loaded successfully");
    }

    /**
     * Tests that objects have unique names (VT-OBJ-001).
     * Verifies that no two objects in the game share the same name.
     */
    @Test
    @DisplayName("VT-OBJ-001: Verify objects have unique names")
    void testObjectNamesAreUnique() {
        List<Location> locations = gameData.getLocations();
        Set<String> objectNames = new HashSet<>();

        for (Location location : locations) {
            List<GameObject> objects = location.getAllObjects().getAllObjects();
            for (GameObject obj : objects) {
                String name = obj.getName();
                assertFalse(objectNames.contains(name),
                        String.format("Duplicate object name found: %s", name));
                objectNames.add(name);
            }
        }
    }

    /**
     * Tests that all objects have descriptions (VT-OBJ-002).
     * Verifies that every object has a non-null, non-empty description.
     */
    @Test
    @DisplayName("VT-OBJ-002: Verify all objects have descriptions")
    void testAllObjectsHaveDescriptions() {
        List<Location> locations = gameData.getLocations();

        for (Location location : locations) {
            List<GameObject> objects = location.getAllObjects().getAllObjects();
            for (GameObject obj : objects) {
                String description = obj.getDescription();
                assertNotNull(description,
                        String.format("Object '%s' has null description", obj.getName()));
                assertFalse(description.trim().isEmpty(),
                        String.format("Object '%s' has empty description", obj.getName()));
            }
        }
    }

    /**
     * Tests that pickable objects can be picked up (VT-OBJ-003).
     * Verifies that at least one object has the pickable flag set to true.
     */
    @Test
    @DisplayName("VT-OBJ-003: Verify pickable objects can be picked up")
    void testPickableObjectsFunctionality() {
        List<Location> locations = gameData.getLocations();
        boolean foundPickableObject = false;

        for (Location location : locations) {
            List<GameObject> objects = location.getAllObjects().getAllObjects();
            for (GameObject obj : objects) {
                if (obj.pickable()) {
                    foundPickableObject = true;
                    // Verify pickable flag is true
                    assertTrue(obj.pickable(),
                            String.format("Object '%s' should be pickable", obj.getName()));
                    break;
                }
            }
            if (foundPickableObject)
                break;
        }

        assertTrue(foundPickableObject, "At least one pickable object should exist");
    }

    /**
     * Tests that non-pickable objects cannot be picked (VT-OBJ-004).
     * Verifies that at least one object has the pickable flag set to false.
     */
    @Test
    @DisplayName("VT-OBJ-004: Verify non-pickable objects cannot be picked")
    void testNonPickableObjectsFunctionality() {
        List<Location> locations = gameData.getLocations();
        boolean foundNonPickableObject = false;

        for (Location location : locations) {
            List<GameObject> objects = location.getAllObjects().getAllObjects();
            for (GameObject obj : objects) {
                if (!obj.pickable()) {
                    foundNonPickableObject = true;
                    // Verify the pickable flag is false
                    assertFalse(obj.pickable(),
                            String.format("Object '%s' should not be pickable", obj.getName()));
                    break;
                }
            }
            if (foundNonPickableObject)
                break;
        }

        assertTrue(foundNonPickableObject, "At least one non-pickable object should exist");
    }

    /**
     * Tests that object attributes work in rules if used (VT-OBJ-005).
     * Verifies that objects referenced in attribute-based UseRules have the
     * required attributes.
     */
    @Test
    @DisplayName("VT-OBJ-005: Verify object attributes work in rules (if used)")
    void testObjectAttributesInRules() {
        List<UseRule> useRules = gameData.getUseRules();

        // Check if any UseRules use attributes
        for (UseRule rule : useRules) {
            String attribute = rule.getSubjectAttribute();
            if (attribute != null && !attribute.isEmpty()) {
                // If attribute is specified, verify objects have it
                boolean foundObjectWithAttribute = false;
                for (String objName : rule.getObjectNames()) {
                    if ("*".equals(objName)) {
                        // wildcard means any object may satisfy the attribute requirement
                        for (GameObject candidate : gameData.getObjects().getAllObjects()) {
                            if (candidate != null && candidate.hasAttribute(attribute)) {
                                foundObjectWithAttribute = true;
                                break;
                            }
                        }
                        if (foundObjectWithAttribute)
                            break;
                    } else {
                        GameObject obj = gameData.getObjectByName(objName);
                        if (obj != null && obj.hasAttribute(attribute)) {
                            foundObjectWithAttribute = true;
                            assertTrue(obj.hasAttribute(attribute),
                                    String.format("Object '%s' should have attribute '%s'", objName, attribute));
                            break;
                        }
                    }
                }
                // If attribute-based rule exists, at least one object should have it
                assertTrue(foundObjectWithAttribute,
                        String.format("At least one object should have attribute '%s'", attribute));
            }
        }

        // Test passes whether or not attributes are used - they are optional
        assertTrue(true, "Attribute validation completed");
    }

    /**
     * Tests that contained objects can be revealed (VT-OBJ-006).
     * Verifies that at least one object contains other objects that can be revealed
     * through examination.
     */
    @Test
    @DisplayName("VT-OBJ-006: Verify contained objects can be revealed")
    void testContainedObjectsFunctionality() {
        List<Location> locations = gameData.getLocations();
        boolean foundObjectWithContainedItems = false;

        for (Location location : locations) {
            List<GameObject> objects = location.getAllObjects().getAllObjects();
            for (GameObject obj : objects) {
                List<GameObject> containedItems = obj.getContainedItems();
                if (containedItems != null && !containedItems.isEmpty()) {
                    foundObjectWithContainedItems = true;

                    // Verify each contained item exists
                    for (GameObject containedObj : containedItems) {
                        assertNotNull(containedObj,
                                "Contained object should not be null");
                        assertNotNull(containedObj.getName(),
                                "Contained object should have a name");
                    }
                    break;
                }
            }
            if (foundObjectWithContainedItems)
                break;
        }

        assertTrue(foundObjectWithContainedItems, "At least one object should contain other objects");
    }
}
