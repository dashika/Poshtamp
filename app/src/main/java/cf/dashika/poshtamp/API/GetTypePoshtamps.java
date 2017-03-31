package cf.dashika.poshtamp.API;

import cf.dashika.poshtamp.Model.TypePoshtamps;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by dashika on 23/03/17.
 */

public interface GetTypePoshtamps {
    @GET("api/GetActiveServices?fmt=json")
    Call<TypePoshtamps> listPoshtamps();
}
