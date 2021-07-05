package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Advertisement;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewArrivalsListAdapter extends RecyclerView.Adapter<NewArrivalsListAdapter.MyViewHolder> {

    private ArrayList<Product> productArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductName, txtProductPrice, txtProductMRP;
        public LinearLayout productLayout;
        public ImageView productBanner, productLike;
        public RatingBar productRating;
        public MyViewHolder(View view) {
            super(view);
            productLayout = (LinearLayout) view.findViewById(R.id.product_layout);
            productBanner = (ImageView) view.findViewById(R.id.product_img);
            productLike = (ImageView) view.findViewById(R.id.product_like);
            productLike.setOnClickListener(this);
            productLayout.setOnClickListener(this);
            txtProductName = (TextView) view.findViewById(R.id.product_name);
            txtProductPrice = (TextView) view.findViewById(R.id.product_price);
            txtProductMRP = (TextView) view.findViewById(R.id.product_mrp);
            productRating = (RatingBar) view.findViewById(R.id.ratingBar);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickNewArrivals(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public NewArrivalsListAdapter(ArrayList<Product> ProductArrayList, OnItemClickListener onItemClickListener) {
        this.productArrayList = ProductArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickNewArrivals(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_new_arrivals, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = productArrayList.get(position);

        holder.txtProductName.setText(product.getproduct_name());
        if (product.getOffer_status().equalsIgnoreCase("0")) {
            holder.txtProductMRP.setVisibility(View.GONE);
            holder.txtProductPrice.setText("₹" + product.getprod_actual_price());
        } else {
            holder.txtProductPrice.setText("₹" + product.getprod_actual_price());
            holder.txtProductMRP.setText("₹" + product.getprod_mrp_price());
            holder.txtProductMRP.setPaintFlags(holder.txtProductMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.productRating.setRating(Float.parseFloat(product.getReview_average()));

        if (OSAValidator.checkNullString(product.getproduct_cover_img())) {
            Picasso.get().load(product.getproduct_cover_img()).into(holder.productBanner);
        } else {
//            newsImage.setImageResource(R.drawable.news_banner);
        }
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
}