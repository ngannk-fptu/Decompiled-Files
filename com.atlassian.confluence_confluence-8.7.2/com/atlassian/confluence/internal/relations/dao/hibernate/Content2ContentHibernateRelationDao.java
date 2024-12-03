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
import com.atlassian.confluence.internal.relations.dao.Content2ContentRelationEntity;
import com.atlassian.confluence.internal.relations.dao.hibernate.HibernateRelationDao;
import com.atlassian.confluence.internal.relations.dao.hibernate.RelationQueryHelper;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import com.atlassian.core.bean.EntityObject;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.dao.support.DataAccessUtils;

public class Content2ContentHibernateRelationDao
extends HibernateRelationDao<ContentEntityObject, ContentEntityObject> {
    private final SchemaInformationService schemaInformationService;

    public Content2ContentHibernateRelationDao(SessionFactory sessionFactory, SchemaInformationService schemaInformationService) {
        super(sessionFactory);
        this.schemaInformationService = schemaInformationService;
    }

    @Override
    public int getRelationsCount(ContentEntityObject source, ContentEntityObject target, RelationDescriptor<?, ?> relationDescriptor) {
        return DataAccessUtils.intResult(this.getRelationEntities("confluence.relation.content2content.count", source, target, relationDescriptor));
    }

    public @Nullable Content2ContentRelationEntity getRelationEntity(ContentEntityObject source, ContentEntityObject target, RelationDescriptor<?, ?> relationDescriptor) {
        return (Content2ContentRelationEntity)DataAccessUtils.singleResult(this.getRelationEntities("confluence.relation.content2content.get.simple", source, target, relationDescriptor));
    }

    @Override
    public @NonNull List<ContentEntityObject> getSources(RelationQuery<ContentEntityObject> request, int start, int limit) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String queryStr = "select re.sourceContent " + RelationQueryHelper.buildConditionalQueryFor(request, parameters, true);
        return this.fetchList(ContentEntityObject.class, queryStr, parameters, start, limit);
    }

    @Override
    public @NonNull List<ContentEntityObject> getTargets(RelationQuery<ContentEntityObject> request, int start, int limit) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String queryStr = "select re.targetContent " + RelationQueryHelper.buildConditionalQueryFor(request, parameters, false);
        return this.fetchList(ContentEntityObject.class, queryStr, parameters, start, limit);
    }

    @Override
    public int getSourcesCount(RelationQuery<ContentEntityObject> request) {
        return this.getSourceOrTargetCount(request, true);
    }

    @Override
    public int getTargetsCount(RelationQuery<ContentEntityObject> request) {
        return this.getSourceOrTargetCount(request, false);
    }

    @Override
    public int removeAllRelations(RelatableEntity relatableEntity) {
        if (!(relatableEntity instanceof ContentEntityObject)) {
            return 0;
        }
        EntityType objectType = this.sessionFactory.getTypeResolver().getTypeFactory().manyToOne(ContentEntityObject.class.getName());
        return Objects.requireNonNull((Integer)this.hibernate.execute(arg_0 -> Content2ContentHibernateRelationDao.lambda$removeAllRelations$0(relatableEntity, (Type)objectType, arg_0)));
    }

    @Override
    public int removeAllRelations(Iterable<? extends RelatableEntity> relatableEntities) {
        Objects.requireNonNull(relatableEntities);
        List relatableContentEntities = StreamSupport.stream(relatableEntities.spliterator(), false).filter(relatableEntity -> relatableEntity instanceof ContentEntityObject).collect(Collectors.toList());
        int deleted = 0;
        for (List innerRelatableEntities : Lists.partition((List)Lists.newArrayList(relatableContentEntities), (int)500)) {
            deleted += Objects.requireNonNull((Integer)this.hibernate.execute(session -> {
                String deleteStatement = "delete from Content2ContentRelationEntity re where re.sourceContent in (:contents) OR re.targetContent in (:contents)";
                Query query = session.createQuery(deleteStatement);
                query.setParameterList("contents", (Collection)innerRelatableEntities);
                return query.executeUpdate();
            })).intValue();
        }
        return deleted;
    }

    @Override
    public int removeAllRelationsFromEntityWithName(String relationName, RelatableEntity relatableEntity) {
        if (!(relatableEntity instanceof ContentEntityObject)) {
            return 0;
        }
        EntityType objectType = this.sessionFactory.getTypeResolver().getTypeFactory().manyToOne(ContentEntityObject.class.getName());
        return Objects.requireNonNull((Integer)this.hibernate.execute(arg_0 -> Content2ContentHibernateRelationDao.lambda$removeAllRelationsFromEntityWithName$3(relatableEntity, relationName, (Type)objectType, arg_0)));
    }

    @Override
    public int removeAllRelationsFromCurrentAndHistoricalEntities(RelatableEntity relatableEntity) {
        return this.removeAllRelations(relatableEntity);
    }

    @Override
    public int removeAllRelationsFromCurrentAndHistoricalEntities(Iterable<? extends RelatableEntity> relatableEntities) {
        return this.removeAllRelations(relatableEntities);
    }

    public List<Long> getAllRelationIdsForContentInSpace(@NonNull String spaceKey, @NonNull Collection<ConfluenceEntityObject> exclusions, int start, int limit) {
        return (List)this.hibernate.execute(session -> {
            Object queryString = "select    distinct re.id from    Content2ContentRelationEntity re,    SpaceContentEntityObject sceo_source,    SpaceContentEntityObject sceo_target,    Space s where    re.sourceContent = sceo_source    and re.targetContent = sceo_target    and sceo_source.space = s    and sceo_target.space = s    and s.key = :spaceKey ";
            if (exclusions != null && !exclusions.isEmpty()) {
                List exclusionIds = exclusions.stream().map(EntityObject::getId).collect(Collectors.toList());
                InExpressionBuilder sourceExprBuilder = InExpressionBuilder.getNotInExpressionBuilderDefaultLimit((String)"sceo_source.id", (String)"exclusions", (Dialect)this.schemaInformationService.getDialect());
                String sourceExclusionInClause = sourceExprBuilder.convertIdsToInClauseString(exclusionIds);
                InExpressionBuilder targetExprBuilder = InExpressionBuilder.getNotInExpressionBuilderDefaultLimit((String)"sceo_target.id", (String)"exclusions", (Dialect)this.schemaInformationService.getDialect());
                String targetExclusionInClause = targetExprBuilder.convertIdsToInClauseString(exclusionIds);
                queryString = (String)queryString + " and " + sourceExclusionInClause + " and " + targetExclusionInClause;
            }
            Query hQuery = session.createQuery((String)queryString);
            return hQuery.setParameter("spaceKey", (Object)spaceKey).setFirstResult(start).setMaxResults(limit).list();
        });
    }

    protected Content2ContentRelationEntity newRelationEntity(ContentEntityObject source, ContentEntityObject target) {
        Content2ContentRelationEntity relationEntity = new Content2ContentRelationEntity();
        relationEntity.setTargetType(RelatableEntityTypeEnum.getByContentEntityObject(target));
        relationEntity.setSourceType(RelatableEntityTypeEnum.getByContentEntityObject(source));
        return relationEntity;
    }

    private static /* synthetic */ Integer lambda$removeAllRelationsFromEntityWithName$3(RelatableEntity relatableEntity, String relationName, Type objectType, Session session) throws HibernateException {
        String query = "from Content2ContentRelationEntity re where (re.sourceContent = :relatableEntity or re.targetContent = :relatableEntity) and re.relationName = :relationName";
        return SessionHelper.delete(session, query, new Object[]{relatableEntity, relatableEntity, relationName}, new Type[]{objectType, objectType, StringType.INSTANCE});
    }

    private static /* synthetic */ Integer lambda$removeAllRelations$0(RelatableEntity relatableEntity, Type objectType, Session session) throws HibernateException {
        String deleteStatement = "delete from Content2ContentRelationEntity re where re.sourceContent = :content OR re.targetContent = :content";
        Query query = SessionHelper.createQuery(session, deleteStatement, new Object[]{relatableEntity, relatableEntity}, new Type[]{objectType, objectType});
        return query.executeUpdate();
    }
}

