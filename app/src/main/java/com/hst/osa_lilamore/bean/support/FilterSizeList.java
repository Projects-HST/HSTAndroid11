package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FilterSizeList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("size_list")
    @Expose
    private ArrayList<com.hst.osa_lilamore.bean.support.FilterSize> sizeArrayList = new ArrayList<>();

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
    public ArrayList<com.hst.osa_lilamore.bean.support.FilterSize> getSizeArrayList() {
        return sizeArrayList;
    }

    /**
     * @param sizeArrayList The sizeArrayList
     */
    public void setSizeArrayList(ArrayList<com.hst.osa_lilamore.bean.support.FilterSize> sizeArrayList) {
        this.sizeArrayList = sizeArrayList;
    }
}
