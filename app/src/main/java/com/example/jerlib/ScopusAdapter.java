package com.example.jerlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScopusAdapter extends RecyclerView.Adapter<ScopusAdapter.ScopusViewHolder>{
    Context context;
    List<Scopus> scopusList;

    public ScopusAdapter(List<Scopus> scopusList){
        this.scopusList = scopusList;
    }

    @NonNull
    @Override
    public ScopusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.scopus_list, parent, false);

        return new ScopusViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScopusAdapter.ScopusViewHolder holder, int position) {
        String paperCategory = scopusList.get(position).getType();
        String paperTitle = scopusList.get(position).getTitle();
        String paperAuthor = scopusList.get(position).getCreator();
        String paperYear = scopusList.get(position).getCoverDisplayDate();
        String paperJournal = scopusList.get(position).getPublicationName();

        holder.paperCategoryTV.setText(paperCategory);
        holder.paperTitleTV.setText(paperTitle);
        holder.paperAuthorTV.setText(paperAuthor);
        holder.paperYearTV.setText(paperYear + " - " + paperJournal);
    }

    @Override
    public int getItemCount() {
        return scopusList.size();
    }

    public class ScopusViewHolder extends RecyclerView.ViewHolder {
        TextView paperCategoryTV, paperTitleTV, paperAuthorTV, paperYearTV;

        public ScopusViewHolder(@NonNull View itemView) {
            super(itemView);

            paperCategoryTV = itemView.findViewById(R.id.paperCategoryTV);
            paperTitleTV = itemView.findViewById(R.id.paperTitleTV);
            paperAuthorTV = itemView.findViewById(R.id.paperAuthorTV);
            paperYearTV = itemView.findViewById(R.id.paperYearTV);
        }
    }
}
