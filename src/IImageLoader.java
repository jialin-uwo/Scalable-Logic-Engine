import java.awt.Image;
import java.io.IOException;

/**
 * Defines the contract for loading image resources used in the game.
 * Implementations should handle the specifics of image loading.
 * @author Jialin Li
 */ 
public interface IImageLoader { 
    /**
     * Loads an image from the given path.
     * @param imagePath the path to the image resource
     * @return the loaded Image object
     * @throws IOException if the image cannot be loaded
     */
    Image loadImage(String imagePath) throws IOException;
}