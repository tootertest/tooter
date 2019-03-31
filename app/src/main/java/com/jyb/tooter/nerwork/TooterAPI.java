package com.jyb.tooter.nerwork;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TooterAPI {

    static String domain = "https://http://47.104.23.231/api/v1/";

    @FormUrlEncoded
    @POST("/register")
    Call<ResponseBody> register(@Field("instance_url") String instanceUrl, @Field("access_token") String accessToken, @Field("device_token") String deviceToken);
    @FormUrlEncoded
    @POST("/unregister")
    Call<ResponseBody> unregister(@Field("instance_url") String instanceUrl, @Field("access_token") String accessToken);
}
