/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.reactivex.rxjava3.core.Flowable
 */
package org.springframework.data.repository.reactive;

import io.reactivex.rxjava3.core.Flowable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.RxJava3CrudRepository;

@NoRepositoryBean
public interface RxJava3SortingRepository<T, ID>
extends RxJava3CrudRepository<T, ID> {
    public Flowable<T> findAll(Sort var1);
}

