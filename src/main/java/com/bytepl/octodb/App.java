package com.bytepl.octodb;

import com.bytepl.octodb.batch.io.impl.FileCrudOperation;
import com.bytepl.octodb.batch.model.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class App implements CommandLineRunner {
	private Log logger = LogFactory.getLog(getClass());
	@Autowired
	private FileCrudOperation fileCrudOperation;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		fileCrudOperation.createDataBase("bytecode")
//				.doOnError(throwable -> {
//					logger.error(throwable);
//				})
//				.subscribe(dataBase -> {
//					logger.info("La base de datos fue creada");
//
//					fileCrudOperation.createCollection(dataBase, "users", "user")
//							.doOnError(throwable -> {
//								logger.error(throwable);
//							})
//							.subscribe(collection -> {
//								for (int i = 0; i <=100; i++){
//									Document document = new Document();
//
//									document.put("_name", "Jose David " + i);
//									document.put("_last_name", "Briones Rosa " + i);
//
//									fileCrudOperation.createDocument(dataBase, collection, document)
//											.doOnError(throwable -> {
//												logger.error(throwable);
//											})
//											.subscribe(document1 -> {
//												logger.info("El documento esta creado _id=" + document1.get("_id") + "_collection=" +collection.getName());
//											});
//								}
//							});
//
//
//					fileCrudOperation.createCollection(dataBase, "todo", "todo")
//							.doOnError(throwable -> {
//								logger.error(throwable);
//							})
//							.subscribe(collection -> {
//								for (int i = 0; i <=100; i++){
//									Document document = new Document();
//
//									document.put("_name", "Todo" + i);
//									document.put("_description", "Todo description " + i);
//
//									fileCrudOperation.createDocument(dataBase, collection, document)
//											.doOnError(throwable -> {
//												logger.error(throwable);
//											})
//											.subscribe(document1 -> {
//												logger.info("El documento esta creado _id=" + document1.get("_id") + "_collection=" +collection.getName());
//											});
//								}
//							});
//				});

		fileCrudOperation.findDatabases()
				.subscribe(dataBase -> {
					logger.info("La base de datos se cargo " + dataBase.getName());

					try {
						dataBase.getCollections().forEach(collection -> {
							logger.info("La collecion se cargo " + collection.getName());

//							collection.getDocuments()
//									.findAll()
//									.subscribe(o -> {
//										logger.info("Se encontro el id " + o.get("_id"));
//									});
							collection.getDocuments().countAll()
									.subscribe(aLong -> {
										logger.info("La cantidad de documentos encontrada es " + aLong);
									});
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}
}
