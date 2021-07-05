package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderHistoryList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("order_details")
    @Expose
    private ArrayList<OrderHistory> orderHistoryArrayList = new ArrayList<>();

    /**
     * @return The count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return The orderHistoryArrayList
     */
    public ArrayList<OrderHistory> getOrderHistoryArrayList() {
        return orderHistoryArrayList;
    }

    /**
     * @param orderHistoryArrayList The orderHistoryArrayList
     */
    public void setOrderHistoryArrayList(ArrayList<OrderHistory> orderHistoryArrayList) {
        this.orderHistoryArrayList = orderHistoryArrayList;
    }
}
