package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.OrderStatus;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderStatusListAdapter extends RecyclerView.Adapter<OrderStatusListAdapter.MyViewHolder> {

    private ArrayList<OrderStatus> orderStatusArrayList;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    String resFor = "";
    int pos;
    String cartID = "";
    String UserID = "";
    String quantity = "";
    OrderStatus orderStatus;
    MyViewHolder myViewHolder;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtStatus, txtVLine;
        public RelativeLayout statusLayout;
        public ImageView imgStatus;

        public MyViewHolder(View view) {
            super(view);
            statusLayout = (RelativeLayout) view.findViewById(R.id.status_layout);
            statusLayout.setOnClickListener(this);
            imgStatus = (ImageView) view.findViewById(R.id.status_img);
            txtStatus = (TextView) view.findViewById(R.id.txt_status);
            txtVLine = (TextView) view.findViewById(R.id.v_line);
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

    public OrderStatusListAdapter(Context context, ArrayList<OrderStatus> orderStatusArrayList, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.orderStatusArrayList = orderStatusArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickHistory(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_order_status, parent, false);

        UserID = PreferenceStorage.getUserId(itemView.getContext());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        orderStatus = orderStatusArrayList.get(position);
        myViewHolder = holder;
        holder.txtStatus.setText(orderStatus.getorder_status());
        if (orderStatus.getstatus().equalsIgnoreCase("1")) {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(holder.imgStatus.getContext(), R.drawable.ic_order_select));
        } else {
            holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(holder.imgStatus.getContext(), R.drawable.ic_order_notselect));
        }
        if (orderStatusArrayList.size()-1 == position) {
            holder.txtVLine.setVisibility(View.GONE);
        }
//        if ()

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
        return orderStatusArrayList.size();
    }
}

