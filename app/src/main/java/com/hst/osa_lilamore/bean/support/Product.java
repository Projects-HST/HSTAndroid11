package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("product_name")
    @Expose
    private String product_name;

    @SerializedName("sku_code")
    @Expose
    private String sku_code;

    @SerializedName("product_cover_img")
    @Expose
    private String product_cover_img;

    @SerializedName("product_description")
    @Expose
    private String product_description;

    @SerializedName("offer_status")
    @Expose
    private String offer_status;

    @SerializedName("combined_status")
    @Expose
    private String combined_status;

    @SerializedName("prod_actual_price")
    @Expose
    private String prod_actual_price;

    @SerializedName("prod_mrp_price")
    @Expose
    private String prod_mrp_price;

    @SerializedName("offer_percentage")
    @Expose
    private String offer_percentage;

    @SerializedName("product_meta_title")
    @Expose
    private String product_meta_title;

    @SerializedName("product_meta_desc")
    @Expose
    private String product_meta_desc;

    @SerializedName("product_meta_keywords")
    @Expose
    private String product_meta_keywords;

    @SerializedName("stocks_left")
    @Expose
    private String stocks_left;

    @SerializedName("review_average")
    @Expose
    private String review_average;

    @SerializedName("wishlisted")
    @Expose
    private String wishlisted;

    @SerializedName("size")
    @Expose
    private int size = 3;

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
     * @return The sku_code
     */
    public String getsku_code() {
        return sku_code;
    }

    /**
     * @param sku_code The sku_code
     */
    public void setsku_code(String sku_code) {
        this.sku_code = sku_code;
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
     * @return The offer_status
     */
    public String getOffer_status() {
        return offer_status;
    }

    /**
     * @param offer_status The offer_status
     */
    public void setOffer_status(String offer_status) {
        this.offer_status = offer_status;
    }

    /**
     * @return The combined_status
     */
    public String getcombined_status() {
        return combined_status;
    }

    /**
     * @param combined_status The combined_status
     */
    public void setcombined_status(String combined_status) {
        this.combined_status = combined_status;
    }

    /**
     * @return The prod_mrp_price
     */
    public String getprod_mrp_price() {
        return prod_mrp_price;
    }

    /**
     * @param prod_mrp_price The prod_mrp_price
     */
    public void setprod_mrp_price(String prod_mrp_price) {
        this.prod_mrp_price = prod_mrp_price;
    }

    /**
     * @return The prod_actual_price
     */
    public String getprod_actual_price() {
        return prod_actual_price;
    }

    /**
     * @param prod_actual_price The prod_actual_price
     */
    public void setprod_actual_price(String prod_actual_price) {
        this.prod_actual_price = prod_actual_price;
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
     * @return The offer_percentage
     */
    public String getoffer_percentage() {
        return offer_percentage;
    }

    /**
     * @param offer_percentage The offer_percentage
     */
    public void setoffer_percentage(String offer_percentage) {
        this.offer_percentage = offer_percentage;
    }

    /**
     * @return The product_meta_title
     */
    public String getproduct_meta_title() {
        return product_meta_title;
    }

    /**
     * @param product_meta_title The product_meta_title
     */
    public void setproduct_meta_title(String product_meta_title) {
        this.product_meta_title = product_meta_title;
    }

    /**
     * @return The product_meta_desc
     */
    public String getproduct_meta_desc() {
        return product_meta_desc;
    }

    /**
     * @param product_meta_desc The product_meta_desc
     */
    public void setproduct_meta_desc(String product_meta_desc) {
        this.product_meta_desc = product_meta_desc;
    }

    /**
     * @return The product_meta_keywords
     */
    public String getproduct_meta_keywords() {
        return product_meta_keywords;
    }

    /**
     * @param product_meta_keywords The product_meta_keywords
     */
    public void setproduct_meta_keywords(String product_meta_keywords) {
        this.product_meta_keywords = product_meta_keywords;
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
     * @return The review_average
     */
    public String getReview_average() {
        return review_average;
    }

    /**
     * @param review_average The review_average
     */
    public void setReview_average(String review_average) {
        this.review_average = review_average;
    }

    /**
     * @return The wishlisted
     */
    public String getWishlisted() {
        return wishlisted;
    }

    /**
     * @param wishlisted The wishlisted
     */
    public void setWishlisted(String wishlisted) {
        this.wishlisted = wishlisted;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}