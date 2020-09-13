package com.rmart.utilits.services;

import com.rmart.BuildConfig;
import com.rmart.utilits.pojos.orders.OrderProductListResponse;
import com.rmart.utilits.pojos.orders.OrderStateListResponse;
import com.rmart.utilits.pojos.orders.OrdersByStatus;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OrderService {
    @POST(BuildConfig.VENDOR_ORDER_HOME)
    @FormUrlEncoded
    Call<OrderStateListResponse> getOrderHome(@Field("user_id") String userID);

    @POST(BuildConfig.VENDOR_ORDER_BY_STATUS)
    @FormUrlEncoded
    Call<OrdersByStatus> getStateOfOrder(@Field("start_index") String startIndex,
                                         @Field("mobile") String mobile,
                                         @Field("status") String status);

    @POST(BuildConfig.VENDOR_ORDER_PRODUCTS)
    @FormUrlEncoded
    Call<OrderProductListResponse> getOrderProductList(@Field("start_index") String startIndex,
                                                       @Field("user_id") String userID,
                                                       @Field("order_id") String orderID);

    @POST(BuildConfig.VENDOR_UPDATE_ORDER)
    @FormUrlEncoded
    Call<UpdatedOrderStatus> updateOrderStatus(@Field("order_id")String orderID,
            @Field("user_id") String id,
            @Field("status") String newOrderStatus);
}
