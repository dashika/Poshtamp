package cf.dashika.poshtamp;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hwangjr.rxbus.Bus;

import java.util.ArrayList;
import java.util.List;

import cf.dashika.poshtamp.API.GetPoshtapms;
import cf.dashika.poshtamp.API.GetRoute;
import cf.dashika.poshtamp.API.GetTypePoshtamps;
import cf.dashika.poshtamp.Model.Poshtamps;
import cf.dashika.poshtamp.Model.Postamat;
import cf.dashika.poshtamp.Model.TypePoshtamps;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by dashika on 23/03/17.
 */

public class PoshtampApllication extends Application {

    private static PoshtampApllication instance;

    public static PoshtampApllication get() {
        return instance;
    }

    //API
/*    private GetTypePoshtamps api_getPoshtampsType;

    public GetTypePoshtamps getApi_getPoshtampsType() {
        return api_getPoshtampsType;
    }

    private GetPoshtapms api_getPoshtamps;

    public GetPoshtapms getApi_getPoshtamps() {
        return api_getPoshtamps;
    }

    public GetRoute getApi_Route() {
        return api_Route;
    }

    public void setApi_Route(GetRoute api_Route) {
        this.api_Route = api_Route;
    }

    public GetRoute api_Route;
*/
    static <S> S createService(Class<S> serviceClass) {
        return builder.create(serviceClass);
    }

    //DATA
    private Poshtamps poshtampses = new Poshtamps();

    public Poshtamps getFilter_poshtampses() {
        if (filter_poshtampses.getPostamats() == null) {
            filter_poshtampses.setPostamats(new ArrayList<>());
            return filter_poshtampses;
        }
        if (filter_poshtampses.getPostamats().size() == 0) {
            filter_poshtampses.setStatus(poshtampses.getStatus());
            List<Postamat> list = filter_poshtampses.getPostamats();
            for (Postamat p : poshtampses.getPostamats()) {
                list.add(p);
            }
        }
            return filter_poshtampses;
    }

    public void setFilter_poshtampses(Poshtamps filter_poshtampses) {
        this.filter_poshtampses = filter_poshtampses;
    }

    private Poshtamps filter_poshtampses = new Poshtamps();

    public Poshtamps getPoshtampses() {
        return poshtampses;
    }

    public void setPoshtampses(Poshtamps poshtampses) {
        this.poshtampses = poshtampses;
    }

    public TypePoshtamps getTypePoshtamps() {
        return typePoshtamps;
    }

    private TypePoshtamps typePoshtamps = new TypePoshtamps();

    public void setTypePoshtamps(TypePoshtamps typePoshtamps) {
        this.typePoshtamps = typePoshtamps;
    }

    private static final String PRIVATE_URL = "https://bpk-postamat.privatbank.ua/";
    public static final String GOOGLE_URL = "https://maps.googleapis.com/";
    public static String apiBaseUrl = PRIVATE_URL;
   // private static Retrofit retrofit;
    private static Retrofit builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(apiBaseUrl).build();
    public static void changeApiBaseUrl(boolean newApiBaseUrl) {
        apiBaseUrl = newApiBaseUrl ? PRIVATE_URL : GOOGLE_URL;

        builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(apiBaseUrl).build();
    }

    public void onCreate() {
        super.onCreate();

        instance = this;
        bus = new RxBus();

        Fresco.initialize(this);

     /*   Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PRIVATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
*/
      //  api_getPoshtampsType = retrofit.create(GetTypePoshtamps.class);
     //   api_getPoshtamps = retrofit.create(GetPoshtapms.class);

    }

    private RxBus bus;

    public RxBus bus() {
        return bus;
    }

    static final class RxBus {
        private static Bus sBus;

        static synchronized Bus get() {
            if (sBus == null) {
                sBus = new Bus();
            }
            return sBus;
        }

        private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

        public void send(Object o) {
            bus.onNext(o);
        }

        public Observable<Object> toObserverable() {
            return bus;
        }

        public boolean hasObservers() {
            return bus.hasObservers();
        }
    }

}
