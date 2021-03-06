package com.rmart.customer.shops.products.repositories;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.rmart.BuildConfig;
import com.rmart.customer.models.AddToCartResponseDetails;
import com.rmart.customer.shops.home.api.Shops;
import com.rmart.customer.shops.home.model.ShopHomePageResponce;
import com.rmart.customer.shops.products.api.Products;
import com.rmart.customer.shops.products.model.AddProductToWishListResponse;
import com.rmart.customer.shops.products.model.ProductDetailsDescResponse;
import com.rmart.customer.shops.products.model.ProductsResponce;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.BaseResponse;
import com.rmart.utilits.RetrofitClientInstance;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rmart.utilits.Utils.CLIENT_ID;

public class ProductsRepository {

    public static MutableLiveData<ProductsResponce> getVenderProducts(Context context, int vendorId, int shop_id, String categoeryid, String searchPrase, String start_page, String sub_category_id, String productstype){

        Products shope = RetrofitClientInstance.getRetrofitInstance().create(Products.class);
        final MutableLiveData<ProductsResponce> resultMutableLiveData = new MutableLiveData<>();
        Call<ProductsResponce> call = shope.getVenderProducts(CLIENT_ID,vendorId,shop_id, MyProfile.getInstance(context).getUserID(),categoeryid,searchPrase,start_page,sub_category_id,MyProfile.getInstance(context).getRoleID(),productstype);
        final ProductsResponce result = new ProductsResponce();

        call.enqueue(new Callback<ProductsResponce>() {
            @Override
            public void onResponse(Call<ProductsResponce> call, Response<ProductsResponce> response) {
                ProductsResponce data = response.body();
                resultMutableLiveData.setValue(data);
            }

            @Override
            public void onFailure(Call<ProductsResponce> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \""+ BuildConfig.BASE_URL+"\": No address associated with hostname"))
                {
                    result.setMsg("Please Check Enternet Connection");
                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                resultMutableLiveData.setValue(result);
            }
        });
        return resultMutableLiveData;

    }

    public static MutableLiveData<ProductDetailsDescResponse> getProductDetails(Context c ,String clientID,int venderID,int shopID,int productID,String customerID){


        Products shope = RetrofitClientInstance.getRetrofitInstance().create(Products.class);
        final MutableLiveData<ProductDetailsDescResponse> resultMutableLiveData = new MutableLiveData<>();
        final ProductDetailsDescResponse result = new ProductDetailsDescResponse();
        Call<ProductDetailsDescResponse> call = shope.getVendorProductDetails(clientID,venderID,shopID,productID,customerID,MyProfile.getInstance(c).getRoleID());
        call.enqueue(new Callback<ProductDetailsDescResponse>() {
            @Override
            public void onResponse(Call<ProductDetailsDescResponse> call, Response<ProductDetailsDescResponse> response) {
                ProductDetailsDescResponse data = response.body();
                resultMutableLiveData.setValue(data);
            }
            @Override
            public void onFailure(Call<ProductDetailsDescResponse> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \""+ BuildConfig.BASE_URL+"\": No address associated with hostname"))
                {
                    result.setMsg("Please Check Enternet Connection");
                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                resultMutableLiveData.setValue(result);
            }
        });
        return resultMutableLiveData;
    }


    public static MutableLiveData<BaseResponse> deleteProductFromWishList(String wishList) {

        Products productsService = RetrofitClientInstance.getRetrofitInstance().create(Products.class);
        final BaseResponse result = new BaseResponse();
        final MutableLiveData<BaseResponse> resultMutableLiveData = new MutableLiveData<>();

        productsService.deleteProductFromWishList("2",wishList).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse data = response.body();
                resultMutableLiveData.setValue(data);

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \""+ BuildConfig.BASE_URL+"\": No address associated with hostname"))
                {
                    result.setMsg("Please Check Enternet Connection");
                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                resultMutableLiveData.setValue(result);

            }
        });
        return resultMutableLiveData;

    }

    public static MutableLiveData<AddToCartResponseDetails> addToCart(Context c,int venderID,String customerID,int productID,int productQty,String event){

        Products productsService = RetrofitClientInstance.getRetrofitInstance().create(Products.class);
        final AddToCartResponseDetails result = new AddToCartResponseDetails();
        final MutableLiveData<AddToCartResponseDetails> resultMutableLiveData = new MutableLiveData<>();
        productsService.addToCart("2",venderID,customerID,productID,productQty,event,MyProfile.getInstance(c).getRoleID()).enqueue(new Callback<AddToCartResponseDetails>() {
            @Override
            public void onResponse(Call<AddToCartResponseDetails> call, Response<AddToCartResponseDetails> response) {
                AddToCartResponseDetails data = response.body();
                resultMutableLiveData.setValue(data);
            }

            @Override
            public void onFailure(Call<AddToCartResponseDetails> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \""+ BuildConfig.BASE_URL+"\": No address associated with hostname"))
                {
                    result.setMsg("Please Check Enternet Connection");
                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                resultMutableLiveData.setValue(result);
            }
        });

        return resultMutableLiveData;

    }


    public static MutableLiveData<AddProductToWishListResponse> addProductToWishList(Context c,int venderID,int productID){

        Products customerProductsService = RetrofitClientInstance.getRetrofitInstance().create(Products.class);
        final AddProductToWishListResponse result = new AddProductToWishListResponse();
        final MutableLiveData<AddProductToWishListResponse> resultMutableLiveData = new MutableLiveData<>();

        Call<AddProductToWishListResponse> call = customerProductsService.moveToWishList("2", venderID,
                MyProfile.getInstance(c).getUserID(), productID,MyProfile.getInstance(c).getRoleID());

        call.enqueue(new Callback<AddProductToWishListResponse>() {
            @Override
            public void onResponse(Call<AddProductToWishListResponse> call, Response<AddProductToWishListResponse> response) {
                AddProductToWishListResponse data = response.body();
                resultMutableLiveData.setValue(data);
            }

            @Override
            public void onFailure(Call<AddProductToWishListResponse> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \""+ BuildConfig.BASE_URL+"\": No address associated with hostname"))
                {
                    result.setMsg("Please Check Enternet Connection");
                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                resultMutableLiveData.setValue(result);
            }
        });
        return resultMutableLiveData;

    }

}
