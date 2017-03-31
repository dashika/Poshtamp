package cf.dashika.poshtamp;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import cf.dashika.poshtamp.API.GetPoshtapms;
import cf.dashika.poshtamp.Model.Poshtamps;
import cf.dashika.poshtamp.Model.Postamat;
import cf.dashika.poshtamp.Model.ServiceForGetTypes;
import cf.dashika.poshtamp.Util.Events;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by dashika on 26/03/17.
 */

public class TypePostampAdapter extends RecyclerView.Adapter<TypePostampAdapter.ViewHolder> {

    @Override
    public TypePostampAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new TypePostampAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TypePostampAdapter.ViewHolder holder, int position) {
        final ServiceForGetTypes serviceForGetTypes = PoshtampApllication.get().getTypePoshtamps().getServices().get(position);
        holder.mContentView.setText(serviceForGetTypes.getTitle());
        holder.simpleDraweeView.setImageURI(serviceForGetTypes.getImg());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PoshtampApllication.get().bus().send(new Events.ChangeDialog());
                PoshtampApllication.get().createService(GetPoshtapms.class).listPoshtamps(serviceForGetTypes.getId()).enqueue(new Callback<Poshtamps>() {
                    @Override
                    public void onResponse(Call<Poshtamps> call, Response<Poshtamps> response) {
                        if(response.isSuccessful())
                            PoshtampApllication.get().bus().send(new Events.setPoshtamps(response.body()));
                    }

                    @Override
                    public void onFailure(Call<Poshtamps> call, Throwable t) {

                    }
                });
            }
        });
    }


    public static <T> Observable<T> bindLoadingAction(final Runnable start, final Runnable finish, final Observable<T> source) {
        final Observable<T> o = source.observeOn(AndroidSchedulers.mainThread());
        return o.lift(new Observable.Operator<T, T>() {
            @Override
            public Subscriber<? super T> call(final Subscriber<? super T> child) {
                return new Subscriber<T>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (start != null) {
                            new Handler(Looper.getMainLooper()).post(start::run);
                        }
                        child.onStart();
                    }

                    @Override
                    public void onCompleted() {
                        if (finish != null) {
                            finish.run();
                        }
                        child.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (finish != null) {
                            finish.run();
                        }
                        child.onError(e);
                    }

                    @Override
                    public void onNext(T t) {
                        if (finish != null) {
                            finish.run();
                        }
                        child.onNext(t);
                    }
                };
            }
        });
    }

    @Override
    public int getItemCount() {
        try {

            return PoshtampApllication.get().getTypePoshtamps().getServices().size();
        }
        catch (NullPointerException e)
        {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        SimpleDraweeView simpleDraweeView;
        public Postamat mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.typePoshtamp);
            simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.my_image_view);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
