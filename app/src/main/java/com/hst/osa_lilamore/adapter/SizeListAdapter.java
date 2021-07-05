package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Category;
import com.hst.osa_lilamore.bean.support.Size;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SizeListAdapter extends RecyclerView.Adapter<SizeListAdapter.MyViewHolder> {

    private ArrayList<Size> sizeArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;
    public static int selected_item = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtSizeName;
        public LinearLayout sizeLay;

        public MyViewHolder(View view) {
            super(view);
            sizeLay = (LinearLayout) view.findViewById(R.id.size_layout);
            sizeLay.setOnClickListener(this);
            txtSizeName = (TextView) view.findViewById(R.id.txt_size_name);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickSize(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public SizeListAdapter(ArrayList<Size> SizeArrayList, OnItemClickListener onItemClickListener) {
        this.sizeArrayList = SizeArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickSize(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_size, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Size size = sizeArrayList.get(position);
        holder.txtSizeName.setText(size.getsize());
//        holder.sizeLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                holder.txtSizeName.setTextColor(ContextCompat.getColor(holder.sizeLay.getContext(), R.color.white));
//                holder.sizeLay.setBackground(ContextCompat.getDrawable(holder.sizeLay.getContext(), R.drawable.btn_size_color));
//                notifyDataSetChanged();
//            }
//        });
        if (position == selected_item) {
            holder.txtSizeName.setTextColor(ContextCompat.getColor(holder.sizeLay.getContext(), R.color.white));
            holder.sizeLay.setBackground(ContextCompat.getDrawable(holder.sizeLay.getContext(), R.drawable.btn_size_color));
//            notifyDataSetChanged();
        } else {
            holder.txtSizeName.setTextColor(ContextCompat.getColor(holder.sizeLay.getContext(), R.color.size_text));
            holder.sizeLay.setBackground(ContextCompat.getDrawable(holder.sizeLay.getContext(), R.drawable.btn_size));
//            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return sizeArrayList.size();
    }
}
