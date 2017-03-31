package cf.dashika.poshtamp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cf.dashika.poshtamp.Model.Postamat;

import java.util.List;

class MyPostampRecyclerViewAdapter extends RecyclerView.Adapter<MyPostampRecyclerViewAdapter.ViewHolder> {

    private final List<Postamat> mValues;

     MyPostampRecyclerViewAdapter(List<Postamat> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_postamp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getAddress());
        holder.mContentView.setText(mValues.get(position).getBranch());

        holder.mView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
         final View mView;
         final TextView mIdView;
         final TextView mContentView;
         Postamat mItem;

         ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
