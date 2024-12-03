/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.data.repository.reactive;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

@NoRepositoryBean
public interface ReactiveSortingRepository<T, ID>
extends ReactiveCrudRepository<T, ID> {
    public Flux<T> findAll(Sort var1);
}

