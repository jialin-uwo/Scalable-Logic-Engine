
/**
 * Defines the contract for the Game User Interface (UI).
 * The Game UI handles all interactions between the player and the game engine, including displaying game state and receiving player input.
 * Implementations may be text-based or graphical.
 * @author Junqi Zheng
 */
public interface IGameUI {
    /**
     * Displays information about the current location.
     * @param location the current game location
     */
    void displayCurrentLocation(Location location);
    
    /**
     * Displays a message or description to the player.
     * @param message the message to display
     */
    void displayMessage(String message);
  
    /**
     * Displays an error message to the player (ex: invalid command).
     * @param error the error message
     */
    void showError(String error);
  
    /**
     * Displays the player's current inventory.
     * @param inventory the player's inventory
     */
    void displayInventory(Inventory inventory);
    
    /**
     * Prompts the player for their next input command.
     * @return the command entered by the player
     */
    String getUserCommand();
  
    /**
     * Resets the whole data file
     */	
    void resetRender(GameData gameData);
  
    /**
     * Updates the count of turns
     * @param the count of turns
     */
    void updateTurnCount(int count);
}