package com.example.jerlib.models;

import com.google.gson.annotations.SerializedName;

public class Scopus {
    @SerializedName("dc:identifier")
    private String id;
    @SerializedName("dc:title")
    private String title;
    @SerializedName("prism:doi")
    private String doi;
    @SerializedName("dc:creator")
    private String creator;
    @SerializedName("prism:publicationName")
    private String publicationName;
    @SerializedName("prism:coverDisplayDate")
    private String coverDisplayDate;
    @SerializedName("subtypeDescription")
    private String type;
    @SerializedName("dc:description")
    private String description;
    @SerializedName("authkeywords")
    private String keywords;

    public Scopus(String id, String title, String doi, String creator, String publicationName, String coverDisplayDate, String type, String description, String keywords) {
        this.id = id;
        this.title = title;
        this.doi = doi;
        this.creator = creator;
        this.publicationName = publicationName;
        this.coverDisplayDate = coverDisplayDate;
        this.type = type;
        this.description = description;
        this.keywords = keywords;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPublicationName() {
        return publicationName;
    }

    public void setPublicationName(String publicationName) {
        this.publicationName = publicationName;
    }

    public String getCoverDisplayDate() {
        return coverDisplayDate;
    }

    public void setCoverDisplayDate(String coverDisplayDate) {
        this.coverDisplayDate = coverDisplayDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}