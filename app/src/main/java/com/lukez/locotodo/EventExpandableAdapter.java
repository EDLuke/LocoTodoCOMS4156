package com.lukez.locotodo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.lukez.locotodo_db.LocoTodoContract;
import com.lukez.locotodo_db.LocoTodoDbHelper;
import com.lukez.locotodo_db.LocoTodoEvent;

import java.util.ArrayList;

/**
 * Created by lukez_000 on 04/28/2017.
 */

public class EventExpandableAdapter extends ExpandableRecyclerAdapter<LocoTodoEvent, LocoTodoEvent, EventParentViewHolder, EventChildViewHolder> {
  LayoutInflater mInflater;
  protected ImageButton mImbDelete;
  private EventListActivity mParentActivity;

  public EventExpandableAdapter(Context context, ArrayList parentEvents){
    super(parentEvents);

    mInflater = LayoutInflater.from(context);
    mParentActivity = (EventListActivity) context;
  }

  @Override
  public EventParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup, int viewType) {
    View parentView = mInflater.inflate(R.layout.item_event_parent, parentViewGroup, false);
    return new EventParentViewHolder(parentView);
  }

  @Override
  public EventChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
    View childView = mInflater.inflate(R.layout.item_event_child, childViewGroup, false);

    return new EventChildViewHolder(childView);
  }

  @Override
  public void onBindParentViewHolder(@NonNull EventParentViewHolder parentViewHolder, int parentPosition, @NonNull LocoTodoEvent parent) {
    parentViewHolder.bind(parent);


  }

  @Override
  public void onBindChildViewHolder(@NonNull final EventChildViewHolder childViewHolder, final int parentPosition, final int childPosition, @NonNull LocoTodoEvent child) {
    childViewHolder.bind(child);

    mImbDelete = (ImageButton) childViewHolder.itemView.findViewById(R.id.imgDelete);
    mImbDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        LocoTodoEvent event = (LocoTodoEvent)childViewHolder.getChild();
        LocoTodoDbHelper dbHelper = new LocoTodoDbHelper(view.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(LocoTodoContract.TodoEntry.TABLE_NAME, LocoTodoContract.TodoEntry._ID + " =?",
                new String[]{String.valueOf(event.getID())});
        db.close();
        notifyParentRemoved(parentPosition);
        mParentActivity.notifyEventsChanged();
      }
    });
  }

}
