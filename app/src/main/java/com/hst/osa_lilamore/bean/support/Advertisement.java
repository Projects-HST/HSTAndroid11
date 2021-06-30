package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Advertisement implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("ad_title")
    @Expose
    private String ad_title;

    @SerializedName("sub_cat_id")
    @Expose
    private String sub_cat_id;

    @SerializedName("ad_img")
    @Expose
    private String ad_img;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The ad_title
     */
    public String getad_title() {
        return ad_title;
    }

    /**
     * @param ad_title The ad_title
     */
    public void setad_title(String ad_title) {
        this.ad_title = ad_title;
    }

    /**
     * @return The sub_cat_id
     */
    public String getsub_cat_id() {
        return sub_cat_id;
    }

    /**
     * @param sub_cat_id The sub_cat_id
     */
    public void setsub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    /**
     * @return The ad_img
     */
    public String getad_img() {
        return ad_img;
    }

    /**
     * @param ad_img The ad_img
     */
    public void setad_img(String ad_img) {
        this.ad_img = ad_img;
    }

}