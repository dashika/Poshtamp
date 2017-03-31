package cf.dashika.poshtamp;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import cf.dashika.poshtamp.API.GetTypePoshtamps;
import cf.dashika.poshtamp.Model.Postamat;
import cf.dashika.poshtamp.Model.TypePoshtamps;
import cf.dashika.poshtamp.Util.Events;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MyClusterManager<Postamat> mClusterManager;
    private RadioButton radioButtonList;
    private boolean MoodFragmentMap = true;
    private SupportMapFragment mapFragment;
    private Dialog dialog;
    private Subscription busSubscription;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        VisibleMap(false);
        if (mClusterManager != null)
            mClusterManager.AddListener();
        autoUnsubBus();
        busSubscription = PoshtampApllication.get().bus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handlerBus
                );

        if (PoshtampApllication.get().getTypePoshtamps().getServices() == null)
            PoshtampApllication.get().createService(GetTypePoshtamps.class).listPoshtamps().enqueue(new Callback<TypePoshtamps>() {
                @Override
                public void onResponse(Call<TypePoshtamps> call, Response<TypePoshtamps> response) {
                    if (response.isSuccessful())
                        PoshtampApllication.get().bus().send(new Events.setTypePoshtamps(response.body()));
                }

                @Override
                public void onFailure(Call<TypePoshtamps> call, Throwable t) {
                    Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    ProgressDialog mProgressDialog;

    private void handlerBus(Object o) {
        if (o instanceof Events.ChangeDialog) {
            dialog.dismiss();
            mProgressDialog = new ProgressDialog(MapsActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.show();
        }

        if (o instanceof Events.setTypePoshtamps) {
            PoshtampApllication.get().setTypePoshtamps(((Events.setTypePoshtamps) o).typePoshtamps);
            runOnUiThread(this::DialogFilter);
        }
        if (o instanceof Events.setPoshtamps) {
            if (mProgressDialog != null) mProgressDialog.dismiss();
            PoshtampApllication.get().setPoshtampses(((Events.setPoshtamps) o).poshtamps);
            if (mMap != null) {
                new GroupingForMap().Grouping(mClusterManager);
            }
            PoshtampApllication.get().bus().send(new Events.filterPoshtamps(PoshtampApllication.get().getFilter_poshtampses().getPostamats()));
        }
    }

    private void autoUnsubBus() {
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        if (mClusterManager != null)
            mClusterManager.RemoveListener();
        autoUnsubBus();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!VisibleMap(true)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        PoshtampApllication.RxBus.get().register(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);

        ImageButton btn_ShowFilterPanel = (ImageButton) findViewById(R.id.btn_ShowFilterPanel);
        btn_ShowFilterPanel.setOnClickListener(view -> DialogFilter());

        radioButtonList = (RadioButton) findViewById(R.id.btnMapLand);
        RelativeLayout mood = (RelativeLayout) findViewById(R.id.mood);
        mood.setOnClickListener(view -> {
            if (MoodFragmentMap) {
                SetList();
                radioButtonList.setChecked(MoodFragmentMap);
            } else {
                if (getFragmentManager() != null) {
                    int count = getFragmentManager().getBackStackEntryCount();
                    for (int i = 0; i < count; ++i) {
                        getFragmentManager().popBackStack();
                    }
                }
                SetVisibleMap(View.VISIBLE);
                radioButtonList.setChecked(MoodFragmentMap);
            }
            MoodFragmentMap = !MoodFragmentMap;
        });

        radioButtonList.setOnClickListener(view -> {
            if (MoodFragmentMap) {
                SetList();
                radioButtonList.setChecked(MoodFragmentMap);
            } else {
                if (getFragmentManager() != null) {
                    int count = getFragmentManager().getBackStackEntryCount();
                    for (int i = 0; i < count; ++i) {
                        getFragmentManager().popBackStack();
                    }
                }
                SetVisibleMap(View.VISIBLE);
                radioButtonList.setChecked(MoodFragmentMap);
            }
            MoodFragmentMap = !MoodFragmentMap;
        });
    }

    private void SetList() {
        SetVisibleMap(View.INVISIBLE);
        radioButtonList.setChecked(true);
        getFragmentManager().popBackStack();
        FragmentTransaction fTrans = (MapsActivity.this).getFragmentManager().beginTransaction();
        FragmentPostamp fragment = new FragmentPostamp();
        fTrans.addToBackStack(null);
        fTrans.replace(R.id.frameLayout, fragment, getString(R.string.fragment_postamps));
        fTrans.commit();
    }

    public void DialogFilter() {
        if (dialog == null) {
            View v = getLayoutInflater().inflate(R.layout.dialog_filter_map, null);

            dialog = new Dialog(MapsActivity.this, R.style.MyDialogTheme);
            dialog.setContentView(v);
            dialog.setTitle(R.string.select_type);

            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycleTypeOfPostamp);
            recyclerView.setAdapter(new TypePostampAdapter());
        }
        dialog.show();
    }

    private void setUpClusterer() {
        mClusterManager = new MyClusterManager<>(this, mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setRenderer(new PersonRenderer(MapsActivity.this, mMap, mClusterManager));
        mClusterManager.AddListener();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpClusterer();
    }

    private void SetVisibleMap(int visible) {
        View v = mapFragment.getView();
        assert v != null;
        v.setVisibility(visible);
    }

    private boolean VisibleMap(boolean something_do) {
        if (something_do) {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
                int count = getFragmentManager().getBackStackEntryCount();
                if (count == 0) return false;
                else if (count == 1) {
                    if (getFragmentManager().findFragmentByTag(getString(R.string.fragment_postamps)) != null) {
                        SetVisibleMap(View.VISIBLE);
                    } else {
                        SetVisibleMap(View.VISIBLE);
                    }
                    return true;
                } else {
                    if (getFragmentManager().findFragmentByTag(getString(R.string.fragment_postamps)) != null & count < 3) {
                        SetVisibleMap(View.INVISIBLE);
                        radioButtonList.setChecked(true);
                    }
                    return true;
                }
            } else return false;
        } else return true;
    }
}
