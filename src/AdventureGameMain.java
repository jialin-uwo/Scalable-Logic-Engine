
/**
 * AdventureGameMain.java
 * Entry point for the CS2212 adventure game application.
 * The main method delegates all Swing-related initialization to the EDT using
 * {@link javax.swing.SwingUtilities#invokeLater(Runnable)} to ensure
 * thread-safe UI operations.
 *
 * @author Xinyan Cai
 */
import javax.swing.SwingUtilities;

/**
 * Main bootstrap class for launching the adventure game.
 * This class contains the {@link #main(String[])} method that initializes the
 * loaders, constructs the {@link GameEngine}, and starts the game.
 */
public class AdventureGameMain {

    /**
     * Application entry point.
     *
     * @param args command-line arguments; if at least one argument is provided,
     *             {@code args[0]} is treated as the path to the game data file.
     *             Otherwise, the default path {@code "DataFile.json"} is used.
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                IDataLoader dataLoader = new DataLoader();
                IImageLoader imageLoader = new ImageLoader();

                
                String dataFilePath = (args.length > 0) ? args[0] : "assets\\data\\DataFile.json";

                
                GameEngine engine = new GameEngine(dataLoader, dataFilePath, imageLoader);

                
                engine.startGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
