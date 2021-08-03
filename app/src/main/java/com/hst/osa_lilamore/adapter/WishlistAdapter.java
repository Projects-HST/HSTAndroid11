package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder> {

    private ArrayList<Product> productArrayList;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    boolean likeClick = false;
    private String resFor;
    private int pos;
//    private View itemView;
//    MyViewHolder holder;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductOffer, txtProductName, txtProductPrice, txtProductMRP;
        public LinearLayout productLayout;
        public ImageView productBanner, productLike;
        public RatingBar productRating;

        public MyViewHolder(View view) {
            super(view);

            productLayout = (LinearLayout) view.findViewById(R.id.product_layout);
            productBanner = (ImageView) view.findViewById(R.id.product_img);
            productLike = (ImageView) view.findViewById(R.id.product_like);
            productLayout.setOnClickListener(this);
            txtProductOffer = (TextView) view.findViewById(R.id.offer);
            txtProductName = (TextView) view.findViewById(R.id.product_name);
            txtProductPrice = (TextView) view.findViewById(R.id.product_price);
            txtProductMRP = (TextView) view.findViewById(R.id.product_mrp);
            productRating = (RatingBar) view.findViewById(R.id.ratingBar);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickBestSelling(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }

    public WishlistAdapter(Context context, ArrayList<Product> newsFeedArrayList, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.productArrayList = newsFeedArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickBestSelling(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_grid_product, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = productArrayList.get(position);
//        likeClick = holder.productLike.performClick();

        holder.txtProductName.setText(product.getproduct_name());
        if (product.getOffer_status().equalsIgnoreCase("0")) {
            holder.txtProductOffer.setVisibility(View.GONE);
            holder.txtProductMRP.setVisibility(View.GONE);
            holder.txtProductPrice.setText("₹" + product.getprod_actual_price());
        } else {
            holder.txtProductOffer.setText(product.getoffer_percentage() + " % Off");
            holder.txtProductPrice.setText("₹" + product.getprod_actual_price());
            holder.txtProductMRP.setText("₹" + product.getprod_mrp_price());
            holder.txtProductMRP.setPaintFlags(holder.txtProductMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (OSAValidator.checkNullString(product.getReview_average())) {
            holder.productRating.setVisibility(View.VISIBLE);
            holder.productRating.setRating(Float.parseFloat(product.getReview_average()));
        } else {
            holder.productRating.setVisibility(View.GONE);
        }
        if (product.getWishlisted().equalsIgnoreCase("1")) {
            likeClick = true;
            holder.productLike.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_filled));
        }
//        else {
//            likeClick = true;
//            holder.productLike.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_filled));
//        }

        if (OSAValidator.checkNullString(product.getproduct_cover_img())) {
            Picasso.get().load(product.getproduct_cover_img()).into(holder.productBanner);
        }
//        else {
//            holder.productBanner.setImageResource(R.drawable.imgsample);
//        }
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if ((position + 1) % 7 == 4 || (position + 1) % 7 == 0) {
            return 2;
        } else {
            return 1;
        }*/
        if (productArrayList.get(position) != null || productArrayList.get(position).getSize() > 0)
            return 2;
        else
            return 1;
    }
}
