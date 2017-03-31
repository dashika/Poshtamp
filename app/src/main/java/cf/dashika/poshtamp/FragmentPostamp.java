package cf.dashika.poshtamp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cf.dashika.poshtamp.Model.Poshtamps;
import cf.dashika.poshtamp.Model.Postamat;
import cf.dashika.poshtamp.Util.Events;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


public class FragmentPostamp extends Fragment {

    private Subscription busSubscription;

    public FragmentPostamp() {
    }

    private void autoUnsubBus() {
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    private void handlerBus(Object o) {
        if (o instanceof Events.filterPoshtamps) {
            if(myPostampRecyclerViewAdapter!=null)
            {
                myPostampRecyclerViewAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onPause() {
        autoUnsubBus();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        autoUnsubBus();
        busSubscription = PoshtampApllication.get().bus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handlerBus
                );
    }

    @SuppressWarnings("unused")
    public static FragmentPostamp newInstance(int columnCount) {
        FragmentPostamp fragment = new FragmentPostamp();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    MyPostampRecyclerViewAdapter myPostampRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postamp_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            myPostampRecyclerViewAdapter = new MyPostampRecyclerViewAdapter(PoshtampApllication.get().getFilter_poshtampses().getPostamats());
            recyclerView.setAdapter(myPostampRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
