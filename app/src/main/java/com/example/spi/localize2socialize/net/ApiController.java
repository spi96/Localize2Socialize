package com.example.spi.localize2socialize.net;

import com.example.spi.localize2socialize.models.Account;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController {
    private static ApiInterface apiInterface;
    private static ApiController apiController;
    private static String BASE_URL = "http://10.0.2.2:8080";

    private ApiController() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    public static ApiController getInstance() {
        if (apiController == null) {
            apiController = new ApiController();
        }
        return apiController;
    }

    public void saveOrUpdateAccount(Account account, Callback<ResponseBody> callback) {
        Call<ResponseBody> accountCall = apiInterface.saveOrUpdateAccount(account);
        accountCall.enqueue(callback);
    }

}
