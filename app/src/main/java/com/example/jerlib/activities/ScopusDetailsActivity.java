package com.example.jerlib.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jerlib.R;
import com.example.jerlib.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class ScopusDetailsActivity extends AppCompatActivity {
    TextView scopusDetailsTypeTV, scopusDetailsTitleTV, scopusDetailsAuthorTV;
    TextView scopusDetailsPublisherTV, scopusDetailsKeywordsTV, scopusDetailsDescriptionTV;
    Button scopusDetailsViewBtn, scopusDetailsAddBtn;
    List<String> articles;
    String shelfID;

    private void fetchUserShelf(String paperDOI) {
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
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        articles = (List<String>) document.get("entries");
                                        for (String article : articles) {
                                            if (Objects.equals(article, paperDOI)) {
                                                scopusDetailsAddBtn.setText("Remove from shelf.");
                                                scopusDetailsAddBtn.setOnClickListener(v -> {
                                                    removeItem(paperDOI);
                                                });
                                                break;
                                            }
                                        }
                                    }
                                }
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

        fetchUserShelf(paperDOI);

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

        scopusDetailsAddBtn.setOnClickListener(v -> {
            Log.d("TAG", "Trying to add item");
            addItem(paperDOI);
        });

    }

    public void removeItem(String paperDOI) {
        FirebaseUtil.db().collection("Shelves").document(shelfID)
                .update("entries", FieldValue.arrayRemove(paperDOI))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        scopusDetailsAddBtn.setText("Add to shelf");
                        scopusDetailsAddBtn.setOnClickListener(v -> {
                            addItem(paperDOI);
                        });
                        Log.d("TAG", "Successfully removed from the entries array.");
                        Toast.makeText(ScopusDetailsActivity.this, "Removed from shelf.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error removing from the entries array", e);
                    }
                });
    }

    public void addItem(String paperDOI) {
        FirebaseUtil.db().collection("Shelves").document(shelfID)
                .update("entries", FieldValue.arrayUnion(paperDOI))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        scopusDetailsAddBtn.setText("Remove from shelf.");
                        scopusDetailsAddBtn.setOnClickListener(v -> {
                            removeItem(paperDOI);
                        });
                        Log.d("TAG", "Successfully added to the entries array.");
                        Toast.makeText(ScopusDetailsActivity.this, "Added to shelf.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error removing from the entries array", e);
                    }
                });
    }
}