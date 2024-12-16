package com.example.jerlib.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jerlib.R;

public class ScopusDetailsActivity extends AppCompatActivity {
    TextView scopusDetailsTypeTV, scopusDetailsTitleTV, scopusDetailsAuthorTV;
    TextView scopusDetailsPublisherTV, scopusDetailsKeywordsTV, scopusDetailsDescriptionTV;
    Button scopusDetailsViewBtn, scopusDetailsAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scopus_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scopusDetailsAuthorTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scopusDetailsTypeTV = findViewById(R.id.scopusDetailsTypeTV);
        scopusDetailsTitleTV = findViewById(R.id.scopusDetailsTitleTV);
        scopusDetailsAuthorTV = findViewById(R.id.scopusDetailsAuthorTV);
        scopusDetailsPublisherTV = findViewById(R.id.scopusDetailsPublisherTV);
        scopusDetailsKeywordsTV = findViewById(R.id.scopusDetailsKeywordsTV);
        scopusDetailsDescriptionTV = findViewById(R.id.scopusDetailsDescriptionTV);
        scopusDetailsViewBtn = findViewById(R.id.scopusDetailsViewBtn);
        scopusDetailsAddBtn = findViewById(R.id.scopusDetailsAddBtn);

        String paperType = getIntent().getStringExtra("type");
        String paperTitle = getIntent().getStringExtra("title");
        String paperAuthor = getIntent().getStringExtra("author");
        String paperPublisher = getIntent().getStringExtra("publisher");
        String paperDOI = getIntent().getStringExtra("doi");
        String paperDescription = getIntent().getStringExtra("description");
        String paperKeywords = getIntent().getStringExtra("keywords");

        scopusDetailsTypeTV.setText(paperType);
        scopusDetailsTitleTV.setText(paperTitle);
        scopusDetailsAuthorTV.setText(paperAuthor);
        scopusDetailsPublisherTV.setText(paperPublisher);
        scopusDetailsKeywordsTV.setText(paperKeywords);
        scopusDetailsDescriptionTV.setText(paperDescription);
        scopusDetailsViewBtn = findViewById(R.id.scopusDetailsViewBtn);
        scopusDetailsAddBtn = findViewById(R.id.scopusDetailsAddBtn);

        scopusDetailsViewBtn.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paperDOI));
            v.getContext().startActivity(browserIntent);
        });

    }
}