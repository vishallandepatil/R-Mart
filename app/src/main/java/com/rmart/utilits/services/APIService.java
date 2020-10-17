package com.rmart.utilits.services;

import com.rmart.BuildConfig;
import com.rmart.inventory.models.Product;
import com.rmart.utilits.pojos.APIBrandListResponse;
import com.rmart.utilits.pojos.APICategoryListResponse;
import com.rmart.utilits.pojos.APIProductListResponse;
import com.rmart.utilits.pojos.APIStockListResponse;
import com.rmart.utilits.pojos.APIUnitMeasureListResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {
    @GET(BuildConfig.API_UNITIES)
    Call<APIUnitMeasureListResponse> getAPIUnitMeasureList();

    @GET(BuildConfig.API_BRANDS)
    Call<APIBrandListResponse> getAPIBrandList();

    @GET(BuildConfig.API_STOCKE)
    Call<APIStockListResponse> getAPIStockList();

    @GET(BuildConfig.API_CATEGORIES)
    Call<APICategoryListResponse> getAPICategoryList();

    @POST(BuildConfig.API_PRODUCT_LIST)
    @FormUrlEncoded
    Call<APIProductListResponse> getProductList(@Field("start_index") String startIndex,
                                                @Field("end_index") String endIndex);

    @POST(BuildConfig.API_CATEGORY_LIST)
    @FormUrlEncoded
    Call<APIProductListResponse> getCategoryList(@Field("start_index") String startIndex,
                                                @Field("end_index") String endIndex);

    @POST(BuildConfig.API_CATEGORY_LIST)
    @FormUrlEncoded
    Call<APIProductListResponse> getSubCategoryList(@Field("start_index") String startIndex,
                                                    @Field("end_index") String endIndex,
                                                    @Field("sub_category_id") String categoryID);

    @POST(BuildConfig.API_CATEGORY_LIST)
    @FormUrlEncoded
    Call<APIProductListResponse> getBrandList(@Field("start_index") String startIndex,
                                                    @Field("end_index") String endIndex);

}
