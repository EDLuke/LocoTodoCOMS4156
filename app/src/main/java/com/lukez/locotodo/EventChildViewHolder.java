package com.lukez.locotodo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.lukez.locotodo_db.LocoTodoContract;
import com.lukez.locotodo_db.LocoTodoDbHelper;
import com.lukez.locotodo_db.LocoTodoEvent;

/**
 * Created by lukez_000 on 04/28/2017.
 */

public class EventChildViewHolder extends ChildViewHolder {
  protected TextView mTxtLocation;
  protected ImageButton mImbDelete;
  protected ImageButton mImbNav;

  public EventChildViewHolder(View itemView){
    super(itemView);

    mTxtLocation = (TextView) itemView.findViewById(R.id.txtLocation);
//    mImbDelete = (ImageButton) itemView.findViewById(R.id.imgDelete);
//    mImbDelete.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        onClickDelete(view);
//      }
//    });
    mImbNav = (ImageButton) itemView.findViewById(R.id.imbNav);
    mImbNav.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onClickNav(view);
      }
    });
  }

  public void bind(LocoTodoEvent event){
    mTxtLocation.setText(event.getLocation());
  }

  public void onClickNav(View view){
    LocoTodoEvent event = (LocoTodoEvent)this.getChild();
    Uri gmmIntentUri = Uri.parse("google.navigation:q="+event.getLatlng().latitude + "," + event.getLatlng().longitude);
    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
    mapIntent.setPackage("com.google.android.apps.maps");
    if (mapIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
      view.getContext().startActivity(mapIntent);
    }
  }

  public void onClickDelete(View view){
//    LocoTodoEvent event = (LocoTodoEvent)this.getChild();
//    LocoTodoDbHelper dbHelper = new LocoTodoDbHelper(view.getContext());
//    SQLiteDatabase db = dbHelper.getWritableDatabase();
//    db.delete(LocoTodoContract.TodoEntry.TABLE_NAME, LocoTodoContract.TodoEntry._ID + " =?",
//            new String[]{String.valueOf(event.getID())});
//    db.close();


  }
}
