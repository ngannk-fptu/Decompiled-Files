/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.relations;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.dao.RelationEntity;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface RelationManager {
    public boolean isRelated(RelatableEntity var1, RelatableEntity var2, RelationDescriptor var3);

    public RelationEntity addRelation(RelatableEntity var1, RelatableEntity var2, RelationDescriptor var3);

    public void removeRelation(RelatableEntity var1, RelatableEntity var2, RelationDescriptor var3);

    public int removeAllRelations(RelatableEntity var1);

    public int removeAllRelationsFromEntityWithType(RelationDescriptor var1, RelatableEntity var2);

    public int removeAllRelationsFromCurrentAndHistoricalEntities(RelatableEntity var1);

    public int removeAllRelationsFromCurrentAndHistoricalEntities(Iterable<? extends RelatableEntity> var1);

    public void moveRelationsToContent(RelatableEntity var1, RelatableEntity var2, RelationDescriptor var3);

    public @NonNull PageResponse<RelatableEntity> getSources(RelationQuery var1, LimitedRequest var2);

    public @NonNull PageResponse<RelatableEntity> getTargets(RelationQuery var1, LimitedRequest var2);

    public int getSourcesCount(RelationQuery var1);

    public int getTargetsCount(RelationQuery var1);
}

