package com.example.tasks1on;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks1on.Adapter.TodoAdapter;
import com.example.tasks1on.Model.TodoModel;
import com.example.tasks1on.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView taskRecyclerView;
    private TodoAdapter taskAdapter;
    private List<TodoModel> taskList;
    public DatabaseHandler db;
    public ImageButton imageButton;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handling system bars insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Declare and initialize the ImageButton
        ImageButton infoButton = findViewById(R.id.infoButton);

        // Set an OnClickListener on the ImageButton
        infoButton.setOnClickListener(v -> {
            // Show the dialog when info button is clicked
            new AlertDialog.Builder(this)
                    .setTitle("Swipe Functions")
                    .setMessage("Swipe left to delete a task.\nSwipe right to edit a task.")
                    .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Hide the action bar
//        getSupportActionBar().hide();

        // Initialize database handler and open the database
        db = new DatabaseHandler(this);
        db.openDatabase();

        // Initialize the task list and RecyclerView
        taskList = new ArrayList<>();
        taskRecyclerView = findViewById(R.id.tasksRecylcerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the task adapter and set it to RecyclerView
        taskAdapter = new TodoAdapter(db, this);
        taskRecyclerView.setAdapter(taskAdapter);

        // Initialize the Floating Action Button (FAB)
        fab = findViewById(R.id.fab);

        // Attach ItemTouchHelper to handle swipe actions
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);

        // Fetch all tasks from the database and reverse the list for recent tasks first
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTasks(taskList);

        // Set the click listener for the FAB to show the AddNewTask dialog
        fab.setOnClickListener(view -> AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        // Refresh the task list when the dialog is closed
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTasks(taskList);
        taskAdapter.notifyDataSetChanged();
    }
}
