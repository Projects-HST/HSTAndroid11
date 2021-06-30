package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddressArrayList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("address_list")
    @Expose
    private ArrayList<AddressList> addressArrayList = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<AddressList> getAddressArrayList() {
        return addressArrayList;
    }

    public void setAddressArrayList(ArrayList<AddressList> addressArrayList) {
        this.addressArrayList = addressArrayList;
    }
}
