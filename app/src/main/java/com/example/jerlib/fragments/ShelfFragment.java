package com.example.jerlib.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jerlib.R;
import com.example.jerlib.adapters.ScopusAdapter;
import com.example.jerlib.models.Author;
import com.example.jerlib.models.Bibjson;
import com.example.jerlib.models.Entry;
import com.example.jerlib.models.Journal;
import com.example.jerlib.models.Link;
import com.example.jerlib.models.Subject;
import com.example.jerlib.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShelfFragment extends Fragment {
    String shelfID;
    List<String> articles;
    List<Entry> listData = new ArrayList<>();
    RecyclerView shelfItemRV;
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
                                        for (String s : articles) {
                                            Link link = new Link();
                                            link.setUrl(s);
                                            Author author = new Author();
                                            author.setName("-");
                                            Entry entry = new Entry();
                                            Bibjson bibjson = new Bibjson();
                                            Journal j = new Journal();
                                            j.setTitle("-");
                                            Subject sb = new Subject();
                                            sb.setTerm("-");
                                            bibjson.setKeywords(List.of("-"));
                                            bibjson.setLink(List.of(link));
                                            bibjson.setTitle("Article");
                                            bibjson.setAuthor(List.of(author));
                                            bibjson.setYear("2024");
                                            bibjson.setJournal(j);
                                            bibjson.setAbstract("-");
                                            bibjson.setSubject(List.of(sb));
                                            entry.setBibjson(bibjson);
                                            listData.add(entry);
                                        }
                                        ScopusAdapter adapter = new ScopusAdapter(listData);
                                        shelfItemRV.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
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

        return view;
    }
}