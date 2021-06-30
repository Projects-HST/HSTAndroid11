package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Colour {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("product_id")
    @Expose
    private String product_id;

    @SerializedName("mas_color_id")
    @Expose
    private String mas_color_id;

    @SerializedName("prod_actual_price")
    @Expose
    private String prod_actual_price;

    @SerializedName("prod_mrp_price")
    @Expose
    private String prod_mrp_price;

    @SerializedName("stocks_left")
    @Expose
    private String stocks_left;

    @SerializedName("color_name")
    @Expose
    private String color_name;

    @SerializedName("color_code")
    @Expose
    private String color_code;

    @SerializedName("anInt")
    @Expose
    private int anInt = 3;

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
     * @return The mas_color_id
     */
    public String getmas_color_id() {
        return mas_color_id;
    }

    /**
     * @param mas_color_id The mas_color_id
     */
    public void setmas_color_id(String mas_color_id) {
        this.mas_color_id = mas_color_id;
    }

    /**
     * @return The prod_actual_price
     */
    public String getProd_actual_price() {
        return prod_actual_price;
    }

    /**
     * @param prod_actual_price The prod_actual_price
     */
    public void setProd_actual_price(String prod_actual_price) {
        this.prod_actual_price = prod_actual_price;
    }

    /**
     * @return The prod_mrp_price
     */
    public String getProd_mrp_price() {
        return prod_mrp_price;
    }

    /**
     * @param prod_mrp_price The prod_mrp_price
     */
    public void setProd_mrp_price(String prod_mrp_price) {
        this.prod_mrp_price = prod_mrp_price;
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

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

}
