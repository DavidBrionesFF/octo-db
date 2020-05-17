package com.bytepl.octodb.batch.model;

import com.bytepl.octodb.batch.io.util.CollectionOperation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class Collection implements Serializable {
    private String name;
    private String description;
    @JsonIgnore
    private CollectionOperation<Document> documents = null;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date = new Date();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CollectionOperation<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(CollectionOperation<Document> documents) {
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
