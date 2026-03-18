import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * GameEngineConstructorTest.java
 *
 * This test class verifies the basic construction behavior of the
 * {@link GameEngine} class. It focuses on ensuring that the engine
 * can be instantiated with simple fake implementations of
 * {@link IDataLoader} and {@link IImageLoader}, and that its initial
 * internal state is as expected before {@code startGame()} is called.
 * @author Xinyan Cai
 */
public class GameEngineConstructorTest {

    /**
     * Simple fake implementation of {@link IDataLoader} used only to
     * satisfy the {@link GameEngine} constructor dependencies.
     * The method {@link #loadGameData(String)} always returns {@code null}
     * because the tests in this class never actually invoke loading logic.
     */
    private static class FakeDataLoader implements IDataLoader {

        /**
         * Returns {@code null} for any given path. This is sufficient
         * for constructor testing where {@link GameEngine#startGame()}
         * is not invoked.
         *
         * @param path the path to the data file (ignored in this fake implementation)
         * @return always {@code null}, as loading is not required for these tests
         */
        @Override
        public GameData loadGameData(String path) {
            return null; // 这里只是给构造用，不会真的调用
        }
    }

    /**
     * Simple fake implementation of {@link IImageLoader} used only to
     * satisfy the {@link GameEngine} constructor dependencies.
     * The method {@link #loadImage(String)} always returns {@code null}
     * because UI image loading is not exercised in this unit test.
     */
    private static class FakeImageLoader implements IImageLoader {

        /**
         * Returns {@code null} for any given image path. This is sufficient
         * for constructor testing where actual image loading is not needed.
         *
         * @param path the path to the image resource (ignored in this fake implementation)
         * @return always {@code null}, as image loading is not required for these tests
         */
        @Override
        public java.awt.Image loadImage(String path) {
            return null;
        }
    }

    /**
     * Tests that the {@link GameEngine} constructor correctly initializes
     * its core fields and dependencies.
     */
    @Test
    public void testConstructorInitializesFields() {
        IDataLoader loader = new FakeDataLoader();
        IImageLoader imageLoader = new FakeImageLoader();
        GameEngine engine = new GameEngine(loader, "dummy.json", imageLoader);

        assertNotNull(engine);
        //assertEquals("dummy.json", TestUtils.getFieldValue(engine, "dataFilePath")); // 如果不想用反射，可以不测这一条
        assertNotNull(engine.getGameUI(), "GameEngine should create a GameUI instance internally");
        assertNull(engine.getGameData(), "GameData should be null before startGame()");
    }
}
