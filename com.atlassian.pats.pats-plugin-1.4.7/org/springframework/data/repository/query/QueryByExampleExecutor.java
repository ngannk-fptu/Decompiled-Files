/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.query;

import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface QueryByExampleExecutor<T> {
    public <S extends T> Optional<S> findOne(Example<S> var1);

    public <S extends T> Iterable<S> findAll(Example<S> var1);

    public <S extends T> Iterable<S> findAll(Example<S> var1, Sort var2);

    public <S extends T> Page<S> findAll(Example<S> var1, Pageable var2);

    public <S extends T> long count(Example<S> var1);

    public <S extends T> boolean exists(Example<S> var1);
}

