package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.activity.CartActivity;
import com.hst.osa_lilamore.bean.support.CartItem;
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

public class CartItemListAdapter extends RecyclerView.Adapter<CartItemListAdapter.MyViewHolder> implements IServiceListener {

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
    CartItemListAdapter.MyViewHolder myViewHolder;

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
                ((CartActivity)mContext).reLoadPage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtProductName, txtProductPrice, txtProductMRP, txtProductStock, productQuantity;
        public LinearLayout productLayout;
        public ImageView productBanner, productDelete;
        public ImageView btnPlus, btnMinus;
        public MyViewHolder(View view) {
            super(view);
            productLayout = (LinearLayout) view.findViewById(R.id.product_layout);
            productBanner = (ImageView) view.findViewById(R.id.product_img);
            productDelete = (ImageView) view.findViewById(R.id.product_delete);
            productDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = getAdapterPosition();
                    cartID = productArrayList.get(pos).getid();
                    deleteItem();
                }
            });
            productLayout.setOnClickListener(this);
            txtProductName = (TextView) view.findViewById(R.id.product_name);
            txtProductPrice = (TextView) view.findViewById(R.id.product_price);
            txtProductMRP = (TextView) view.findViewById(R.id.product_mrp);
            txtProductStock = (TextView) view.findViewById(R.id.stock_status);
            txtProductMRP = (TextView) view.findViewById(R.id.product_mrp);
            btnMinus = (ImageView) view.findViewById(R.id.minus);
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = getAdapterPosition();
                    cartID = productArrayList.get(pos).getid();
                    checkValueMinus();
                }
            });
            productQuantity = (TextView) view.findViewById(R.id.quantity);
            btnPlus = (ImageView) view.findViewById(R.id.plus);
            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = getAdapterPosition();
                    cartID = productArrayList.get(pos).getid();
                    checkValuePlus();
                }
            });
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


    private void checkValueMinus() {
        int compareWithOneTen = Integer.parseInt(productArrayList.get(pos).getquantity());
        if ((compareWithOneTen > 1) && ((compareWithOneTen) <= 10)) {
            int currentVal = compareWithOneTen;
            currentVal--;
            productArrayList.get(pos).setquantity(String.valueOf(currentVal));
            notifyItemChanged(pos);
            notifyDataSetChanged();
            if (currentVal <= Integer.parseInt(productArrayList.get(pos).getstocks_left())) {
                myViewHolder.txtProductStock.setText("In Stock");
                myViewHolder.txtProductStock.setTextColor(ContextCompat.getColor(myViewHolder.txtProductStock.getContext(), R.color.in_stock));
                quantity = String.valueOf(currentVal);
                changeQuantity();
            }
        }
    }

    private void checkValuePlus() {
        int compareWithOneTen = Integer.parseInt(productArrayList.get(pos).getquantity());
        if ((compareWithOneTen >= 1) && ((compareWithOneTen) < 10)) {
            int currentVal = compareWithOneTen;
            currentVal++;
            productArrayList.get(pos).setquantity(String.valueOf(currentVal));
            notifyItemChanged(pos);
            notifyDataSetChanged();
            if (currentVal >= Integer.parseInt(productArrayList.get(pos).getstocks_left())) {
                myViewHolder.txtProductStock.setText("Out of Stock");
                myViewHolder.txtProductStock.setTextColor(ContextCompat.getColor(myViewHolder.txtProductStock.getContext(), R.color.out_of_stock));
            } else {
                quantity = String.valueOf(currentVal);
                changeQuantity();
            }
        }
    }

    private void deleteItem() {
        resFor = "delete";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_CART_ID, cartID);
            jsonObject.put(OSAConstants.KEY_USER_ID, UserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.DELETE_FROM__CART;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void changeQuantity() {
        resFor = "quantity";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_CART_ID, cartID);
            jsonObject.put(OSAConstants.KEY_USER_ID, UserID);
            jsonObject.put(OSAConstants.PARAMS_QUANTITY, quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.UPDATE_QUANTITY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }


    public  CartItemListAdapter(Context context, ArrayList<CartItem> ProductArrayList, CartItemListAdapter.OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.productArrayList = ProductArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickCart(View view, int position);
    }


    @Override
    public CartItemListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cart, parent, false);


        serviceHelper = new ServiceHelper(itemView.getContext());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(itemView.getContext());
        UserID = PreferenceStorage.getUserId(itemView.getContext());
        return new CartItemListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartItemListAdapter.MyViewHolder holder, int position) {
        product = productArrayList.get(position);
        myViewHolder = holder;
        holder.txtProductName.setText(product.getproduct_name());
        holder.txtProductMRP.setVisibility(View.GONE);
        holder.txtProductPrice.setText("₹" + product.getprice());
        holder.productQuantity.setText(product.getquantity());

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