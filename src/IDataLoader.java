/**
 * Defines the behavior for loading game data from an external source, such as a file or database.
 * Implementations can support various formats (ex: JSON, XML).
 * @author Junqi Zheng	
 */
public interface IDataLoader {
    /**
     * Loads and parses game data from the specified file path.
     * @param filePath the path to the game data file
     * @return the loaded GameData instance
     */  
    GameData loadGameData(String filePath) throws Exception;
}