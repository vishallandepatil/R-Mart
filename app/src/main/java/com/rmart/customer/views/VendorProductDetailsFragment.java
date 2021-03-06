package com.rmart.customer.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.rmart.R;
import com.rmart.RMartApplication;
import com.rmart.baseclass.CallBackInterface;
import com.rmart.baseclass.Constants;
import com.rmart.baseclass.views.BaseFragment;
import com.rmart.customer.OnCustomerHomeInteractionListener;
import com.rmart.customer.adapters.VendorProductDetailsAdapter;
import com.rmart.customer.models.AddShopToWishListResponse;
import com.rmart.customer.models.ContentModel;
import com.rmart.customer.models.CustomerProductDetailsModel;
import com.rmart.customer.shops.list.models.ShopDetailsModel;
import com.rmart.customer.models.ProductBaseModel;
import com.rmart.customer.models.VendorProductDetailsResponse;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.CommonUtils;
import com.rmart.utilits.LoggerInfo;
import com.rmart.utilits.RetrofitClientInstance;
import com.rmart.utilits.Utils;
import com.rmart.utilits.custom_views.CustomNetworkImageView;
import com.rmart.utilits.BaseResponse;
import com.rmart.utilits.services.CustomerProductsService;

import org.jetbrains.annotations.NotNull;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorProductDetailsFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    public VendorProductDetailsFragment() {
    }
    private AppCompatEditText etProductsSearchField;
    private int currentPage = 0;
    private String searchProductName = "";
    private ShopDetailsModel productsShopDetailsModel;
    private TextView tvShopNameField;
    private TextView tvPhoneNoField;
    private TextView tvViewAddressField;
    private CustomNetworkImageView ivShopImageField;
    private VendorProductDetailsAdapter vendorProductDetailsAdapter;
    private List<ProductBaseModel> vendorProductsList;
    private OnCustomerHomeInteractionListener onCustomerHomeInteractionListener;
    private ImageView ivFavouriteImageField;
    private boolean isWishListShop = false;
    private LinearLayout progressLayoutField;
    private ImageView ivSearchField;
    public static VendorProductDetailsFragment getInstance(ShopDetailsModel productsShopDetailsModel) {
        VendorProductDetailsFragment fragment = new VendorProductDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, productsShopDetailsModel);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productsShopDetailsModel = (ShopDetailsModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LoggerInfo.printLog("Fragment", "VendorProductDetailsFragment");
        return inflater.inflate(R.layout.fragment_vendor_product_details, container, false);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnCustomerHomeInteractionListener) {
            onCustomerHomeInteractionListener = (OnCustomerHomeInteractionListener) context;
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUIComponents(view);
        CommonUtils.dismissAllDialogs(requireActivity().getSupportFragmentManager());
    }
    @Override
    public void onResume() {
        super.onResume();
        updateToolBar();
    }
    public void updateToolBar() {
        requireActivity().setTitle(productsShopDetailsModel.getShopName());
        ((CustomerHomeActivity) (requireActivity())).showCartIcon();
    }
    private void loadUIComponents(View view) {
        RecyclerView productsListField = view.findViewById(R.id.products_list_field);
        productsListField.setHasFixedSize(false);

        etProductsSearchField = view.findViewById(R.id.edt_product_search_field);
        ivSearchField = view.findViewById(R.id.iv_search_field);

        ivSearchField.setOnClickListener(v -> {
            etProductsSearchField.setText("");
            searchProductName = "";
            currentPage = 0;
            CommonUtils.closeVirtualKeyboard(requireActivity(), ivSearchField);
        });
        etProductsSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        progressLayoutField = view.findViewById(R.id.progress_layout_field);
        ivShopImageField = view.findViewById(R.id.iv_shop_image);
        tvShopNameField = view.findViewById(R.id.tv_shop_name_field);
        tvPhoneNoField = view.findViewById(R.id.tv_phone_no_field);
        tvViewAddressField = view.findViewById(R.id.tv_view_address_field);

        ivFavouriteImageField = view.findViewById(R.id.iv_favourite_image_field);

        vendorProductsList = new ArrayList<>();
        vendorProductDetailsAdapter = new VendorProductDetailsAdapter(requireActivity(), vendorProductsList, callBackListener);
        productsListField.setAdapter(vendorProductDetailsAdapter);

        isWishListShop = productsShopDetailsModel.getShopWishListStatus() == 1;
        ivFavouriteImageField.setImageResource(isWishListShop ? R.drawable.heart_active : R.drawable.heart_black);

        ivFavouriteImageField.setOnClickListener(v -> {
            if (isWishListShop) deleteShopFromWishList();
            else addShopFromWishList();
        });
        view.findViewById(R.id.iv_call_field).setOnClickListener(v -> callSelected());

        view.findViewById(R.id.iv_message_field).setOnClickListener(v -> messageSelected());
        etProductsSearchField.setText("");
    }
    private void callSelected() {
        Utils.openDialPad(requireActivity(), productsShopDetailsModel.getShopMobileNo());
    }
    private void messageSelected() {
        Utils.openGmailWindow(requireActivity(), productsShopDetailsModel.getEmailId());
    }
    private void resetVendorProductDetails() {
        vendorProductsList.clear();
        vendorProductDetailsAdapter.updateItems(vendorProductsList);
        vendorProductDetailsAdapter.notifyDataSetChanged();
    }
    private final CallBackInterface callBackListener = pObject -> {
        if (Utils.isNetworkConnected(requireActivity())) {
            if (pObject instanceof CustomerProductDetailsModel) {
                onCustomerHomeInteractionListener.gotoProductDescDetails((CustomerProductDetailsModel) pObject, productsShopDetailsModel);
                etProductsSearchField.setText("");
                searchProductName = "";
            } else if (pObject instanceof ContentModel) {
                ContentModel contentModel = (ContentModel) pObject;
                String status = contentModel.getStatus();
                if (status.equalsIgnoreCase(Constants.TAG_VIEW_ALL)) {
                    ProductBaseModel selectedProductCategoryDetails = (ProductBaseModel) contentModel.getValue();
                    currentPage = 0;
                    //productCategoryId = selectedProductCategoryDetails.getProductCategoryId();
                    onCustomerHomeInteractionListener.gotoVendorSameProductListScreen(selectedProductCategoryDetails, productsShopDetailsModel);
                    etProductsSearchField.setText("");
                    searchProductName = "";
                }
            }
        } else {
            showDialog(getString(R.string.error_internet), getString(R.string.error_internet_text));
        }
    };
    private void getVendorProductDetails() {
        if (Utils.isNetworkConnected(requireActivity())) {
            //productCategoryId = -1;
            vendorProductsList.clear();
            progressDialog.show();
            CustomerProductsService customerProductsService = RetrofitClientInstance.getRetrofitInstance().create(CustomerProductsService.class);
            String clientID = "2";
            String customerId = MyProfile.getInstance(getContext()).getUserID();
            Call<VendorProductDetailsResponse> call;
            if (TextUtils.isEmpty(searchProductName)) {
                call = customerProductsService.getVendorShopDetails(clientID, productsShopDetailsModel.getVendorId(),
                        productsShopDetailsModel.getShopId(), customerId);
            } else {
                call = customerProductsService.getVendorShopDetails(clientID, productsShopDetailsModel.getVendorId(), productsShopDetailsModel.getShopId(),
                        currentPage, searchProductName, customerId);
            }
            call.enqueue(new Callback<VendorProductDetailsResponse>() {
                @Override
                public void onResponse(@NotNull Call<VendorProductDetailsResponse> call, @NotNull Response<VendorProductDetailsResponse> response) {
                    progressDialog.dismiss();
                    resetVendorProductDetails();
                    if (response.isSuccessful()) {
                        VendorProductDetailsResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                List<CustomerProductDetailsModel> productDataList = body.getVendorProductDataResponse().getProductsListData();
                                productsShopDetailsModel = body.getVendorProductDataResponse().getVendorShopDetails();
                                updateShopDetailsUI();
                                updateAdapter(productDataList);
                            } else {
                                showDialog(body.getMsg());
                            }
                        } else {
                            showDialog(getString(R.string.no_information_available));
                        }
                    }  else {
                        progressLayoutField.setVisibility(View.GONE);
                        showDialog("", response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<VendorProductDetailsResponse> call, @NotNull Throwable t) {
                    if(t instanceof SocketTimeoutException){
                        showCloseDialog("", getString(R.string.network_slow));
                    } else {
                        showCloseDialog("", t.getMessage());
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            showDialog(getString(R.string.error_internet), getString(R.string.error_internet_text));
        }
    }
    private void showCloseDialog(String title, String message) {
        progressLayoutField.setVisibility(View.GONE);
        Activity activity = getActivity();
        if(activity != null) {
            if(!activity.isFinishing()) {
                showDialog(title, message, pObject -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
            }
        }
    }
    private void updateAdapter(List<CustomerProductDetailsModel> productDataList) {
        if(productDataList != null && !productDataList.isEmpty()) {
            LinkedHashMap<ProductBaseModel, List<CustomerProductDetailsModel>> linkedMapDetails = groupDataIntoHashMap(productDataList);
            for (Map.Entry<ProductBaseModel, List<CustomerProductDetailsModel>> entry : linkedMapDetails.entrySet()) {
                ProductBaseModel productBaseModel = entry.getKey();
                List<CustomerProductDetailsModel> listData = entry.getValue();
                if(listData != null && !listData.isEmpty()) {
                    productBaseModel.setProductsList(listData);
                    vendorProductsList.add(productBaseModel);
                }
            }
        }
        vendorProductDetailsAdapter.updateItems(vendorProductsList);
        vendorProductDetailsAdapter.notifyDataSetChanged();
    }
    private LinkedHashMap<ProductBaseModel, List<CustomerProductDetailsModel>> groupDataIntoHashMap(List<CustomerProductDetailsModel> productDataList) {
        LinkedHashMap<ProductBaseModel, List<CustomerProductDetailsModel>> groupedHashMap = new LinkedHashMap<>();
        for(CustomerProductDetailsModel element : productDataList) {
            ProductBaseModel productBaseModel = new ProductBaseModel();
            productBaseModel.setProductCategoryName(element.getParentCategoryName());
            productBaseModel.setProductCategoryId(element.getParentCategoryId());
            if(groupedHashMap.containsKey(productBaseModel)) {
                List<CustomerProductDetailsModel> listData = groupedHashMap.get(productBaseModel);
                if(listData != null) {
                    listData.add(element);
                }
            } else {
                List<CustomerProductDetailsModel> listData = new ArrayList<>();
                listData.add(element);
                groupedHashMap.put(productBaseModel, listData);
            }
        }
        return groupedHashMap;
    }
    private void updateShopImageUI(String shopImageUrl) {
        if (!TextUtils.isEmpty(shopImageUrl)) {
            progressLayoutField.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = RMartApplication.getInstance().getImageLoader();
            imageLoader.get(shopImageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        ivShopImageField.setLocalImageBitmap(bitmap);
                    }
                    progressLayoutField.setVisibility(View.GONE);
                    ivShopImageField.setVisibility(View.VISIBLE);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressLayoutField.setVisibility(View.GONE);
                    ivShopImageField.setVisibility(View.VISIBLE);
                    ivShopImageField.setBackgroundResource(R.mipmap.ic_launcher);
                    tvShopNameField.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    tvViewAddressField.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                }
            });
        } else {
            progressLayoutField.setVisibility(View.GONE);
            ivShopImageField.setVisibility(View.VISIBLE);
            ivShopImageField.setBackgroundResource(R.mipmap.ic_launcher);
            tvShopNameField.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));
            tvViewAddressField.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));
        }
    }
    private void updateShopDetailsUI() {
        requireActivity().setTitle(productsShopDetailsModel.getShopName());
        Object lShopImageObject = productsShopDetailsModel.getShopImage();
        if (lShopImageObject instanceof ArrayList) {
            List<String> shopImagesList = (List<String>) lShopImageObject;
            if (!shopImagesList.isEmpty()) {
                String shopImageUrl = shopImagesList.get(0);
                updateShopImageUI(shopImageUrl);
            }
        } else if (lShopImageObject instanceof String) {
            updateShopImageUI((String) lShopImageObject);
        }
        tvShopNameField.setText(productsShopDetailsModel.getShopName());
        tvViewAddressField.setText(productsShopDetailsModel.getShopAddress());
        tvPhoneNoField.setText(productsShopDetailsModel.getShopMobileNo());
    }
    private void performSearch() {
        searchProductName = Objects.requireNonNull(etProductsSearchField.getText()).toString().trim();
        if (searchProductName.length() == 0) {
            ivSearchField.setImageResource(R.drawable.search);
            if(vendorProductsList.isEmpty()) {
                getVendorProductDetails();
            } else {
                vendorProductDetailsAdapter.updateItems(vendorProductsList);
                vendorProductDetailsAdapter.notifyDataSetChanged();
            }
        } else if (searchProductName.length() == 3) {
            ivSearchField.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            currentPage = 0;
            getVendorProductDetails();
        } else {
            ivSearchField.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            vendorProductDetailsAdapter.getFilter().filter(searchProductName);
        }
    }
    private void deleteShopFromWishList() {
        if (Utils.isNetworkConnected(requireActivity())) {
            progressDialog.show();
            CustomerProductsService customerProductsService = RetrofitClientInstance.getRetrofitInstance().create(CustomerProductsService.class);
            String clientID = "2";
            Call<BaseResponse> call = customerProductsService.deleteShopFromWishList(clientID, productsShopDetailsModel.getVendorId(), productsShopDetailsModel.getShopId(),
                    MyProfile.getInstance(getContext()).getUserID(),MyProfile.getInstance(getContext()).getRoleID());
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(@NotNull Call<BaseResponse> call, @NotNull Response<BaseResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        BaseResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                showDialog(body.getMsg(), pObject -> {
                                    isWishListShop = !isWishListShop;
                                    ivFavouriteImageField.setImageResource(R.drawable.heart_black);
                                    productsShopDetailsModel.setShopWishListId(-1);
                                    productsShopDetailsModel.setShopWishListStatus(0);
                                    onCustomerHomeInteractionListener.updateShopWishListStatus(productsShopDetailsModel);
                                });
                            } else {
                                showDialog(body.getMsg());
                            }
                        } else {
                            showDialog(getString(R.string.no_information_available));
                        }
                    } else {
                        showDialog(getString(R.string.no_information_available));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<BaseResponse> call, @NotNull Throwable t) {
                    if(t instanceof SocketTimeoutException){
                        showDialog("", getString(R.string.network_slow));
                    } else {
                        showDialog("", t.getMessage());
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            showDialog(getString(R.string.error_internet), getString(R.string.error_internet_text));
        }
    }
    private void addShopFromWishList() {
        if (Utils.isNetworkConnected(requireActivity())) {
            progressDialog.show();
            CustomerProductsService customerProductsService = RetrofitClientInstance.getRetrofitInstance().create(CustomerProductsService.class);
            String clientID = "2";
            Call<AddShopToWishListResponse> call = customerProductsService.addShopToWishList(clientID, productsShopDetailsModel.getVendorId(),
                    productsShopDetailsModel.getShopId(), MyProfile.getInstance(getContext()).getUserID(),MyProfile.getInstance(getContext()).getRoleID());
            call.enqueue(new Callback<AddShopToWishListResponse>() {
                @Override
                public void onResponse(@NotNull Call<AddShopToWishListResponse> call, @NotNull Response<AddShopToWishListResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        AddShopToWishListResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                showDialog(body.getMsg(), pObject -> {
                                    isWishListShop = !isWishListShop;
                                    ivFavouriteImageField.setImageResource(R.drawable.heart_active);
                                    int shopWishId = body.getShopToWishListDataResponse().getShopWishListId();
                                    productsShopDetailsModel.setShopWishListId(shopWishId);
                                    productsShopDetailsModel.setShopWishListStatus(1);
                                    onCustomerHomeInteractionListener.updateShopWishListStatus(productsShopDetailsModel);
                                });
                            } else {
                                showDialog(body.getMsg());
                            }
                        } else {
                            showDialog(getString(R.string.no_information_available));
                        }
                    } else {
                        showDialog(getString(R.string.no_information_available));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<AddShopToWishListResponse> call, @NotNull Throwable t) {
                    if(t instanceof SocketTimeoutException){
                        showDialog("", getString(R.string.network_slow));
                    } else {
                        showDialog("", t.getMessage());
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            showDialog(getString(R.string.error_internet), getString(R.string.error_internet_text));
        }
    }
}