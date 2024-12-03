/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Session
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.internal.relations.dao.hibernate;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.internal.relations.RelationshipTypeEnum;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Session;
import org.hibernate.query.Query;

class RelationQueryHelper {
    RelationQueryHelper() {
    }

    public static String buildConditionalQueryFor(RelationQuery<?> request, Map<String, Object> parameters, boolean isFetchingSources) {
        RelationshipTypeEnum relationTypeEnum = RelationshipTypeEnum.getRelationshipType(request.getRelationDescriptor());
        StringBuilder query = new StringBuilder("from ").append(RelationQueryHelper.getTableName(relationTypeEnum)).append(" re");
        RelationQueryHelper.joinTables(query, request, isFetchingSources);
        query.append(" where re.relationName = :relationName ");
        parameters.put("relationName", request.getRelationDescriptor().getRelationName());
        RelationQueryHelper.addJoiningCondition(query, request, isFetchingSources);
        if (RelationshipTypeEnum.USER2CONTENT.equals((Object)relationTypeEnum)) {
            if (isFetchingSources) {
                query.append(" and re.targetContent = :content ");
                parameters.put("content", request.getEntity());
            } else {
                query.append(" and re.sourceContent = :user ");
                parameters.put("user", request.getEntity());
                RelationQueryHelper.fillInFilteringClauses(query, parameters, request, isFetchingSources);
            }
        } else if (RelationshipTypeEnum.CONTENT2CONTENT.equals((Object)relationTypeEnum)) {
            query.append(isFetchingSources ? " and re.targetContent = :content " : " and re.sourceContent = :content ");
            parameters.put("content", request.getEntity());
            RelationQueryHelper.fillInFilteringClauses(query, parameters, request, isFetchingSources);
        }
        return query.toString();
    }

    private static String getTableName(RelationshipTypeEnum relationshipTypeEnum) {
        if (RelationshipTypeEnum.CONTENT2CONTENT.equals((Object)relationshipTypeEnum)) {
            return "Content2ContentRelationEntity";
        }
        if (RelationshipTypeEnum.USER2CONTENT.equals((Object)relationshipTypeEnum)) {
            return "User2ContentRelationEntity";
        }
        throw new UnsupportedOperationException("Query generator should not be used for User2User type of relations");
    }

    private static void joinTables(StringBuilder query, RelationQuery<?> request, boolean isFetchingSources) {
        boolean isCeoRequired = RelationQueryHelper.isCeoRequired(request, isFetchingSources);
        boolean isSpacesRequired = RelationQueryHelper.isSpacesRequired(request, isFetchingSources);
        if (isCeoRequired && isSpacesRequired) {
            query.append(", SpaceContentEntityObject ceo ");
        } else if (isCeoRequired) {
            query.append(", ContentEntityObject ceo ");
        }
        if (isSpacesRequired) {
            query.append(", Space s ");
        }
    }

    private static void addJoiningCondition(StringBuilder query, RelationQuery<?> request, boolean isFetchingSources) {
        boolean isCeoRequired = RelationQueryHelper.isCeoRequired(request, isFetchingSources);
        boolean isSpacesRequired = RelationQueryHelper.isSpacesRequired(request, isFetchingSources);
        if (isCeoRequired) {
            query.append(isFetchingSources ? " and ceo = re.sourceContent " : " and ceo = re.targetContent ");
        }
        if (isSpacesRequired) {
            query.append(" and ceo.space = s ");
        }
    }

    private static boolean isCeoRequired(RelationQuery<?> request, boolean isFetchingSources) {
        boolean isCeoRequired = false;
        RelationshipTypeEnum relationshipTypeEnum = RelationshipTypeEnum.getRelationshipType(request.getRelationDescriptor());
        if ((RelationshipTypeEnum.CONTENT2CONTENT.equals((Object)relationshipTypeEnum) || RelationshipTypeEnum.USER2CONTENT.equals((Object)relationshipTypeEnum) && !isFetchingSources) && (!request.isIncludeDeleted() || RelationQueryHelper.isSpacesRequired(request, isFetchingSources))) {
            isCeoRequired = true;
        }
        return isCeoRequired;
    }

    private static boolean isSpacesRequired(RelationQuery<?> request, boolean isFetchingSources) {
        boolean isSpaceRequired = false;
        RelationshipTypeEnum relationshipTypeEnum = RelationshipTypeEnum.getRelationshipType(request.getRelationDescriptor());
        if (RelationshipTypeEnum.CONTENT2CONTENT.equals((Object)relationshipTypeEnum) || RelationshipTypeEnum.USER2CONTENT.equals((Object)relationshipTypeEnum) && !isFetchingSources) {
            isSpaceRequired = request.getSpaceKeysFilter() != null && !request.getSpaceKeysFilter().isEmpty() || request.getSpaceStatusesFilter() != null && !request.getSpaceStatusesFilter().isEmpty();
        }
        return isSpaceRequired;
    }

    private static void fillInFilteringClauses(StringBuilder query, Map<String, Object> parameters, RelationQuery<?> request, boolean isFetchingSources) {
        if (request.getSpaceKeysFilter() != null && !request.getSpaceKeysFilter().isEmpty()) {
            query.append(" and s.key in (:spaceKeys)");
            parameters.put("spaceKeys", request.getSpaceKeysFilter());
        }
        if (request.getSpaceStatusesFilter() != null && !request.getSpaceStatusesFilter().isEmpty()) {
            query.append(" and s.spaceStatus in (:spaceStatuses)");
            parameters.put("spaceStatuses", request.getSpaceStatusesFilter().stream().map(Enum::name).collect(Collectors.toList()));
        }
        if (!request.isIncludeDeleted()) {
            query.append(" and ceo.contentStatus != 'deleted' ");
        }
        if (request.getContentTypeFilters() != null && !request.getContentTypeFilters().isEmpty()) {
            query.append(isFetchingSources ? " and re.sourceType in (:filteredTypes) " : " and re.targetType in (:filteredTypes) ");
            parameters.put("filteredTypes", request.getContentTypeFilters());
        }
    }

    public static void feedQueryParameters(Query<?> query, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
            if (Collection.class.isAssignableFrom(parameterEntry.getValue().getClass())) {
                query = query.setParameterList(parameterEntry.getKey(), (Collection)parameterEntry.getValue());
                continue;
            }
            query = query.setParameter(parameterEntry.getKey(), parameterEntry.getValue());
        }
    }

    public static Query createExportQueryFor(Session session, String spaceKey, @Nullable Collection<ConfluenceEntityObject> exclusions, String queryNameForAll, String queryNameForExclusions) {
        Query hQuery = session.getNamedQuery(exclusions == null || exclusions.isEmpty() ? queryNameForAll : queryNameForExclusions);
        hQuery.setParameter("spaceKey", (Object)spaceKey);
        if (exclusions != null && !exclusions.isEmpty()) {
            hQuery.setParameterList("exclusions", exclusions);
        }
        return hQuery;
    }
}

