package com.example.jerlib.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultResponse {

    @SerializedName("results")
    @Expose
    private List<Entry> entries;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setResultResponse(List<Entry> entries) {
        this.entries = entries;
    }
}
