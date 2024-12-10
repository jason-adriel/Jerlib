package com.example.jerlib;

import java.util.List;

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
