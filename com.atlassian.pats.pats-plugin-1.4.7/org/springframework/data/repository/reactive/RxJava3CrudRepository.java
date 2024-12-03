/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.reactivex.rxjava3.core.Completable
 *  io.reactivex.rxjava3.core.Flowable
 *  io.reactivex.rxjava3.core.Maybe
 *  io.reactivex.rxjava3.core.Single
 */
package org.springframework.data.repository.reactive;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface RxJava3CrudRepository<T, ID>
extends Repository<T, ID> {
    public <S extends T> Single<S> save(S var1);

    public <S extends T> Flowable<S> saveAll(Iterable<S> var1);

    public <S extends T> Flowable<S> saveAll(Flowable<S> var1);

    public Maybe<T> findById(ID var1);

    public Maybe<T> findById(Single<ID> var1);

    public Single<Boolean> existsById(ID var1);

    public Single<Boolean> existsById(Single<ID> var1);

    public Flowable<T> findAll();

    public Flowable<T> findAllById(Iterable<ID> var1);

    public Flowable<T> findAllById(Flowable<ID> var1);

    public Single<Long> count();

    public Completable deleteById(ID var1);

    public Completable delete(T var1);

    public Completable deleteAllById(Iterable<? extends ID> var1);

    public Completable deleteAll(Iterable<? extends T> var1);

    public Completable deleteAll(Flowable<? extends T> var1);

    public Completable deleteAll();
}

