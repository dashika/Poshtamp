package cf.dashika.poshtamp.GPS;

import java.util.EventListener;

/**
 * Created by dashika on 8/6/2015.
 */
public interface IGpsListener extends EventListener {
    void OnGpsEvent(GpsEvent event);
}
