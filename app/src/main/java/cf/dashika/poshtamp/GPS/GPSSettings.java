package cf.dashika.poshtamp.GPS;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import cf.dashika.poshtamp.Util.EventListenerList;


public class GPSSettings {
    private final int minDistance = 100; //meters
    private final int time = 10 * 60 * 1000;// 1 * 60 * 1000 ; ms

    public static boolean first = true;

    private LocationManager mlocManager;
    private LocationListener mListner;
    private Double lat, longi;
    private Activity context;

    private EventListenerList listeners = new EventListenerList();

    public EventListenerList getListeners() {
        return listeners;
    }

    public void AddListener(IGpsListener listener) {
        listeners.add(IGpsListener.class, listener);
    }

    public void RemoveListener(IGpsListener listener) {
        listeners.remove(IGpsListener.class, listener);
    }

    private void FireGpsEvent(GpsEvent event) {
        Object[] listeners = this.listeners.getListenerList();
        int numListeners = listeners.length;
        for (int i = 0; i < numListeners; i += 2) {
            if (listeners[i] == IGpsListener.class) {
                ((IGpsListener) listeners[i + 1]).OnGpsEvent(event);
            }
        }
    }

    public GPSSettings(Activity context) {
        this.context = context;
        mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean IsGPSOn() {
        return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) | mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private GoogleApiClient googleApiClient;

    /*
    * Get current position
     */
    public void getLatitudeAndLongitude() {
        try {
            mListner = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    setLatitude(location.getLatitude());
                    setLongitude(location.getLongitude());
                    FireGpsEvent(new GpsEvent(GpsEvent.EventType.CurrentPositionChange, new LatLng(getLatitude(), getLongitude())));
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            try {
                //    final Criteria criteria = new Criteria();
                //     criteria.setAccuracy(Criteria.ACCURACY_FINE);
                //       criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                //     criteria.setBearingRequired(true);
                //     criteria.setCostAllowed(true);
                //   String provider = mlocManager.getBestProvider(criteria, true);
                //   Location location = mlocManager.getLastKnownLocation(provider);
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, minDistance, mListner);
                LocationRequest mLocationRequest = LocationRequest.create();
                // Use high accuracy
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                // Set the update interval to 1 seconds
                mLocationRequest.setInterval(time); //ms
                // Set the fastest update interval to 1 second
                //mLocationRequest.setFastestInterval(time);

                if (first) {
                    first = false;
                    if (googleApiClient == null) {
                        googleApiClient = new GoogleApiClient.Builder(context)
                                .addApi(LocationServices.API)
                                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                    @Override
                                    public void onConnected(Bundle bundle) {

                                    }

                                    @Override
                                    public void onConnectionSuspended(int i) {

                                    }
                                })
                                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                    @Override
                                    public void onConnectionFailed(ConnectionResult connectionResult) {

                                    }
                                }).build();
                        googleApiClient.connect();
                    }
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest);

                    //**************************
                    builder.setAlwaysShow(true); //this is the key ingredient
                    //**************************

                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            final LocationSettingsStates state = result.getLocationSettingsStates();
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(context, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied. The client can initialize location
                                    // requests here.
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied. But could be fixed by showing the user
                                    // a dialog.

                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    break;
                            }
                        }
                    });
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            context.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                         int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mlocManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, time * 5, minDistance * 100, mListner);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    try {
                        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time * 3, minDistance * 10, mListner);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setLatitude(Double latitide) {
        lat = latitide;
    }

    private void setLongitude(Double longitude) {
        longi = longitude;
    }

    private Double getLatitude() {
        if (lat != null) {
            return lat;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return (double) 0;
        }
        Location loc = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc == null) {
                loc = mlocManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            if (loc != null) {
                return loc.getLatitude();
            }
        } else {
            return loc.getLatitude();
        }
        return 0.0;
    }

    private Double getLongitude() {
        if (longi != null) {
            return longi;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return (double) 0;
        }
        Location loc = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc == null) {
                loc = mlocManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            if (loc != null) {
                return loc.getLongitude();
            }
        } else {
            return loc.getLongitude();
        }
        return 0.0;
    }

}
