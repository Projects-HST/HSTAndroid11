package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Question {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("question")
    @Expose
    private String question;

    @SerializedName("user_type")
    @Expose
    private String user_type;

    @SerializedName("status")
    @Expose
    private String status;


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
     * @return The question
     */
    public String getquestion() {
        return question;
    }

    /**
     * @param question The question
     */
    public void setquestion(String question) {
        this.question = question;
    }

    /**
     * @return The user_type
     */
    public String getuser_type() {
        return user_type;
    }

    /**
     * @param user_type The user_type
     */
    public void setuser_type(String user_type) {
        this.user_type = user_type;
    }

    /**
     * @return The status
     */
    public String getstatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setstatus(String status) {
        this.status = status;
    }

}
