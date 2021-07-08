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

public class BestSellingListAdapter extends RecyclerView.Adapter<BestSellingListAdapter.MyViewHolder> implements IServiceListener {

    private ArrayList<Product> productArrayList;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    boolean likeClick = false;
    private String resFor;
    private int pos;

    @Override
    public void onResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            if (status.equalsIgnoreCase("success")) {
                if (resFor.equalsIgnoreCase("addWish")) {
                }
                if (resFor.equalsIgnoreCase("removeWish")) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {
    }

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
            productLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = getAdapterPosition();
                    if (PreferenceStorage.getUserId(mContext).isEmpty()) {
                        if (PreferenceStorage.getUserId(mContext).equalsIgnoreCase("")) {
                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mContext, R.style.alertDialogueTheme);
                            alertDialogBuilder.setTitle(R.string.login);
                            alertDialogBuilder.setMessage(R.string.login_to_continue);
                            alertDialogBuilder.setPositiveButton(R.string.alert_button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });
                            alertDialogBuilder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialogBuilder.show();
                        }
                    } else {
                        if (!likeClick) {
                            productLike.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_filled));
                            addToWishlist();
                        } else {
                            likeClick = false;
                            productLike.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart));
                            removeWishlist();
                        }
                    }
                }
            });
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

    private void addToWishlist() {
        resFor = "addWish";
        JSONObject jsonObject = new JSONObject();
        Product product = productArrayList.get(pos);
        try {
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, product.getid());
            jsonObject.put(OSAConstants.KEY_USER_ID, PreferenceStorage.getUserId(mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.ADD_TO_WISHLIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void removeWishlist() {
        resFor = "removeWish";
        JSONObject jsonObject = new JSONObject();
        Product product = productArrayList.get(pos);
        try {
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, product.getid());
            jsonObject.put(OSAConstants.KEY_USER_ID, PreferenceStorage.getUserId(mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.DELETE_WISHLIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    public BestSellingListAdapter(Context context, ArrayList<Product> newsFeedArrayList, OnItemClickListener onItemClickListener) {
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

        serviceHelper = new ServiceHelper(itemView.getContext());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(itemView.getContext());

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
        }else {
            holder.productRating.setVisibility(View.GONE);
        }
//        if (likeClick){
//            holder.productLike.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_filled));
//        }
//        else {
//            holder.productLike.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart));
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
