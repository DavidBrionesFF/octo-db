package com.bytepl.octodb.batch.model;

import com.bytepl.octodb.batch.io.util.CollectionTransaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataBase {
    private static String path;
    private String name;
    private String descripcion;
    private Date date;
    private List<Document> users;
    @JsonIgnore
    private List<Collection> collections = null;

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

    public List<Collection> getCollections() throws IOException {
        if (this.collections == null){
            File file = new File(path + name);

            ObjectMapper objectMapper = new ObjectMapper();

            this.collections = new ArrayList<>();

            for (String name : file.list()){
                File file_collection =
                        new File(path + getName() + "/" + name + "/colection_descriptor.json");
                Collection collection = objectMapper.readValue(file_collection, Collection.class);
                collection.setDocuments(new CollectionTransaction(path, this, collection));
                this.collections.add(collection);
            }
        }
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        DataBase.path = path;
    }
}
