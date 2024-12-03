/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.reactivex.Flowable
 */
package org.springframework.data.repository.reactive;

import io.reactivex.Flowable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.RxJava2CrudRepository;

@NoRepositoryBean
public interface RxJava2SortingRepository<T, ID>
extends RxJava2CrudRepository<T, ID> {
    public Flowable<T> findAll(Sort var1);
}

