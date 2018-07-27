package com.example.muhammadfahad.jslocation.Interface;

import com.example.muhammadfahad.jslocation.bean.StatusBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StatusInterface {
    @GET("Data.php")
    Call<StatusBean> insert(@Query("catId")int catId,@Query("recId")int recId,@Query("attribute")String attribute
                            ,@Query("value")String value,@Query("mobileIMEI")String mobileIMEI,@Query("recordDate")String recordDate,
                            @Query("mobileNo")String mobileNo,@Query("cnicNo")String cnicNo,@Query("channelId")String channelId,
                            @Query("income")String income);



}
