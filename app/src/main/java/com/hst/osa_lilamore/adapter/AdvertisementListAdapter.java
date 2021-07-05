package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Advertisement;
import com.hst.osa_lilamore.bean.support.Category;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdvertisementListAdapter extends RecyclerView.Adapter<AdvertisementListAdapter.MyViewHolder> {

    private ArrayList<Advertisement> advertisementArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView categoryImage;
        public RelativeLayout advertisementLay;
        public MyViewHolder(View view) {
            super(view);
            advertisementLay = (RelativeLayout) view.findViewById(R.id.ad_layout);
            advertisementLay.setOnClickListener(this);
            categoryImage = (ImageView) view.findViewById(R.id.ad_image);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemAdvertisementClick(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public AdvertisementListAdapter(ArrayList<Advertisement> AdvertisementArrayList, OnItemClickListener onItemClickListener) {
        this.advertisementArrayList = AdvertisementArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemAdvertisementClick(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_advertisement, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Advertisement advertisement = advertisementArrayList.get(position);

        if (OSAValidator.checkNullString(advertisement.getad_img())) {
            Picasso.get().load(advertisement.getad_img()).into(holder.categoryImage);
        } else {
            holder.categoryImage.setImageResource(R.drawable.ic_profile);
        }

    }

    @Override
    public int getItemCount() {
        return advertisementArrayList.size();
    }
}