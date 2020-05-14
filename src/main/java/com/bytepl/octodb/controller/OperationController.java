package com.bytepl.octodb.controller;

import com.bytepl.octodb.batch.io.CrudOperation;
import com.bytepl.octodb.batch.model.Collection;
import com.bytepl.octodb.batch.model.DataBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/database")
public class OperationController {
    private static final Log logger = LogFactory.getLog(OperationController.class);

    @Autowired
    private CrudOperation crudOperation;

    @GetMapping("/findAll")
    public Flux<DataBase> findAll(){
        return crudOperation.findDatabases();
    }

    @GetMapping("/{databaseName}/collection/findAll")
    public Mono<List<Collection>> findAll(
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
}
