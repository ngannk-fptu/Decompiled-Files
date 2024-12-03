/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.util.collections.GuavaConversionUtil
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.internal.ContentDraftManagerInternal;
import com.atlassian.confluence.util.collections.GuavaConversionUtil;
import java.util.List;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public interface ContentEntityManagerInternal
extends ContentEntityManager,
ContentDraftManagerInternal {
    public @Nullable ContentEntityObject getById(ContentId var1);

    public @Nullable ContentEntityObject getById(ContentId var1, int var2);

    @Deprecated
    public @NonNull PageResponse<ContentEntityObject> getByIds(List<ContentId> var1, LimitedRequest var2, com.google.common.base.Predicate<? super ContentEntityObject> ... var3);

    default public @NonNull PageResponse<ContentEntityObject> getByIdsAndFilters(List<ContentId> contentIds, LimitedRequest limitedRequest, Predicate<? super ContentEntityObject> ... filter) {
        return this.getByIds(contentIds, limitedRequest, GuavaConversionUtil.toGuavaPredicates((Predicate[])filter));
    }

    public @NonNull PageResponse<VersionHistorySummary> getVersionHistorySummaries(ContentId var1, LimitedRequest var2);
}

