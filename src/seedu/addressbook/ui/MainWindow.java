package seedu.addressbook.ui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import seedu.addressbook.commands.ExitCommand;
import seedu.addressbook.commands.LockCommand;
import seedu.addressbook.logic.Logic;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.autocorrect.EditDistance;
import seedu.addressbook.autocorrect.Dictionary;
import seedu.addressbook.password.Password;
import seedu.addressbook.timeanddate.TimeAndDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static seedu.addressbook.common.Messages.*;

/**
 * Main Window of the GUI.
 */
public class MainWindow {

    private Logic logic;
    private Stoppable mainApp;

    public MainWindow(){
    }



    public void setLogic(Logic logic){
        this.logic = logic;
    }

    public void setMainApp(Stoppable mainApp){
        this.mainApp = mainApp;
    }


    public String checkDistance(String commandInput) {
        Dictionary A = new Dictionary();
        EditDistance B = new EditDistance();
        String prediction = "none";
        ArrayList<String> commandList = Dictionary.getCommands();
        int distance, check = 0;
        for (String command : commandList) {
            distance = EditDistance.computeDistance(commandInput, command);
            if (distance == 1) {
                prediction = command;
                check = 1;
                break;
            }
        }
        if (check == 0) {
            prediction = "none";
        }
        return prediction;
    }

    private Password password = new Password();
    private TimeAndDate tad = new TimeAndDate();

    @FXML
    private TextArea outputConsole;

    @FXML
    private TextField commandInput;

    @FXML
    void onCommand(ActionEvent event) {
        try {
            String userCommandText = commandInput.getText();
            decipherUserCommandText(userCommandText);
        } catch (Exception e) {
            e.printStackTrace();
            display(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void decipherUserCommandText(String userCommandText) throws Exception {
        if(toCloseApp(userCommandText)){
            password.lockDevice();
            mainApp.stop();
        }
        else if(isLockCommand(userCommandText)){
            CommandResult result = logic.execute(userCommandText);
            clearScreen();
            displayResult(result);
        }
        else if(password.isLocked()) {
            String unlockDeviceResult = password.unlockDevice(userCommandText,password.getWrongPasswordCounter());
            clearScreen();
            display(unlockDeviceResult);
        }
        else if(canUpdatePassword(userCommandText)){
            String prepareUpdatePasswordResult = password.prepareUpdatePassword();
            clearScreen();
            display(prepareUpdatePasswordResult);
        }
        else if(password.isUpdatingPasswordNow()){
            String updatePasswordResult;
            if(password.isUpdatePasswordConfirmNow()) {
                updatePasswordResult = password.updatePasswordFinal(userCommandText);
            }
            else{
                updatePasswordResult = password.updatePassword(userCommandText,password.getWrongPasswordCounter());
            }
            clearScreen();
            display(updatePasswordResult);
        }

        else if(password.isUnauthorizedAccess(userCommandText)){
            clearScreen();
            String unauthorizedCommandResult = password.invalidPOResult(userCommandText);
            display(unauthorizedCommandResult);
        }
        else {
            String arr[] = userCommandText.split(" ", 2);
            String commandWordInput = arr[0];
            String output = checkDistance(commandWordInput);
            if(!(output.equals("none"))) {
                clearScreen();
                display("Did you mean to use " + output + "?", "Please try changing the command.");
            }
            else{
                CommandResult result = logic.execute(userCommandText);
                displayResult(result);
                clearCommandInput();
            }
        }
    }


    private boolean canUpdatePassword(String userCommandText){
        return password.isHQPUser() && isUpdatePasswordCommand(userCommandText);
    }

    /** Returns true if the result given is the result of an exit command */
    private boolean isExitCommand(String userCommandText) {
        return userCommandText.equals(ExitCommand.COMMAND_WORD);
    }

    private boolean isUpdatePasswordCommand(String userCommandText) {
        return userCommandText.equals("update password");
    }

    private boolean toCloseApp(String userCommandText){
        return isExitCommand(userCommandText) || password.isShutDown();
    }

    private boolean isLockCommand(String userCommandText) {
        return userCommandText.equals(LockCommand.COMMAND_WORD);
    }


    /** Clears the command input box */
    private void clearCommandInput() {
        commandInput.setText("");
    }

    /** Clears the output display area */
    private void clearOutputConsole(){
        outputConsole.clear();
    }

    private void clearScreen(){
        clearCommandInput();
        clearOutputConsole();
    }

    /** Displays the result of a command execution to the user. */
    private void displayResult(CommandResult result) {
        clearOutputConsole();
        final Optional<List<? extends ReadOnlyPerson>> resultPersons = result.getRelevantPersons();
        if(resultPersons.isPresent()) {
            display(resultPersons.get());
        }
        display(result.feedbackToUser);
    }

    void displayWelcomeMessage(String version, String storageFilePath) {
        String storageFileInfo = String.format(MESSAGE_USING_STORAGE_FILE, storageFilePath);
        display(MESSAGE_WELCOME, version, storageFileInfo, tad.outputDATHrs() + "\n" , Password.MESSAGE_ENTER_PASSWORD);
    }

    /**
     * Displays the list of persons in the output display area, formatted as an indexed list.
     * Private contact details are hidden.
     */
    private void display(List<? extends ReadOnlyPerson> persons) {
        display(new Formatter().format(persons));
    }

    public void displayTimestamps(List<String> history){
        display(new Formatter().formatForTstamps(history));
    }

    /**
     * Displays the given messages on the output display area, after formatting appropriately.
     */
    private void display(String... messages) {
        outputConsole.setText(outputConsole.getText() + new Formatter().format(messages));
    }

}
