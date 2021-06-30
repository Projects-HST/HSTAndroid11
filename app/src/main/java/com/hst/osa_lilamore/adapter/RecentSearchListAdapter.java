package com.hst.osa_lilamore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.RecentSearch;

import java.util.ArrayList;

public class RecentSearchListAdapter extends RecyclerView.Adapter<RecentSearchListAdapter.MyViewHolder> {

    private ArrayList<RecentSearch> recentSearchArrayList;
    private RecentSearchListAdapter.OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout txtLayout;
        public TextView recentSearchName;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtLayout = (LinearLayout)itemView.findViewById(R.id.recentLay);
            recentSearchName = (TextView)itemView.findViewById(R.id.searchName);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickRecentSearch(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }

    }

    public RecentSearchListAdapter(ArrayList<RecentSearch> CategoryArrayList, RecentSearchListAdapter.OnItemClickListener onItemClickListener) {
        this.recentSearchArrayList = CategoryArrayList;
//        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickRecentSearch(View view, int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_recent_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        RecentSearch recentSearch = recentSearchArrayList.get(position);
        holder.recentSearchName.setText(recentSearch.getSearch_text());
    }

    @Override
    public int getItemCount() {
        return recentSearchArrayList.size();
    }

    public void clearText(){
        if (recentSearchArrayList != null){
            recentSearchArrayList.clear();
            notifyDataSetChanged();
        }
    }
}
