/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.google.common.base.Predicate
 *  org.apache.commons.lang3.NotImplementedException
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.pages;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentPermissionSummary;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public interface PageManagerInternal
extends PageManager {
    public AbstractPage createDraft(String var1, String var2);

    public AbstractPage createDraft(String var1, String var2, long var3);

    public AbstractPage createOrFindDraftFor(AbstractPage var1);

    public @NonNull PageResponse<Page> getTopLevelPages(Space var1, LimitedRequest var2);

    @Deprecated
    default public @NonNull PageResponse<Page> getPages(Space space, LimitedRequest pageRequest, com.google.common.base.Predicate<? super Page> ... filter) {
        return this.getFilteredPages(space, pageRequest, (Predicate<? super Page>[])filter);
    }

    @Deprecated
    public void reconcileIfNeeded(AbstractPage var1, @Nullable SaveContext var2);

    public @NonNull PageResponse<Page> getFilteredPages(Space var1, LimitedRequest var2, Predicate<? super Page> ... var3);

    default public @NonNull PageResponse<Page> scanFilteredPages(Space space, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super Page> ... filter) {
        throw new NotImplementedException("Method PageManagerInternal.scanFilteredPages is not implemented");
    }

    @Deprecated
    default public @NonNull PageResponse<Page> getPages(LimitedRequest pageRequest, com.google.common.base.Predicate<? super Page> ... filter) {
        return this.getFilteredPages(pageRequest, (Predicate<? super Page>[])filter);
    }

    public @NonNull PageResponse<Page> getFilteredPages(LimitedRequest var1, Predicate<? super Page> ... var2);

    default public @NonNull PageResponse<Page> scanFilteredPages(List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super Page> ... filter) {
        throw new NotImplementedException("Method Method PageManagerInternal.scanFilteredPages is not implemented");
    }

    @Deprecated
    default public @NonNull PageResponse<BlogPost> getBlogPosts(Space space, LimitedRequest pageRequest, com.google.common.base.Predicate<? super BlogPost> ... filter) {
        return this.getFilteredBlogPosts(space, pageRequest, (Predicate<? super BlogPost>[])filter);
    }

    public @NonNull PageResponse<BlogPost> getFilteredBlogPosts(Space var1, LimitedRequest var2, Predicate<? super BlogPost> ... var3);

    public @NonNull PageResponse<Page> getChildren(Page var1, LimitedRequest var2, Depth var3);

    public PageResponse<Page> getAllChildren(Page var1, LimitedRequest var2, Depth var3);

    @Deprecated
    default public @NonNull PageResponse<AbstractPage> getAbstractPagesByTitle(String title, LimitedRequest pageRequest, com.google.common.base.Predicate<? super AbstractPage> ... filter) {
        return this.getFilteredAbstractPagesByTitle(title, pageRequest, (Predicate<? super AbstractPage>[])filter);
    }

    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPagesByTitle(String var1, LimitedRequest var2, Predicate<? super AbstractPage> ... var3);

    @Deprecated
    default public @NonNull PageResponse<AbstractPage> getAbstractPages(List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest, com.google.common.base.Predicate<? super AbstractPage> ... filter) {
        return this.getFilteredAbstractPages(contentTypes, statuses, pageRequest, (Predicate<? super AbstractPage>[])filter);
    }

    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPages(List<ContentType> var1, List<ContentStatus> var2, LimitedRequest var3, Predicate<? super AbstractPage> ... var4);

    @Deprecated
    default public @NonNull PageResponse<AbstractPage> getAbstractPages(Space space, List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest, com.google.common.base.Predicate<? super AbstractPage> ... filter) {
        return this.getFilteredAbstractPages(space, contentTypes, statuses, pageRequest, (Predicate<? super AbstractPage>[])filter);
    }

    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPages(Space var1, List<ContentType> var2, List<ContentStatus> var3, LimitedRequest var4, Predicate<? super AbstractPage> ... var5);

    public PageResponse<AbstractPage> getAbstractPages(LocalDate var1, ZoneId var2, List<ContentStatus> var3, LimitedRequest var4, Predicate<? super AbstractPage> ... var5);

    @Deprecated
    default public @NonNull PageResponse<AbstractPage> getAbstractPages(Space space, String title, List<ContentStatus> statuses, LimitedRequest pageRequest, com.google.common.base.Predicate<? super AbstractPage> ... filter) {
        return this.getFilteredAbstractPages(space, title, statuses, pageRequest, (Predicate<? super AbstractPage>[])filter);
    }

    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPages(Space var1, String var2, List<ContentStatus> var3, LimitedRequest var4, Predicate<? super AbstractPage> ... var5);

    @Deprecated
    default public @NonNull PageResponse<AbstractPage> getAbstractPages(String title, List<ContentStatus> statuses, LimitedRequest pageRequest, com.google.common.base.Predicate<? super AbstractPage> ... filter) {
        return this.getFilteredAbstractPages(title, statuses, pageRequest, (Predicate<? super AbstractPage>[])filter);
    }

    public @NonNull PageResponse<AbstractPage> getFilteredAbstractPages(String var1, List<ContentStatus> var2, LimitedRequest var3, Predicate<? super AbstractPage> ... var4);

    public List<ContentPermissionSummary> findContentPermissionSummaryByIds(Collection<Long> var1);

    public Collection<Page> getPermissionPages(Space var1, LimitedRequest var2);

    public long getPermissionPagesCount(Space var1);
}

