package com.example.jerlib.fragments;

import static com.example.jerlib.adapters.ScopusAdapter.getCleanText;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jerlib.R;
import com.example.jerlib.activities.ScopusDetailsActivity;
import com.example.jerlib.activities.ScopusSearchActivity;
import com.example.jerlib.adapters.ScopusAdapter;
import com.example.jerlib.models.Bibjson;
import com.example.jerlib.models.Entry;
import com.example.jerlib.models.ResultResponse;
import com.example.jerlib.services.ApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://doaj.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ApiService apiService = retrofit.create(ApiService.class);

    ArrayList<String> keywords = new ArrayList<String>(Arrays.asList("Artificial Intelligence",
            "Machine Learning",
            "Big Data",
            "Cybersecurity",
            "Quantum Computing",
            "Natural Language Processing",
            "Blockchain",
            "Internet of Things",
            "Cloud Computing",
            "Augmented Reality",
            "Virtual Reality",
            "Data Analytics",
            "Robotics",
            "Fintech",
            "Neural Networks",
            "Edge Computing",
            "Smart Cities",
            "Renewable Energy",
            "Genetic Engineering",
            "Digital Transformation",
            "Mobile Applications",
            "E-commerce",
            "Social Media",
            "Automation",
            "Sustainable Development",
            "Space Exploration",
            "Bioinformatics",
            "Nanotechnology",
            "Autonomous Vehicles",
            "Renewable Resources",
            "Climate Change",
            "3D Printing",
            "Human-Computer Interaction",
            "Computer Vision",
            "Game Development",
            "Microservices",
            "DevOps",
            "Privacy Protection",
            "Data Privacy",
            "Virtual Assistants",
            "Deep Learning",
            "Open Source",
            "Smart Devices",
            "Chatbots",
            "Predictive Modeling",
            "Health Informatics",
            "Education Technology",
            "Supply Chain",
            "Digital Marketing",
            "Renewable Fuels"
    ));

    Random rand = new Random();

    List<Entry> listData = new ArrayList<>();

    private void inflateCards(CardView view, int i) {
        View scopusView = LayoutInflater.from(requireContext()).inflate(R.layout.scopus_list, view, false);
        Bibjson metadata = listData.get(i).getBibjson();
        String paperCategory = metadata.getSubject().get(0).getTerm();
        String paperTitle = getCleanText(metadata.getTitle());
        String paperAuthor = getCleanText(metadata.getAuthor().get(0).getName());
        String paperYear = metadata.getYear();
        String paperJournal = metadata.getJournal().getTitle();
        String paperDOI = metadata.getLink().get(0).getUrl();
        String paperDescription = getCleanText(metadata.getAbstract() == null ? "-" : metadata.getAbstract());
        String paperKeywords = metadata.getKeywords() == null ? "None specified" : metadata.getKeywords().get(0).toUpperCase(Locale.ROOT);

        TextView paperCategoryTV = scopusView.findViewById(R.id.paperCategoryTV);
        TextView paperTitleTV = scopusView.findViewById(R.id.paperTitleTV);
        TextView paperAuthorTV = scopusView.findViewById(R.id.paperAuthorTV);
        TextView paperYearTV = scopusView.findViewById(R.id.paperYearTV);

        paperCategoryTV.setText(paperCategory);
        paperTitleTV.setText(paperTitle);
        paperAuthorTV.setText(paperAuthor);
        paperYearTV.setText(paperYear + " - " + paperJournal);

        scopusView.setOnClickListener(v -> {
            Intent seeDetails = new Intent(v.getContext(), ScopusDetailsActivity.class);
            seeDetails.putExtra("type", paperCategory + " - " + paperYear);
            seeDetails.putExtra("title", paperTitle);
            seeDetails.putExtra("doi", paperDOI);
            seeDetails.putExtra("keywords", paperKeywords);
            seeDetails.putExtra("description", paperDescription);
            seeDetails.putExtra("publisher", paperJournal);
            seeDetails.putExtra("author", paperAuthor);

            v.getContext().startActivity(seeDetails);
        });

        view.addView(scopusView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String query = "bibjson.keywords:" + keywords.get(rand.nextInt(keywords.size()));

        CardView homeScopusCard = view.findViewById(R.id.homeScopusCard);
        CardView homeScopusCard2 = view.findViewById(R.id.homeScopusCard2);

        fetchScopusData(query, homeScopusCard, homeScopusCard2);
    }

    private void fetchScopusData(String query, CardView v1, CardView v2) {
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
                        Toast.makeText(getActivity(), "No Results Found", Toast.LENGTH_LONG).show();
                    }
                    else{
                        // Inflate scopus_list.xml
                        inflateCards(v1, 0);
                        inflateCards(v2, 1);
                    }

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