/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.Query
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.Page;
import com.atlassian.migration.agent.store.jpa.Pageable;
import com.atlassian.migration.agent.store.jpa.QueryBuilder;
import com.atlassian.migration.agent.store.jpa.impl.DefaultPage;
import com.atlassian.migration.agent.store.jpa.impl.PageRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

class DefaultQueryBuilder<T>
implements QueryBuilder<T> {
    private static final int MIN_ACCEPTED_INT = 0;
    private boolean flush;
    private Map<String, Object> hints = new HashMap<String, Object>();
    private Map<String, Object> namedParams = new HashMap<String, Object>();
    private Map<Integer, Object> idxParams = new HashMap<Integer, Object>();
    private LockModeType lockMode;
    private FlushModeType flushMode;
    private int max = -1;
    private int first = -1;
    private final EntityManagerTemplate tmpl;
    private final Function<EntityManager, Query> queryFactory;

    DefaultQueryBuilder(EntityManagerTemplate tmpl, Function<EntityManager, Query> queryFactory) {
        this.tmpl = tmpl;
        this.queryFactory = queryFactory;
    }

    @Override
    public DefaultQueryBuilder<T> first(int first) {
        this.first = first;
        return this;
    }

    @Override
    public DefaultQueryBuilder<T> max(int max) {
        this.max = max;
        return this;
    }

    @Override
    public DefaultQueryBuilder<T> param(String name, Object value) {
        this.namedParams.put(name, value);
        return this;
    }

    @Override
    public DefaultQueryBuilder<T> param(int idx, Object value) {
        this.idxParams.put(idx, value);
        return this;
    }

    @Override
    public DefaultQueryBuilder<T> hint(String hint, Object value) {
        this.hints.put(hint, value);
        return this;
    }

    @Override
    public DefaultQueryBuilder<T> lock(LockModeType lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public DefaultQueryBuilder<T> flushMode(FlushModeType flushMode) {
        this.flushMode = flushMode;
        return this;
    }

    @Override
    public DefaultQueryBuilder<T> flush(boolean flush) {
        this.flush = flush;
        return this;
    }

    private Query createQuery(EntityManager em) {
        Query query = this.queryFactory.apply(em);
        if (this.first >= 0) {
            query.setFirstResult(this.first);
        }
        if (this.max >= 0) {
            query.setMaxResults(this.max);
        }
        DefaultQueryBuilder.applyIfPresent(this.lockMode, arg_0 -> ((Query)query).setLockMode(arg_0));
        DefaultQueryBuilder.applyIfPresent(this.flushMode, arg_0 -> ((Query)query).setFlushMode(arg_0));
        this.namedParams.forEach((arg_0, arg_1) -> ((Query)query).setParameter(arg_0, arg_1));
        this.idxParams.forEach((arg_0, arg_1) -> ((Query)query).setParameter(arg_0, arg_1));
        this.hints.forEach((arg_0, arg_1) -> ((Query)query).setHint(arg_0, arg_1));
        return query;
    }

    private static <R> void applyIfPresent(R value, Consumer<? super R> consumer) {
        if (Objects.isNull(value)) {
            return;
        }
        consumer.accept(value);
    }

    @Override
    public List<T> list() {
        return this.tmpl.execute(this::prepareList);
    }

    @Override
    public T single() {
        return (T)this.tmpl.execute(this::prepareSingle);
    }

    @Override
    public Optional<T> first() {
        return this.tmpl.execute(this::prepareFirst);
    }

    @Override
    public Stream<T> stream() {
        return this.tmpl.execute(this::prepareStream);
    }

    @Override
    public int update() {
        return this.tmpl.execute(this::prepareUpdate);
    }

    @Override
    public Page<T> page(int pageSize) {
        return this.page(new PageRequest(pageSize), Function.identity());
    }

    @Override
    public Page<T> page(Pageable pageable) {
        return this.page(pageable, Function.identity());
    }

    @Override
    public <P> Page<P> page(int pageSize, Function<T, P> transformFunc) {
        return this.page(new PageRequest(pageSize), transformFunc);
    }

    @Override
    public <P> Page<P> page(Pageable pageable, Function<T, P> transformFunc) {
        return this.tmpl.execute(em -> {
            List entities = this.createQuery((EntityManager)em).setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize()).getResultList();
            List result = entities.stream().map(transformFunc).collect(Collectors.toList());
            return new DefaultPage(result, pageable, pg -> this.page((Pageable)pg, transformFunc));
        });
    }

    private List<T> prepareList(EntityManager em) {
        return this.createQuery(em).getResultList();
    }

    private T prepareSingle(EntityManager em) {
        return (T)this.createQuery(em).getSingleResult();
    }

    private Stream<T> prepareStream(EntityManager em) {
        return this.createQuery(em).getResultStream();
    }

    private int prepareUpdate(EntityManager em) {
        int ret = this.createQuery(em).executeUpdate();
        if (this.flush) {
            em.flush();
        }
        return ret;
    }

    private Optional<T> prepareFirst(EntityManager em) {
        return this.createQuery(em).setMaxResults(1).getResultList().stream().findFirst();
    }
}

