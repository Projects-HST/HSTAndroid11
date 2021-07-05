package com.hst.osa_lilamore.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.bean.support.Wallet;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WalletListAdapter extends RecyclerView.Adapter<WalletListAdapter.MyViewHolder> {

    private ArrayList<Wallet> walletArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTransactionName, txtTransactionDate, txtTransactionAmount;
        public MyViewHolder(View view) {
            super(view);
            txtTransactionName = (TextView) view.findViewById(R.id.note);
            txtTransactionDate = (TextView) view.findViewById(R.id.time);
            txtTransactionAmount = (TextView) view.findViewById(R.id.amt);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public WalletListAdapter(ArrayList<Wallet> walletArrayList, OnItemClickListener onItemClickListener) {
        this.walletArrayList = walletArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_wallet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Wallet wallet = walletArrayList.get(position);

        holder.txtTransactionName.setText(wallet.getnotes());
        holder.txtTransactionDate.setText(wallet.getcreated_at());

        if (wallet.getstatus().equalsIgnoreCase("Credited")) {
            holder.txtTransactionAmount.setText("+₹" + wallet.gettransaction_amt());
            holder.txtTransactionAmount.setTextColor(ContextCompat.getColor(holder.txtTransactionAmount.getContext(), R.color.wallet_green));
        } else {
            holder.txtTransactionAmount.setText("-₹" + wallet.gettransaction_amt());
            holder.txtTransactionAmount.setTextColor(ContextCompat.getColor(holder.txtTransactionAmount.getContext(), R.color.wallet_red));
        }

    }

    @Override
    public int getItemCount() {
        return walletArrayList.size();
    }
}
