package com.example.tasks1on.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks1on.AddNewTask;
import com.example.tasks1on.MainActivity;
import com.example.tasks1on.Model.TodoModel;
import com.example.tasks1on.R;
import com.example.tasks1on.Utils.DatabaseHandler;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
private List<TodoModel> todolist;
private MainActivity activity;
private DatabaseHandler db;

public TodoAdapter(DatabaseHandler db ,MainActivity activity){
    this.db = db;
    this.activity = activity;
}
public ViewHolder onCreateViewHolder(ViewGroup parent ,int viewType)
{
    View itemview= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent,false);
    return new ViewHolder(itemview);
}

public void onBindViewHolder(ViewHolder holder,int postition)
{
    db.openDatabase();
    TodoModel item = todolist.get(postition);
    holder.task.setText(item.getTask());
    holder.task.setChecked(toBoolean(item.getStatus()));
    holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(compoundButton.isChecked())    {
            db.updateStatus(item.getId(),1);
            }
            else {

                db.updateStatus(item.getId(),0);

            }
        }
    });

}

public int getItemCount(){
    return todolist.size();
}
private Boolean toBoolean(int n ) {
    return n!=0;
}
public void setTasks(List<TodoModel> todolist){
     this.todolist = todolist;
     notifyDataSetChanged();
}

public void deleteItem(int position){

    TodoModel item = todolist.get(position);
    db.deleteTask(item.getId());
    todolist.remove(position);
    notifyItemRemoved(position);
}
public void editItem(int position){
    TodoModel item = todolist.get(position);
    Bundle bundle = new Bundle();
    bundle.putInt("id",item.getId());
    bundle.putString("task",item.getTask());
    AddNewTask fragment = new AddNewTask();
    fragment.setArguments(bundle);
    fragment.show((activity.getSupportFragmentManager()),AddNewTask.TAG);
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    CheckBox task;
    ViewHolder(View view){
        super(view);
        task = view.findViewById(R.id.todoCheckBox);
    }
}
}
