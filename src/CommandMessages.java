import java.util.HashMap;
import java.util.Map;

/**
 * Stores and retrieves messages for all commands in the game.
 * 
 * @author Jialin Li
 */
public class CommandMessages {
        /**
         * Maps command name to an array of success/failure messages.
         * Index 0 is success, index 1 is failure.
         */
        private final Map<String, String[]> messages = new HashMap<>();

        /**
         * Initializes all messages for the commands.
         */
        public CommandMessages() {
                messages.put("go", new String[] {
                                "You move to the next location.",
                                "You can't go that way."
                });
                messages.put("pick", new String[] {
                                "You picked up the item.",
                                "You cannot pick up this item."
                });
                messages.put("pickup", new String[] {
                                "You picked up the item.",
                                "You cannot pick up this item."
                });
                messages.put("drop", new String[] {
                                "You dropped the item.",
                                "You cannot drop that here."
                });
                messages.put("examine", new String[] {
                                "", 
                                "There's nothing to examine."
                });
                messages.put("use", new String[] {
                                "", 
                                "That doesn't work."
                });
                messages.put("inventory", new String[] {
                                "You view the items in your inventory.",
                                "Your inventory is empty."
                });
                messages.put("talk", new String[] {
                                "", 
                                "No one responds."
                });
                messages.put("give", new String[] {
                                "", 
                                "You can't give that."
                });
                // Add more commands as needed
        }

        /**
         * Get the message for the given command and result.
         * 
         * @param command   the command name (e.g. "pickup")
         * @param isSuccess true for success, false for failure
         * @return the corresponding message, or an empty string if command not found
         */
        public String get(String command, boolean isSuccess) {
                String[] msg = messages.get(command);
                if (msg == null)
                        return "";
                return isSuccess ? msg[0] : msg[1];
        }
}
