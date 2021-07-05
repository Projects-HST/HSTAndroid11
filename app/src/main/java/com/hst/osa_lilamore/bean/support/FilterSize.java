package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterSize {

    @SerializedName("cat_id")
    @Expose
    private String cat_id;

    @SerializedName("sub_cat_id")
    @Expose
    private String sub_cat_id;

    @SerializedName("mas_size_id")
    @Expose
    private String mas_size_id;

    @SerializedName("size")
    @Expose
    private String size;

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
     * @return The mas_size_id
     */
    public String getmas_size_id() {
        return mas_size_id;
    }

    /**
     * @param mas_size_id The mas_size_id
     */
    public void setmas_size_id(String mas_size_id) {
        this.mas_size_id = mas_size_id;
    }

    /**
     * @return The size
     */
    public String getsize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setsize(String size) {
        this.size = size;
    }

}
