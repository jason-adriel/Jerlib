package com.example.jerlib.fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.jerlib.R;
import com.example.jerlib.adapters.ScopusAdapter;
import com.example.jerlib.models.Entry;
import com.example.jerlib.models.ResultResponse;
import com.example.jerlib.services.ApiService;
import com.example.jerlib.utils.CustomAPIDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment {
    RecyclerView searchScopusRV;
    List<Entry> listData = new ArrayList<>();
    EditText searchScopusET;
    ImageButton searchScopusBtn, searchScopusConfigBtn;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Entry.class, new CustomAPIDeserializer())
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://doaj.org/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    ApiService apiService = retrofit.create(ApiService.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        searchScopusET = view.findViewById(R.id.searchScopusET);
        searchScopusBtn = view.findViewById(R.id.searchScopusBtn);
        searchScopusConfigBtn = view.findViewById(R.id.searchScopusConfigBtn);
        searchScopusBtn.setOnClickListener(v -> {
            // Get the query from the EditText
            String query = searchScopusET.getText().toString().trim();

            // Use the query from EditText or default to AF-ID(60103610) if empty
            if (!query.isEmpty()) {
                fetchScopusData(query);
            } else {
                fetchScopusData("technology");
            }
        });

        searchScopusConfigBtn.setOnClickListener(v -> {
            SearchConfigBottomSheet dialog = new SearchConfigBottomSheet();
            dialog.show(getChildFragmentManager(), "SearchConfig Bottom Dialog");
        });

        searchScopusET.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Trigger the button click when Enter is pressed
                searchScopusBtn.performClick();

                hideKeyboard(v);

                return true;
            }
            return false;
        });

        // Initialize RecyclerView
        searchScopusRV = view.findViewById(R.id.searchScopusRV);
        searchScopusRV.setLayoutManager(new LinearLayoutManager(getContext()));

        // Perform the initial request with the default query
        fetchScopusData("title:technology");

        return view;
    }

    private void fetchScopusData(String query) {
        Log.i("ApiService", "Sending API request...");

        // Perform the request
        Call<ResultResponse> call = apiService.getResponse(query, "created_date:desc", 1);

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ResultResponse> call, @NonNull Response<ResultResponse> response) {
                Log.i("ApiService", "Response received: " + response);
                if (response.isSuccessful()) {
                    ResultResponse resultResponse = response.body();
                    assert resultResponse != null;
                    listData = resultResponse.getEntries();
                    listData.removeIf(Objects::isNull);

                    if (listData.isEmpty()) {
                        Toast.makeText(getActivity(), "No Results Found", Toast.LENGTH_LONG).show();
                    }

                    // Update the RecyclerView adapter with the new data
                    ScopusAdapter adapter = new ScopusAdapter(listData);
                    searchScopusRV.setAdapter(adapter);

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();

                    Log.i("ApiService", "Scopus Data: " + listData);
                } else {
                    Log.e("ApiService", "Request failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultResponse> call, @NonNull Throwable t) {
                // Log the failure message
                Log.e("ApiService", "Request failed: " + t.getMessage(), t);

                // You can also check for different types of errors
                if (t instanceof java.net.UnknownHostException) {
                    // No internet connection
                    Log.e("ApiService", "No internet connection: " + t.getMessage());
                } else if (t instanceof java.net.SocketTimeoutException) {
                    // Timeout occurred
                    Log.e("ApiService", "Request timed out: " + t.getMessage());
                } else {
                    // Other types of errors
                    Log.e("ApiService", "Unexpected error: " + t.getMessage());
                }
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}