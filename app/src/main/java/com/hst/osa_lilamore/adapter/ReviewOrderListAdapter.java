package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.activity.ReviewOrderActivity;
import com.hst.osa_lilamore.bean.support.CartItem;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewOrderListAdapter extends RecyclerView.Adapter<ReviewOrderListAdapter.MyViewHolder> implements IServiceListener {

    private ArrayList<CartItem> productArrayList;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    String resFor = "";
    int pos ;
    String cartID = "";
    String UserID = "";
    String quantity = "";
    CartItem product;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    MyViewHolder myViewHolder;

    @Override
    public void onResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            if (status.equalsIgnoreCase("success")) {
                if (resFor.equalsIgnoreCase("quantity")) {
                } else {
                    productArrayList.remove(pos);
                    notifyItemRemoved(pos);
                    notifyDataSetChanged();
                }
                ((ReviewOrderActivity)mContext).reLoadPage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductName, txtProductPrice, txtProductMRP, productQuantity;
        public LinearLayout productLayout;
        public ImageView productBanner, productDelete;
        public ImageView btnPlus, btnMinus;
        public MyViewHolder(View view) {
            super(view);
            productLayout = (LinearLayout) view.findViewById(R.id.product_layout);
            productBanner = (ImageView) view.findViewById(R.id.product_img);
            productLayout.setOnClickListener(this);
            txtProductName = (TextView) view.findViewById(R.id.product_name);
            txtProductPrice = (TextView) view.findViewById(R.id.product_price);
            txtProductMRP = (TextView) view.findViewById(R.id.product_mrp);
            productQuantity = (TextView) view.findViewById(R.id.product_quantity);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickCart(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }

    public  ReviewOrderListAdapter(Context context, ArrayList<CartItem> ProductArrayList, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.productArrayList = ProductArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickCart(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review_order, parent, false);


        serviceHelper = new ServiceHelper(itemView.getContext());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(itemView.getContext());
        UserID = PreferenceStorage.getUserId(itemView.getContext());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        product = productArrayList.get(position);
        myViewHolder = holder;
        holder.txtProductName.setText(product.getproduct_name());
        holder.txtProductMRP.setVisibility(View.GONE);
        holder.txtProductPrice.setText("₹" + product.getprice());

//        if(OSAValidator.checkNullString(product.getquantity())) {
            holder.productQuantity.setText("Quantity: " + product.getquantity());
//        }
//        else {
//            holder.productQuantity.setVisibility(View.GONE);
//        }

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