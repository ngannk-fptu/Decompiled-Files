/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.data.querydsl;

import com.querydsl.core.types.Predicate;
import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

public class QuerydslRepositoryInvokerAdapter
implements RepositoryInvoker {
    private final RepositoryInvoker delegate;
    private final QuerydslPredicateExecutor<Object> executor;
    private final Predicate predicate;

    public QuerydslRepositoryInvokerAdapter(RepositoryInvoker delegate, QuerydslPredicateExecutor<Object> executor, Predicate predicate) {
        Assert.notNull((Object)delegate, (String)"Delegate RepositoryInvoker must not be null!");
        Assert.notNull(executor, (String)"QuerydslPredicateExecutor must not be null!");
        this.delegate = delegate;
        this.executor = executor;
        this.predicate = predicate;
    }

    @Override
    public Iterable<Object> invokeFindAll(Pageable pageable) {
        return this.executor.findAll(this.predicate, pageable);
    }

    @Override
    public Iterable<Object> invokeFindAll(Sort sort) {
        return this.executor.findAll(this.predicate, sort);
    }

    @Override
    public boolean hasDeleteMethod() {
        return this.delegate.hasDeleteMethod();
    }

    @Override
    public boolean hasFindAllMethod() {
        return this.delegate.hasFindAllMethod();
    }

    @Override
    public boolean hasFindOneMethod() {
        return this.delegate.hasFindOneMethod();
    }

    @Override
    public boolean hasSaveMethod() {
        return this.delegate.hasSaveMethod();
    }

    @Override
    public void invokeDeleteById(Object id) {
        this.delegate.invokeDeleteById(id);
    }

    @Override
    public <T> Optional<T> invokeFindById(Object id) {
        return this.delegate.invokeFindById(id);
    }

    @Override
    public Optional<Object> invokeQueryMethod(Method method, MultiValueMap<String, ? extends Object> parameters, Pageable pageable, Sort sort) {
        return this.delegate.invokeQueryMethod(method, parameters, pageable, sort);
    }

    @Override
    public <T> T invokeSave(T object) {
        return this.delegate.invokeSave(object);
    }
}

