package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("parent_id")
    @Expose
    private String parent_id;

    @SerializedName("category_name")
    @Expose
    private String category_name;

    @SerializedName("category_image")
    @Expose
    private String category_image;

    @SerializedName("category_desc")
    @Expose
    private String category_desc;

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
     * @return The parent_id
     */
    public String getparent_id() {
        return parent_id;
    }

    /**
     * @param parent_id The parent_id
     */
    public void setparent_id(String parent_id) {
        this.parent_id = parent_id;
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
     * @return The category_image
     */
    public String getcategory_image() {
        return category_image;
    }

    /**
     * @param category_image The category_image
     */
    public void setcategory_image(String category_image) {
        this.category_image = category_image;
    }

    /**
     * @return The category_desc
     */
    public String getCategory_desc() {
        return category_desc;
    }

    /**
     * @param category_desc The category_desc
     */
    public void setCategory_desc(String category_desc) {
        this.category_desc = category_desc;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}