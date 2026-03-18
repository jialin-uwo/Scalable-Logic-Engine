/*
 * ICommandMessages.java 
 *  * @author Jialin Li
 */
public interface ICommandMessages {
    /**
     * Get the message for the given command and result.
     * 
     * @param command   the command name (e.g. "pickup")
     * @param isSuccess true for success, false for failure
     * @return the corresponding message, or an empty string if command not found
     */
    String get(String command, boolean isSuccess);
    
}
