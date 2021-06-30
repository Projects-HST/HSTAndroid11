package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Wallet implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("transaction_amt")
    @Expose
    private String transaction_amt;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("created_by")
    @Expose
    private String created_by;
    
    @SerializedName("notes")
    @Expose
    private String notes;

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
     * @return The transaction_amt
     */
    public String gettransaction_amt() {
        return transaction_amt;
    }

    /**
     * @param transaction_amt The transaction_amt
     */
    public void settransaction_amt(String transaction_amt) {
        this.transaction_amt = transaction_amt;
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

    /**
     * @return The created_at
     */
    public String getcreated_at() {
        return created_at;
    }

    /**
     * @param created_at The created_at
     */
    public void setcreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * @return The created_by
     */
    public String getcreated_by() {
        return created_by;
    }

    /**
     * @param created_by The created_by
     */
    public void setcreated_by(String created_by) {
        this.created_by = created_by;
    }


    /**
     * @return The notes
     */
    public String getnotes() {
        return notes;
    }

    /**
     * @param notes The notes
     */
    public void setnotes(String notes) {
        this.notes = notes;
    }


}