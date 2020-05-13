package com.bytepl.octodb.batch.io.util;

import com.bytepl.octodb.batch.model.Collection;
import com.bytepl.octodb.batch.model.DataBase;
import com.bytepl.octodb.batch.model.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CollectionTransaction implements  CollectionOperation<Document>{
    private String path;
    private DataBase database;
    private Collection collection;
    private File file_collection;
    private ObjectMapper objectMapper = new ObjectMapper();

    public CollectionTransaction(String path, DataBase dataBase, Collection collection) {
        this.database = dataBase;
        this.collection = collection;
        this.path = path;
        file_collection = new File(path +
                database.getName() + "/" + collection.getName() + "/");
    }

    public CollectionTransaction() {
    }

    @Override
    public Mono save(Document document) {
        return Mono.create(documentMonoSink -> {
            String _id = UUID.randomUUID().toString().replace("-", "");
            File file = new File(path +
                    database.getName() + "/" + collection.getName() + "/" + _id + ".json");
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
    public Mono update(Document document) {
        return Mono.create(documentMonoSink -> {
            String _id = document.get("_id").toString();
            File file = new File(path +
                    database.getName() + "/" + collection.getName() + "/" + _id + ".json");
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
    public Flux<Document> findAll() {
        return Flux.create(fluxSink -> {
            for (File document : file_collection.listFiles()){
                try {
                    fluxSink.next(objectMapper.readValue(document, Document.class));
                } catch (IOException e) {
                    fluxSink.error(e);
                }
            }
            fluxSink.complete();
        }).filter(o -> {
            return ((Document) o).get("colection_descriptor") != null;
        }).map(o -> {
            return ((Document) o);
        });
    }

    @Override
    public Mono<Document> findById(String _id) {
        return Mono.create(monoSink -> {
            File file = new File(this.file_collection.getAbsolutePath() + _id + ".json");
            try {
                monoSink.success(objectMapper.readValue(file, Document.class));
            } catch (IOException e) {
                monoSink.error(e);
            }
        });
    }

    @Override
    public Mono removeById(String _id) {
        return existsById(_id)
                .map(o -> {
                    if ((boolean)o){
                        File file = new File(this.file_collection.getAbsolutePath() + _id + ".json");
                        return file.delete();
                    }
                    return false;
                });
    }

    @Override
    public Flux removeAll() {
        return Flux.create(fluxSink -> {
            for (File document : file_collection.listFiles()){
                if (!document.getName().contains("colection_descriptor.json")){
                    fluxSink.next(document.delete());
                }
            }
            fluxSink.complete();
        });
    }

    @Override
    public Mono<Long> countAll() {
        return Mono.create(monoSink -> {
            monoSink.success((long)(file_collection.list().length - 1));
        });
    }

    @Override
    public Mono<Boolean> existsById(String _id) {
        return Mono.create(monoSink -> {
            File file = new File(this.file_collection.getAbsolutePath() + _id + ".json");
            monoSink.success(file.exists());
        });
    }
}
