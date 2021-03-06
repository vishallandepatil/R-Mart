package com.rmart.customer.shops.products.viewmodel;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.rmart.R;
import com.rmart.customer.models.AddToCartResponseDetails;
import com.rmart.customer.models.CustomerProductsDetailsUnitModel;
import com.rmart.customer.shops.home.model.ProductData;
import com.rmart.customer.shops.list.models.ShopDetailsModel;
import com.rmart.customer.shops.products.model.AddProductToWishListResponse;
import com.rmart.customer.shops.products.model.ProductDetailsDescResponse;
import com.rmart.customer.shops.products.repositories.ProductsRepository;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.BaseResponse;
import com.rmart.utilits.UpdateCartCountDetails;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class ProductDetailsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> iserror = new MutableLiveData<>();
//    public MutableLiveData<Integer> noOfQuantity = new MutableLiveData<>();
    public MutableLiveData<ShopDetailsModel> vendorShopDetails = new MutableLiveData<>();
    public MutableLiveData<ProductData> vendorProductDataDetails = new MutableLiveData<>();
    public MutableLiveData<CustomerProductsDetailsUnitModel> customerProductsDetailsUnitModelMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ProductDetailsDescResponse> productDetailsDescResponseMutableLiveData = new MutableLiveData<>();

    public  void getProductDetails(Context context) {
        isLoading.setValue(true);
        ProductsRepository.getProductDetails(context,"2", vendorShopDetails.getValue().getVendorId(), vendorShopDetails.getValue().getShopId(),
                vendorProductDataDetails.getValue().getProductId(), MyProfile.getInstance(context).getUserID()).observeForever(new Observer<ProductDetailsDescResponse>() {
            @Override
            public void onChanged(ProductDetailsDescResponse productDetailsDescResponse) {
                if(productDetailsDescResponse!=null) {
                    if(productDetailsDescResponse.getStatus().equalsIgnoreCase("success")){
                        iserror.postValue(false);

                    } else {
                        iserror.postValue(true);

                    }
                    productDetailsDescResponseMutableLiveData.setValue(productDetailsDescResponse);
                } else {
                    iserror.postValue(true);
                }
                isLoading.postValue(false);
            }
        });



    }

    public void onClick(final View view) {

            switch (view.getId())
            {
                case R.id.iv_favourite_image_field:
                case R.id.btn_wish_list_field:

                     toggleWishList(view);
                     break;
                case R.id.btn_add_to_cart_field:

                    addToCart(view,1);
                    break;




            }


    }


   public void  addToCart(View view,int type){
        ProductsRepository.addToCart(view.getContext(),vendorShopDetails.getValue().getVendorId(),MyProfile.getInstance(view.getContext()).getUserID(),customerProductsDetailsUnitModelMutableLiveData.getValue().getProductUnitId(),customerProductsDetailsUnitModelMutableLiveData.getValue().getTotalProductCartQty()+1,"").observeForever(new Observer<AddToCartResponseDetails>() {
            @Override
            public void onChanged(AddToCartResponseDetails addToCartResponseDetails) {
                if (addToCartResponseDetails.getStatus().equalsIgnoreCase("success")) {
                    AddToCartResponseDetails.AddToCartDataResponse addToCartDataResponse = addToCartResponseDetails.getAddToCartDataResponse();
                    if (addToCartDataResponse != null) {
                        Integer totalCartCount = addToCartDataResponse.getTotalCartCount();
                        UpdateCartCountDetails.updateCartCountDetails.onNext(totalCartCount);
                        CustomerProductsDetailsUnitModel a =customerProductsDetailsUnitModelMutableLiveData.getValue();
                        if(type==1) {
                            a.setTotalProductCartQty(customerProductsDetailsUnitModelMutableLiveData.getValue().getTotalProductCartQty() + 1);
                        } else {
                            a.setTotalProductCartQty(customerProductsDetailsUnitModelMutableLiveData.getValue().getTotalProductCartQty() - 1);

                        }
                        customerProductsDetailsUnitModelMutableLiveData.postValue(a);

                    } else {
                    }
                    Toast.makeText(view.getContext(), addToCartResponseDetails.getMsg(), Toast.LENGTH_LONG).show();


                }else {

                }
            }
        });
    }


    private void toggleWishList(View view) {
        if (productDetailsDescResponseMutableLiveData.getValue().getProductDetailsDescProductDataModel().getProductDetailsDescModel().getWishList()){

            ProductsRepository.deleteProductFromWishList(productDetailsDescResponseMutableLiveData.getValue().getProductDetailsDescProductDataModel().getProductDetailsDescModel().getWishListId()).observeForever(new Observer<BaseResponse>() {
                @Override
                public void onChanged(BaseResponse baseResponse) {
                    if (baseResponse.getStatus().equalsIgnoreCase("success")) {
                        ProductDetailsDescResponse a =productDetailsDescResponseMutableLiveData.getValue();
                        a.getProductDetailsDescProductDataModel().getProductDetailsDescModel().setWishList(false);
                        productDetailsDescResponseMutableLiveData.setValue(a);
                    } else {

                    }
                    Toast.makeText(view.getContext(),baseResponse.getMsg(),Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            if(vendorShopDetails.getValue()!=null) {
                ProductsRepository.addProductToWishList(view.getContext(),vendorShopDetails.getValue().getVendorId(), productDetailsDescResponseMutableLiveData.getValue().getProductDetailsDescProductDataModel().getProductDetailsDescModel().getProductId()).observeForever(new Observer<AddProductToWishListResponse>() {
                    @Override
                    public void onChanged(AddProductToWishListResponse addProductToWishListResponse) {

                        if (addProductToWishListResponse.getStatus().equalsIgnoreCase("success")) {

                            ProductDetailsDescResponse a =productDetailsDescResponseMutableLiveData.getValue();
                            a.getProductDetailsDescProductDataModel().getProductDetailsDescModel().setWishList(true);
                            a.getProductDetailsDescProductDataModel().getProductDetailsDescModel().setWishListId(addProductToWishListResponse.getAddProductToWishListDataResponse().getProductWishListId());
                            productDetailsDescResponseMutableLiveData.setValue(a);
                        } else {

                        }
                        Toast.makeText(view.getContext(), addProductToWishListResponse.getMsg(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }


}