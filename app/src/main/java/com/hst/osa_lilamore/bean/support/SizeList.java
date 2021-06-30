package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SizeList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("product_size")
    @Expose
    private ArrayList<Size> sizeArrayList = new ArrayList<>();

    /**
     * @return The count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return The sizeArrayList
     */
    public ArrayList<Size> getSizeArrayList() {
        return sizeArrayList;
    }

    /**
     * @param sizeArrayList The sizeArrayList
     */
    public void setSizeArrayList(ArrayList<Size> sizeArrayList) {
        this.sizeArrayList = sizeArrayList;
    }
}
