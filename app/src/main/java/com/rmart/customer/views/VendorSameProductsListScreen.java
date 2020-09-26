package com.rmart.customer.views;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rmart.R;
import com.rmart.baseclass.views.BaseFragment;
import com.rmart.customer.OnCustomerHomeInteractionListener;
import com.rmart.customer.adapters.VendorProductsListAdapter;
import com.rmart.customer.models.CustomerProductDetailsModel;
import com.rmart.customer.models.CustomerProductsShopDetailsModel;
import com.rmart.customer.models.ProductBaseModel;
import com.rmart.customer.models.VendorProductDetailsResponse;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.EqualSpacingItemDecoration;
import com.rmart.utilits.GridSpacesItemDecoration;
import com.rmart.utilits.LoggerInfo;
import com.rmart.utilits.RecyclerTouchListener;
import com.rmart.utilits.RetrofitClientInstance;
import com.rmart.utilits.Utils;
import com.rmart.utilits.services.CustomerProductsService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorSameProductsListScreen extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ProductBaseModel productCategoryDetails;
    private CustomerProductsShopDetailsModel vendorShopDetails;
    private int currentPage = 0;
    private String searchProductName = "";
    private List<CustomerProductDetailsModel> productsList;
    private VendorProductsListAdapter vendorProductsListAdapter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int PAGE_SIZE = 20;
    private OnCustomerHomeInteractionListener onCustomerHomeInteractionListener;

    public VendorSameProductsListScreen() {
        // Required empty public constructor
    }

    public static VendorSameProductsListScreen getInstance(ProductBaseModel productCategoryDetails, CustomerProductsShopDetailsModel vendorShopDetails) {
        VendorSameProductsListScreen fragment = new VendorSameProductsListScreen();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, productCategoryDetails);
        args.putSerializable(ARG_PARAM2, vendorShopDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productCategoryDetails = (ProductBaseModel) getArguments().getSerializable(ARG_PARAM1);
            vendorShopDetails = (CustomerProductsShopDetailsModel) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LoggerInfo.printLog("Fragment", "VendorSameProductsListScreen");
        return inflater.inflate(R.layout.fragment_vendor_same_products_list_screen, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnCustomerHomeInteractionListener) {
            onCustomerHomeInteractionListener = (OnCustomerHomeInteractionListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToolBar();
    }

    public void updateToolBar() {
        requireActivity().setTitle(vendorShopDetails.getShopName());
        ((CustomerHomeActivity)(requireActivity())).showCartIcon();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUIComponents(view);

        getVendorDetails();
    }

    private void loadUIComponents(View view) {

        RecyclerView productsListField = view.findViewById(R.id.products_list_field);
        productsListField.setHasFixedSize(false);

        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false);
        productsListField.setLayoutManager(layoutManager);

        productsListField.addItemDecoration(new GridSpacesItemDecoration(20));

        productsListField.setItemAnimator(new DefaultItemAnimator());
        productsListField.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        loadMoreItems();
                    }
                }
            }
        });

        productsListField.addOnItemTouchListener(new RecyclerTouchListener(requireActivity(), "", productsListField, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomerProductDetailsModel selectedProductDetails = productsList.get(position);
                onCustomerHomeInteractionListener.gotoProductDescDetails(selectedProductDetails, vendorShopDetails);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        productsList = new ArrayList<>();
        vendorProductsListAdapter = new VendorProductsListAdapter(requireActivity(), productsList);
        productsListField.setAdapter(vendorProductsListAdapter);
    }

    private void loadMoreItems() {
        isLoading = true;
        currentPage += 1;
        getVendorDetails();
    }

    private void getVendorDetails() {
        if (Utils.isNetworkConnected(requireActivity())) {
            progressDialog.show();
            CustomerProductsService customerProductsService = RetrofitClientInstance.getRetrofitInstance().create(CustomerProductsService.class);
            String clientID = "2";
            Call<VendorProductDetailsResponse> call;
            String customerId = MyProfile.getInstance().getUserID();
            if (TextUtils.isEmpty(searchProductName) && currentPage != 0) {
                call = customerProductsService.getVendorShopDetails(clientID, vendorShopDetails.getVendorId(), vendorShopDetails.getShopId(),
                        productCategoryDetails.getProductCategoryId(), currentPage, customerId);
            } else {
                call = customerProductsService.getVendorShopDetails(clientID, vendorShopDetails.getVendorId(), vendorShopDetails.getShopId(),
                        productCategoryDetails.getProductCategoryId(), currentPage, searchProductName, customerId);
            }
            call.enqueue(new Callback<VendorProductDetailsResponse>() {
                @Override
                public void onResponse(@NotNull Call<VendorProductDetailsResponse> call, @NotNull Response<VendorProductDetailsResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        VendorProductDetailsResponse body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                List<CustomerProductDetailsModel> productDataList = body.getVendorProductDataResponse().getProductsListData();
                                if (productDataList != null && !productDataList.isEmpty()) {
                                    productsList.addAll(productDataList);
                                }
                                updateAdapter(body.getMsg());
                            } else {
                                showCloseDialog(getString(R.string.message), body.getMsg());
                            }
                        } else {
                            showCloseDialog(getString(R.string.message), getString(R.string.no_information_available));
                        }
                    } else {
                        showCloseDialog(getString(R.string.message), getString(R.string.no_information_available));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<VendorProductDetailsResponse> call, @NotNull Throwable t) {
                    progressDialog.dismiss();
                    showCloseDialog(getString(R.string.message), t.getMessage());
                }
            });
        } else {
            showCloseDialog(getString(R.string.error_internet), getString(R.string.error_internet_text));
        }
    }

    private void showCloseDialog(String title, String message) {
        showDialog(title, message, pObject -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void updateAdapter(String message) {
        if(!productsList.isEmpty()) {
            vendorProductsListAdapter.updateItems(productsList);
            vendorProductsListAdapter.notifyDataSetChanged();
        } else {
            showCloseDialog(getString(R.string.message), message);
        }
    }
}