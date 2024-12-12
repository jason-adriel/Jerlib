package com.example.jerlib;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScopusResponse {
    @SerializedName("search-results")
    private SearchResults searchResults;

    public SearchResults getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults) {
        this.searchResults = searchResults;
    }

    public static class SearchResults {

        @SerializedName("entry")
        private List<Scopus> entry;

        public List<Scopus> getEntry() {
            return entry;
        }

        public void setEntry(List<Scopus> entry) {
            this.entry = entry;
        }
    }
}