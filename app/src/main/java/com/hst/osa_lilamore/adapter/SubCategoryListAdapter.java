package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.SubCategory;

import java.util.ArrayList;

public class SubCategoryListAdapter extends RecyclerView.Adapter<SubCategoryListAdapter.MyViewHolder> {

    private ArrayList<SubCategory> categoryArrayList;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    public static int selected_item = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout txtLayout;
        public TextView txtCategoryName;

        public MyViewHolder(View view) {
            super(view);
            txtLayout = (LinearLayout) view.findViewById(R.id.txtLay);
            txtLayout.setOnClickListener(this);
            txtCategoryName = (TextView) view.findViewById(R.id.txt_sub_category);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public SubCategoryListAdapter(Context context, ArrayList<SubCategory> CategoryArrayList, OnItemClickListener onItemClickListener) {
        this.categoryArrayList = CategoryArrayList;
        this.mContext = context;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_sub_cat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (position == selected_item) {
            holder.txtLayout.setBackground(ContextCompat.getDrawable(holder.txtLayout.getContext(), R.drawable.btn_sel_sub_cat));
            holder.txtCategoryName.setTextColor(ContextCompat.getColor(holder.txtCategoryName.getContext(), R.color.white));
        }
        else {
            holder.txtLayout.setBackground(ContextCompat.getDrawable(holder.txtLayout.getContext(), R.drawable.btn_sub_cat));
            holder.txtCategoryName.setTextColor(ContextCompat.getColor(holder.txtCategoryName.getContext(), R.color.black));
        }
        SubCategory category = categoryArrayList.get(position);
        holder.txtCategoryName.setText(category.getCategory_name());
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if ((position + 1) % 7 == 4 || (position + 1) % 7 == 0) {
            return 2;
        } else {
            return 1;
        }*/
        if (categoryArrayList.get(position) != null || categoryArrayList.get(position).getSize() > 0)
            return 2;
        else
            return 1;
    }

}
