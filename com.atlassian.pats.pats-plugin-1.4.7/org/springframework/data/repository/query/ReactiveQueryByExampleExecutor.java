/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.repository.query;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveQueryByExampleExecutor<T> {
    public <S extends T> Mono<S> findOne(Example<S> var1);

    public <S extends T> Flux<S> findAll(Example<S> var1);

    public <S extends T> Flux<S> findAll(Example<S> var1, Sort var2);

    public <S extends T> Mono<Long> count(Example<S> var1);

    public <S extends T> Mono<Boolean> exists(Example<S> var1);
}

