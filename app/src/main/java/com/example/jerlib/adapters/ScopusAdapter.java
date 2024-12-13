package com.example.jerlib.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jerlib.R;
import com.example.jerlib.models.Scopus;

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
        String paperDOI = scopusList.get(position).getDoi();

        holder.paperCategoryTV.setText(paperCategory);
        holder.paperTitleTV.setText(paperTitle);
        holder.paperAuthorTV.setText(paperAuthor);
        holder.paperYearTV.setText(paperYear + " - " + paperJournal);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://doi.org/" + paperDOI;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                v.getContext().startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scopusList.size();
    }

    public static class ScopusViewHolder extends RecyclerView.ViewHolder {
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
