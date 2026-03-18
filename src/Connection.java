import java.util.Objects;

/**
 * Represents a connection between two locations in the game world.
 * Each connection has a name, description, and target location.
 * 
 * @author Peiyong Wang
 */
public class Connection implements IConnection {

    private final String name;
    private final String description;
    private final String targetLocationName;

    /**
     * Constructs a new Connection with the specified properties.
     * 
     * @param name               the name of the connection (must not be null)
     * @param description        the description of the connection (must not be
     *                           null)
     * @param targetLocationName the name of the target location (must not be null)
     * @throws NullPointerException if any parameter is null
     */
    public Connection(String name,
            String description,
            String targetLocationName) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.targetLocationName = Objects.requireNonNull(targetLocationName,
                "targetLocationName must not be null");
    }

    /**
     * Gets the name of this connection.
     * 
     * @return the connection's name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this connection.
     * 
     * @return the connection's description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name of the target location this connection leads to.
     * 
     * @return the target location's name
     */
    @Override
    public String getTargetLocationName() {
        return targetLocationName;
    }
}
