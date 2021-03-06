package com.rmart.customer.shops.list.models;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rmart.R;
import com.rmart.customer.models.CustomerProductsDetailsUnitModel;
import com.rmart.glied.GlideApp;
import com.rmart.retiler.inventory.category.model.Category;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

public class SearchProducts {
    @SerializedName("product_id")
    @Expose
    private int productId;
    @SerializedName("product_cat_id")
    @Expose
    private int productCatId;

    public int getProductId() {
        return productId;
    }

    public int getProductCatId() {
        return productCatId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ShopDetailsModel getShopDetailsModel() {
        return shopDetailsModel;
    }

    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("product_details")
    @Expose
    private String productDetails;
    @SerializedName("display_image")
    @Expose
    private String displayImage;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("shop")
    @Expose
    private ShopDetailsModel shopDetailsModel ;

    public ArrayList<CustomerProductsDetailsUnitModel> getProduct_unit() {
        return product_unit;
    }

    @SerializedName("product_unit")
    @Expose
    private ArrayList<CustomerProductsDetailsUnitModel> product_unit ;


    @BindingAdapter("imageUrl")
    public static void loadImage(View view, SearchProducts data) {

        ImageView imageview = view.findViewById(R.id.imageview);
        ImageView selectedgreeting = view.findViewById(R.id.selectedgreeting);
        selectedgreeting.setVisibility(View.VISIBLE);
        GlideApp.with(view.getContext()).load(data.displayImage) .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                selectedgreeting.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                selectedgreeting.setVisibility(View.GONE);
                return false;
            }
        }).dontAnimate().
                diskCacheStrategy(DiskCacheStrategy.ALL).
                signature(new ObjectKey(data.displayImage==null?"":data.displayImage)).
                error(R.mipmap.default_product_image).thumbnail(0.5f).into(imageview);
    }

}
