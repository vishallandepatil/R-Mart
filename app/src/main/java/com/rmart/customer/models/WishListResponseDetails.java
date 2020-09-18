package com.rmart.customer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Satya Seshu on 17/09/20.
 */
public class WishListResponseDetails implements Serializable {

    @SerializedName("shop_name")
    @Expose
    private String shopName;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("wishlist_count")
    @Expose
    private Integer wishListCount;
    @SerializedName("vendor_id")
    @Expose
    private Integer vendorId;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getWishListCount() {
        return wishListCount;
    }

    public void setWishListCount(Integer wishListCount) {
        this.wishListCount = wishListCount;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }
}
