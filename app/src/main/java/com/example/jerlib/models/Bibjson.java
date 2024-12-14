package com.example.jerlib.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

public class Bibjson {
    @SerializedName("identifier")
    @Expose
    private List<Identifier> identifier;
    @SerializedName("journal")
    @Expose
    private Journal journal;
    @SerializedName("end_page")
    @Expose
    @Nullable
    private String endPage;
    @SerializedName("keywords")
    @Expose
    private List<String> keywords;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("start_page")
    @Expose
    private String startPage;
    @SerializedName("subject")
    @Expose
    private List<Subject> subject;
    @SerializedName("author")
    @Expose
    private List<Author> author;
    @SerializedName("link")
    @Expose
    private List<Link> link;
    @SerializedName("abstract")
    @Expose
    @Nullable
    private String _abstract;
    @SerializedName("title")
    @Expose
    private String title;

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public String getEndPage() {
        return endPage;
    }

    public void setEndPage(String endPage) {
        this.endPage = endPage;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

    public List<Subject> getSubject() {
        return subject;
    }

    public void setSubject(List<Subject> subject) {
        this.subject = subject;
    }

    public List<Author> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    public List<Link> getLink() {
        return link;
    }

    public void setLink(List<Link> link) {
        this.link = link;
    }

    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}