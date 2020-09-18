package com.rmart.customer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created by Satya Seshu on 13/09/20.
 */
public class ProductInCartDetailsModel implements Serializable {

    @SerializedName("cart_id")
    @Expose
    private Integer cartId;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("product_unit_id")
    @Expose
    private Integer productUnitId;
    @SerializedName("unit_number")
    @Expose
    private String unitNumber;
    @SerializedName("total_product_cart_qty")
    @Expose
    private Integer totalProductCartQty;
    @SerializedName("unit_measure")
    @Expose
    private String unitMeasure;
    @SerializedName("short_unit_measure")
    @Expose
    private String shortUnitMeasure;
    @SerializedName("total_unit_price")
    @Expose
    private String totalUnitPrice;
    @SerializedName("total_selling_price")
    @Expose
    private String totalSellingPrice;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("product_expiry_date")
    @Expose
    private String productExpiryDate;
    @SerializedName("vendor_id")
    @Expose
    private Integer vendorId;

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductUnitId() {
        return productUnitId;
    }

    public void setProductUnitId(Integer productUnitId) {
        this.productUnitId = productUnitId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Integer getTotalProductCartQty() {
        return totalProductCartQty;
    }

    public void setTotalProductCartQty(Integer totalProductCartQty) {
        this.totalProductCartQty = totalProductCartQty;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public String getShortUnitMeasure() {
        return shortUnitMeasure;
    }

    public void setShortUnitMeasure(String shortUnitMeasure) {
        this.shortUnitMeasure = shortUnitMeasure;
    }

    public String getTotalUnitPrice() {
        return totalUnitPrice;
    }

    public void setTotalUnitPrice(String totalUnitPrice) {
        this.totalUnitPrice = totalUnitPrice;
    }

    public String getTotalSellingPrice() {
        return totalSellingPrice;
    }

    public void setTotalSellingPrice(String totalSellingPrice) {
        this.totalSellingPrice = totalSellingPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductExpiryDate() {
        return productExpiryDate;
    }

    public void setProductExpiryDate(String productExpiryDate) {
        this.productExpiryDate = productExpiryDate;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    @NotNull
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("cartId", cartId).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(cartId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ProductInCartDetailsModel)) {
            return false;
        }
        ProductInCartDetailsModel rhs = ((ProductInCartDetailsModel) other);
        return new EqualsBuilder().append(cartId, rhs.cartId).isEquals();
    }
}
