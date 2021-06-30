package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Size {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("product_id")
    @Expose
    private String product_id;

    @SerializedName("mas_size_id")
    @Expose
    private String mas_size_id;

    @SerializedName("size")
    @Expose
    private String size;

    @SerializedName("stocks_left")
    @Expose
    private String stocks_left;

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
