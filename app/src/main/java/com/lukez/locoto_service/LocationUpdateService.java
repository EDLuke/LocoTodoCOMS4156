package com.lukez.locoto_service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
  private static final String LOG_TAG = "LocationUpdateService";
  public static final String LOCATION_UPDATE_ACTION = "com.lukez.locoto_service.LocationUpdateService";

  /**
   * The desired interval for location updates. Inexact. Updates may be more or less frequent.
   */
  public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

  /**
   * The desired distance change for location updates. Inexact. Updates may be more or less frequent.
   */
  public static final long UPDATE_DISTANCE_IN_METERS = 15;

  /**
   * The fastest rate for active location updates. Exact. Updates will never be more frequent
   * than this value.
   */
  public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

  /**
   * Provides the entry point to Google Play services.
   */
  protected GoogleApiClient mGoogleApiClient;

  /**
   * Stores parameters for requests to the FusedLocationProviderApi.
   */
  protected LocationRequest mLocationRequest;

  /**
   * Represents a geographical location.
   */
  protected Location mCurrentLocation;

  protected LocationManager mLocationManager;


  public LocationUpdateService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null; //Not using this
  }

  @Override
  public void onCreate() {
    super.onCreate();

    buildGoogleApiClient();
    buildLocationManager();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(LOG_TAG, "Service initializing");

    if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
      startLocationUpdates();

    sendBroadCast();

    return Service.START_REDELIVER_INTENT;
  }

  @Override
  public void onConnected(Bundle bundle) {
    startLocationUpdates();
  }

  @Override
  public void onConnectionSuspended(int i) {
    // The connection to Google Play services was lost for some reason. We call connect() to
    // attempt to re-establish the connection.
    Log.i(LOG_TAG, "Connection suspended");
    mGoogleApiClient.connect();
  }

  @Override
  public void onLocationChanged(Location location) {
    Location manager = getLastLocationFromManager();

    //TODO: Fine tune this
    if(manager != null)
      mCurrentLocation = manager;
    else
      mCurrentLocation = location;

    //Broadcast the new location
    sendBroadCast();
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
    // onConnectionFailed.
    Log.i(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
  }


  /**
   * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
   * LocationServices API.
   */
  protected synchronized void buildGoogleApiClient() {
    Log.i(LOG_TAG, "Building GoogleApiClient");
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

    createLocationRequest();
  }

  protected synchronized void buildLocationManager() {
    android.location.LocationListener locationListener = new android.location.LocationListener() {
      @Override
      public void onLocationChanged(Location location) {

      }

      public void onStatusChanged(String provider, int status, Bundle extras) {
      }

      public void onProviderEnabled(String provider) {
      }

      public void onProviderDisabled(String provider) {
      }
    };
    mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS, UPDATE_DISTANCE_IN_METERS , locationListener);

  }

  /**
   * Sets up the location request. Android has two location request settings:
   * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
   * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
   * the AndroidManifest.xml.
   * <p/>
   * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
   * interval (5 seconds), the Fused Location Provider API returns location updates that are
   * accurate to within a few feet.
   * <p/>
   * These settings are appropriate for mapping applications that show real-time location
   * updates.
   */
  protected void createLocationRequest() {
    mGoogleApiClient.connect();
    mLocationRequest = new LocationRequest();

    // Sets the desired interval for active location updates. This interval is
    // inexact. You may not receive updates at all if no location sources are available, or
    // you may receive them slower than requested. You may also receive updates faster than
    // requested if other applications are requesting location at a faster interval.
    mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

    // Sets the fastest rate for active location updates. This interval is exact, and your
    // application will never receive updates faster than this value.
    mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  /**
   * Requests location updates from the FusedLocationApi.
   */
  protected void startLocationUpdates() {
    // The final argument to {@code requestLocationUpdates()} is a LocationListener
    // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    Log.i(LOG_TAG, "start LocationUpdates");
  }

  /**
   * Removes location updates from the FusedLocationApi.
   */
  protected void stopLocationUpdates() {
    // It is a good practice to remove location requests when the activity is in a paused or
    // stopped state. Doing so helps battery performance and is especially
    // recommended in applications that request frequent location updates.

    Log.d(LOG_TAG, "stop LocationUpdates");
    // The final argument to {@code requestLocationUpdates()} is a LocationListener
    // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
  }

  protected Location getLastLocationFused() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return null;
    }
    return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
  }

  protected Location getLastLocationFromManager() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return null;
    }
    Location location_gps = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    Location location_wifi = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    Location location_passive = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

    Location[] locations = new Location[]{location_gps, location_wifi, location_passive};
    for (Location loc : locations) {
      if (loc != null)
        return loc;
    }

    return null;
  }

  protected void sendBroadCast(){
    if(mCurrentLocation == null) {
      Location manager = getLastLocationFromManager();
      Location fused = getLastLocationFused();
      if(fused != null)
        mCurrentLocation = fused;

      if(manager != null)
        mCurrentLocation = manager;

      if(fused == null && manager == null){
        mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
        mCurrentLocation.setLatitude(40.8075);
        mCurrentLocation.setLongitude(73.9626);
      }
    }

    Intent localIntent = new Intent(LOCATION_UPDATE_ACTION).putExtra("Location", mCurrentLocation);
    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
  }



  @Override
  public void onDestroy() {
    super.onDestroy();
    stopLocationUpdates();
  }


}
