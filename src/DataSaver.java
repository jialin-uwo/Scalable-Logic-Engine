import com.google.gson.Gson;
import java.io.IOException;

/**
 * Utility class for saving game data to a JSON file.
 * * @author Jialin Li
 */
public class DataSaver {
    /**
     * Saves the current game data to a JSON file.
     * 
     * @param filePath the path to save the game data
     * @param data     the GameData instance to save
     */
    public static void saveGameData(String filePath, GameData data) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        java.nio.file.Files.write(java.nio.file.Paths.get(filePath),
                json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
