package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReviewList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("product_review")
    @Expose
    private ArrayList<Review> reviewArrayList = new ArrayList<>();

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
     * @return The reviewArrayList
     */
    public ArrayList<Review> getReviewArrayList() {
        return reviewArrayList;
    }

    /**
     * @param reviewArrayList The reviewArrayList
     */
    public void setReviewArrayList(ArrayList<Review> reviewArrayList) {
        this.reviewArrayList = reviewArrayList;
    }
}
