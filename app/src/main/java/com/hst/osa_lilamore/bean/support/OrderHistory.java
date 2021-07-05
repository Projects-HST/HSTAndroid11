package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderHistory implements Serializable {
    
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("order_id")
    @Expose
    private String order_id;

    @SerializedName("cus_id")
    @Expose
    private String cus_id;

    @SerializedName("purchase_date")
    @Expose
    private String purchase_date;

    @SerializedName("total_amount")
    @Expose
    private String total_amount;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("order_cover_img")
    @Expose
    private String order_cover_img;

    @SerializedName("order_status")
    @Expose
    private String order_status;


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
     * @return The order_id
     */
    public String getorder_id() {
        return order_id;
    }

    /**
     * @param order_id The order_id
     */
    public void setorder_id(String order_id) {
        this.order_id = order_id;
    }

    /**
     * @return The cus_id
     */
    public String getcus_id() {
        return cus_id;
    }

    /**
     * @param cus_id The cus_id
     */
    public void setcus_id(String cus_id) {
        this.cus_id = cus_id;
    }

    /**
     * @return The purchase_date
     */
    public String getpurchase_date() {
        return purchase_date;
    }

    /**
     * @param purchase_date The purchase_date
     */
    public void setpurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
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

    /**
     * @return The order_cover_img
     */
    public String getorder_cover_img() {
        return order_cover_img;
    }

    /**
     * @param order_cover_img The order_cover_img
     */
    public void setorder_cover_img(String order_cover_img) {
        this.order_cover_img = order_cover_img;
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
     * @return The total_amount
     */
    public String getTotal_amount() {
        return total_amount;
    }

    /**
     * @param total_amount The total_amount
     */
    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

}
