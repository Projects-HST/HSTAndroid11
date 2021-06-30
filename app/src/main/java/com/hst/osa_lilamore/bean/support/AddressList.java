package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressList implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("pincode")
    @Expose
    private String pincode;

    @SerializedName("house_no")
    @Expose
    private String house_no;

    @SerializedName("street")
    @Expose
    private String street;

    @SerializedName("landmark")
    @Expose
    private String landmark;

    @SerializedName("full_name")
    @Expose
    private String full_name;

    @SerializedName("mobile_number")
    @Expose
    private String mobile_number;

    @SerializedName("email_address")
    @Expose
    private String email_address;

    @SerializedName("alternative_mobile_number")
    @Expose
    private String alternative_mobile_number;

    @SerializedName("address_mode")
    @Expose
    private String address_mode;

    private int size = 3;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getAlternative_mobile_number() {
        return alternative_mobile_number;
    }

    public void setAlternative_mobile_number(String alternative_mobile_number) {
        this.alternative_mobile_number = alternative_mobile_number;
    }

    public String getAddress_mode() {
        return address_mode;
    }

    public void setAddress_mode(String address_mode) {
        this.address_mode = address_mode;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
