//@@author andyrobert3
package seedu.addressbook.commands;

import seedu.addressbook.PatrolResourceStatus;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.inbox.MessageFilePaths;
import seedu.addressbook.inbox.Msg;
import seedu.addressbook.inbox.WriteNotification;
import seedu.addressbook.password.Password;

import java.io.IOException;

public class RequestHelpCommand extends Command {
    public static final String COMMAND_WORD = "rb";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Requests help from headquarters.\n\t"
            + "Message from police officer can be appended in text.\n\t"
            + "Example: " + COMMAND_WORD
            + " gun "
            + " Help needed on Jane Street";


    public static String MESSAGE_REQUEST_SUCCESS = "Request for backup case from %s has been sent to HQP.";

    private static Msg requestHelpMessage;
    private WriteNotification writeNotification;

    /**
     * Constructor for the Writers to write to headquarters personnel file.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public RequestHelpCommand(String caseName, String messageString) throws IllegalValueException {
        writeNotification = new WriteNotification(MessageFilePaths.FILEPATH_HQP_INBOX, true);
        requestHelpMessage = new Msg(Offense.getPriority(caseName), messageString, PatrolResourceStatus.getLocation(Password.getID()));
    }


    @Override
    public CommandResult execute() {
        try {
            writeNotification.writeToFile(requestHelpMessage);
            return new CommandResult(String.format(MESSAGE_REQUEST_SUCCESS, Password.getID()));
        } catch (IOException ioe) {
            return new CommandResult(Messages.MESSAGE_SAVE_ERROR);
        }
    }

    public static void resetRecentMessage() {
        requestHelpMessage = null;
    }

    public static Msg getRecentMessage() {
        if (requestHelpMessage == null) {
            throw new NullPointerException("Request command was never called");
        }
        return requestHelpMessage;
    }


}
