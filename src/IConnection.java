/**
 * Represents a named connection between locations in the game world.
 * Connections have a name, description, and target location.
 * 
 * @author Jialin Li
 */
public interface IConnection {
    /**
     * Gets the name of this connection.
     * 
     * @return name of the connection
     */
    String getName();

    /**
     * Gets the description of this connection.
     * 
     * @return description of the connection
     */
    String getDescription();

    /**
     * Gets the unique name of the destination location this connection points to.
     * 
     * @return name of the destination location
     */
    String getTargetLocationName();
}
