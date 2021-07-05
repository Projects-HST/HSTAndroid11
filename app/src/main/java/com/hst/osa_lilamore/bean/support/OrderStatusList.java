package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderStatusList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("order_status_list")
    @Expose
    private ArrayList<OrderStatus> orderStatusArrayList = new ArrayList<>();

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
     * @return The orderStatusArrayList
     */
    public ArrayList<OrderStatus> getOrderStatusArrayList() {
        return orderStatusArrayList;
    }

    /**
     * @param orderStatusArrayList The orderStatusArrayList
     */
    public void setOrderStatusArrayList(ArrayList<OrderStatus> orderStatusArrayList) {
        this.orderStatusArrayList = orderStatusArrayList;
    }
}
