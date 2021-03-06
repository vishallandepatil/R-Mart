package com.rmart.customer.dashboard.model;

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
import com.rmart.glied.GlideApp;
import com.rmart.retiler.inventory.brand.model.Brand;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

public class ShopType implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("images")
    @Expose
    private String images;
    @SerializedName("click")
    @Expose
    private String click;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }


    @BindingAdapter("imageUrl")
    public static void loadImage(View view, ShopType data) {

        ImageView imageview = view.findViewById(R.id.imageview);
        ImageView selectedgreeting = view.findViewById(R.id.selectedgreeting);
        selectedgreeting.setVisibility(View.VISIBLE);
        GlideApp.with(view.getContext()).load(data.images) .listener(new RequestListener<Drawable>() {

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
                signature(new ObjectKey(data.images==null?"":data.images)).
                error(R.mipmap.default_product_image).thumbnail(0.5f).into(imageview);
    }

}
