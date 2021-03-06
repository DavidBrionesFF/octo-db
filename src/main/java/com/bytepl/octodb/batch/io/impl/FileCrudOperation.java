package com.bytepl.octodb.batch.io.impl;

import com.bytepl.octodb.batch.io.CrudOperation;
import com.bytepl.octodb.batch.io.util.CollectionTransaction;
import com.bytepl.octodb.batch.io.util.exception.NotCreatedException;
import com.bytepl.octodb.batch.model.Collection;
import com.bytepl.octodb.batch.model.DataBase;
import com.bytepl.octodb.batch.model.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Component
@Scope
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
                    DataBase.setPath(path_name);
                    dataBase.setName(name);
                    dataBase.setDate(new Date());
                    dataBase.setCollections(null);
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
                    FileUtils.deleteDirectory(file);
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
                collection.setDocuments(new CollectionTransaction(path_name, dataBase, collection));

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
                File file = new File(path_name + dataBase.getName() + "/" + collection.getName() + "/");
                FileUtils.deleteDirectory(file);
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
                    dataBase.getName() + "/" + collection.getName() + "/" + document.get("_id").toString() + ".json");
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
                    DataBase.setPath(path_name);
                    dataBase.setName(name);
                    dataBase.setCollections(null);
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
        return Mono.create(dataBaseMonoSink -> {
            File file = new File(path_name + name);
            if (file.isDirectory()){
                DataBase dataBase = new DataBase();
                DataBase.setPath(path_name);
                dataBase.setName(name);
                dataBase.setCollections(null);
                dataBase.setDate(new Date());


                dataBaseMonoSink.success(dataBase);
            }else {
                dataBaseMonoSink.error(new Exception("El directorio que especifico no es un directorio"));
            }
        });
    }

    @Override
    public Collection findCollectionByDataBaseAndName(String database, String name) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        DataBase dataBase = new DataBase();
        DataBase.setPath(path_name);
        dataBase.setName(database);
        dataBase.setCollections(null);
        dataBase.setDate(new Date());

        File file_collection =
                new File(path_name + database + "/" + name + "/colection_descriptor.json");
        Collection collection = objectMapper.readValue(file_collection, Collection.class);
        collection.setDocuments(new CollectionTransaction(path_name, dataBase, collection));
        return collection;
    }
}
