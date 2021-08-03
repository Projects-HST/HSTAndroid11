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
import com.hst.osa_lilamore.activity.CartActivity;
import com.hst.osa_lilamore.bean.support.CartItem;
import com.hst.osa_lilamore.bean.support.OrderHistory;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class OrderHistoryListAdapter extends RecyclerView.Adapter<OrderHistoryListAdapter.MyViewHolder> {

    private ArrayList<OrderHistory> orderHistoryArrayList;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    String resFor = "";
    int pos;
    String cartID = "";
    String UserID = "";
    String quantity = "";
    OrderHistory orderHistory;
    MyViewHolder myViewHolder;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductName, txtProductPrice, txtProductMRP, productStatus, productDate;
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
            productDate = (TextView) view.findViewById(R.id.product_date);
            productStatus = (TextView) view.findViewById(R.id.product_status);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickHistory(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }

    public OrderHistoryListAdapter(Context context, ArrayList<OrderHistory> orderHistoryArrayList, OnItemClickListener onItemClickListener) {
        Collections.reverse(orderHistoryArrayList);
        this.mContext = context;
        this.orderHistoryArrayList = orderHistoryArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickHistory(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_order_history, parent, false);

        UserID = PreferenceStorage.getUserId(itemView.getContext());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        orderHistory = orderHistoryArrayList.get(position);
        myViewHolder = holder;
        holder.txtProductName.setText("Order ID: " + orderHistory.getorder_id());
        holder.txtProductMRP.setVisibility(View.GONE);
        holder.txtProductPrice.setText("₹" + orderHistory.getTotal_amount());
        String prodDate = getserverdateformat(orderHistory.getpurchase_date());
        holder.productDate.setText(prodDate);
        holder.productStatus.setText(orderHistory.getorder_status());

    }

    private String getserverdateformat(String dd) {
        String serverFormatDate = "";
        if (dd != null && dd != "") {

            String date = dd;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date testDate = null;
            try {
                testDate = formatter.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            serverFormatDate = sdf.format(testDate);
            System.out.println(".....Date..." + serverFormatDate);
        }
        return serverFormatDate;
    }

    @Override
    public int getItemCount() {
        return orderHistoryArrayList.size();
    }
}
