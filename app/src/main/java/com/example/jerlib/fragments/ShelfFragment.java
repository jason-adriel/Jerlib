package com.example.jerlib.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jerlib.R;
import com.example.jerlib.adapters.ScopusAdapter;
import com.example.jerlib.models.Entry;
import com.example.jerlib.models.ResultResponse;
import com.example.jerlib.services.ApiService;
import com.example.jerlib.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShelfFragment extends Fragment {
    String shelfID;
    List<String> articles;
    List<Entry> listData = new ArrayList<>();
    RecyclerView shelfItemRV;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://doaj.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ApiService apiService = retrofit.create(ApiService.class);
    TextView shelfNotificationTV;
    private void fetchUserShelf() {
        FirebaseFirestore db = FirebaseUtil.db();
        String uuid = FirebaseUtil.auth.getUid();
        assert uuid != null;
        DocumentReference userRef = db.collection("Users").document(uuid);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        List<String> shelves = (List<String>) document.get("shelves");
                        assert shelves != null;
                        shelfID = shelves.get(0);
                        DocumentReference shelfRef = db.collection("Shelves").document(shelfID);
                        shelfRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        listData.clear();
                                        articles = (List<String>) document.get("entries");
                                        assert articles != null;
                                        if (!articles.isEmpty()) {
                                            shelfNotificationTV.setText("");
                                            String queryString = getQueryString();
                                            fetchScopusData(queryString);
                                        } else if (shelfItemRV.getAdapter() != null) {
                                            shelfItemRV.getAdapter().notifyDataSetChanged();
                                            shelfNotificationTV.setText("You recently emptied your shelf! Go search for more now!");
                                        } else {
                                            shelfNotificationTV.setText("Your shelf is empty, go search for some references!");
                                        }
                                    }
                                }
                            }

                            @NonNull
                            private String getQueryString() {
                                StringBuilder query = new StringBuilder();
                                query.append("id:(");
                                for (int i = 0; i < articles.size(); ++i) {
                                    query.append(articles.get(i));
                                    if (i != articles.size() - 1) {
                                        query.append(" OR ");
                                    }
                                }
                                query.append(")");
                                String queryString = query.toString();
                                return queryString;
                            }
                        });


                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserShelf();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_shelf, container, false);
        fetchUserShelf();
        shelfItemRV = view.findViewById(R.id.shelfItemRV);
        shelfItemRV.setLayoutManager(new LinearLayoutManager(getContext()));
        shelfNotificationTV = view.findViewById(R.id.shelfNotificationTV);
        return view;
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
                    assert resultResponse != null;
                    listData = resultResponse.getEntries();
                    ScopusAdapter adapter = new ScopusAdapter(listData);
                    shelfItemRV.setAdapter(adapter);
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
}