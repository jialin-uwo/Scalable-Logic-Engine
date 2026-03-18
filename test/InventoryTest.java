import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for Inventory (Object Layer)
 * Tests inventory management functionality
 * 
 * @author Jialin Li
 */
public class InventoryTest {
    private Inventory inventory;
    private GameObject key;
    private GameObject sword;
    private GameObject potion;

    @BeforeEach
    public void setUp() {
        inventory = new Inventory();

        // Create test GameObjects (Object Layer) for testing Inventory (Data Layer)
        key = new GameObject("key", "A rusty key", "key.png",
                Arrays.asList("metal", "small"), null, true);
        sword = new GameObject("sword", "A sharp sword", "sword.png",
                Arrays.asList("weapon", "metal"), null, true);
        potion = new GameObject("potion", "Health potion", "potion.png",
                Arrays.asList("consumable"), null, true);
    }

    @Test
    public void testAddObject() {
        // Test Data Layer method that uses Object Layer
        inventory.addObject(key);
        assertTrue(inventory.containsObject("key"));
    }

    @Test
    public void testRemoveObject() {
        inventory.addObject(key);
        inventory.addObject(sword);

        boolean removed = inventory.removeObject("key");
        assertTrue(removed);
        assertFalse(inventory.containsObject("key"));
        assertTrue(inventory.containsObject("sword"));
    }

    @Test
    public void testGetObjectByName() {
        inventory.addObject(key);
        inventory.addObject(sword);

        GameObject found = inventory.getObjectByName("key");
        assertNotNull(found);
        assertEquals("key", found.getName());
        assertEquals("A rusty key", found.getDescription());
    }

    @Test
    public void testGetAllObjects() {
        inventory.addObject(key);
        inventory.addObject(sword);
        inventory.addObject(potion);

        List<GameObject> all = inventory.getAllObjects();
        assertEquals(3, all.size());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(inventory.isEmpty());

        inventory.addObject(key);
        assertFalse(inventory.isEmpty());

        inventory.removeObject("key");
        assertTrue(inventory.isEmpty());
    }

    @Test
    public void testContainsObject() {
        assertFalse(inventory.containsObject("key"));

        inventory.addObject(key);
        assertTrue(inventory.containsObject("key"));
        assertFalse(inventory.containsObject("sword"));
    }

    @Test
    public void testGameObjectAttributes() {
        // Test Object Layer through Data Layer operations
        inventory.addObject(sword);

        GameObject retrieved = inventory.getObjectByName("sword");
        assertTrue(retrieved.hasAttribute("weapon"));
        assertTrue(retrieved.hasAttribute("metal"));
        assertFalse(retrieved.hasAttribute("magic"));
    }

    @Test
    public void testGameObjectPickable() {
        // Test Object Layer pickable property
        inventory.addObject(key);

        GameObject retrieved = inventory.getObjectByName("key");
        assertTrue(retrieved.pickable());
    }

    @Test
    public void testMultipleObjectsSameName() {
        inventory.addObject(key);
        inventory.addObject(key); // Add same object again

        // Should still only have one
        List<GameObject> all = inventory.getAllObjects();
        assertEquals(1, all.size());
    }
}
