package com.bytepl.octodb.batch.io.util;

import com.bytepl.octodb.batch.model.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CollectionOperation<T> {
    public Mono save(T document);
    public Mono update(T document);

    public Flux<T> findAll();
    public Mono<T> findById(String _id);

    public Mono removeById(String _id);
    public Flux removeAll();

    public Mono<Long> countAll();
    public Mono<Boolean> existsById(String _id);
}
