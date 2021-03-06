package seedu.addressbook;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import seedu.addressbook.logic.Logic;
import seedu.addressbook.ui.Gui;
import seedu.addressbook.ui.Stoppable;

/**
 * Main entry point to the application.
 */
public class Main extends Application implements Stoppable {

    /** Version info of the program. */

    private static final String VERSION = "PRISM - Version 1.3";


    @Override
    public void start(Stage primaryStage) throws Exception {
        Gui gui = new Gui(new Logic(), VERSION);
        gui.start(primaryStage, this);

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }


}


