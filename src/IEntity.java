/**
 * Defines the fundamental contract for any item or element that exists in the
 * game world. It specifies common, core properties like name and description
 * that must be shared by all game entities.
 * 
 * @author Jialin Li
 */
public interface IEntity {
    /**
     * Gets the unique name of the entity.
     * 
     * @return The name of the entity.
     */
    String getName();

    /**
     * Gets the description of the entity.
     * 
     * @return The description of the entity
     */
    String getDescription();

    /**
     * Gets the path to the image representing the entity.
     * 
     * @return The path to the image of the entity
     */
    String getImagePath();

}