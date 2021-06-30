package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartItem implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("product_name")
    @Expose
    private String product_name;

    @SerializedName("product_id")
    @Expose
    private String product_id;

    @SerializedName("product_cover_img")
    @Expose
    private String product_cover_img;

    @SerializedName("product_description")
    @Expose
    private String product_description;

    @SerializedName("category_name")
    @Expose
    private String category_name;

    @SerializedName("color_code")
    @Expose
    private String color_code;

    @SerializedName("color_name")
    @Expose
    private String color_name;

    @SerializedName("quantity")
    @Expose
    private String quantity;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("total_amount")
    @Expose
    private String total_amount;
    
    @SerializedName("stocks_left")
    @Expose
    private String stocks_left;

    @SerializedName("size")
    @Expose
    private String size;

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
     * @return The product_name
     */
    public String getproduct_name() {
        return product_name;
    }

    /**
     * @param product_name The product_name
     */
    public void setproduct_name(String product_name) {
        this.product_name = product_name;
    }

    /**
     * @return The product_id
     */
    public String getproduct_id() {
        return product_id;
    }

    /**
     * @param product_id The product_id
     */
    public void setproduct_id(String product_id) {
        this.product_id = product_id;
    }

    /**
     * @return The product_cover_img
     */
    public String getproduct_cover_img() {
        return product_cover_img;
    }

    /**
     * @param product_cover_img The product_cover_img
     */
    public void setproduct_cover_img(String product_cover_img) {
        this.product_cover_img = product_cover_img;
    }

    /**
     * @return The category_name
     */
    public String getcategory_name() {
        return category_name;
    }

    /**
     * @param category_name The category_name
     */
    public void setcategory_name(String category_name) {
        this.category_name = category_name;
    }

    /**
     * @return The color_code
     */
    public String getcolor_code() {
        return color_code;
    }

    /**
     * @param color_code The color_code
     */
    public void setcolor_code(String color_code) {
        this.color_code = color_code;
    }

    /**
     * @return The color_name
     */
    public String getcolor_name() {
        return color_name;
    }

    /**
     * @param color_name The color_name
     */
    public void setcolor_name(String color_name) {
        this.color_name = color_name;
    }

    /**
     * @return The quantity
     */
    public String getquantity() {
        return quantity;
    }

    /**
     * @param quantity The quantity
     */
    public void setquantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * @return The product_description
     */
    public String getproduct_description() {
        return product_description;
    }

    /**
     * @param product_description The product_description
     */
    public void setproduct_description(String product_description) {
        this.product_description = product_description;
    }

    /**
     * @return The price
     */
    public String getprice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setprice(String price) {
        this.price = price;
    }

    /**
     * @return The total_amount
     */
    public String gettotal_amount() {
        return total_amount;
    }

    /**
     * @param total_amount The total_amount
     */
    public void settotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    /**
     * @return The stocks_left
     */
    public String getstocks_left() {
        return stocks_left;
    }

    /**
     * @param stocks_left The stocks_left
     */
    public void setstocks_left(String stocks_left) {
        this.stocks_left = stocks_left;
    }

    /**
     * @return The size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(String size) {
        this.size = size;
    }

}