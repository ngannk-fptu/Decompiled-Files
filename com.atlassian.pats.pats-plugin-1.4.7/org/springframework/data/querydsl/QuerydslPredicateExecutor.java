/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface QuerydslPredicateExecutor<T> {
    public Optional<T> findOne(Predicate var1);

    public Iterable<T> findAll(Predicate var1);

    public Iterable<T> findAll(Predicate var1, Sort var2);

    public Iterable<T> findAll(Predicate var1, OrderSpecifier<?> ... var2);

    public Iterable<T> findAll(OrderSpecifier<?> ... var1);

    public Page<T> findAll(Predicate var1, Pageable var2);

    public long count(Predicate var1);

    public boolean exists(Predicate var1);
}

