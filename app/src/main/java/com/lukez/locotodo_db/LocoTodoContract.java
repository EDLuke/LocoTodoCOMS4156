package com.lukez.locotodo_db;

import android.provider.BaseColumns;

/**
 * Created by lukez_000 on 04/26/2017.
 */

public final class LocoTodoContract {
  private LocoTodoContract(){}

  public static class TodoEntry implements BaseColumns{
    public static final String TABLE_NAME = "todo";
    public static final String COLUMN_NAME_EVENT = "event";
    public static final String COLUMN_NAME_LOCATION = "location";
    public static final String COLUMN_NAME_LAT = "lat";
    public static final String COLUMN_NAME_LNG = "lng";
  }
}
