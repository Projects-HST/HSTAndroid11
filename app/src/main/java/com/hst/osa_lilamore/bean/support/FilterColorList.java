package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FilterColorList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("colour_list")
    @Expose
    private ArrayList<FilterColor> colourArrayList = new ArrayList<>();

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
     * @return The colourArrayList
     */
    public ArrayList<FilterColor> getColourArrayList() {
        return colourArrayList;
    }

    /**
     * @param colourArrayList The colourArrayList
     */
    public void setColourArrayList(ArrayList<FilterColor> colourArrayList) {
        this.colourArrayList = colourArrayList;
    }
}
