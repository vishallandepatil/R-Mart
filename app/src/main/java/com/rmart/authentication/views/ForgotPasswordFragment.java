package com.rmart.authentication.views;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.textfield.TextInputLayout;
import com.rmart.BuildConfig;
import com.rmart.R;
import com.rmart.authentication.OnAuthenticationClickedListener;
import com.rmart.utilits.RetrofitClientInstance;
import com.rmart.utilits.Utils;
import com.rmart.utilits.pojos.ForgotPasswordResponse;
import com.rmart.utilits.services.AuthenticationService;

import org.jetbrains.annotations.NotNull;

import java.net.SocketTimeoutException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends LoginBaseFragment {

    private OnAuthenticationClickedListener mListener;
    private EditText etMobileNumber;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnAuthenticationClickedListener)context ;
    }

    public static ForgotPasswordFragment getInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        try{
           ImageView slice = view.findViewById(R.id.slice);
            ImageView  app_logo = view.findViewById(R.id.app_logo);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            int sliceheight=(height*30)/100;
            int app_logoheight=(height*15)/100;
            slice.getLayoutParams().height = sliceheight;
            app_logo.getLayoutParams().height = app_logoheight;
            app_logo.getLayoutParams().width = app_logoheight;
            //slice.setMaxHeight(sliceheight);
            slice.requestLayout();
            app_logo.requestLayout();



        } catch (Exception e){



        }

        etMobileNumber = view.findViewById(R.id.mobile_number);
        view.findViewById(R.id.forgot_password).setOnClickListener(v -> forgotPasswordSelected());
    }

    private void forgotPasswordSelected() {
        String mobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString().trim();
        if (TextUtils.isEmpty(mobileNumber) || !Utils.isValidMobile(mobileNumber)) {
            showDialog("", getString(R.string.error_mobile));
            return;
        }
        if(!Utils.isNetworkConnected(requireActivity())) {
            showDialog(getString(R.string.error_internet), getString(R.string.error_internet_text));
            return;
        }
        progressDialog.show();
        AuthenticationService authenticationService = RetrofitClientInstance.getRetrofitInstance().create(AuthenticationService.class);
        authenticationService.forgotPassword(mobileNumber, BuildConfig.ROLE_ID).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(@NotNull Call<ForgotPasswordResponse> call, @NotNull Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful()) {
                    ForgotPasswordResponse data = response.body();
                    if (data != null) {
                        if (data.getStatus().equalsIgnoreCase("Success")) {
                            showDialog("", String.format("%s otp: %s", data.getMsg(), data.getOtp()), (dialog, i) -> mListener.changePassword("otp", mobileNumber));
                        } else {
                            if (data.getMsg().contains("role id")) {
                                showDialog("", getString(R.string.error_role_login));
                            } else {
                                showDialog(data.getMsg());
                            }
                        }
                    } else {
                        showDialog(getString(R.string.no_information_available));
                    }
                } else {
                    showDialog(response.message());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<ForgotPasswordResponse> call, @NotNull Throwable t) {
                if(t instanceof SocketTimeoutException){
                    showDialog("", getString(R.string.network_slow));
                } else {
                    showDialog("", t.getMessage());
                }
                progressDialog.dismiss();
            }
        });
    }
}