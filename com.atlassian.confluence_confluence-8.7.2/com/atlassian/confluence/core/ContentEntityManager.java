/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContributionStatus;
import com.atlassian.confluence.core.Modification;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public interface ContentEntityManager {
    public static final int ITERATE_ALL = 0;

    public void refreshContentEntity(ContentEntityObject var1);

    public void saveContentEntity(ContentEntityObject var1, @Nullable SaveContext var2);

    public void saveContentEntity(ContentEntityObject var1, @Nullable ContentEntityObject var2, @Nullable SaveContext var3);

    public <T extends ContentEntityObject> void saveNewVersion(T var1, Modification<T> var2);

    public <T extends ContentEntityObject> void saveNewVersion(T var1, Modification<T> var2, @Nullable SaveContext var3);

    public void removeContentEntity(ContentEntityObject var1);

    public void revertContentEntityBackToVersion(ContentEntityObject var1, int var2, @Nullable String var3, boolean var4);

    public @NonNull Iterator getRecentlyAddedEntities(@Nullable String var1, int var2);

    public @NonNull Iterator getRecentlyModifiedEntities(String var1, int var2);

    public @NonNull List getRecentlyModifiedForChangeDigest(Date var1);

    public @NonNull Iterator getRecentlyModifiedEntitiesForUser(String var1);

    public @NonNull PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUser(@Nullable UserKey var1, LimitedRequest var2);

    public @NonNull PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUserIncludingDrafts(@Nullable UserKey var1, LimitedRequest var2);

    @Deprecated
    public @Nullable ContentEntityObject getById(long var1);

    @Deprecated
    public @Nullable ContentEntityObject getPreviousVersion(ContentEntityObject var1);

    @Deprecated
    public @Nullable ContentEntityObject getNextVersion(ContentEntityObject var1);

    @Deprecated
    public @Nullable ContentEntityObject getOtherVersion(ContentEntityObject var1, int var2);

    public @NonNull List<VersionHistorySummary> getVersionHistorySummaries(ContentEntityObject var1);

    public @NonNull Map<Long, ContentEntityObject> getVersionsLastEditedByUser(@NonNull Collection<ContentId> var1, @Nullable UserKey var2);

    public Map<Long, ContributionStatus> getContributionStatusByUser(@NonNull Collection<ContentId> var1, @Nullable UserKey var2);

    public void removeHistoricalVersion(ContentEntityObject var1);
}

