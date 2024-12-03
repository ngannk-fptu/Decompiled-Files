/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.relations.dao;

import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.dao.RelationEntity;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface RelationDao<S extends RelatableEntity, T extends RelatableEntity> {
    public int getRelationsCount(S var1, T var2, RelationDescriptor<?, ?> var3);

    public RelationEntity<S, T> createRelationEntity(S var1, T var2, RelationDescriptor<?, ?> var3);

    public void removeRelationEntity(RelationEntity<S, T> var1);

    public @Nullable RelationEntity<S, T> getRelationEntity(S var1, T var2, RelationDescriptor<?, ?> var3);

    public @NonNull List<S> getSources(RelationQuery<T> var1, int var2, int var3);

    public @NonNull List<T> getTargets(RelationQuery<S> var1, int var2, int var3);

    public int getSourcesCount(RelationQuery<T> var1);

    public int getTargetsCount(RelationQuery<S> var1);

    public int removeAllRelations(RelatableEntity var1);

    public int removeAllRelations(Iterable<? extends RelatableEntity> var1);

    public int removeAllRelationsFromEntityWithName(String var1, RelatableEntity var2);

    public int removeAllRelationsFromCurrentAndHistoricalEntities(RelatableEntity var1);

    public int removeAllRelationsFromCurrentAndHistoricalEntities(Iterable<? extends RelatableEntity> var1);
}

