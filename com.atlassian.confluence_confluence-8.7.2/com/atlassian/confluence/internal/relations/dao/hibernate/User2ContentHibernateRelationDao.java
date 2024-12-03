/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.impl.hibernate.query.InExpressionBuilder
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.collect.Lists
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.query.Query
 *  org.hibernate.type.EntityType
 *  org.hibernate.type.LongType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.internal.relations.dao.hibernate;

import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.atlassian.confluence.impl.hibernate.query.InExpressionBuilder;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.RelatableEntityTypeEnum;
import com.atlassian.confluence.internal.relations.dao.RelationEntity;
import com.atlassian.confluence.internal.relations.dao.User2ContentRelationEntity;
import com.atlassian.confluence.internal.relations.dao.hibernate.HibernateRelationDao;
import com.atlassian.confluence.internal.relations.dao.hibernate.RelationQueryHelper;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.core.bean.EntityObject;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.query.Query;
import org.hibernate.type.EntityType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.dao.support.DataAccessUtils;

public class User2ContentHibernateRelationDao
extends HibernateRelationDao<ConfluenceUser, ContentEntityObject> {
    private final SchemaInformationService schemaInformationService;

    public User2ContentHibernateRelationDao(SessionFactory sessionFactory, SchemaInformationService schemaInformationService) {
        super(sessionFactory);
        this.schemaInformationService = schemaInformationService;
    }

    @Override
    public int getRelationsCount(ConfluenceUser source, ContentEntityObject target, RelationDescriptor<?, ?> relationDescriptor) {
        return DataAccessUtils.intResult(this.getRelationEntities("confluence.relation.user2content.count", source, target, relationDescriptor));
    }

    public @Nullable User2ContentRelationEntity getRelationEntity(ConfluenceUser source, ContentEntityObject target, RelationDescriptor<?, ?> relationDescriptor) {
        return (User2ContentRelationEntity)DataAccessUtils.singleResult(this.getRelationEntities("confluence.relation.user2content.get.simple", source, target, relationDescriptor));
    }

    @Override
    public @NonNull List<ConfluenceUser> getSources(RelationQuery<ContentEntityObject> request, int start, int limit) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String queryStr = "select re.sourceContent " + RelationQueryHelper.buildConditionalQueryFor(request, parameters, true);
        return this.fetchList(ConfluenceUser.class, queryStr, parameters, start, limit);
    }

    @Override
    public @NonNull List<ContentEntityObject> getTargets(RelationQuery<ConfluenceUser> request, int start, int limit) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String queryStr = "select re.targetContent " + RelationQueryHelper.buildConditionalQueryFor(request, parameters, false);
        return this.fetchList(ContentEntityObject.class, queryStr, parameters, start, limit);
    }

    @Override
    public int getSourcesCount(RelationQuery<ContentEntityObject> request) {
        return this.getSourceOrTargetCount(request, true);
    }

    @Override
    public int getTargetsCount(RelationQuery<ConfluenceUser> request) {
        return this.getSourceOrTargetCount(request, false);
    }

    @Override
    public int removeAllRelations(RelatableEntity relatableEntity) {
        String query = relatableEntity instanceof ContentEntityObject ? "delete from User2ContentRelationEntity re where re.targetContent = :content" : "delete from User2ContentRelationEntity re where re.sourceContent = :content";
        Class entityType = relatableEntity instanceof ContentEntityObject ? ContentEntityObject.class : ConfluenceUserImpl.class;
        EntityType objectType = this.sessionFactory.getTypeResolver().getTypeFactory().manyToOne(entityType.getName());
        return Objects.requireNonNull((Integer)this.hibernate.execute(arg_0 -> User2ContentHibernateRelationDao.lambda$removeAllRelations$0(query, relatableEntity, (Type)objectType, arg_0)));
    }

    @Override
    public int removeAllRelations(Iterable<? extends RelatableEntity> relatableEntities) {
        Objects.requireNonNull(relatableEntities);
        String[] deleteStatements = new String[]{"delete from User2ContentRelationEntity re where re.targetContent in (:contents)", "delete from User2ContentRelationEntity re where re.sourceContent in (:contents)"};
        List contentRelatableEntities = StreamSupport.stream(relatableEntities.spliterator(), false).filter(relatableEntity -> relatableEntity instanceof ContentEntityObject).collect(Collectors.toList());
        List nonContentRelatableEntities = StreamSupport.stream(relatableEntities.spliterator(), false).filter(relatableEntity -> !(relatableEntity instanceof ContentEntityObject)).collect(Collectors.toList());
        ArrayList params = Lists.newArrayList((Object[])new List[]{contentRelatableEntities, nonContentRelatableEntities});
        int deleted = 0;
        for (int i = 0; i < params.size(); ++i) {
            String deleteStatement = deleteStatements[i];
            List toDeleteEntities = (List)params.get(i);
            for (List innerRelatableEntities : Lists.partition((List)Lists.newArrayList((Iterable)toDeleteEntities), (int)500)) {
                deleted += Objects.requireNonNull((Integer)this.hibernate.execute(session -> {
                    Query query = session.createQuery(deleteStatement);
                    query.setParameterList("contents", (Collection)innerRelatableEntities);
                    return query.executeUpdate();
                })).intValue();
            }
        }
        return deleted;
    }

    @Override
    public int removeAllRelationsFromEntityWithName(String relationName, RelatableEntity relatableEntity) {
        String query = relatableEntity instanceof ConfluenceUser ? "from User2ContentRelationEntity re where re.sourceContent = :relatableEntity and re.relationName = :relationName" : "from User2ContentRelationEntity re where re.targetContent = :relatableEntity and re.relationName = :relationName";
        Class entityType = relatableEntity instanceof ConfluenceUser ? ConfluenceUserImpl.class : ContentEntityObject.class;
        EntityType objectType = this.sessionFactory.getTypeResolver().getTypeFactory().manyToOne(entityType.getName());
        return Objects.requireNonNull((Integer)this.hibernate.execute(arg_0 -> User2ContentHibernateRelationDao.lambda$removeAllRelationsFromEntityWithName$4(query, relatableEntity, relationName, (Type)objectType, arg_0)));
    }

    @Override
    public int removeAllRelationsFromCurrentAndHistoricalEntities(RelatableEntity relatableEntity) {
        ContentEntityObject ceo;
        int deleted = this.removeAllRelations(relatableEntity);
        if (relatableEntity instanceof ContentEntityObject && (ceo = (ContentEntityObject)relatableEntity).isLatestVersion()) {
            deleted += Objects.requireNonNull((Integer)this.hibernate.execute(session -> {
                String statement = "from User2ContentRelationEntity re where re.targetContent.originalVersion.id = :content and re.targetContent.contentStatus = 'current'";
                return SessionHelper.delete(session, statement, new Object[]{ceo.getId()}, new Type[]{LongType.INSTANCE});
            })).intValue();
        }
        return deleted;
    }

    @Override
    public int removeAllRelationsFromCurrentAndHistoricalEntities(Iterable<? extends RelatableEntity> relatableEntities) {
        Objects.requireNonNull(relatableEntities);
        int deleted = this.removeAllRelations(relatableEntities);
        Set innerRelatableEntities = StreamSupport.stream(relatableEntities.spliterator(), false).filter(relatableEntity -> relatableEntity instanceof ContentEntityObject && ((ContentEntityObject)relatableEntity).isLatestVersion()).collect(Collectors.toSet());
        if (!innerRelatableEntities.isEmpty()) {
            for (List partitionRelatableEntities : Lists.partition((List)Lists.newArrayList(innerRelatableEntities), (int)500)) {
                deleted += Objects.requireNonNull((Integer)this.hibernate.execute(session -> {
                    String statement = "from User2ContentRelationEntity re where re.targetContent.originalVersion in (:contents) and re.targetContent.contentStatus = 'current'";
                    Query query = session.createQuery(statement, User2ContentRelationEntity.class);
                    query.setParameterList("contents", (Collection)partitionRelatableEntities);
                    List resultList = query.list();
                    int size = resultList.size();
                    resultList.forEach(arg_0 -> ((Session)session).delete(arg_0));
                    return size;
                })).intValue();
            }
        }
        return deleted;
    }

    public List<Long> getAllRelationIdsForContentInSpace(@NonNull String spaceKey, @NonNull Collection<ConfluenceEntityObject> exclusions, int start, int limit) {
        return (List)this.hibernate.execute(session -> {
            Object queryString = "select    re.id from    User2ContentRelationEntity re, SpaceContentEntityObject sceo where    re.targetContent = sceo    and (case when sceo.originalVersion is not null then sceo.originalVersion.id else sceo.id end) in (        select            ceo.id        from            SpaceContentEntityObject ceo, Space sp        where            ceo.space = sp            and sp.key = :spaceKey ";
            if (exclusions != null && !exclusions.isEmpty()) {
                List exclusionIds = exclusions.stream().map(EntityObject::getId).collect(Collectors.toList());
                InExpressionBuilder expr = InExpressionBuilder.getNotInExpressionBuilderDefaultLimit((String)"ceo.id", (String)"exclusions", (Dialect)this.schemaInformationService.getDialect());
                String exclusionInClause = expr.convertIdsToInClauseString(exclusionIds);
                queryString = (String)queryString + " and " + exclusionInClause + ")";
            } else {
                queryString = (String)queryString + ")";
            }
            Query hQuery = session.createQuery((String)queryString);
            return hQuery.setParameter("spaceKey", (Object)spaceKey).setFirstResult(start).setMaxResults(limit).list();
        });
    }

    protected User2ContentRelationEntity newRelationEntity(ConfluenceUser source, ContentEntityObject target) {
        User2ContentRelationEntity relationEntity = new User2ContentRelationEntity();
        relationEntity.setTargetType(RelatableEntityTypeEnum.getByContentEntityObject(target));
        relationEntity.setSourceType(RelatableEntityTypeEnum.USER);
        return relationEntity;
    }

    @Override
    protected RelationEntity<ConfluenceUser, ContentEntityObject> constructRelationEntity(RelationEntity<ConfluenceUser, ContentEntityObject> relationEntity, ConfluenceUser source, ContentEntityObject target, RelationDescriptor<?, ?> relationDescriptor) {
        relationEntity.setTargetContent(target);
        relationEntity.setSourceContent(source);
        relationEntity.setRelationName(relationDescriptor.getRelationName());
        relationEntity.setCreationDate(new Date());
        relationEntity.setLastModificationDate(new Date());
        if (relationEntity.getRelationName().equals("collaborator") || relationEntity.getRelationName().equals("contributor")) {
            relationEntity.setCreator(source);
            relationEntity.setLastModifier(source);
        } else {
            relationEntity.setCreator(AuthenticatedUserThreadLocal.get());
            relationEntity.setLastModifier(AuthenticatedUserThreadLocal.get());
        }
        return relationEntity;
    }

    private static /* synthetic */ Integer lambda$removeAllRelationsFromEntityWithName$4(String query, RelatableEntity relatableEntity, String relationName, Type objectType, Session session) throws HibernateException {
        return SessionHelper.delete(session, query, new Object[]{relatableEntity, relationName}, new Type[]{objectType, StringType.INSTANCE});
    }

    private static /* synthetic */ Integer lambda$removeAllRelations$0(String query, RelatableEntity relatableEntity, Type objectType, Session session) throws HibernateException {
        Query hibernateQuery = session.createQuery(query);
        hibernateQuery.setParameter("content", (Object)relatableEntity, objectType);
        return hibernateQuery.executeUpdate();
    }
}

