/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentPermissionSummary;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageStatisticsDTO;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Predicate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joda.time.DateTime;

@Deprecated(forRemoval=true)
public interface PageDao
extends ContentEntityObjectDao<Page> {
    public Page getPageById(long var1);

    public List<Page> getPagesByIds(Iterable<Long> var1);

    public Page getPageByIdWithComments(long var1);

    public int getCommentCountOnPage(long var1);

    public Page getPage(Space var1, String var2);

    public Page getPageWithComments(Space var1, String var2);

    public List<Page> getPages(@Nullable Space var1, boolean var2);

    public List<Page> getPagesWithPermissions(Space var1);

    @Deprecated
    public PageResponse<Page> getPages(Space var1, LimitedRequest var2, @Nullable Predicate<? super Page> var3);

    @Deprecated
    public PageResponse<Page> getPages(LimitedRequest var1, @Nullable Predicate<? super Page> var2);

    @Deprecated
    public PageResponse<BlogPost> getBlogPosts(Space var1, LimitedRequest var2, @Nullable Predicate<? super BlogPost> var3);

    public List<Page> getPagesStartingWith(Space var1, String var2);

    public List<Page> getRecentlyAddedPages(int var1, @Nullable String var2);

    public List<Page> getRecentlyUpdatedPages(int var1, @Nullable String var2);

    @Deprecated
    public List<Page> getOrphanedPages(@Nullable String var1);

    @Deprecated
    public List<Page> getUndefinedPages(@Nullable String var1);

    public List<OutgoingLink> getUndefinedLinks(@Nullable String var1);

    public List<Page> getPermissionPages(Space var1);

    public int getAuthoredPagesCountByUser(String var1);

    public List<Page> getRecentlyAuthoredPagesByUser(String var1, int var2);

    public List<Page> getPagesCreatedOrUpdatedSinceDate(Date var1);

    public List<AbstractPage> findPagesWithCurrentOrHistoricalTitleInPermittedSpace(SpacePermissionQueryBuilder var1, String var2, Space var3, int var4);

    public List<AbstractPage> findBlogsWithCurrentOrHistoricalTitleInPermittedSpace(SpacePermissionQueryBuilder var1, String var2, Space var3, int var4);

    public List<AbstractPage> findBlogsWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept(SpacePermissionQueryBuilder var1, String var2, Space var3, int var4);

    public List<AbstractPage> findPagesWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept(SpacePermissionQueryBuilder var1, String var2, Space var3, int var4);

    public List<ContentPermissionSummary> findContentPermissionSummaryByIds(Collection<Long> var1);

    public int countCurrentPages();

    public int countDraftPages();

    public int countPagesWithUnpublishedChanges();

    public int countAllPages();

    public Optional<PageStatisticsDTO> getPageStatistics();

    public long getPageCount(@NonNull String var1);

    public long getPageCount(@NonNull String var1, List<ContentStatus> var2);

    public List<Page> getPageInTrash(String var1, String var2);

    public Map<Long, List<Long>> getAncestorsFor(Collection<Long> var1);

    public List<Page> getDescendants(Page var1);

    public List<String> getDescendantTitles(Page var1);

    public List<Long> getDescendantIds(Page var1);

    public List<Long> getDescendantIds(Page var1, ContentStatus ... var2);

    public int countPagesInSubtree(@NonNull Page var1);

    public List<Page> getTopLevelPages(Space var1);

    @Deprecated
    public PageResponse<Page> getTopLevelPages(Space var1, LimitedRequest var2, Predicate<? super Page> var3);

    @Deprecated
    public PageResponse<Page> getChildren(Page var1, LimitedRequest var2, Predicate<? super Page> var3, Depth var4);

    public PageResponse<Page> getDraftChildren(Page var1, LimitedRequest var2, Depth var3);

    public PageResponse<Page> getAllChildren(Page var1, LimitedRequest var2, Depth var3);

    public Integer getMaxSiblingPosition(Page var1);

    public Collection<Long> getPageIds(Space var1);

    @Deprecated
    public PageResponse<AbstractPage> getAbstractPagesByTitle(String var1, LimitedRequest var2, Predicate<? super AbstractPage> var3);

    @Deprecated
    public PageResponse<AbstractPage> getAbstractPagesByCreationDate(DateTime var1, LimitedRequest var2, Predicate<? super AbstractPage> var3);

    @Deprecated
    public PageResponse<AbstractPage> getAbstractPages(List<ContentType> var1, List<ContentStatus> var2, LimitedRequest var3, @Nullable Predicate<? super AbstractPage> var4);

    @Deprecated
    public PageResponse<AbstractPage> getAbstractPages(Space var1, List<ContentType> var2, List<ContentStatus> var3, LimitedRequest var4, @Nullable Predicate<? super AbstractPage> var5);

    @Deprecated
    public PageResponse<AbstractPage> getAbstractPages(Space var1, String var2, List<ContentStatus> var3, LimitedRequest var4, Predicate<? super AbstractPage> var5);

    @Deprecated
    public PageResponse<AbstractPage> getAbstractPages(String var1, List<ContentStatus> var2, LimitedRequest var3, Predicate<? super AbstractPage> var4);

    @Deprecated
    public PageResponse<AbstractPage> getAbstractPages(DateTime var1, List<ContentStatus> var2, LimitedRequest var3, Predicate<? super AbstractPage> var4);
}

