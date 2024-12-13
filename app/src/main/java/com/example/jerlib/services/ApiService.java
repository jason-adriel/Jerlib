package com.example.jerlib.services;

import com.example.jerlib.models.ResultResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/search/articles/{query}")
    Call<ResultResponse> getResponse(
            @Path("query") String query,
            @Query("sort") String sort,
            @Query("page") Integer page
    );
}
