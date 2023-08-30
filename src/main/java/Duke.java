
import exceptions.DukeException;
import fileHandler.Storage;
import parsers.InputParser;
import tasks.Task;
import ui.Ui;

import java.util.ArrayList;
public class Duke {


    private static ArrayList<Task> tasks;
    private Ui ui;
    private Storage storage;

    public Duke(String filePath) {
        ui = new Ui();
        ui.printGreeting();
        storage = new Storage(filePath);
        try {
            tasks = storage.readTasks();
        } catch (DukeException e) {
            ui.showLoadingError();
            tasks = new ArrayList<>();
        }
    }

    public void run() {
        InputParser.parse(tasks);
    }
    public static void main(String[] args) throws DukeException {

        new Duke("tasks.txt").run();

        }
    }

