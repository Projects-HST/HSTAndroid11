package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RecentSearchList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("search_keywords")
    @Expose
    private ArrayList<RecentSearch> recentSearchArrayList = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<RecentSearch> getRecentSearchArrayList() {
        return recentSearchArrayList;
    }

    public void setRecentSearchArrayList(ArrayList<RecentSearch> recentSearchArrayList) {
        this.recentSearchArrayList = recentSearchArrayList;
    }
}
