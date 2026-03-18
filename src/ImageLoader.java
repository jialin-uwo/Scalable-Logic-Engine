import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Implements the image loading functionality for the game.
 * Loads an image from a file within the project directory.
 * 
 * @author Jialin Li
 */

public class ImageLoader implements IImageLoader {
    /**
     * Constructs a new ImageLoader instance.
     */
    public ImageLoader() {

    }

    /**
     * Loads an image from the given path.
     * 
     * @param imagePath the path to the image resource
     * @return the loaded Image object
     * @throws IOException if the image cannot be loaded
     */
    public Image loadImage(String imagePath) throws IOException {
        BufferedImage img = ImageIO.read(new File(imagePath));
        return img;
    }
}
