package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ColourList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("product_color")
    @Expose
    private ArrayList<Colour> colourArrayList = new ArrayList<>();

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
    public ArrayList<Colour> getColourArrayList() {
        return colourArrayList;
    }

    /**
     * @param colourArrayList The colourArrayList
     */
    public void setColourArrayList(ArrayList<Colour> colourArrayList) {
        this.colourArrayList = colourArrayList;
    }
}
