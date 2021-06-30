package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Colour;

import java.util.ArrayList;

public class ColourListAdapter extends RecyclerView.Adapter<ColourListAdapter.MyViewHolder> {

    private ArrayList<Colour> colourArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;
    public static int selected_item = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtSizeName;
        public LinearLayout colourLay;
        public LinearLayout colourSelectionLay;

        public MyViewHolder(View view) {
            super(view);
            colourSelectionLay = (LinearLayout) view.findViewById(R.id.selection_layout);
            colourLay = (LinearLayout) view.findViewById(R.id.colour_layout);
            colourLay.setOnClickListener(this);
            txtSizeName = (TextView) view.findViewById(R.id.txt_size_name);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickColour(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public ColourListAdapter(ArrayList<Colour> colourArrayList1, ColourListAdapter.OnItemClickListener onItemClickListener) {
        this.colourArrayList = colourArrayList1;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickColour(View view, int position);
    }


    @Override
    public ColourListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_colour, parent, false);

        return new ColourListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ColourListAdapter.MyViewHolder holder, int position) {
        Colour colour = colourArrayList.get(position);
        holder.txtSizeName.setText(colour.getcolor_name());
//        holder.colourLay.setBackgroundColor(Color.parseColor(colour.getcolor_code()));
        holder.colourLay.getBackground().setColorFilter(Color.parseColor(colour.getcolor_code()), PorterDuff.Mode.SRC_ATOP);
        if (position == selected_item) {
            holder.colourSelectionLay.setBackground(ContextCompat.getDrawable(holder.colourSelectionLay.getContext(), R.drawable.btn_colour_selected));
//            notifyDataSetChanged();
        } else {
            holder.colourSelectionLay.setBackground(ContextCompat.getDrawable(holder.colourSelectionLay.getContext(), R.drawable.btn_colour_unselected));
//            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return colourArrayList.size();
    }
}