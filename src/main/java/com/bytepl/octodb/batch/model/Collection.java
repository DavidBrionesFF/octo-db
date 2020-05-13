package com.bytepl.octodb.batch.model;

import com.bytepl.octodb.batch.io.util.CollectionTransaction;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Collection implements Serializable {
    private String name;
    private String description;
    private List<Document> documents = new CollectionTransaction<>();
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
