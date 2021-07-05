package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QuestionList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("reason_list")
    @Expose
    private ArrayList<Question> questionArrayList = new ArrayList<>();

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
     * @return The questionArrayList
     */
    public ArrayList<Question> getQuestionArrayList() {
        return questionArrayList;
    }

    /**
     * @param questionArrayList The questionArrayList
     */
    public void setQuestionArrayList(ArrayList<Question> questionArrayList) {
        this.questionArrayList = questionArrayList;
    }
}
