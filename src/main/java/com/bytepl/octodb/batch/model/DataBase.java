package com.bytepl.octodb.batch.model;

import java.util.Date;
import java.util.List;

public class DataBase {
    private String name;
    private String descripcion;
    private Date date;
    private List<Document> users;
    private List<Collection> collections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Document> getUsers() {
        return users;
    }

    public void setUsers(List<Document> users) {
        this.users = users;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }
}
