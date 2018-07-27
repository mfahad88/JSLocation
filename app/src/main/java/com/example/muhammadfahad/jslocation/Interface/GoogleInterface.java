package com.example.muhammadfahad.jslocation.Interface;

import com.example.muhammadfahad.jslocation.bean.Map;

import java.util.List;



import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GoogleInterface {

	@GET("json")
	Call<Map> getLocation(@Query("location") String location, @Query("radius")String radius,@Query("key")String key);
}