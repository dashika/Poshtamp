package cf.dashika.poshtamp.API;

import cf.dashika.poshtamp.Model.Poshtamps;
import cf.dashika.poshtamp.Model.TypePoshtamps;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dashika on 26/03/17.
 */

public interface GetPoshtapms {
    @GET("api/GetPostamatsByServiceId?fmt=json")
    Call<Poshtamps> listPoshtamps(@Query("service") String serviceId);
}
