package com.bytepl.octodb.batch.io.impl;

import com.bytepl.octodb.batch.io.CrudOperation;
import com.bytepl.octodb.batch.io.util.CollectionTransaction;
import com.bytepl.octodb.batch.io.util.exception.NotCreatedException;
import com.bytepl.octodb.batch.model.Collection;
import com.bytepl.octodb.batch.model.DataBase;
import com.bytepl.octodb.batch.model.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Date;

@Component
public class FileCrudOperation implements CrudOperation {
    @Value("${octa-db.path.name}")
    private String path_name;

    @Override
    public Mono<DataBase> createDataBase(String name) {
        return Mono.create(dataBaseMonoSink -> {
            File file = new File(path_name + name);

            try{
                file.mkdir();

                if (file.exists()){
                    DataBase dataBase = new DataBase();
                    dataBase.setDate(new Date());
                    dataBase.setCollections(new CollectionTransaction<Collection>());
                    dataBaseMonoSink.success(dataBase);
                } else {
                    dataBaseMonoSink.error(new NotCreatedException("La base de datos no existe"));
                }
            }catch (Exception e){
                dataBaseMonoSink.error(new NotCreatedException("La base de datos no se creo porque no tiene permisos"));
            }
        });
    }

    @Override
    public Mono<Boolean> dropDataBase(DataBase dataBase) {
        return Mono.create(dataBaseMonoSink -> {
            File file = new File(path_name + dataBase.getName());

            try{
                file.mkdir();

                if (file.exists()){
                    dataBaseMonoSink.success(true);
                } else {
                    dataBaseMonoSink.error(new NotCreatedException("La base de datos no existe"));
                }
            }catch (Exception e){
                dataBaseMonoSink.error(new NotCreatedException("La base de datos no se elimino porque no tiene permisos"));
            }
        });
    }

    @Override
    public Mono<Collection> createCollection(DataBase dataBase, String name, String descripcion) {
        return Mono.create(dataBaseMonoSink -> {
            String path_db = path_name + dataBase.getName();

            try{
                new File(path_db + name).mkdir();

                Collection collection = new Collection();

                collection.setDate(new Date());
                collection.setName(name);
                collection.setDescription(descripcion);
                collection.setDocuments(new CollectionTransaction<>());

                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.writeValue(new File(path_db + name + "colection_descriptor.json"),
                        collection);

                dataBaseMonoSink.success(collection);
            }catch (Exception e){
                dataBaseMonoSink.error(new NotCreatedException("La coleccion de datos no se creo porque no tiene permisos"));
            }
        });
    }

    @Override
    public Mono<Boolean> dropColection(Collection collection) {
        return null;
    }

    @Override
    public Mono<Document> createDocument(DataBase dataBase, Collection collection, Document document) {
        return null;
    }

    @Override
    public Mono<Document> updateDocument(DataBase dataBase, Collection collection, Document document) {
        return null;
    }

    @Override
    public Mono<Boolean> dropDocument(DataBase dataBase, Collection collection, Document document) {
        return null;
    }
}
