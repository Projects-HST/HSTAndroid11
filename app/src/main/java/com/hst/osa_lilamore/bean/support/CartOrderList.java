package com.hst.osa_lilamore.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CartOrderList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("cart_items")
    @Expose
    private ArrayList<CartItem> cartItemArrayList = new ArrayList<>();

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
     * @return The cartItemArrayList
     */
    public ArrayList<CartItem> getCartItemArrayList() {
        return cartItemArrayList;
    }

    /**
     * @param cartItemArrayList The cartItemArrayList
     */
    public void setCartItemArrayList(ArrayList<CartItem> cartItemArrayList) {
        this.cartItemArrayList = cartItemArrayList;
    }
}
