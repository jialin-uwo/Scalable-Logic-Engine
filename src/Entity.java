import java.util.Objects;

/**
 * Base class for all entities in the game world.
 * Entities have a name, description, and image path.
 * 
 * @author Peiyong Wang
 */
public class Entity implements IEntity {

    private final String name;
    private final String description;
    private final String imagePath;

    /**
     * Constructs a new Entity with the specified properties.
     * 
     * @param name        the name of the entity (must not be null)
     * @param description the description of the entity
     * @param imagePath   the path to the entity's image
     * @throws NullPointerException if name is null
     */
    public Entity(String name,
            String description,
            String imagePath) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = description;
        this.imagePath = imagePath;
    }

    /**
     * Gets the name of this entity.
     * 
     * @return the entity's name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this entity.
     * 
     * @return the entity's description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the image path for this entity.
     * 
     * @return the path to the entity's image
     */
    @Override
    public String getImagePath() {
        return imagePath;
    }
}
