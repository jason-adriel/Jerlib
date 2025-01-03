package com.example.jerlib.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jerlib.fragments.SearchConfigBottomSheet;
import com.example.jerlib.models.Entry;
import com.example.jerlib.models.ResultResponse;
import com.example.jerlib.services.ApiService;
import com.example.jerlib.R;
import com.example.jerlib.adapters.ScopusAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScopusSearchActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView searchScopusRV;
    List<Entry> listData = new ArrayList<>();
    EditText searchScopusET;
    ImageButton searchScopusBtn, searchScopusConfigBtn;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://doaj.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ApiService apiService = retrofit.create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scopus_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activityScopusConstraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        searchScopusET = findViewById(R.id.searchScopusET);
        searchScopusBtn = findViewById(R.id.searchScopusBtn);
        searchScopusConfigBtn = findViewById(R.id.searchScopusConfigBtn);
        searchScopusBtn.setOnClickListener(this);
        searchScopusConfigBtn.setOnClickListener(v -> {
            SearchConfigBottomSheet dialog = new SearchConfigBottomSheet();
            dialog.show(getSupportFragmentManager(), "SearchConfig Bottom Dialog");
        });

        searchScopusET.setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Trigger the button click when Enter is pressed
                searchScopusBtn.performClick();

                hideKeyboard(view);

                return true;
            }
            return false;
        });

        // Initialize RecyclerView
        searchScopusRV = findViewById(R.id.searchScopusRV);
        searchScopusRV.setLayoutManager(new LinearLayoutManager(this));

        // Perform the initial request with the default query
        fetchScopusData("title:technology");
    }

    @Override
    public void onClick(View v) {
        if (v == searchScopusBtn) {
            // Get the query from the EditText
            String query = searchScopusET.getText().toString().trim();

            // Use the query from EditText or default to AF-ID(60103610) if empty
            if (!query.isEmpty()) {
                fetchScopusData(query);
            } else {
                fetchScopusData("technology");
            }
        }
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    private void fetchScopusData(String query) {
        Log.i("ApiService", "Sending API request...");

        // Perform the request
        Call<ResultResponse> call = apiService.getResponse(query, "created_date:desc", 1);

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                Log.i("ApiService", "Response received: " + response.toString());
                if (response.isSuccessful()) {
                    ResultResponse resultResponse = response.body();
                    listData = resultResponse.getEntries();

                    if (listData.get(0).getId() == null) {
                        Toast.makeText(ScopusSearchActivity.this, "No Results Found", Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<ResultResponse> call, Throwable t) {
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
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}