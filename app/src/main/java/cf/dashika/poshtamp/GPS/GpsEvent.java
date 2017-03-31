package cf.dashika.poshtamp.GPS;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dashika on 8/6/2015.
 */
public class GpsEvent {
    public enum EventType {
        CurrentPositionChange
    }

    public final EventType eventType;
    public final LatLng latLng;

    public GpsEvent(EventType eventType, LatLng latLng) {
        this.eventType = eventType;
        this.latLng = latLng;
    }
}
