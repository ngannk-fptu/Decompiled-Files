/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PagingAndSortingRepository<T, ID>
extends CrudRepository<T, ID> {
    public Iterable<T> findAll(Sort var1);

    public Page<T> findAll(Pageable var1);
}

