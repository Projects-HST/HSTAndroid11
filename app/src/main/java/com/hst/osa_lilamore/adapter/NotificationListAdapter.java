package com.hst.osa_lilamore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.ProductOffer;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    private ArrayList<ProductOffer> productOfferArrayList;
    private OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout notificationLayout;
        private ImageView offerImage;
        private TextView productName;
        private TextView productDescription;
        private TextView productOffer;

        public MyViewHolder(View itemView) {
            super(itemView);

            notificationLayout = (LinearLayout)itemView.findViewById(R.id.offerLayout);
            notificationLayout.setOnClickListener(this);
            offerImage = (ImageView)itemView.findViewById(R.id.offerImage);
            productName = (TextView)itemView.findViewById(R.id.prd_name );
            productDescription = (TextView)itemView.findViewById(R.id.prod_desc);
            productOffer = (TextView)itemView.findViewById(R.id.offer);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickNotification(v, getAdapterPosition());
            }
//            else {
//                onItemClickListener.onItemClickAddress(Selecttick);
//            }
        }
    }

    public NotificationListAdapter(ArrayList<ProductOffer> productOfferArrayList, OnItemClickListener onItemClickListener) {
        this.productOfferArrayList = productOfferArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickNotification(View view, int position);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_notification_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ProductOffer productOffer = productOfferArrayList.get(position);

        holder.productName.setText(productOffer.getProduct_name());
        holder.productDescription.setText(productOffer.getProduct_description());
        if (productOffer.getOffer_status().equalsIgnoreCase("0")) {
            holder.productOffer.setVisibility(View.GONE);
        } else {
            holder.productOffer.setText(productOffer.getOffer_percentage() + " % Off");
        }

        if (OSAValidator.checkNullString(productOffer.getProduct_cover_img())){
            Picasso.get().load(productOffer.getProduct_cover_img()).into(holder.offerImage);
        }
    }

    @Override
    public int getItemCount() {
        return productOfferArrayList.size();
    }
}
