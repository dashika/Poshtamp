package cf.dashika.poshtamp.API;

import cf.dashika.poshtamp.Model.Poshtamps;
import cf.dashika.poshtamp.Model.Route.MyRoute;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by programer on 27.03.17.
 */

public interface GetRoute {
    @GET("maps/api/directions/json")
    Call<MyRoute> route(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);
}
