/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 */
package com.atlassian.data.activeobjects.repository;

import com.atlassian.activeobjects.tx.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface ActiveObjectsRepository<T extends RawEntity<ID>, ID>
extends PagingAndSortingRepository<T, ID> {
    @Override
    @Transactional
    public List<T> findAll();

    @Override
    @Transactional
    public List<T> findAll(Sort var1);

    @Override
    @Transactional
    public List<T> findAllById(Iterable<ID> var1);

    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> var1);

    @Transactional
    public void flushAll();

    @Transactional
    public void deleteInBatch(Iterable<T> var1);

    @Transactional
    public List<T> findByQuery(Query var1);

    @Transactional
    public long count(Query var1);

    @Override
    @Transactional
    public T save(Map<String, Object> var1);

    @Transactional
    public void saveAllEntities(List<Map<String, Object>> var1);

    @Override
    @Transactional
    public void deleteAll();

    @Override
    @Transactional
    public <S extends T> S save(S var1);

    @Override
    @Transactional
    public Optional<T> findById(ID var1);

    @Override
    @Transactional
    public boolean existsById(ID var1);

    @Override
    @Transactional
    public long count();

    @Override
    @Transactional
    public void deleteById(ID var1);

    @Override
    @Transactional
    public void delete(T var1);

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends T> var1);
}

