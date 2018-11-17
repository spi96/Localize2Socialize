package com.example.spi.localize2socialize.net;

import com.example.spi.localize2socialize.models.Account;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/users/save")
    Call<ResponseBody> saveOrUpdateAccount(@Body Account account);
}
