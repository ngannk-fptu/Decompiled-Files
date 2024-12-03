/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.persistence.EntityManager
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.StatelessSession
 *  org.hibernate.query.Query
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.QueryBuilder;
import com.atlassian.migration.agent.store.jpa.SessionFactorySupplier;
import com.atlassian.migration.agent.store.jpa.impl.DefaultQueryBuilder;
import com.atlassian.migration.agent.store.jpa.impl.StatelessResults;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;

@ParametersAreNonnullByDefault
public class DefaultEntityManagerTemplate
implements EntityManagerTemplate {
    private final SessionFactorySupplier sessionFactorySupplier;

    public DefaultEntityManagerTemplate(SessionFactorySupplier sessionFactorySupplier) {
        this.sessionFactorySupplier = sessionFactorySupplier;
    }

    @Override
    public <T> T execute(Function<EntityManager, T> action) {
        Session session = ((SessionFactory)this.sessionFactorySupplier.get()).getCurrentSession();
        return action.apply((EntityManager)session);
    }

    @Override
    public void execute(Consumer<EntityManager> action) {
        Session session = ((SessionFactory)this.sessionFactorySupplier.get()).getCurrentSession();
        action.accept((EntityManager)session);
    }

    @Override
    @Nonnull
    public QueryBuilder<Void> query(String jpql) {
        return new DefaultQueryBuilder<Void>(this, em -> em.createQuery(jpql));
    }

    @Override
    @Nonnull
    public <T> QueryBuilder<T> query(Class<T> entityType, String jpql) {
        return new DefaultQueryBuilder(this, em -> em.createQuery(jpql, entityType));
    }

    @Override
    @Nonnull
    public <T> QueryBuilder<T> nativeQuery(Class<T> resultType, String sql) {
        return new DefaultQueryBuilder(this, em -> em.createNativeQuery(sql, resultType));
    }

    @Override
    @Nonnull
    public <T> QueryBuilder<T> namedQuery(Class<T> entityType, String queryName) {
        return new DefaultQueryBuilder(this, em -> em.createNamedQuery(queryName, entityType));
    }

    @Override
    public <T> StatelessResults<T> getStatelessResults(Class<T> entityClass, String query, UnaryOperator<Query<T>> queryBuilder) {
        StatelessSession session = ((SessionFactory)this.sessionFactorySupplier.get()).openStatelessSession();
        try {
            Query statelessQuery = (Query)queryBuilder.apply(session.createQuery(query, entityClass));
            return new StatelessResults(session, statelessQuery);
        }
        catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public void evictAll(List<?> entities) {
        Session session = ((SessionFactory)this.sessionFactorySupplier.get()).getCurrentSession();
        entities.forEach(arg_0 -> ((Session)session).evict(arg_0));
    }
}

