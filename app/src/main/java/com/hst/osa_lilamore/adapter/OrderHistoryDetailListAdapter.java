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
import com.hst.osa_lilamore.activity.OrderHistoryDetailPage;
import com.hst.osa_lilamore.bean.support.CartItem;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderHistoryDetailListAdapter extends RecyclerView.Adapter<OrderHistoryDetailListAdapter.MyViewHolder>{

    private ArrayList<CartItem> productArrayList;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    public boolean resFor = false;
    int pos ;
    String orderID = "";
    String UserID = "";
    String quantity = "";
    CartItem product;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    MyViewHolder myViewHolder;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductName, txtProductPrice, txtProductMRP, productReplace;
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
            productReplace = (TextView) view.findViewById(R.id.product_review);
            productReplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String productID = productArrayList.get(pos).getproduct_id();
                    String productName = productArrayList.get(pos).getproduct_name();
                    PreferenceStorage.saveProductId(mContext, productID);
                    ((OrderHistoryDetailPage)mContext).replaceProduct(pos);
                }
            });
            if (resFor) {
                productReplace.setVisibility(View.VISIBLE);
            }
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

    public  OrderHistoryDetailListAdapter(Context context, ArrayList<CartItem> ProductArrayList, OnItemClickListener onItemClickListener) {
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
                .inflate(R.layout.list_item_order_history_detail, parent, false);

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

        if (OSAValidator.checkNullString(product.getproduct_cover_img())) {
            Picasso.get().load(product.getproduct_cover_img()).into(holder.productBanner);
        } else {
//            newsImage.setImageResource(R.drawable.news_banner);
        }

        if (resFor) {
            holder.productReplace.setVisibility(View.GONE);
        }
    }

    public void hideall() {
        myViewHolder.productReplace.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
}
