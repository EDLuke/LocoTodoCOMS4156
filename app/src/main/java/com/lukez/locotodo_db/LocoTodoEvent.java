package com.lukez.locotodo_db;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by lukez_000 on 04/28/2017.
 */

public class LocoTodoEvent implements Parent<LocoTodoEvent> {
  private String mID;
  private String mLocation;
  private String mEvent;
  private LatLng mLatlng;

  public String getLocation() {
    return mLocation;
  }

  public void setLocation(String mLocation) {
    this.mLocation = mLocation;
  }

  public String getEvent() {
    return mEvent;
  }

  public void setEvent(String mEvent) {
    this.mEvent = mEvent;
  }

  public LatLng getLatlng() {
    return mLatlng;
  }

  public void setLatlng(LatLng mLatlng) {
    this.mLatlng = mLatlng;
  }

  public String getID(){
    return mID;
  }

  public LocoTodoEvent(String location, String event, LatLng latlng, String id){
    mLocation = location;
    mEvent = event;
    mLatlng = latlng;
    mID = id;
  }


  @Override
  public List<LocoTodoEvent> getChildList() {
    //TODO: un-do the hack here to improve the parent-child relationship (HA)
    java.util.ArrayList<LocoTodoEvent> list = new java.util.ArrayList<>();
    list.add(this);
    return list;
  }

  @Override
  public boolean isInitiallyExpanded() {
    return false;
  }
}
