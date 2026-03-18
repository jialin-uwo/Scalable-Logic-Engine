import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

/**
 * Unit tests for GameObjectCollection (Object Layer)
 * Tests game object collection management
 * 
 * @author Jialin Li
 */
public class GameObjectCollectionTest {
    private GameObjectCollection collection;
    private GameObject book;
    private GameObject candle;
    private GameObject map;

    @BeforeEach
    public void setUp() {
        // Create test GameObjects (Object Layer)
        book = new GameObject("book", "An old book", "book.png",
                Arrays.asList("readable"), null, true);
        candle = new GameObject("candle", "A lit candle", "candle.png",
                Arrays.asList("light"), null, true);
        map = new GameObject("map", "A treasure map", "map.png",
                Arrays.asList("paper"), null, true);

        // Create GameObjectCollection (Data Layer)
        collection = new GameObjectCollection(null);
    }

    @Test
    public void testAddAndGetObject() {
        collection.addObject(book);

        GameObject retrieved = collection.getObjectByName("book");
        assertNotNull(retrieved);
        assertEquals("book", retrieved.getName());
    }

    @Test
    public void testSize() {
        assertEquals(0, collection.size());

        collection.addObject(book);
        assertEquals(1, collection.size());

        collection.addObject(candle);
        collection.addObject(map);
        assertEquals(3, collection.size());
    }

    @Test
    public void testContains() {
        assertFalse(collection.containsObject("book"));

        collection.addObject(book);
        assertTrue(collection.containsObject("book"));
        assertFalse(collection.containsObject("candle"));
    }

    @Test
    public void testRemoveObject() {
        collection.addObject(book);
        collection.addObject(candle);

        boolean removed = collection.removeObject("book");
        assertTrue(removed);
        assertFalse(collection.containsObject("book"));
        assertTrue(collection.containsObject("candle"));
    }

    @Test
    public void testGetObjectByNameCaseInsensitive() {
        collection.addObject(book);

        // Test case insensitivity if implemented
        GameObject found = collection.getObjectByName("BOOK");
        // Depending on your implementation, adjust assertion
    }

    @Test
    public void testGameObjectProperties() {
        // Test that Object Layer properties are preserved through Data Layer
        collection.addObject(book);

        GameObject retrieved = collection.getObjectByName("book");
        assertEquals("An old book", retrieved.getDescription());
        assertTrue(retrieved.hasAttribute("readable"));
        assertTrue(retrieved.pickable());
    }

    @Test
    public void testGameObjectWithContainedItems() {
        // Create a chest containing a key
        GameObject key = new GameObject("small key", "A tiny key", "key.png",
                null, null, true);
        GameObject chest = new GameObject("chest", "A wooden chest", "chest.png",
                Arrays.asList("container"),
                Arrays.asList(key), false);

        collection.addObject(chest);
        GameObject retrieved = collection.getObjectByName("chest");

        assertFalse(retrieved.pickable()); // Chest is not pickable
        // You can add more tests for contained items if your GameObject supports it
    }

    @Test
    public void testNullObjectName() {
        GameObject nullName = collection.getObjectByName(null);
        assertNull(nullName);
    }

    @Test
    public void testEmptyCollection() {
        assertTrue(collection.size() == 0);
        assertNull(collection.getObjectByName("anything"));
    }
}
