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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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
                    dataBase.setName(name);
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

            try{
                new File(path_name + dataBase.getName() + "/" + name).mkdir();

                Collection collection = new Collection();

                collection.setDate(new Date());
                collection.setName(name);
                collection.setDescription(descripcion);
                collection.setDocuments(new ArrayList<>());

                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.writeValue(new File(path_name + dataBase.getName() + "/" + name + "/colection_descriptor.json"),
                        collection);

                dataBaseMonoSink.success(collection);
            }catch (Exception e){
                e.printStackTrace();
                dataBaseMonoSink.error(new NotCreatedException("La coleccion de datos no se creo porque no tiene permisos"));
            }
        });
    }

    @Override
    public Mono<Boolean> dropColection(DataBase dataBase, Collection collection) {
        return Mono.create(dataBaseMonoSink -> {
            try{
                File file = new File(path_name + dataBase.getName() + "/" + collection.getName());
                file.delete();
                dataBaseMonoSink.success(true);
            }catch (Exception e){
                dataBaseMonoSink.error(new NotCreatedException("La base de datos no se elimino porque no tiene permisos"));
            }
        });
    }

    @Override
    public Mono<Document> createDocument(DataBase dataBase, Collection collection, Document document) {
        return Mono.create(documentMonoSink -> {
            String _id = UUID.randomUUID().toString().replace("-", "");
            File file = new File(path_name +
                    dataBase.getName() + "/" + collection.getName() + "/" + _id + ".json");
            document.put("_id", _id);
            try {
                new ObjectMapper()
                            .writeValue(file, document);
                documentMonoSink.success(document);
            } catch (IOException e) {
                documentMonoSink.error(e);
            }
        });
    }

    @Override
    public Mono<Document> updateDocument(DataBase dataBase, Collection collection, Document document) {
        return Mono.create(documentMonoSink -> {
            File file = new File(path_name +
                    dataBase.getName() + "/" + collection.getName() + "/" + document.get("_id") + ".json");
            try {
                new ObjectMapper()
                        .writeValue(file, document);
                documentMonoSink.success(document);
            } catch (IOException e) {
                documentMonoSink.error(e);
            }
        });
    }

    @Override
    public Mono<Boolean> dropDocument(DataBase dataBase, Collection collection, Document document) {
        return Mono.create(documentMonoSink -> {
            File file = new File(path_name +
                    dataBase.getName() + "/" + collection.getName() + "/" + document.get("_id") + ".json");
            if (file.exists()){
                file.delete();
                documentMonoSink.success(true);
            }else {
                documentMonoSink.error(new Exception());
            }
        });
    }

    @Override
    public Flux<DataBase> findDatabases() {
        return Flux.create(dataBaseFluxSink -> {
            File file = new File(path_name);
            if (file.isDirectory()){
                for (String name:
                     file.list()) {
                    DataBase dataBase = new DataBase();
                    dataBase.setName(name);
                    dataBase.setCollections(new CollectionTransaction<Collection>());
                    dataBase.setDate(new Date());

                    dataBaseFluxSink.next(dataBase);
                }
                dataBaseFluxSink.complete();
            }else {
                dataBaseFluxSink.error(new Exception("El directorio que especifico no es un directorio"));
            }
        } );
    }

    @Override
    public Mono<DataBase> findDatabasesByName(String name) {
        return null;
    }
}
