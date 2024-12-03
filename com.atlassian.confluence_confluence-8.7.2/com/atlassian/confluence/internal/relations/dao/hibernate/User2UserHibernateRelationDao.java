/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.google.common.collect.Lists
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.hibernate.type.EntityType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.internal.relations.dao.hibernate;

import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.RelatableEntityTypeEnum;
import com.atlassian.confluence.internal.relations.dao.User2UserRelationEntity;
import com.atlassian.confluence.internal.relations.dao.hibernate.HibernateRelationDao;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.EntityType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.dao.support.DataAccessUtils;

public class User2UserHibernateRelationDao
extends HibernateRelationDao<ConfluenceUser, ConfluenceUser> {
    public User2UserHibernateRelationDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public int getRelationsCount(ConfluenceUser source, ConfluenceUser target, RelationDescriptor<?, ?> relationDescriptor) {
        return DataAccessUtils.intResult(this.getRelationEntities("confluence.relation.user2user.count", source, target, relationDescriptor));
    }

    public @Nullable User2UserRelationEntity getRelationEntity(ConfluenceUser source, ConfluenceUser target, RelationDescriptor<?, ?> relationDescriptor) {
        return (User2UserRelationEntity)DataAccessUtils.singleResult(this.getRelationEntities("confluence.relation.user2user.get.simple", source, target, relationDescriptor));
    }

    @Override
    public @NonNull List<ConfluenceUser> getSources(RelationQuery<ConfluenceUser> request, int start, int limit) {
        return Objects.requireNonNull((List)this.hibernate.execute(session -> {
            Query query = session.createNamedQuery("confluence.relation.user2user.get.sources", ConfluenceUser.class);
            query.setParameter("target", (Object)request.getEntity());
            query.setParameter("relationName", (Object)request.getRelationDescriptor().getRelationName());
            return query.setFirstResult(start).setMaxResults(limit).list();
        }));
    }

    @Override
    public @NonNull List<ConfluenceUser> getTargets(RelationQuery<ConfluenceUser> request, int start, int limit) {
        return Objects.requireNonNull((List)this.hibernate.execute(session -> {
            Query query = session.createNamedQuery("confluence.relation.user2user.get.targets", ConfluenceUser.class);
            query.setParameter("source", (Object)request.getEntity());
            query.setParameter("relationName", (Object)request.getRelationDescriptor().getRelationName());
            return query.setFirstResult(start).setMaxResults(limit).list();
        }));
    }

    @Override
    public int getSourcesCount(RelationQuery<ConfluenceUser> request) {
        return DataAccessUtils.intResult((Collection)((Collection)this.hibernate.execute(session -> {
            Query query = session.getNamedQuery("confluence.relation.user2user.get.sources.count");
            query.setParameter("target", (Object)request.getEntity());
            query.setParameter("relationName", (Object)request.getRelationDescriptor().getRelationName());
            return query.list();
        })));
    }

    @Override
    public int getTargetsCount(RelationQuery<ConfluenceUser> request) {
        return DataAccessUtils.intResult((Collection)((Collection)this.hibernate.execute(session -> {
            Query query = session.getNamedQuery("confluence.relation.user2user.get.targets.count");
            query.setParameter("source", (Object)request.getEntity());
            query.setParameter("relationName", (Object)request.getRelationDescriptor().getRelationName());
            return query.list();
        })));
    }

    @Override
    public int removeAllRelations(RelatableEntity relatableEntity) {
        if (!(relatableEntity instanceof ConfluenceUser)) {
            return 0;
        }
        EntityType objectType = this.sessionFactory.getTypeResolver().getTypeFactory().manyToOne(ConfluenceUserImpl.class.getName());
        return Objects.requireNonNull((Integer)this.hibernate.execute(arg_0 -> User2UserHibernateRelationDao.lambda$removeAllRelations$4(relatableEntity, (Type)objectType, arg_0)));
    }

    @Override
    public int removeAllRelations(Iterable<? extends RelatableEntity> relatableEntities) {
        Objects.requireNonNull(relatableEntities);
        Iterable filteredRelatableEntities = StreamSupport.stream(relatableEntities.spliterator(), false).filter(relatableEntity -> relatableEntity instanceof ConfluenceUser).collect(Collectors.toList());
        int deleted = 0;
        for (List innerRelatableEntities : Lists.partition((List)Lists.newArrayList((Iterable)filteredRelatableEntities), (int)500)) {
            deleted += Objects.requireNonNull((Integer)this.hibernate.execute(session -> {
                String deleteStatement = "delete from User2UserRelationEntity re where re.sourceContent in (:contents) OR re.targetContent in (:contents)";
                Query query = session.createQuery(deleteStatement);
                query.setParameterList("contents", (Collection)innerRelatableEntities);
                return query.executeUpdate();
            })).intValue();
        }
        return deleted;
    }

    @Override
    public int removeAllRelationsFromEntityWithName(String relationName, RelatableEntity relatableEntity) {
        if (!(relatableEntity instanceof ConfluenceUser)) {
            return 0;
        }
        String query = "from User2UserRelationEntity re where (re.sourceContent = :relatableEntity or re.targetContent = :relatableEntity) and re.relationName = :relationName";
        EntityType objectType = this.sessionFactory.getTypeResolver().getTypeFactory().manyToOne(ConfluenceUserImpl.class.getName());
        return Objects.requireNonNull((Integer)this.hibernate.execute(arg_0 -> User2UserHibernateRelationDao.lambda$removeAllRelationsFromEntityWithName$7(query, relatableEntity, relationName, (Type)objectType, arg_0)));
    }

    @Override
    public int removeAllRelationsFromCurrentAndHistoricalEntities(RelatableEntity relatableEntity) {
        return this.removeAllRelations(relatableEntity);
    }

    @Override
    public int removeAllRelationsFromCurrentAndHistoricalEntities(Iterable<? extends RelatableEntity> relatableEntities) {
        return this.removeAllRelations(relatableEntities);
    }

    protected User2UserRelationEntity newRelationEntity(ConfluenceUser source, ConfluenceUser target) {
        User2UserRelationEntity relationEntity = new User2UserRelationEntity();
        relationEntity.setTargetType(RelatableEntityTypeEnum.USER);
        relationEntity.setSourceType(RelatableEntityTypeEnum.USER);
        return relationEntity;
    }

    private static /* synthetic */ Integer lambda$removeAllRelationsFromEntityWithName$7(String query, RelatableEntity relatableEntity, String relationName, Type objectType, Session session) throws HibernateException {
        return SessionHelper.delete(session, query, new Object[]{relatableEntity, relatableEntity, relationName}, new Type[]{objectType, objectType, StringType.INSTANCE});
    }

    private static /* synthetic */ Integer lambda$removeAllRelations$4(RelatableEntity relatableEntity, Type objectType, Session session) throws HibernateException {
        Query query = SessionHelper.createQuery(session, "delete from User2UserRelationEntity re where re.sourceContent = :content or re.targetContent = :content", new Object[]{relatableEntity, relatableEntity}, new Type[]{objectType, objectType});
        return query.executeUpdate();
    }
}

