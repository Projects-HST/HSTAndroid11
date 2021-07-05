package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.activity.AddAddressActivity;
import com.hst.osa_lilamore.activity.ShippingAddressActivity;
import com.hst.osa_lilamore.bean.support.AddressList;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.MyViewHolder> implements IServiceListener {

    private ArrayList<AddressList> addressList;
    Context mContext;
    int indexPos;
    String addressId = "";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private OnItemClickListener onItemClickListener;

    @Override
    public void onResponse(JSONObject response) {
        try {
            String status = response.getString("status");

            if (status.equalsIgnoreCase("success")) {
                addressList.remove(indexPos);
                notifyItemRemoved(indexPos);
                notifyDataSetChanged();
                ((ShippingAddressActivity) mContext).reLoadPage();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtCustomerName, txtCustomerAddress1, txtCustomerAddress2, txtDistrict,
                txtPinCode, txtMobNumber, btnEdit, btnDelete;
        public ImageView selectAddress;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtCustomerName = (TextView) itemView.findViewById(R.id.customerName);
            txtCustomerAddress1 = (TextView) itemView.findViewById(R.id.cusAddress1);
            txtCustomerAddress2 = (TextView) itemView.findViewById(R.id.cusAddress2);
            txtDistrict = (TextView) itemView.findViewById(R.id.cityName);
            txtPinCode = (TextView) itemView.findViewById(R.id.pinCode);
            txtMobNumber = (TextView) itemView.findViewById(R.id.mobNum);
            selectAddress = (ImageView) itemView.findViewById(R.id.sel_address);
            selectAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    indexPos = getAdapterPosition();
//                    boolean checked = selectAddress.isClickable();
                    for (int i = 0; i < addressList.size(); i++) {
//                    AddressList address = addressList.get(indexPos);
//                        if ((i == indexPos)) {
                            addressList.get(i).setAddress_mode("0");
//                        }
                    }
                    addressList.get(indexPos).setAddress_mode("1");
                    notifyItemChanged(indexPos);
                    notifyDataSetChanged();
//                    ((ShippingAddressActivity)mContext).reLoadPage();
                    Intent refInt = new Intent("addressMode");
                    refInt.putExtra("addId", addressList.get(indexPos).getId());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(refInt);
                }
            });
            btnEdit = (TextView) itemView.findViewById(R.id.btnEdit);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    indexPos = getAdapterPosition();
                    AddressList address = addressList.get(indexPos);
                    Intent editInt = new Intent(mContext, AddAddressActivity.class);
                    Bundle bundle = new Bundle();
//                    editInt.putExtra("addId", address.getId());
                    bundle.putSerializable("addressObj", address);
                    editInt.putExtras(bundle);
                    mContext.startActivity(editInt);
                }
            });
            btnDelete = (TextView) itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle(R.string.title_delete);
                    alertDialogBuilder.setMessage(R.string.txt_delete);
                    alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            indexPos = getAdapterPosition();
                            addressId = addressList.get(indexPos).getId();
                            if (!addressId.equals("")) {
                                deleteAddress();
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialogBuilder.show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickAddress(v, getAdapterPosition());
            }
//            else {
//                onItemClickListener.onItemClickAddress(Selecttick);
//            }
        }

        private void deleteAddress() {
            JSONObject jsonObject = new JSONObject();
            String id = PreferenceStorage.getUserId(mContext);
            try {
                jsonObject.put(OSAConstants.KEY_ADDRESS_ID, addressId);
                jsonObject.put(OSAConstants.KEY_USER_ID, id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = OSAConstants.BUILD_URL + OSAConstants.DELETE_ADDRESS;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        }

//        private void setDefaultAddress() {
//
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put(OSAConstants.KEY_USER_ID, "3");
//                jsonObject.put(OSAConstants.KEY_ADDRESS_ID, addressId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            String url = OSAConstants.BUILD_URL + OSAConstants.DEFAULT_ADDRESS;
//            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
//        }
    }

    public AddressListAdapter(Context context, ArrayList<AddressList> addressArrayList, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.addressList = addressArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickAddress(View view, int position);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_address, parent, false);

        serviceHelper = new ServiceHelper(itemView.getContext());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(itemView.getContext());

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AddressList address = addressList.get(position);

        holder.txtCustomerName.setText(address.getFull_name());

        if (OSAValidator.checkNullString(address.getHouse_no())) {
            holder.txtCustomerAddress1.setText(address.getHouse_no());
        }
        if (OSAValidator.checkNullString(address.getStreet())) {
            holder.txtCustomerAddress2.setText(address.getStreet());
        }
        holder.txtDistrict.setText(address.getCity());
        holder.txtPinCode.setText(address.getPincode());
        holder.txtMobNumber.setText(address.getMobile_number());

        if (address.getAddress_mode().equalsIgnoreCase("0")) {
            holder.selectAddress.setImageResource(R.drawable.ic_check_mark_unchecked);
        } else {
            holder.selectAddress.setImageResource(R.drawable.ic_check_mark_checked);
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

//    public boolean itemIsChecked(int position) {
//        return itemCheckedPosition[position];
//    }
}
