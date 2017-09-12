package com.lukez.locotodo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.lukez.locotodo_db.LocoTodoDbHelper;
import com.lukez.locotodo_db.LocoTodoEvent;

import java.util.ArrayList;

import static com.lukez.locotodo_db.LocoTodoContract.TodoEntry.TABLE_NAME;

public class EventListActivity extends AppCompatActivity {
  private RecyclerView mRecyclerView;
  private EventExpandableAdapter mAdatper;
  private boolean eventsChanged;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_eventlist);

    //Link the list view
    mRecyclerView = (RecyclerView) findViewById(R.id.elv_list);

    //Link the adapter
    ArrayList<LocoTodoEvent> events = new ArrayList<>();

    //Populate from sqllite database
    LocoTodoDbHelper dbHelper = new LocoTodoDbHelper(this);
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    if(cursor.moveToFirst()) {
      while (cursor.moveToNext()) {
        String eventName = cursor.getString(4);
        String location  = cursor.getString(1);
        float lat = cursor.getFloat(2);
        float lng = cursor.getFloat(3);
        String id = cursor.getString(0);
        events.add(new LocoTodoEvent(location, eventName, new LatLng(lat, lng), id));
      }
    }
    db.close();

    mAdatper = new EventExpandableAdapter(this, events);
    mRecyclerView.setAdapter(mAdatper);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override
  public void onResume(){
    eventsChanged = false;
    super.onResume();
  }

  @Override
  public void onBackPressed(){
    Intent intent = new Intent();
    intent.putExtra("Changed", eventsChanged);
    setResult(RESULT_OK, intent);
    super.onBackPressed();
  }

  public void notifyEventsChanged(){
    eventsChanged = true;
  }
}
