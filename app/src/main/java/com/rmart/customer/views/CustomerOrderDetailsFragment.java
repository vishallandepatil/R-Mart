package com.rmart.customer.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.rmart.R;
import com.rmart.baseclass.views.BaseFragment;
import com.rmart.customer.OnCustomerHomeInteractionListener;
import com.rmart.customer.models.CustomerOrderPaymentInfoDetails;
import com.rmart.customer.models.CustomerOrderPersonalDetails;
import com.rmart.customer.models.CustomerOrderProductOrderedDetails;
import com.rmart.customer.models.CustomerOrderedResponseModel;
import com.rmart.customer.shops.list.models.ShopDetailsModel;
import com.rmart.customer_order.adapters.ProductListAdapter;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.LoggerInfo;
import com.rmart.utilits.RetrofitClientInstance;
import com.rmart.utilits.Utils;
import com.rmart.utilits.services.CustomerProductsService;

import org.jetbrains.annotations.NotNull;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Satya Seshu on 22/09/20.
 */
public class CustomerOrderDetailsFragment extends BaseFragment {
    public static final String DELIVERY ="Delivery";
    public static final String PICKUP ="Pickup";
    private ShopDetailsModel vendorShoppingCartDetails;
    private AppCompatButton btnProceedToBuyField;
    private OnCustomerHomeInteractionListener onCustomerHomeInteractionListener;
    private AppCompatTextView tvAmount, tvExpectedDateDelivery;
    private AppCompatTextView tvDeliveryCharges;
    private AppCompatTextView tvTotalCharges;
    private AppCompatTextView tvCustomerName;
    private AppCompatTextView tvCustomerNumber;
    private AppCompatTextView tvCustomerAddress;
    private RecyclerView productsListField;
    AppCompatTextView deliveryAddress,customerAddress;
    CheckBox checkBoxPickUPFromShop;

    public static CustomerOrderDetailsFragment getInstance(ShopDetailsModel vendorShopDetails) {
        CustomerOrderDetailsFragment fragment = new CustomerOrderDetailsFragment();
        Bundle extras = new Bundle();
        extras.putSerializable("VendorShopDetails", vendorShopDetails);
        fragment.setArguments(extras);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LoggerInfo.printLog("Fragment", "CustomerOrderDetailsFragment");
        return inflater.inflate(R.layout.customers_order_details_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            vendorShoppingCartDetails = (ShopDetailsModel) extras.getSerializable("VendorShopDetails");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CustomerHomeActivity) {
            onCustomerHomeInteractionListener = (OnCustomerHomeInteractionListener) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUIComponents(view);

        showCartOrderDetails(DELIVERY);
    }

    private void showCartOrderDetails(String menthod) {
        if (Utils.isNetworkConnected(requireActivity())) {
            progressDialog.show();
            MyProfile myProfile = MyProfile.getInstance(getActivity());
            CustomerProductsService customerProductsService = RetrofitClientInstance.getRetrofitInstance().create(CustomerProductsService.class);
            String clientID = "2";
            Call<CustomerOrderedResponseModel> call = customerProductsService.showCartOrderDetails(clientID, vendorShoppingCartDetails.getVendorId(), vendorShoppingCartDetails.getShopId(),
                    myProfile.getPrimaryAddressId(), myProfile.getUserID(),menthod);
            call.enqueue(new Callback<CustomerOrderedResponseModel>() {
                @Override
                public void onResponse(@NotNull Call<CustomerOrderedResponseModel> call, @NotNull Response<CustomerOrderedResponseModel> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        CustomerOrderedResponseModel body = response.body();
                        if (body != null) {
                            if (body.getStatus().equalsIgnoreCase("success")) {
                                updateUI(body);
                            } else {
                                showCloseDialog(body.getMsg());
                            }
                        } else {
                            showCloseDialog(getString(R.string.no_information_available));
                        }
                    } else {
                        showCloseDialog(getString(R.string.no_information_available));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<CustomerOrderedResponseModel> call, @NotNull Throwable t) {
                    if(t instanceof SocketTimeoutException){
                        showDialog("", getString(R.string.network_slow));
                    } else {
                        showDialog("", t.getMessage());
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            showDialog(getString(R.string.error_internet), getString(R.string.error_internet_text), pObject -> requireActivity().getSupportFragmentManager().popBackStack());
        }
    }

    private void showCloseDialog(String message) {
        showDialog(message, pObject -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void loadUIComponents(View view) {
        productsListField = view.findViewById(R.id.product_list_field);

        //Vendor Info
        AppCompatTextView tvVendorName = view.findViewById(R.id.vendor_name);
        AppCompatTextView tvVendorNumber = view.findViewById(R.id.vendor_number);
        AppCompatTextView tvVendorAddress = view.findViewById(R.id.vendor_address);
        deliveryAddress = view.findViewById(R.id.labelDelevery);
        customerAddress = view.findViewById(R.id.customer_address);
        checkBoxPickUPFromShop = view.findViewById(R.id.checkBox1);

        tvVendorName.setText(vendorShoppingCartDetails.getShopName());
        tvVendorNumber.setText(vendorShoppingCartDetails.getShopMobileNo());
        tvVendorAddress.setText(vendorShoppingCartDetails.getShopAddress());

        // Customer Info
        tvCustomerName = view.findViewById(R.id.customer_name);
        tvCustomerNumber = view.findViewById(R.id.customer_number);
        tvCustomerAddress = view.findViewById(R.id.customer_address);

        // delivery boy info
        view.findViewById(R.id.delivery_boy_info).setVisibility(View.GONE);

        //Payment info
        tvAmount = view.findViewById(R.id.amount);
        tvExpectedDateDelivery = view.findViewById(R.id.expected_date_of_delivery);
        tvDeliveryCharges = view.findViewById(R.id.delivery_charges);
        tvTotalCharges = view.findViewById(R.id.total_charges);

        DividerItemDecoration divider = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), R.drawable.recycler_decoration_divider_transparent)));
        productsListField.addItemDecoration(divider);

        btnProceedToBuyField = view.findViewById(R.id.btn_proceed_to_buy_field);
        btnProceedToBuyField.setOnClickListener(v -> proceedToBuySelected());



    }

    private void proceedToBuySelected() {
        onCustomerHomeInteractionListener.gotoPaymentOptionsScreen(vendorShoppingCartDetails,-1);
    }

    private void updateUI( CustomerOrderedResponseModel customerOrderedResponseModel ) {
         CustomerOrderedResponseModel.CustomerOrderedDataResponseModel customerOrderedDataResponse= customerOrderedResponseModel.getCustomerOrderedDataResponseModel();
        btnProceedToBuyField.setVisibility(View.VISIBLE);
        CustomerOrderPersonalDetails customerOrderPersonalDetails = customerOrderedDataResponse.getCustomerOrderPersonalDetails();
        if (customerOrderPersonalDetails != null) {
            tvCustomerName.setText(customerOrderPersonalDetails.getCustomerName());
            tvCustomerNumber.setText(customerOrderPersonalDetails.getMobileNumber());
            tvCustomerAddress.setText(customerOrderPersonalDetails.getCustomerAddress());
        }

        CustomerOrderPaymentInfoDetails customerOrderPaymentInfoDetails = customerOrderedDataResponse.getCustomerOrderPaymentInfoDetails();
        if (customerOrderPaymentInfoDetails != null) {
            tvAmount.setText(Utils.roundOffDoubleValue(customerOrderPaymentInfoDetails.getOrderAmount(), "0.00"));
            tvExpectedDateDelivery.setText(String.valueOf(customerOrderPaymentInfoDetails.getExpectedDateOfDelivery()));
            int deliveryCharges = customerOrderPaymentInfoDetails.getDeliveryCharges();
            tvDeliveryCharges.setText(Utils.roundOffDoubleValue((double) deliveryCharges, "0.00"));
            tvDeliveryCharges.setText(deliveryCharges);

            tvTotalCharges.setText(Utils.roundOffDoubleValue(customerOrderPaymentInfoDetails.getTotalAmount(), "0.00"));
        }

        List<CustomerOrderProductOrderedDetails> productsOrderedList = customerOrderedDataResponse.getCustomerOrderProductDetailsList();
        if (productsOrderedList != null && !productsOrderedList.isEmpty()) {
            List<Object> lUpdatedProductsList = new ArrayList<>(productsOrderedList);
            ProductListAdapter productListAdapter = new ProductListAdapter(requireActivity(), lUpdatedProductsList);
            productsListField.setAdapter(productListAdapter);
        }
        checkBoxPickUPFromShop.setOnCheckedChangeListener(null);
        vendorShoppingCartDetails.deliveryMethod=customerOrderedDataResponse.deliveryMethod;

        checkBoxPickUPFromShop.setChecked(customerOrderedDataResponse.deliveryMethod.equalsIgnoreCase(DELIVERY)?false:true);
        if(customerOrderedDataResponse.deliveryMethod.equalsIgnoreCase(DELIVERY)) {
            customerAddress.setVisibility(View.VISIBLE);
            deliveryAddress.setVisibility(View.VISIBLE);
        } else {

            customerAddress.setVisibility(View.GONE);
            deliveryAddress.setVisibility(View.GONE);
        }
        checkBoxPickUPFromShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                showCartOrderDetails(b?PICKUP:DELIVERY);
            }
        });


    }
}
