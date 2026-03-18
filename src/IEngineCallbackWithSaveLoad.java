/**
 * Optional interface for engines that support save/load from the UI.
 * Extends IEngineCallback with save/load hooks.
 * @author Xinyan Cai
 */
public interface IEngineCallbackWithSaveLoad extends IEngineCallback {
    void onSaveRequested(String filePath, GameData data);

    void onLoadRequested(String filePath);
}