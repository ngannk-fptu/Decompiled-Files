/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.query.Query
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.internal.relations.dao.hibernate;

import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.dao.RelationDao;
import com.atlassian.confluence.internal.relations.dao.RelationEntity;
import com.atlassian.confluence.internal.relations.dao.hibernate.RelationQueryHelper;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public abstract class HibernateRelationDao<S extends RelatableEntity, T extends RelatableEntity>
implements RelationDao<S, T> {
    protected static final String SOURCE_PARAM_NAME = "source";
    protected static final String TARGET_PARAM_NAME = "target";
    protected static final String RELATION_PARAM_NAME = "relationName";
    protected static final int BATCHING_CHUNK_SIZE = 500;
    protected final HibernateTemplate hibernate;
    protected final SessionFactoryImplementor sessionFactory;

    public HibernateRelationDao(SessionFactory sessionFactory) {
        this.hibernate = new HibernateTemplate(sessionFactory);
        this.sessionFactory = (SessionFactoryImplementor)sessionFactory;
    }

    @Override
    public RelationEntity<S, T> createRelationEntity(S source, T target, RelationDescriptor<?, ?> relationDescriptor) {
        RelationEntity<S, T> relationEntity = this.constructRelationEntity(this.newRelationEntity(source, target), source, target, relationDescriptor);
        this.hibernate.save(relationEntity);
        return relationEntity;
    }

    @Override
    public void removeRelationEntity(RelationEntity<S, T> relationEntity) {
        this.hibernate.delete(relationEntity);
    }

    protected @NonNull List<RelationEntity<S, T>> getRelationEntities(String queryName, S source, T target, RelationDescriptor<?, ?> relationDescriptor) {
        ImmutableMap parameters = ImmutableMap.of((Object)SOURCE_PARAM_NAME, source, (Object)TARGET_PARAM_NAME, target, (Object)RELATION_PARAM_NAME, (Object)relationDescriptor.getRelationName());
        return Objects.requireNonNull((List)this.hibernate.execute(arg_0 -> HibernateRelationDao.lambda$getRelationEntities$0(queryName, (Map)parameters, arg_0)));
    }

    int getSourceOrTargetCount(RelationQuery<?> request, boolean isFetchingSources) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String queryStr = "select count(*) " + RelationQueryHelper.buildConditionalQueryFor(request, parameters, isFetchingSources);
        return DataAccessUtils.intResult((Collection)((Collection)this.hibernate.execute(session -> {
            Query query = session.createQuery(queryStr);
            RelationQueryHelper.feedQueryParameters(query, parameters);
            return query.list();
        })));
    }

    protected abstract RelationEntity<S, T> newRelationEntity(S var1, T var2);

    <X> List<X> fetchList(Class<X> resultType, String queryStr, Map<String, Object> parameters, int start, int limit) {
        return (List)this.hibernate.executeWithNativeSession(session -> {
            Query query = session.createQuery(queryStr, resultType);
            RelationQueryHelper.feedQueryParameters(query, parameters);
            return query.setFirstResult(start).setMaxResults(limit).list();
        });
    }

    protected RelationEntity<S, T> constructRelationEntity(RelationEntity<S, T> relationEntity, S source, T target, RelationDescriptor<?, ?> relationDescriptor) {
        relationEntity.setTargetContent(target);
        relationEntity.setSourceContent(source);
        relationEntity.setRelationName(relationDescriptor.getRelationName());
        relationEntity.setCreationDate(new Date());
        relationEntity.setLastModificationDate(new Date());
        relationEntity.setCreator(AuthenticatedUserThreadLocal.get());
        relationEntity.setLastModifier(AuthenticatedUserThreadLocal.get());
        return relationEntity;
    }

    private static /* synthetic */ List lambda$getRelationEntities$0(String queryName, Map parameters, Session session) throws HibernateException {
        Query query = session.getNamedQuery(queryName);
        RelationQueryHelper.feedQueryParameters(query, parameters);
        return query.list();
    }
}

