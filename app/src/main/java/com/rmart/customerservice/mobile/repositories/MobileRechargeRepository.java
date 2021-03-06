package com.rmart.customerservice.mobile.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.rmart.BuildConfig;
import com.rmart.customerservice.mobile.api.MobileRechargeService;
import com.rmart.customerservice.mobile.models.GetHistroryBaseClass;
import com.rmart.customerservice.mobile.models.MRechargeBaseClass;
import com.rmart.electricity.RSAKeyResponse;
import com.rmart.utilits.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileRechargeRepository {

    public final static int MOBLIE_RECHARGE_SERVICE_TYPE= 1;
    public final static int DTH_RECHARGE_SERVICE_TYPE= 2;
    public static MutableLiveData<GetHistroryBaseClass> getHistory(String UserId) {

        MobileRechargeService mobileRechargeService = RetrofitClientInstance.getRetrofitInstanceRokad().create(MobileRechargeService.class);
        final MutableLiveData<GetHistroryBaseClass> resultMutableLiveData = new MutableLiveData<>();
        Call<GetHistroryBaseClass> call = mobileRechargeService.getCustomerHistory(UserId, BuildConfig.service_name,"1");
        final GetHistroryBaseClass result = new GetHistroryBaseClass();

        call.enqueue(new Callback<GetHistroryBaseClass>() {
            @Override
            public void onResponse(Call<GetHistroryBaseClass> call, Response<GetHistroryBaseClass> response) {
                Gson s = new Gson();
                if(response.isSuccessful()) {
                    GetHistroryBaseClass data = response.body();
                    data.setStatus(response.code());
                    Log.d("Recharge",s.toJson(data));
                    resultMutableLiveData.setValue(data);
                } else {
                    final GetHistroryBaseClass result = new GetHistroryBaseClass();
                    result.setStatus(response.code());
                    result.setMsg(response.message());
                    resultMutableLiveData.setValue(result);
                }
            }

            @Override
            public void onFailure(Call<GetHistroryBaseClass> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \"hungryindia.co.in\": No address associated with hostname"))
                {
                    result.setMsg("Please Check Internet Connection");
                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                resultMutableLiveData.setValue(result);
           }
        });
        return resultMutableLiveData;
    }

    public static MutableLiveData<MRechargeBaseClass> performVRecharge(int service_type, String preOperator_dth, String customer_number, int recharge_type, String preOperator, String PostOperator,
                                                                   String Location, String Mobile_number, int rechargeType, String Recharge_amount, String user_id, String ccavneuData) {

        MobileRechargeService mobileRechargeService = RetrofitClientInstance.getRetrofitInstanceRokad().create(MobileRechargeService.class);
        final MutableLiveData<MRechargeBaseClass> resultMutableLiveData = new MutableLiveData<>();
        Call<MRechargeBaseClass> call = mobileRechargeService.VRecharge(service_type, preOperator_dth, customer_number, recharge_type+"", preOperator, PostOperator, Location, Mobile_number, rechargeType+"", Recharge_amount, user_id, ccavneuData);
        final MRechargeBaseClass result = new MRechargeBaseClass();

        call.enqueue(new Callback<MRechargeBaseClass>() {
            @Override
            public void onResponse(Call<MRechargeBaseClass> call, Response<MRechargeBaseClass> response) {
                MRechargeBaseClass data = response.body();
                if(response.isSuccessful()) {
                    resultMutableLiveData.setValue(data);
                } else {

                    result.setStatus("failed");
                    resultMutableLiveData.setValue(result);
                }
            }

            @Override
            public void onFailure(Call<MRechargeBaseClass> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \"hungryindia.co.in\": No address associated with hostname"))
                {
                    result.setMsg("Please Check Internet Connection");
                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                resultMutableLiveData.setValue(result);
            }
        });
        return resultMutableLiveData;
    }

    public static MutableLiveData<RSAKeyResponse> getRSAKey(String user_id, String txt_amount, String service_id, String service_name) {

        MobileRechargeService mobileRechargeService = RetrofitClientInstance.getRetrofitInstance().create(MobileRechargeService.class);
        final MutableLiveData<RSAKeyResponse> resultMutableLiveData = new MutableLiveData<>();
        Call<RSAKeyResponse> call = mobileRechargeService.RsaKeyVRecharge(user_id,txt_amount, service_id, service_name);
        final RSAKeyResponse result = new RSAKeyResponse();

        call.enqueue(new Callback<RSAKeyResponse>() {
            @Override
            public void onResponse(Call<RSAKeyResponse> call, Response<RSAKeyResponse> response) {
                RSAKeyResponse data = response.body();
                if(data!=null && data.getData()!=null) {
                    resultMutableLiveData.setValue(data);
                } else {

                    result.setStatus("failed");
                    resultMutableLiveData.setValue(result);
                }
            }

            @Override
            public void onFailure(Call<RSAKeyResponse> call, Throwable t) {
                if(t.getLocalizedMessage().equalsIgnoreCase("Unable to resolve host \"hungryindia.co.in\": No address associated with hostname"))
                {
                    result.setMsg("Please Check Internet Connection");
                } else {
                    result.setMsg(t.getLocalizedMessage());
                }
                result.setStatus("failed");
                resultMutableLiveData.setValue(result);
            }
        });
        return resultMutableLiveData;
    }
}
