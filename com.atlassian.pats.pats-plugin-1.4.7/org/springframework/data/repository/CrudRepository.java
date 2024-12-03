/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository;

import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface CrudRepository<T, ID>
extends Repository<T, ID> {
    public <S extends T> S save(S var1);

    public <S extends T> Iterable<S> saveAll(Iterable<S> var1);

    public Optional<T> findById(ID var1);

    public boolean existsById(ID var1);

    public Iterable<T> findAll();

    public Iterable<T> findAllById(Iterable<ID> var1);

    public long count();

    public void deleteById(ID var1);

    public void delete(T var1);

    public void deleteAllById(Iterable<? extends ID> var1);

    public void deleteAll(Iterable<? extends T> var1);

    public void deleteAll();
}

