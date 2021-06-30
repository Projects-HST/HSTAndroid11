package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {
    
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("product_id")
    @Expose
    private String product_id;

    @SerializedName("customer_name")
    @Expose
    private String customer_name;

    @SerializedName("rating")
    @Expose
    private String rating;

    @SerializedName("comment")
    @Expose
    private String comment;
    
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
     * @return The customer_name
     */
    public String getcustomer_name() {
        return customer_name;
    }

    /**
     * @param customer_name The customer_name
     */
    public void setcustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    /**
     * @return The rating
     */
    public String getrating() {
        return rating;
    }

    /**
     * @param rating The rating
     */
    public void setrating(String rating) {
        this.rating = rating;
    }

    /**
     * @return The comment
     */
    public String getcomment() {
        return comment;
    }

    /**
     * @param comment The comment
     */
    public void setcomment(String comment) {
        this.comment = comment;
    }

}
