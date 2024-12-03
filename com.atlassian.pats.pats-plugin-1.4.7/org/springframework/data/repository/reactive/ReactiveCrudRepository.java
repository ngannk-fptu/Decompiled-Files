/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.repository.reactive;

import org.reactivestreams.Publisher;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface ReactiveCrudRepository<T, ID>
extends Repository<T, ID> {
    public <S extends T> Mono<S> save(S var1);

    public <S extends T> Flux<S> saveAll(Iterable<S> var1);

    public <S extends T> Flux<S> saveAll(Publisher<S> var1);

    public Mono<T> findById(ID var1);

    public Mono<T> findById(Publisher<ID> var1);

    public Mono<Boolean> existsById(ID var1);

    public Mono<Boolean> existsById(Publisher<ID> var1);

    public Flux<T> findAll();

    public Flux<T> findAllById(Iterable<ID> var1);

    public Flux<T> findAllById(Publisher<ID> var1);

    public Mono<Long> count();

    public Mono<Void> deleteById(ID var1);

    public Mono<Void> deleteById(Publisher<ID> var1);

    public Mono<Void> delete(T var1);

    public Mono<Void> deleteAllById(Iterable<? extends ID> var1);

    public Mono<Void> deleteAll(Iterable<? extends T> var1);

    public Mono<Void> deleteAll(Publisher<? extends T> var1);

    public Mono<Void> deleteAll();
}

