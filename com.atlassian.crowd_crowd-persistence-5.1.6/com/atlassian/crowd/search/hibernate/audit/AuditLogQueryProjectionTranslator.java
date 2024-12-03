/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  com.atlassian.crowd.audit.AuditLogEntity
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.atlassian.crowd.audit.ImmutableAuditLogAuthor
 *  com.atlassian.crowd.audit.ImmutableAuditLogEntity$Builder
 *  com.atlassian.crowd.audit.query.AuditLogChangesetProjection
 *  com.atlassian.crowd.audit.query.AuditLogQuery
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.audit.AuditLogAuthorType;
import com.atlassian.crowd.audit.AuditLogEntity;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.ImmutableAuditLogAuthor;
import com.atlassian.crowd.audit.ImmutableAuditLogEntity;
import com.atlassian.crowd.audit.query.AuditLogChangesetProjection;
import com.atlassian.crowd.audit.query.AuditLogQuery;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class AuditLogQueryProjectionTranslator {
    private static final Collector<CharSequence, ?, String> COLUMN_JOINER = Collectors.joining(", ");

    AuditLogQueryProjectionTranslator() {
    }

    static String selectFor(AuditLogQuery<?> query) {
        AuditLogChangesetProjection projection = (AuditLogChangesetProjection)Preconditions.checkNotNull((Object)query.getProjection());
        switch (projection) {
            case EVENT_TYPE: {
                return AuditLogQueryProjectionTranslator.columns("chset", "eventType");
            }
            case AUTHOR: {
                return AuditLogQueryProjectionTranslator.columns("chset", "authorName", "authorId", "authorType");
            }
            case ENTITY_USER: {
                Preconditions.checkState((!query.getUsers().isEmpty() ? 1 : 0) != 0, (Object)"Need to specify user entity restriction to do user entity projection");
                return AuditLogQueryProjectionTranslator.entityColumns("userentities");
            }
            case ENTITY_GROUP: {
                Preconditions.checkState((!query.getGroups().isEmpty() ? 1 : 0) != 0, (Object)"Need to specify group entity restriction to do group entity projection");
                return AuditLogQueryProjectionTranslator.entityColumns("groupentities");
            }
            case ENTITY_APPLICATION: {
                Preconditions.checkState((!query.getApplications().isEmpty() ? 1 : 0) != 0, (Object)"Need to specify application entity restriction to do application entity projection");
                return AuditLogQueryProjectionTranslator.entityColumns("applicationentities");
            }
            case ENTITY_DIRECTORY: {
                Preconditions.checkState((!query.getDirectories().isEmpty() ? 1 : 0) != 0, (Object)"Need to specify directory entity restriction to do directory entity projection");
                return AuditLogQueryProjectionTranslator.entityColumns("directoryentities");
            }
        }
        throw new IllegalArgumentException("Unsupported projection " + projection);
    }

    static <RESULT> Function<List<Object[]>, List<RESULT>> resultMapperFor(AuditLogQuery<RESULT> query) {
        AuditLogChangesetProjection projection = (AuditLogChangesetProjection)Preconditions.checkNotNull((Object)query.getProjection());
        switch (projection) {
            case EVENT_TYPE: {
                AuditLogQueryProjectionTranslator.checkProjectionReturnType(query, AuditLogEventType.class);
                return AuditLogQueryProjectionTranslator.uniqueChangesets(row -> row[0]);
            }
            case AUTHOR: {
                AuditLogQueryProjectionTranslator.checkProjectionReturnType(query, ImmutableAuditLogAuthor.class);
                return AuditLogQueryProjectionTranslator.uniqueChangesets(row -> new ImmutableAuditLogAuthor((Long)row[1], (String)row[0], (AuditLogAuthorType)row[2]));
            }
            case ENTITY_USER: 
            case ENTITY_GROUP: 
            case ENTITY_APPLICATION: 
            case ENTITY_DIRECTORY: {
                AuditLogQueryProjectionTranslator.checkProjectionReturnType(query, AuditLogEntity.class);
                return AuditLogQueryProjectionTranslator.uniqueChangesets(row -> {
                    ImmutableAuditLogEntity.Builder entity = new ImmutableAuditLogEntity.Builder().setEntityId((Long)row[1]).setEntityName((String)row[0]).setEntityType((AuditLogEntityType)row[2]);
                    return entity.build();
                });
            }
            case SOURCE: {
                throw new IllegalArgumentException("Unsupported projection " + projection);
            }
        }
        throw new IllegalArgumentException("Unsupported projection " + projection);
    }

    private static void checkProjectionReturnType(AuditLogQuery<?> query, Class<?> expected) {
        Class returnType = query.getReturnType();
        AuditLogChangesetProjection projection = query.getProjection();
        Preconditions.checkArgument((boolean)returnType.isAssignableFrom(expected), (String)"Unsupported return type %s for projection %s", (Object)returnType, (Object)projection);
    }

    private static String entityColumns(String entityAlias) {
        return AuditLogQueryProjectionTranslator.columns(entityAlias, "entityName", "entityId", "entityType");
    }

    private static String columns(String alias, String ... columns) {
        return Stream.concat(Arrays.stream(columns).limit(1L).map(column1 -> column1 + " as " + "projection"), Arrays.stream(columns).skip(1L)).map(column -> alias + "." + column).collect(COLUMN_JOINER);
    }

    private static <RESULT> Function<List<Object[]>, List<RESULT>> uniqueChangesets(Function<Object[], RESULT> mapping) {
        return rows -> rows.stream().map(mapping).collect(Collectors.toList());
    }
}

