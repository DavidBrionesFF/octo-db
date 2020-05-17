package com.bytepl.octodb.controller;

import com.bytepl.octodb.batch.io.CrudOperation;
import com.bytepl.octodb.batch.model.Collection;
import com.bytepl.octodb.batch.model.DataBase;
import com.bytepl.octodb.batch.model.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/database")
public class OperationController {
    private static final Log logger = LogFactory.getLog(OperationController.class);
    @Value("${octa-db.path.name}")
    private String path;

    @Autowired
    private CrudOperation crudOperation;

    /* Obtener la base de datos*/
    @GetMapping("/findAll")
    public Flux<DataBase> findAllDatabase(){
        return crudOperation.findDatabases();
    }

    /* Obtener las colecciones*/
    @GetMapping("/{databaseName}/collection/findAll")
    public Mono<List<Collection>> findCollectionByDatabase(
            @PathVariable String databaseName
    ){
        logger.info(databaseName);
        return crudOperation.findDatabasesByName(databaseName)
                .map(dataBase -> {
                    try {
                        return dataBase.getCollections();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    /* Obteniendo todas las documentos*/
    @GetMapping("/{databaseName}/collection/{collectionname}/findAll")
    public Flux findAllDocumentByCollectionAndDataBase(
            @PathVariable String databaseName,
            @PathVariable String collectionname
    ) throws IOException {
        return crudOperation.findCollectionByDataBaseAndName(databaseName, collectionname)
                .getDocuments()
                .findAll();
    }

    /* Obtenido documentos por Id*/
    @GetMapping("/{databaseName}/collection/{collectionname}/findById/{id}")
    public Mono<Document> findAllDocumentByCollectionAndDataBaseAndIdDocument(
            @PathVariable String databaseName,
            @PathVariable String collectionname,
            @PathVariable String id
    ) throws IOException {
        return crudOperation.findCollectionByDataBaseAndName(databaseName, collectionname)
                .getDocuments()
                .findById(id);
    }

    @GetMapping("/{databaseName}/collection/{collectionname}/countAll")
    public Mono<Long> countAllDocumentByCollectionAndDataBaseAndIdDocument(
            @PathVariable String databaseName,
            @PathVariable String collectionname
    ) throws IOException {
        return crudOperation.findCollectionByDataBaseAndName(databaseName, collectionname)
                .getDocuments()
                .countAll();
    }

    @GetMapping("/{databaseName}/collection/{collectionname}/existsById/{id}")
    public Mono<Boolean> existsAllDocumentByCollectionAndDataBaseAndIdDocument(
            @PathVariable String databaseName,
            @PathVariable String collectionname,
            @PathVariable String id
    ) throws IOException {
        return crudOperation.findCollectionByDataBaseAndName(databaseName, collectionname)
                .getDocuments()
                .existsById(id);
    }

    @PostMapping("/{database}")
    public Mono createDatabase(@PathVariable String database){
        return crudOperation.createDataBase(database);
    }

    @PostMapping("/{database}/collection")
    public Mono createCollection(@PathVariable String database,
                                 @RequestBody Collection collection){
        DataBase dataBase = new DataBase();
        dataBase.setPath(path);
        dataBase.setName(database);
        dataBase.setDate(new Date());
        return crudOperation.createCollection(dataBase, collection.getName(), collection.getDescription());
    }

    @PostMapping("/{database}/collection/{collection}/document")
    public Mono createDocument(@PathVariable String database,
                               @PathVariable String collection,
                               @RequestBody Document document) throws IOException {
        DataBase dataBase = new DataBase();
        dataBase.setPath(path);
        dataBase.setName(database);
        dataBase.setDate(new Date());
        Collection collection1 = crudOperation.findCollectionByDataBaseAndName(database, collection);
        return crudOperation.createDocument(dataBase, collection1, document);
    }

    /* Remover datos de la base de datos */
    @DeleteMapping("/{database}")
    public Mono removeDatabase(
            @PathVariable String database
    ){
        DataBase dataBase = new DataBase();
        dataBase.setPath(path);
        dataBase.setName(database);
        dataBase.setDate(new Date());
        return crudOperation.dropDataBase(dataBase);
    }

    /* Remover colecciones*/
    @DeleteMapping("/{database}/collection/{collection}")
    public Mono removeCollection(
            @PathVariable String database,
            @PathVariable String collection
    ) throws IOException {
        DataBase dataBase = new DataBase();
        dataBase.setPath(path);
        dataBase.setName(database);
        dataBase.setDate(new Date());

        Collection collection1 = crudOperation.findCollectionByDataBaseAndName(database, collection);

        return crudOperation.dropColection(dataBase, collection1);
    }

    /* Remover documentos*/
    @DeleteMapping("/{database}/collection/{collection}/document/{document}")
    public Mono removeDocument(
            @PathVariable String database,
            @PathVariable String collection,
            @PathVariable String document
    ) throws IOException {
        DataBase dataBase = new DataBase();
        dataBase.setPath(path);
        dataBase.setName(database);
        dataBase.setDate(new Date());

        Collection collection1 = crudOperation.findCollectionByDataBaseAndName(database, collection);
        Document document1 = new Document();
        document1.put("_id", document);
        return crudOperation.dropDocument(dataBase, collection1, document1);
    }
}
