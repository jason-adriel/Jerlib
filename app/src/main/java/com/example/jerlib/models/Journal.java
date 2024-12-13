package com.example.jerlib.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Journal {

    @SerializedName("volume")
    @Expose
    private String volume;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("issns")
    @Expose
    private List<String> issns;
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("language")
    @Expose
    private List<String> language;
    @SerializedName("title")
    @Expose
    private String title;

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getIssns() {
        return issns;
    }

    public void setIssns(List<String> issns) {
        this.issns = issns;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}