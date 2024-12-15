package com.example.jerlib.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jerlib.R;
import com.example.jerlib.activities.ScopusDetailsActivity;
import com.example.jerlib.models.Bibjson;
import com.example.jerlib.models.Entry;

import java.util.List;
import java.util.Locale;

public class ScopusAdapter extends RecyclerView.Adapter<ScopusAdapter.ScopusViewHolder>{
    Context context;
    List<Entry> scopusList;

    public ScopusAdapter(List<Entry> scopusList){
        this.scopusList = scopusList;
    }

    public static String getCleanText(String s) {
        return HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim();
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
        Bibjson metadata = scopusList.get(position).getBibjson();
        String paperCategory = metadata.getSubject().get(0).getTerm();
        String paperTitle = getCleanText(metadata.getTitle());
        String paperAuthor = getCleanText(metadata.getAuthor().get(0).getName());
        String paperYear = metadata.getYear();
        String paperJournal = metadata.getJournal().getTitle();
        String paperDOI = metadata.getLink().get(0).getUrl();
        String paperDescription = getCleanText(metadata.getAbstract() == null ? "-" : metadata.getAbstract());
        String paperKeywords = metadata.getKeywords() == null ? "None specified" : metadata.getKeywords().get(0).toUpperCase(Locale.ROOT);

        holder.paperCategoryTV.setText(paperCategory);
        holder.paperTitleTV.setText(paperTitle);
        holder.paperAuthorTV.setText(paperAuthor);
        holder.paperYearTV.setText(paperYear + " - " + paperJournal);

        holder.itemView.setOnClickListener(v -> {
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
