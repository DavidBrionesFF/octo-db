package com.bytepl.octodb.batch.io;

import com.bytepl.octodb.batch.model.Collection;
import com.bytepl.octodb.batch.model.DataBase;
import com.bytepl.octodb.batch.model.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudOperation {
    public Mono<DataBase> createDataBase(String name);
    public Mono<Boolean> dropDataBase(DataBase dataBase);

    public Mono<Collection> createCollection(DataBase dataBase,
                                             String name,
                                             String descripcion);
    public Mono<Boolean> dropColection(DataBase dataBase, Collection collection);

    public Mono<Document> createDocument(DataBase dataBase,
                                         Collection collection,
                                         Document document);

    public Mono<Document> updateDocument(DataBase dataBase,
                                         Collection collection,
                                         Document document);

    public Mono<Boolean> dropDocument(DataBase dataBase,
                                      Collection collection,
                                      Document document);

    public Flux<DataBase> findDatabases();
    public Mono<DataBase> findDatabasesByName(String name);
}
