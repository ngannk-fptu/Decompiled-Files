/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.influx;

import io.micrometer.common.lang.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

class CreateDatabaseQueryBuilder {
    private static final String QUERY_MANDATORY_TEMPLATE = "CREATE DATABASE \"%s\"";
    private static final String RETENTION_POLICY_INTRODUCTION = " WITH";
    private static final String DURATION_CLAUSE_TEMPLATE = " DURATION %s";
    private static final String REPLICATION_FACTOR_CLAUSE_TEMPLATE = " REPLICATION %d";
    private static final String SHARD_DURATION_CLAUSE_TEMPLATE = " SHARD DURATION %s";
    private static final String NAME_CLAUSE_TEMPLATE = " NAME %s";
    private final String databaseName;
    private final String[] retentionPolicyClauses = new String[4];

    CreateDatabaseQueryBuilder(String databaseName) {
        if (this.isEmpty(databaseName)) {
            throw new IllegalArgumentException("The database name cannot be null or empty");
        }
        this.databaseName = databaseName;
    }

    CreateDatabaseQueryBuilder setRetentionDuration(@Nullable String retentionDuration) {
        if (!this.isEmpty(retentionDuration)) {
            this.retentionPolicyClauses[0] = String.format(DURATION_CLAUSE_TEMPLATE, retentionDuration);
        }
        return this;
    }

    CreateDatabaseQueryBuilder setRetentionReplicationFactor(@Nullable Integer retentionReplicationFactor) {
        if (retentionReplicationFactor != null) {
            this.retentionPolicyClauses[1] = String.format(REPLICATION_FACTOR_CLAUSE_TEMPLATE, retentionReplicationFactor);
        }
        return this;
    }

    CreateDatabaseQueryBuilder setRetentionShardDuration(@Nullable String retentionShardDuration) {
        if (!this.isEmpty(retentionShardDuration)) {
            this.retentionPolicyClauses[2] = String.format(SHARD_DURATION_CLAUSE_TEMPLATE, retentionShardDuration);
        }
        return this;
    }

    CreateDatabaseQueryBuilder setRetentionPolicyName(@Nullable String retentionPolicyName) {
        if (!this.isEmpty(retentionPolicyName)) {
            this.retentionPolicyClauses[3] = String.format(NAME_CLAUSE_TEMPLATE, retentionPolicyName);
        }
        return this;
    }

    String build() {
        StringBuilder queryStringBuilder = new StringBuilder(String.format(QUERY_MANDATORY_TEMPLATE, this.databaseName));
        if (this.hasAnyRetentionPolicy()) {
            String retentionPolicyClause = Stream.of(this.retentionPolicyClauses).filter(Objects::nonNull).reduce(RETENTION_POLICY_INTRODUCTION, String::concat);
            queryStringBuilder.append(retentionPolicyClause);
        }
        return queryStringBuilder.toString();
    }

    private boolean hasAnyRetentionPolicy() {
        return Stream.of(this.retentionPolicyClauses).anyMatch(Objects::nonNull);
    }

    private boolean isEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }
}

