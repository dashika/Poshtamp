package cf.dashika.poshtamp;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import cf.dashika.poshtamp.API.GetRoute;
import cf.dashika.poshtamp.GPS.GPSSettings;
import cf.dashika.poshtamp.GPS.GpsEvent;
import cf.dashika.poshtamp.GPS.IGpsListener;
import cf.dashika.poshtamp.Model.Postamat;
import cf.dashika.poshtamp.Model.Route.MyRoute;
import cf.dashika.poshtamp.Model.Route.Route;
import cf.dashika.poshtamp.Model.Route.Step;
import cf.dashika.poshtamp.Util.NetworkStateReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dashika on 23/03/17.
 */

class MyClusterManager<T> extends ClusterManager implements
        NetworkStateReceiver.NetworkStateReceiverListener, IGpsListener, SearchView.OnQueryTextListener {

    private LatLng LL = new LatLng(0, 0);
    private Activity activity;
    private GoogleMap mMap;
    private float zoom;
    private CameraPosition cameraPosition;

    private GPSSettings gpsSettings;
    private NetworkStateReceiver networkStateReceiver;

    MyClusterManager(Activity activity, GoogleMap map) {
        super(activity, map);
        this.activity = activity;
        mMap = map;
        this.clearItems();
        mMap.clear();
        SetDefault();
        SearchView searchView = (SearchView) activity.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
    }

    public void AddListener() {
        ShowAfterResume();
        gpsSettings = new GPSSettings(activity);
        if (gpsSettings.getListeners().getListenerCount() < 1) gpsSettings.AddListener(this);
        try {
            gpsSettings.getLatitudeAndLongitude();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!gpsSettings.IsGPSOn()) {
            Toast.makeText(activity, R.string.gps_off, Toast.LENGTH_SHORT).show();
        }

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        activity.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    void RemoveListener() {
        if (gpsSettings != null) {
            gpsSettings.RemoveListener(this);
        }
        if (networkStateReceiver != null) {
            networkStateReceiver.removeListener(this);
            activity.unregisterReceiver(networkStateReceiver);
        }
    }

    private void ShowAfterResume() {
        GroupingForMap groupingUsersForMap = new GroupingForMap();
        groupingUsersForMap.FilterByName(this, "");
    }

    private void SetDefault() {
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
        super.onCameraChange(cameraPosition);
        zoom = cameraPosition.zoom;
    }

    Polyline polyline;

    @Override
    public boolean onMarkerClick(Marker marker) {
        zoom = this.cameraPosition.zoom;
        if (zoom < 10) zoom = 10;
        try {
            String id = marker.getSnippet() + "";
            List<Postamat> poshtamps = PoshtampApllication.get().getPoshtampses().getPostamats();
            for (Postamat postamat : poshtamps) {
                if (postamat.getId().equals(id)) {
                    DialogFilter(postamat);
                    PoshtampApllication.changeApiBaseUrl(false);
                    LatLng dest = postamat.getPosition();
                    if (polyline != null) polyline.remove();
                    PoshtampApllication.createService(GetRoute.class).route(LL.latitude + "," + LL.longitude,
                            dest.latitude + "," + dest.longitude,
                            "AIzaSyB7TJtZ5N0YZcdPDUjZORcyF9RZ2bU3aNM").enqueue(new Callback<MyRoute>() {
                        @Override
                        public void onResponse(Call<MyRoute> call, Response<MyRoute> response) {
                            if (response.isSuccessful()) {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyRoute res = response.body();
                                        PolylineOptions rectOptions = new PolylineOptions();
                                        for (Route rout : res.getRoutes()) {
                                            List<Step> legs = rout.getLegs().get(0).getSteps();
                                            for (Step leg : legs) {

                                                rectOptions
                                                        .add(new LatLng(leg.getStartLocation().getLat(), leg.getStartLocation().getLng()))
                                                        .add(new LatLng(leg.getEndLocation().getLat(), leg.getEndLocation().getLng()))
                                                        .clickable(true)
                                                        .color(Color.BLUE);

                                            }
                                        }
                                        polyline = mMap.addPolyline(rectOptions);

                                    }
                                });

                            }
                        }

                        @Override
                        public void onFailure(Call<MyRoute> call, Throwable t) {
                            Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    PoshtampApllication.changeApiBaseUrl(true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(marker.getPosition())
                    .zoom(zoom += 4)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        }
        return true;
    }

    private void DialogFilter(Postamat postamat) {
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_detail, null);

        Dialog dialog = new Dialog(activity, R.style.MyDialogTheme);
        dialog.setContentView(v);
        dialog.setTitle(R.string.detail);
        TextView textViewDetail = (TextView) v.findViewById(R.id.twDetail);
        textViewDetail.setText(postamat.getBranch() + "\n" + postamat.getAddress() + "\n" + postamat.getSchedule().toString(activity));

        dialog.show();
    }

    @Override
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {

    }

    // GPS
    private void ChangeLL(LatLng ll) {
        this.LL = ll;
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(ll)
                .zoom((float) 13)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
    }

    @Override
    public void OnGpsEvent(GpsEvent event) {
        ChangeLL(event.latLng);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.clearItems();
        mMap.clear();
        this.clearItems();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        GroupingForMap groupingForMap = new GroupingForMap();
        groupingForMap.FilterByName(this, newText);
        return true;
    }

}