package cf.dashika.poshtamp;

import android.app.Activity;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import cf.dashika.poshtamp.Model.Postamat;

/**
 * Created by Belyaeva on 4/21/2016.
 */
public class PersonRenderer extends DefaultClusterRenderer<Postamat> {

    public PersonRenderer(Activity context, GoogleMap mMap, MyClusterManager<Postamat> mClusterManager) {
        super(context.getApplicationContext(), mMap, mClusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Postamat item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).snippet(item.getId() + "").title(item.getAddress());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }


    @Override
    protected void onBeforeClusterRendered(Cluster<Postamat> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }
}
