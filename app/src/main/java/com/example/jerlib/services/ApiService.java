package com.example.jerlib.services;

import com.example.jerlib.utils.ScopusResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("content/search/scopus")
    Call<ScopusResponse> getScopuses(
            @Query("query") String query,
            @Query("apiKey") String apiKey
    );
}
