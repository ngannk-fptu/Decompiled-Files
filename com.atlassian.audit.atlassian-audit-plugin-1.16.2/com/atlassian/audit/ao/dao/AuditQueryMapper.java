/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditQuery$AuditResourceIdentifier
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.api.util.pagination.PageRequest$Builder
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.Query
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.ao.dao.SearchTokenizer;
import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.Query;

public class AuditQueryMapper {
    public Query map(@Nonnull AuditQuery auditQuery, @Nonnull PageRequest<AuditEntityCursor> pageRequest) {
        Objects.requireNonNull(auditQuery, "auditQuery");
        Objects.requireNonNull(pageRequest, "pageRequest");
        WhereClause clause = this.buildWhereClause(auditQuery, pageRequest);
        return Query.select((String)AuditQueryMapper.getColumnNames()).where(clause.getClause(), clause.getParams()).limit(pageRequest.getLimit() + 1).order(String.format("%s DESC, %s DESC", "ENTITY_TIMESTAMP", "ID")).offset(pageRequest.getOffset());
    }

    public Query map(@Nonnull AuditQuery auditQuery) {
        Objects.requireNonNull(auditQuery, "auditQuery");
        WhereClause clause = this.buildWhereClause(auditQuery, (PageRequest<AuditEntityCursor>)new PageRequest.Builder().build());
        return Query.select((String)AuditQueryMapper.getColumnNames()).where(clause.getClause(), clause.getParams());
    }

    private static String getColumnNames() {
        return String.join((CharSequence)",", Arrays.asList("ACTION", "AREA", "ATTRIBUTES", "CATEGORY", "CHANGE_VALUES", "LEVEL", "METHOD", "SYSTEM_INFO", "NODE", "RESOURCES", "SOURCE", "ENTITY_TIMESTAMP", "USER_ID", "USER_NAME", "USER_TYPE"));
    }

    private WhereClause buildWhereClause(@Nonnull AuditQuery auditQuery, @Nonnull PageRequest<AuditEntityCursor> pageRequest) {
        WhereClauseBuilder whereClauseBuilder = WhereClause.builder();
        pageRequest.getCursor().ifPresent(cursor -> whereClauseBuilder.and(WhereClause.builder().lessThan("ENTITY_TIMESTAMP", cursor.getTimestamp().toEpochMilli(), false).or(WhereClause.builder().eq("ENTITY_TIMESTAMP", cursor.getTimestamp().toEpochMilli()).and(WhereClause.lessThan("ID", cursor.getId(), false)).build()).build()));
        whereClauseBuilder.and(WhereClause.in("ACTION", auditQuery.getActions())).and(WhereClause.in("CATEGORY", auditQuery.getCategories())).and(this.likeClause(auditQuery)).and(WhereClause.in("USER_ID", auditQuery.getUserIds())).and(WhereClause.between("ENTITY_TIMESTAMP", auditQuery.getFrom().orElse(Instant.EPOCH).toEpochMilli(), auditQuery.getTo().orElse(Instant.now()).toEpochMilli()));
        this.buildWhereClauseForResources(auditQuery.getResources(), whereClauseBuilder);
        if (auditQuery.getMinId().isPresent()) {
            whereClauseBuilder.and(WhereClause.greaterThan("ID", (Long)auditQuery.getMinId().get(), true));
        }
        if (auditQuery.getMaxId().isPresent()) {
            whereClauseBuilder.and(WhereClause.lessThan("ID", (Long)auditQuery.getMaxId().get(), true));
        }
        return whereClauseBuilder.build();
    }

    private void buildWhereClauseForResources(@Nonnull Set<AuditQuery.AuditResourceIdentifier> resources, WhereClauseBuilder whereClauseBuilder) {
        Map typeToAuditResourceIdentifierSetMap = resources.stream().collect(Collectors.groupingBy(AuditQuery.AuditResourceIdentifier::getType, Collectors.toSet()));
        typeToAuditResourceIdentifierSetMap.values().forEach(auditResourceIdentifierSet -> whereClauseBuilder.and(WhereClause.builder().or(this.buildWhereClauseForResourceColumn((Set<AuditQuery.AuditResourceIdentifier>)auditResourceIdentifierSet, "PRIMARY_RESOURCE_ID", "PRIMARY_RESOURCE_TYPE")).or(this.buildWhereClauseForResourceColumn((Set<AuditQuery.AuditResourceIdentifier>)auditResourceIdentifierSet, "SECONDARY_RESOURCE_ID", "SECONDARY_RESOURCE_TYPE")).or(this.buildWhereClauseForResourceColumn((Set<AuditQuery.AuditResourceIdentifier>)auditResourceIdentifierSet, "RESOURCE_ID_3", "RESOURCE_TYPE_3")).or(this.buildWhereClauseForResourceColumn((Set<AuditQuery.AuditResourceIdentifier>)auditResourceIdentifierSet, "RESOURCE_ID_4", "RESOURCE_TYPE_4")).or(this.buildWhereClauseForResourceColumn((Set<AuditQuery.AuditResourceIdentifier>)auditResourceIdentifierSet, "RESOURCE_ID_5", "RESOURCE_TYPE_5")).build()));
    }

    private WhereClause buildWhereClauseForResourceColumn(@Nonnull Set<AuditQuery.AuditResourceIdentifier> resourceIdentifiers, @Nonnull String resourceIdColumn, @Nonnull String resourceTypeColumn) {
        WhereClauseBuilder builder = WhereClause.builder();
        resourceIdentifiers.forEach(r -> builder.or(WhereClause.builder().eq(resourceIdColumn, r.getId()).and(WhereClause.eq(resourceTypeColumn, r.getType())).build()));
        return builder.build();
    }

    private WhereClause likeClause(@Nonnull AuditQuery auditQuery) {
        ImmutableSet<String> tokens = new SearchTokenizer().put(auditQuery.getSearchText().orElse(null)).getTokens();
        WhereClauseBuilder likeClause = WhereClause.builder();
        tokens.forEach(t -> likeClause.or(WhereClause.like("SEARCH_STRING", "%" + t + "%")));
        return likeClause.build();
    }

    private static class WhereClauseBuilder {
        private StringBuilder clause = new StringBuilder();
        private List<Object> params = new ArrayList<Object>();

        private WhereClauseBuilder() {
        }

        WhereClause build() {
            return new WhereClause(this.clause.toString(), this.params);
        }

        @Nonnull
        WhereClauseBuilder and(@Nonnull WhereClause subClause) {
            Objects.requireNonNull(subClause, "subClause");
            if (!subClause.isEmpty()) {
                this.mayAppendAnd();
                this.clause.append(" (").append(subClause.getClause()).append(") ");
                this.params.addAll(Arrays.asList(subClause.getParams()));
            }
            return this;
        }

        @Nonnull
        WhereClauseBuilder or(@Nonnull WhereClause subClause) {
            Objects.requireNonNull(subClause, "subClause");
            if (!subClause.isEmpty()) {
                this.mayAppendOr();
                this.clause.append(" (").append(subClause.getClause()).append(") ");
                this.params.addAll(Arrays.asList(subClause.getParams()));
            }
            return this;
        }

        @Nonnull
        <T> WhereClauseBuilder eq(@Nonnull String column, @Nullable T value) {
            Objects.requireNonNull(column, "column");
            if (value != null) {
                this.append(column, value);
            }
            return this;
        }

        @Nonnull
        <T> WhereClauseBuilder in(@Nonnull String column, @Nullable Set<T> values) {
            Objects.requireNonNull(column, "column");
            Objects.requireNonNull(values, "values");
            if (!values.isEmpty()) {
                this.append(column, values);
            }
            return this;
        }

        @Nonnull
        WhereClauseBuilder like(@Nonnull String column, @Nullable String value) {
            Objects.requireNonNull(column, "column");
            if (value != null) {
                this.clause.append(column).append(" LIKE ?");
                this.params.add(value);
            }
            return this;
        }

        @Nonnull
        WhereClauseBuilder between(@Nonnull String column, long value1, long value2) {
            Objects.requireNonNull(column, "column");
            this.clause.append(column).append(" BETWEEN ? AND ?");
            this.params.add(value1);
            this.params.add(value2);
            return this;
        }

        @Nonnull
        WhereClauseBuilder greaterThan(@Nonnull String column, long value, boolean inclusive) {
            Objects.requireNonNull(column, "column");
            this.clause.append(column);
            if (inclusive) {
                this.clause.append(" >= ?");
            } else {
                this.clause.append(" > ?");
            }
            this.params.add(value);
            return this;
        }

        @Nonnull
        WhereClauseBuilder lessThan(@Nonnull String column, long value, boolean inclusive) {
            Objects.requireNonNull(column, "column");
            this.clause.append(column);
            if (inclusive) {
                this.clause.append(" <= ?");
            } else {
                this.clause.append(" < ?");
            }
            this.params.add(value);
            return this;
        }

        private void mayAppendAnd() {
            if (!this.params.isEmpty()) {
                this.clause.append(" AND ");
            }
        }

        private void mayAppendOr() {
            if (!this.params.isEmpty()) {
                this.clause.append(" OR ");
            }
        }

        private <T> void append(String column, T value) {
            this.clause.append(column).append(" = ?");
            this.params.add(value);
        }

        private <T> void append(String column, Set<T> values) {
            String collect = values.stream().map(i -> "?").collect(Collectors.joining(","));
            this.clause.append(column).append(" IN (").append(collect).append(")");
            this.params.addAll(values);
        }
    }

    static class WhereClause {
        private final String clause;
        private final List<Object> params;

        private WhereClause(String clause, List<Object> params) {
            this.clause = clause;
            this.params = params;
        }

        static WhereClauseBuilder builder() {
            return new WhereClauseBuilder();
        }

        String getClause() {
            return this.clause;
        }

        Object[] getParams() {
            return this.params.toArray(new Object[0]);
        }

        boolean isEmpty() {
            return this.clause.isEmpty();
        }

        public static <T> WhereClause eq(@Nonnull String column, @Nullable T value) {
            return WhereClause.builder().eq(column, value).build();
        }

        @Nonnull
        public static <T> WhereClause in(@Nonnull String column, @Nullable Set<T> values) {
            return WhereClause.builder().in(column, values).build();
        }

        @Nonnull
        public static WhereClause like(@Nonnull String column, @Nullable String value) {
            return WhereClause.builder().like(column, value).build();
        }

        @Nonnull
        public static WhereClause between(@Nonnull String column, long value1, long value2) {
            return WhereClause.builder().between(column, value1, value2).build();
        }

        @Nonnull
        public static WhereClause greaterThan(@Nonnull String column, long value, boolean inclusive) {
            return WhereClause.builder().greaterThan(column, value, inclusive).build();
        }

        @Nonnull
        public static WhereClause lessThan(@Nonnull String column, long value, boolean inclusive) {
            return WhereClause.builder().lessThan(column, value, inclusive).build();
        }
    }
}

