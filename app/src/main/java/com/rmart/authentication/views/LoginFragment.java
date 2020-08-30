package com.rmart.authentication.views;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.rmart.R;
import com.rmart.baseclass.views.CustomEditTextWithErrorText;
import com.rmart.profile.model.MyProfile;
import com.rmart.utilits.RetrofitClientInstance;
import com.rmart.utilits.Utils;
import com.rmart.utilits.pojos.LoginResponse;
import com.rmart.utilits.pojos.ResendOTPResponse;
import com.rmart.utilits.services.AuthenticationService;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends LoginBaseFragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String mMobileNumber;

    private String mParam1;
    private String mParam2;
    private String mPassword;
    private String mDeviceKey;
    AppCompatEditText etPassword, etMobileNumber;
    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etMobileNumber = ((CustomEditTextWithErrorText)view.findViewById(R.id.mobile_number)).getAppCompatEditText();
        etPassword = ((CustomEditTextWithErrorText)view.findViewById(R.id.password)).getAppCompatEditText();
        view.findViewById(R.id.login).setOnClickListener(this);
        view.findViewById(R.id.register).setOnClickListener(this);
        view.findViewById(R.id.forgot_password).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login) {
            // mListener.validateMailOTP();
            mMobileNumber = Objects.requireNonNull(etMobileNumber.getText()).toString();
            mPassword = Objects.requireNonNull(etPassword.getText()).toString();
            if(mMobileNumber.length()<=0 || !Utils.isValidMobile(mMobileNumber)) {
                showDialog("", getString(R.string.error_mobile));
            } else if(mPassword.length()<=0) {
                showDialog("", getString(R.string.error_password));
            } else {
                checkCredentials();
            }
            /*
                MyProfile.getInstance();
                if (MyProfile.getInstance().getMyLocations() == null || MyProfile.getInstance().getMyLocations().size() <= 0) {
                    mListener.goToProfileActivity();
                } else {
                    mListener.goToHomeActivity();
                }
            */
        } else if (view.getId() == R.id.forgot_password) {
            mListener.goToForgotPassword();
        } else  {
            mListener.goToRegistration();
        }
    }

    private void checkCredentials() {
        progressDialog.show();
        AuthenticationService authenticationService = RetrofitClientInstance.getRetrofitInstance().create(AuthenticationService.class);
        // mPassword = "12345";
        mDeviceKey = "deviceKey";
        authenticationService.login(mDeviceKey, mMobileNumber, mPassword).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse data = response.body();
                    if (data != null) {
                        if (data.getStatus().equalsIgnoreCase("success")) {
                            if(data.getLoginData().getRoleID().equalsIgnoreCase(getString(R.string.role_id))) {
                                try {
                                    MyProfile.getInstance(data.getLoginData());
                                    if (MyProfile.getInstance().getMyLocations() == null || MyProfile.getInstance().getMyLocations().size() <= 0) {
                                        mListener.goToProfileActivity();
                                    } else {
                                        mListener.goToHomeActivity();
                                    }
                                    Objects.requireNonNull(getActivity()).onBackPressed();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showDialog("",getString(R.string.error_role_login));
                            }

                        } else {
                            showDialog("", data.getMsg(), (dialogInterface, i) -> {
                                if (data.getMsg().contains("verify")) {
                                    resendOTP();
                                } else if (data.getMsg().contains("mail_verify")) {
                                    mListener.validateMailOTP();
                                }
                            });
                        }
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                showDialog("", t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void resendOTP() {
        AuthenticationService authenticationService = RetrofitClientInstance.getRetrofitInstance().create(AuthenticationService.class);
        authenticationService.resendOTP(mMobileNumber).enqueue(new Callback<ResendOTPResponse>() {
            @Override
            public void onResponse(Call<ResendOTPResponse> call, Response<ResendOTPResponse> response) {
                if(response.isSuccessful()) {
                    ResendOTPResponse date = response.body();
                    assert date != null;
                    if(date.getStatus().equals("Success")) {
                        showDialog("", date.getMsg()+" OTP: "+date.getOtp(),((dialogInterface, i) -> mListener.validateOTP(mMobileNumber)));
                    } else {
                        showDialog("", date.getMsg());
                    }
                } else {
                    showDialog("", response.message());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResendOTPResponse> call, Throwable t) {

            }
        });
    }

}