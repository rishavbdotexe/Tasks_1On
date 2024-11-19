package com.example.tasks1on.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tasks1on.Model.TodoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";

    // Correct the SQL query by adding spaces between column definitions
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TASK + " TEXT, " +
            STATUS + " INTEGER)";

    private SQLiteDatabase db;

    // Change constructor visibility to public
    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE); // Execute the SQL to create the table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase(); // Open database for writing
    }

    // Correct the method name to "insertTask"
    public void insertTask(TodoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0); // Assuming '0' means the task is not completed
        db.insert(TODO_TABLE, null, cv);
    }

    public List<TodoModel> getAllTasks() {
        List<TodoModel> taskList = new ArrayList<>();
        Cursor cur = null;

        db.beginTransaction(); // Start a transaction
        try {
            // Query the database for all tasks
            cur = db.query(TODO_TABLE, null, null, null, null, null, null);

            // Check if cursor is not null and move to the first row
            if (cur != null && cur.moveToFirst()) {
                do {
                    TodoModel task = new TodoModel();

                    // Get column indexes
                    int idIndex = cur.getColumnIndex(ID);
                    int taskIndex = cur.getColumnIndex(TASK);
                    int statusIndex = cur.getColumnIndex(STATUS);

                    // Check if columns exist
                    if (idIndex != -1 && taskIndex != -1 && statusIndex != -1) {
                        task.setId(cur.getInt(idIndex));
                        task.setTask(cur.getString(taskIndex));
                        task.setStatus(cur.getInt(statusIndex));
                        taskList.add(task);
                    } else {
                        // Log error if columns are missing
                        Log.e("Database", "One or more columns are missing in the query result!");
                    }
                } while (cur.moveToNext()); // Continue iterating through rows
            }
        } finally {
            db.endTransaction(); // End the transaction
            if (cur != null) {
                cur.close(); // Safely close the cursor
            }
        }
        return taskList;
    }
    public void updateStatus(int id,int status)
    {
        ContentValues cv = new ContentValues();
        cv.put(STATUS,status);
        db.update(TODO_TABLE,cv,ID + "=?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id ,String task){
        ContentValues cv = new ContentValues();
        cv.put(TASK,task);
        db.update(TODO_TABLE,cv,ID + "=?", new String[] {String.valueOf(id)});
    }
    public void deleteTask(int id){
        db.delete(TODO_TABLE,ID + "=?",new String[]{String.valueOf(id)});
    }
}
