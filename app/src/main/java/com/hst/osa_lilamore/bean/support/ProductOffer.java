package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductOffer implements Serializable {

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

    @SerializedName("prod_size_chart")
    @Expose
    private String prod_size_chart;

    @SerializedName("product_description")
    @Expose
    private String product_description;

    @SerializedName("offer_status")
    @Expose
    private String offer_status;

    @SerializedName("specification_status")
    @Expose
    private String specification_status;

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

    @SerializedName("delivery_fee_status")
    @Expose
    private String delivery_fee_status;

    @SerializedName("prod_return_policy")
    @Expose
    private String prod_return_policy;

    @SerializedName("prod_cod")
    @Expose
    private String prod_cod;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getSku_code() {
        return sku_code;
    }

    public void setSku_code(String sku_code) {
        this.sku_code = sku_code;
    }

    public String getProduct_cover_img() {
        return product_cover_img;
    }

    public void setProduct_cover_img(String product_cover_img) {
        this.product_cover_img = product_cover_img;
    }

    public String getProd_size_chart() {
        return prod_size_chart;
    }

    public void setProd_size_chart(String prod_size_chart) {
        this.prod_size_chart = prod_size_chart;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getOffer_status() {
        return offer_status;
    }

    public void setOffer_status(String offer_status) {
        this.offer_status = offer_status;
    }

    public String getSpecification_status() {
        return specification_status;
    }

    public void setSpecification_status(String specification_status) {
        this.specification_status = specification_status;
    }

    public String getCombined_status() {
        return combined_status;
    }

    public void setCombined_status(String combined_status) {
        this.combined_status = combined_status;
    }

    public String getProd_actual_price() {
        return prod_actual_price;
    }

    public void setProd_actual_price(String prod_actual_price) {
        this.prod_actual_price = prod_actual_price;
    }

    public String getProd_mrp_price() {
        return prod_mrp_price;
    }

    public void setProd_mrp_price(String prod_mrp_price) {
        this.prod_mrp_price = prod_mrp_price;
    }

    public String getOffer_percentage() {
        return offer_percentage;
    }

    public void setOffer_percentage(String offer_percentage) {
        this.offer_percentage = offer_percentage;
    }

    public String getDelivery_fee_status() {
        return delivery_fee_status;
    }

    public void setDelivery_fee_status(String delivery_fee_status) {
        this.delivery_fee_status = delivery_fee_status;
    }

    public String getProd_return_policy() {
        return prod_return_policy;
    }

    public void setProd_return_policy(String prod_return_policy) {
        this.prod_return_policy = prod_return_policy;
    }

    public String getProd_cod() {
        return prod_cod;
    }

    public void setProd_cod(String prod_cod) {
        this.prod_cod = prod_cod;
    }

    public String getProduct_meta_title() {
        return product_meta_title;
    }

    public void setProduct_meta_title(String product_meta_title) {
        this.product_meta_title = product_meta_title;
    }

    public String getProduct_meta_desc() {
        return product_meta_desc;
    }

    public void setProduct_meta_desc(String product_meta_desc) {
        this.product_meta_desc = product_meta_desc;
    }

    public String getProduct_meta_keywords() {
        return product_meta_keywords;
    }

    public void setProduct_meta_keywords(String product_meta_keywords) {
        this.product_meta_keywords = product_meta_keywords;
    }

    public String getStocks_left() {
        return stocks_left;
    }

    public void setStocks_left(String stocks_left) {
        this.stocks_left = stocks_left;
    }

    public String getReview_average() {
        return review_average;
    }

    public void setReview_average(String review_average) {
        this.review_average = review_average;
    }

    public String getWishlisted() {
        return wishlisted;
    }

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
