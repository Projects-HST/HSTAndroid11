package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterColor {

    @SerializedName("cat_id")
    @Expose
    private String cat_id;

    @SerializedName("sub_cat_id")
    @Expose
    private String sub_cat_id;

    @SerializedName("mas_color_id")
    @Expose
    private String mas_color_id;

    @SerializedName("attribute_value")
    @Expose
    private String attribute_value;

    @SerializedName("attribute_name")
    @Expose
    private String attribute_name;

    /**
     * @return The cat_id
     */
    public String getid() {
        return cat_id;
    }

    /**
     * @param cat_id The cat_id
     */
    public void setid(String cat_id) {
        this.cat_id = cat_id;
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
     * @return The attribute_value
     */
    public String getAttribute_value() {
        return attribute_value;
    }

    /**
     * @param attribute_value The attribute_value
     */
    public void setAttribute_value(String attribute_value) {
        this.attribute_value = attribute_value;
    }

    /**
     * @return The attribute_name
     */
    public String getAttribute_name() {
        return attribute_name;
    }

    /**
     * @param attribute_name The attribute_name
     */
    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

}
