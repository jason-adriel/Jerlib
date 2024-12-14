package com.example.jerlib.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("bibjson")
    @Expose
    private Bibjson bibjson;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("id")
    @Expose
    private String id;

    public Bibjson getBibjson() {
        return bibjson;
    }

    public void setBibjson(Bibjson bibjson) {
        this.bibjson = bibjson;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}