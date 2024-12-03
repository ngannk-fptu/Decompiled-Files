/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.persistence.EntityManager
 *  org.hibernate.Session
 *  org.hibernate.query.Query
 */
package com.atlassian.migration.agent.store.jpa;

import com.atlassian.migration.agent.store.jpa.QueryBuilder;
import com.atlassian.migration.agent.store.jpa.impl.StatelessResults;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;

@ParametersAreNonnullByDefault
public interface EntityManagerTemplate {
    public <T> T execute(Function<EntityManager, T> var1);

    public void execute(Consumer<EntityManager> var1);

    @Nonnull
    public QueryBuilder<Void> query(String var1);

    @Nonnull
    public <T> QueryBuilder<T> query(Class<T> var1, String var2);

    @Nonnull
    public <T> QueryBuilder<T> nativeQuery(Class<T> var1, String var2);

    @Nonnull
    public <T> QueryBuilder<T> namedQuery(Class<T> var1, String var2);

    public <T> StatelessResults<T> getStatelessResults(Class<T> var1, String var2, UnaryOperator<Query<T>> var3);

    default public <T> void persist(T entity) {
        this.execute((EntityManager em) -> em.persist(entity));
    }

    default public <T> void merge(T entity) {
        this.execute((EntityManager em) -> {
            if (!em.contains(entity)) {
                em.merge(entity);
            }
        });
    }

    default public <T> void saveOrUpdate(T entity) {
        this.execute((EntityManager em) -> ((Session)em.getDelegate()).saveOrUpdate(entity));
    }

    default public <T> T refresh(T entity) {
        return (T)this.execute((EntityManager em) -> {
            em.refresh(entity);
            return entity;
        });
    }

    default public void flush() {
        this.execute(EntityManager::flush);
    }

    default public void clear() {
        this.execute(EntityManager::clear);
    }

    public void evictAll(List<?> var1);
}

