package com.lukez.locotodo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.lukez.locotodo_db.LocoTodoContract;
import com.lukez.locotodo_db.LocoTodoDbHelper;

public class AddActivity extends Activity {
  LatLng mLatLng;
  EditText mTxfLocation;
  EditText mTxfEvent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add);

    Intent intent = getIntent();

    //Set the map screen shot
    byte[] bytes = intent.getByteArrayExtra("BMP");
    if(bytes != null) {
      Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
      ImageView imgView = (ImageView) findViewById(R.id.imgSnap);
      imgView.setImageBitmap(bitmap);
    }

    //Update text
    LatLng latLng = intent.getParcelableExtra("LatLng");
    mLatLng = latLng;
    TextView txtLatLng = (TextView)findViewById(R.id.txvLatLong);
    txtLatLng.setText(latLng.toString());

    //Get both edit text view
    mTxfLocation = (EditText)findViewById(R.id.txfLocation);
    mTxfEvent = (EditText)findViewById(R.id.txfEvent);
  }

  @Override
  public void onBackPressed(){
    Intent intent = new Intent();
    intent.putExtra("Cancel", true);
    setResult(RESULT_OK, intent);
    super.onBackPressed();
  }

  public void onClickCancel(View v){
    onBackPressed();
  }

  public void onClickAdd(View v) {
    //Initialize db helper
    LocoTodoDbHelper dbHelper = new LocoTodoDbHelper(getApplicationContext());

    SQLiteDatabase db = dbHelper.getWritableDatabase();

    String event = mTxfEvent.getText().toString();
    String location = mTxfLocation.getText().toString();
    double lat = mLatLng.latitude;
    double lng = mLatLng.longitude;

    ContentValues values = new ContentValues();
    values.put(LocoTodoContract.TodoEntry.COLUMN_NAME_EVENT, event);
    values.put(LocoTodoContract.TodoEntry.COLUMN_NAME_LOCATION, location);
    values.put(LocoTodoContract.TodoEntry.COLUMN_NAME_LAT, lat);
    values.put(LocoTodoContract.TodoEntry.COLUMN_NAME_LNG, lng);

    long newRowId = db.insert(LocoTodoContract.TodoEntry.TABLE_NAME, null, values);
    db.close();

    Intent intent = new Intent();
    intent.putExtra("Event", event);
    intent.putExtra("Location", location);
    setResult(RESULT_OK, intent);

    finish();
  }
}
