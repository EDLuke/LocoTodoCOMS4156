package com.lukez.locotodo;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.lukez.locotodo_db.LocoTodoEvent;


/**
 * Created by lukez_000 on 04/28/2017.
 */

public class EventParentViewHolder extends ParentViewHolder {
  protected TextView mTxtEvent;
  protected ImageButton mImbExpand;

  public EventParentViewHolder(View itemView){
    super(itemView);

    mTxtEvent = (TextView) itemView.findViewById(R.id.txtEvent);
    mImbExpand = (ImageButton) itemView.findViewById(R.id.imbExpand);
  }

  public void bind(LocoTodoEvent event){
    mTxtEvent.setText(event.getEvent());
  }
}
