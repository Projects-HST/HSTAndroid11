package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderStatus {
    
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("order_status")
    @Expose
    private String order_status;

    @SerializedName("status")
    @Expose
    private String status;
    
    /**
     * @return The id
     */
    public String getid() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setid(String id) {
        this.id = id;
    }

    /**
     * @return The order_status
     */
    public String getorder_status() {
        return order_status;
    }

    /**
     * @param order_status The order_status
     */
    public void setorder_status(String order_status) {
        this.order_status = order_status;
    }

    /**
     * @return The status
     */
    public String getstatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setstatus(String status) {
        this.status = status;
    }
}
