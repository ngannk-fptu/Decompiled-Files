/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.relations;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.internal.relations.RelationUtils;
import com.atlassian.confluence.internal.relations.RelationshipTypeEnum;
import com.atlassian.confluence.internal.relations.dao.RelationDao;
import com.atlassian.confluence.internal.relations.dao.RelationEntity;
import com.atlassian.confluence.internal.relations.dao.hibernate.Content2ContentHibernateRelationDao;
import com.atlassian.confluence.internal.relations.dao.hibernate.User2ContentHibernateRelationDao;
import com.atlassian.confluence.internal.relations.dao.hibernate.User2UserHibernateRelationDao;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DefaultRelationManager
implements RelationManager {
    private static final int RELATION_REQUEST_LIMIT = 100;
    private final Map<RelationshipTypeEnum, RelationDao> relationDaos;

    public DefaultRelationManager(@NonNull Content2ContentHibernateRelationDao content2ContentHibernateRelationDao, @NonNull User2ContentHibernateRelationDao user2ContentHibernateRelationDao, @NonNull User2UserHibernateRelationDao user2UserHibernateRelationDao) {
        this.relationDaos = ImmutableMap.of((Object)((Object)RelationshipTypeEnum.CONTENT2CONTENT), (Object)content2ContentHibernateRelationDao, (Object)((Object)RelationshipTypeEnum.USER2CONTENT), (Object)user2ContentHibernateRelationDao, (Object)((Object)RelationshipTypeEnum.USER2USER), (Object)user2UserHibernateRelationDao);
    }

    @Override
    public boolean isRelated(RelatableEntity source, RelatableEntity target, RelationDescriptor relationDescriptor) {
        RelationDao dao = this.getRelationDao(relationDescriptor, false);
        return dao != null && dao.getRelationsCount(source, target, relationDescriptor) > 0;
    }

    @Override
    public RelationEntity addRelation(RelatableEntity source, RelatableEntity target, RelationDescriptor relationDescriptor) {
        ValidationResult validationResult = RelationUtils.validateAgainstApiModel(source, target, relationDescriptor);
        validationResult.throwIfNotSuccessful(RelationUtils.extractError(validationResult, "Validation error: relation is not permitted"));
        RelationEntity<RelatableEntity, RelatableEntity> relationEntity = this.getRelationDao(relationDescriptor).getRelationEntity(source, target, relationDescriptor);
        if (relationEntity == null) {
            relationEntity = this.getRelationDao(relationDescriptor).createRelationEntity(source, target, relationDescriptor);
        }
        return relationEntity;
    }

    @Override
    public void removeRelation(RelatableEntity source, RelatableEntity target, RelationDescriptor relationDescriptor) {
        RelationEntity<RelatableEntity, RelatableEntity> relationEntity = this.getRelationDao(relationDescriptor).getRelationEntity(source, target, relationDescriptor);
        if (relationEntity != null) {
            this.getRelationDao(relationDescriptor).removeRelationEntity(relationEntity);
        }
    }

    @Override
    public int removeAllRelations(RelatableEntity relatableEntity) {
        int deleted = 0;
        for (RelationDao relationDao : this.relationDaos.values()) {
            deleted += relationDao.removeAllRelations(relatableEntity);
        }
        return deleted;
    }

    @Override
    public int removeAllRelationsFromEntityWithType(RelationDescriptor relationDescriptor, RelatableEntity relatableEntity) {
        RelationshipTypeEnum relationshipType = RelationshipTypeEnum.getRelationshipType(relationDescriptor);
        RelationDao relationDao = this.relationDaos.get((Object)relationshipType);
        if (relationDao == null) {
            return 0;
        }
        return relationDao.removeAllRelationsFromEntityWithName(relationDescriptor.getRelationName(), relatableEntity);
    }

    @Override
    public int removeAllRelationsFromCurrentAndHistoricalEntities(RelatableEntity relatableEntity) {
        int deleted = 0;
        for (RelationDao relationDao : this.relationDaos.values()) {
            deleted += relationDao.removeAllRelationsFromCurrentAndHistoricalEntities(relatableEntity);
        }
        return deleted;
    }

    @Override
    public int removeAllRelationsFromCurrentAndHistoricalEntities(Iterable<? extends RelatableEntity> readableEntities) {
        int deleted = 0;
        for (RelationDao relationDao : this.relationDaos.values()) {
            deleted += relationDao.removeAllRelationsFromCurrentAndHistoricalEntities(readableEntities);
        }
        return deleted;
    }

    @Override
    public @NonNull PageResponse<RelatableEntity> getSources(RelationQuery request, LimitedRequest pageRequest) {
        int totalNumber = this.getSourcesCount(request);
        PageResponseImpl result = null;
        if (totalNumber > pageRequest.getStart()) {
            List entities = this.getRelationDao(request).getSources(request, pageRequest.getStart(), pageRequest.getLimit());
            result = PageResponseImpl.from(entities, (totalNumber > pageRequest.getStart() + pageRequest.getLimit() ? 1 : 0) != 0).build();
        } else {
            result = PageResponseImpl.empty((boolean)false);
        }
        return result;
    }

    @Override
    public @NonNull PageResponse<RelatableEntity> getTargets(RelationQuery request, LimitedRequest pageRequest) {
        int totalNumber = this.getTargetsCount(request);
        PageResponseImpl result = null;
        if (totalNumber > pageRequest.getStart()) {
            List entities = this.getRelationDao(request).getTargets(request, pageRequest.getStart(), pageRequest.getLimit());
            result = PageResponseImpl.from(entities, (totalNumber > pageRequest.getStart() + pageRequest.getLimit() ? 1 : 0) != 0).build();
        } else {
            result = PageResponseImpl.empty((boolean)false);
        }
        return result;
    }

    @Override
    public int getSourcesCount(RelationQuery request) {
        return this.getRelationDao(request).getSourcesCount(request);
    }

    @Override
    public int getTargetsCount(RelationQuery request) {
        return this.getRelationDao(request).getTargetsCount(request);
    }

    @Override
    public void moveRelationsToContent(RelatableEntity fromRelatableEntity, RelatableEntity toRelatableEntity, RelationDescriptor descriptor) {
        PageResponse<RelatableEntity> pageResponse;
        RelationQuery fromRelationQuery = RelationQuery.create(fromRelatableEntity, descriptor).build();
        int startIndex = 0;
        do {
            pageResponse = this.getSources(fromRelationQuery, LimitedRequestImpl.create((int)startIndex, (int)100, (int)100));
            List collaborators = pageResponse.getResults();
            for (RelatableEntity collaborator : collaborators) {
                this.addRelation(collaborator, toRelatableEntity, descriptor);
            }
            startIndex += pageResponse.size();
        } while (pageResponse.hasMore());
        this.removeAllRelationsFromEntityWithType(descriptor, fromRelatableEntity);
    }

    private RelationDao getRelationDao(RelationQuery request) {
        return this.getRelationDao(request.getRelationDescriptor());
    }

    private RelationDao getRelationDao(RelationDescriptor relationDescriptor) {
        return this.getRelationDao(relationDescriptor, true);
    }

    private RelationDao getRelationDao(RelationDescriptor relationDescriptor, boolean throwOnError) {
        RelationshipTypeEnum relationshipTypeEnum = RelationshipTypeEnum.getRelationshipType(relationDescriptor, throwOnError);
        return this.relationDaos.get((Object)relationshipTypeEnum);
    }
}

