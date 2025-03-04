package duke.filehandler;

import duke.exceptions.DukeException;
import duke.tasks.Deadline;
import duke.tasks.Event;
import duke.tasks.Task;
import duke.tasks.Todo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Represents Storage class which handles reading from and storing tasks to txt. file
 */
public class Storage {
    private static String FILE_PATH;

    public Storage(String file_path) {
        FILE_PATH = file_path;
    }

    /**
     * converts string from saved file to Date object
     *
     * @param dateString
     * @return Date
     * @throws DukeException
     */
    public static Date fileDateParser(String dateString) throws DukeException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy h a");

        try {
            Date date = sdf.parse(dateString);
            return date;
        } catch (Exception e) {
            System.out.println(e);
            throw new DukeException("Cannot read time from file");
        }
    }

    /**
     * Saves tasks in memory to file
     *
     * @param tasks
     * @throws DukeException
     */
    public static void saveTasks(ArrayList<Task> tasks) throws DukeException {
        try {
            File taskFile = new File(FILE_PATH);
            if (! taskFile.exists()) {
                taskFile.getParentFile().mkdirs();
                taskFile.createNewFile();
            }
            assert taskFile.exists();
        } catch (IOException e) {
            throw new DukeException("Error creating task file!");
        }

        try {
            FileWriter taskWriter = new FileWriter(FILE_PATH);
            for (Task task : tasks) {
                taskWriter.write(task.toString());
                taskWriter.write("\n");
            }
            taskWriter.close();
        } catch (IOException e) {
            throw new DukeException(" Error saving tasks to file");
        }
    }

    /**
     * reads all tasks from saved file
     *
     * @return tasks as an ArrayList
     * @throws DukeException
     */
    public static ArrayList<Task> readTasks() throws DukeException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            File taskFile = new File(FILE_PATH);
            if (! taskFile.exists()) {
                taskFile.getParentFile().mkdirs();
                taskFile.createNewFile();
                return tasks;
            }
            Scanner reader = new Scanner(taskFile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                tasks = taskCreator(data, tasks);
            }
        } catch (IOException e) {
            throw new DukeException("error reading from file");
        }
        return tasks;
    }

    /**
     * Reads given line, creates the corresponding task object and adds it to taskList
     *
     * @param data  line read from saved file
     * @param tasks current tasks list
     * @return new tasks list
     * @throws DukeException
     */
    public static ArrayList<Task> taskCreator(String data, ArrayList<Task> tasks) throws DukeException {
        if (data.length() == 0) {
            return tasks;
        }
        if (data.charAt(1) == 'T') {
            String[] description = data.split("#");
            Todo todo = new Todo(description[0].substring(7));
            if (data.charAt(4) == 'X') {
                todo.setAction("mark");
            }
            todo.setTag(description[1]);
            tasks.add(todo);
        } else if (data.charAt(1) == 'D') {
            //format: [D][X] assignment (by: 02 Feb 2001 7 PM) #urgent
            String tag = data.split("#")[1];
            data = data.split("#")[0];
            String dates = data.split(":", 2)[1];
            String desc = data.split("\\(")[0];
            Date date = fileDateParser(dates.substring(1, dates.length() - 1));
            Deadline deadline = new Deadline(desc.substring(7), date);
            if (data.charAt(4) == 'X') {
                deadline.setAction("mark");
            }
            deadline.setTag(tag);
            tasks.add(deadline);
        } else if (data.charAt(1) == 'E') {
            String tag = data.split("#")[1];
            data = data.split("#")[0];
            String[] dates = data.split(":");
            String desc = data.split("\\(")[0];
            Date from = fileDateParser(dates[1].substring(1, dates[1].length() - 2));
            Date to = fileDateParser(dates[2].substring(1, dates[2].length() - 1));
            Event event = new Event(desc.substring(7), from, to);
            if (data.charAt(4) == 'X') {
                event.setAction("mark");
            }
            event.setTag(tag);
            tasks.add(event);
        }
        return tasks;
    }
}



