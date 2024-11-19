package com.example.tasks1on;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks1on.Adapter.TodoAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.Callback {

    private TodoAdapter adapter;

    // Constructor with TodoAdapter parameter
    public RecyclerItemTouchHelper(TodoAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true; // Allow swipe gestures
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false; // Disable long press drag
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition(); // Get item position
        if (direction == ItemTouchHelper.LEFT) {
            // Show delete confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext()); // Correct context retrieval
            builder.setTitle("Delete Task");
            builder.setMessage("Are you sure you want to delete this Task?");
            builder.setPositiveButton("Confirm", (dialogInterface, i) -> {
                adapter.deleteItem(position); // Delete item
            });
            builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                adapter.notifyItemChanged(viewHolder.getAdapterPosition()); // Undo swipe
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (direction == ItemTouchHelper.RIGHT) {
            // If swiped to the right, edit the item
            adapter.editItem(position);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        // Get the current item view and draw the background and icons
        View itemView = viewHolder.itemView;
        Drawable icon;
        ColorDrawable background;

        int backgroundCornerOffset = 20;
        if (dX > 0) { // Swiping to the right
            icon = ContextCompat.getDrawable(itemView.getContext(), R.drawable.edit); // Correct context retrieval
            background = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.primary_blue));
        } else { // Swiping to the left
            icon = ContextCompat.getDrawable(itemView.getContext(), R.drawable.delete); // Correct context retrieval
            background = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.accent_orange));
        }

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getLeft(), itemView.getTop(), (int) (itemView.getLeft() + dX + backgroundCornerOffset), itemView.getBottom());
        } else if (dX < 0) { // Swiping to left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + (int) dX - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // No swipe
            background.setBounds(0, 0, 0, 0);
        }

        // Draw the background and icon
        background.draw(c);
        icon.draw(c);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Enable swiping left and right
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false; // Since you're not supporting drag and drop, return false
    }
}
