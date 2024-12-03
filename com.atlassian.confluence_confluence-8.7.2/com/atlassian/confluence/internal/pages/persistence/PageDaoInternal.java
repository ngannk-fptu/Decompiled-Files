/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.core.util.DateUtils$DateRange
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.internal.pages.persistence;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.internal.persistence.ContentEntityObjectDaoInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.PageDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.DateUtils;
import com.google.common.base.Predicate;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joda.time.DateTime;

public interface PageDaoInternal
extends PageDao,
ContentEntityObjectDaoInternal<Page> {
    @Override
    @Deprecated
    default public PageResponse<Page> getPages(Space space, LimitedRequest pageRequest, @Nullable Predicate<? super Page> filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getPages(space, pageRequest), filter);
    }

    @Override
    @Deprecated
    default public PageResponse<Page> getPages(LimitedRequest pageRequest, @Nullable Predicate<? super Page> filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getPages(pageRequest), filter);
    }

    public List<Page> getPages(LimitedRequest var1);

    public List<Page> getPages(Space var1, LimitedRequest var2);

    public List<Page> scanFilteredPages(List<ContentStatus> var1, LimitedRequest var2);

    public List<Page> scanFilteredPages(Space var1, List<ContentStatus> var2, LimitedRequest var3);

    @Override
    @Deprecated
    default public PageResponse<BlogPost> getBlogPosts(Space space, LimitedRequest pageRequest, @Nullable Predicate<? super BlogPost> filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getBlogPosts(space, pageRequest), filter);
    }

    public List<BlogPost> getBlogPosts(Space var1, LimitedRequest var2);

    @Override
    @Deprecated
    default public PageResponse<Page> getTopLevelPages(Space space, LimitedRequest limitedRequest, Predicate<? super Page> predicate) {
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, this.getTopLevelPages(space, limitedRequest), predicate);
    }

    public List<Page> getTopLevelPages(Space var1, LimitedRequest var2);

    @Override
    @Deprecated
    default public PageResponse<Page> getChildren(Page page, LimitedRequest pageRequest, Predicate<? super Page> predicate, Depth depth) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getChildren(page, pageRequest, depth), predicate);
    }

    public List<Page> getChildren(Page var1, LimitedRequest var2, Depth var3);

    @Override
    @Deprecated
    default public PageResponse<AbstractPage> getAbstractPagesByTitle(String title, LimitedRequest pageRequest, Predicate<? super AbstractPage> filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getAbstractPagesByTitle(title, pageRequest), filter);
    }

    public List<AbstractPage> getAbstractPagesByTitle(String var1, LimitedRequest var2);

    @Override
    @Deprecated
    default public PageResponse<AbstractPage> getAbstractPagesByCreationDate(DateTime date, LimitedRequest pageRequest, Predicate<? super AbstractPage> filter) {
        DateUtils.DateRange range = DateUtils.toDateRange((Calendar)date.toCalendar(Locale.getDefault()), (int)5);
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getAbstractPagesByCreationDate(range, pageRequest), filter);
    }

    @Override
    @Deprecated
    default public PageResponse<AbstractPage> getAbstractPages(List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest, @Nullable Predicate<? super AbstractPage> filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getAbstractPages(contentTypes, statuses, pageRequest), filter);
    }

    public List<AbstractPage> getAbstractPages(List<ContentType> var1, List<ContentStatus> var2, LimitedRequest var3);

    @Override
    @Deprecated
    default public PageResponse<AbstractPage> getAbstractPages(Space space, List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest, @Nullable Predicate<? super AbstractPage> filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getAbstractPages(space, contentTypes, statuses, pageRequest), filter);
    }

    public List<AbstractPage> getAbstractPages(Space var1, List<ContentType> var2, List<ContentStatus> var3, LimitedRequest var4);

    @Override
    @Deprecated
    default public PageResponse<AbstractPage> getAbstractPages(Space space, String title, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super AbstractPage> filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getAbstractPages(space, title, statuses, pageRequest), filter);
    }

    public List<AbstractPage> getAbstractPagesByCreationDate(DateUtils.DateRange var1, LimitedRequest var2);

    public List<AbstractPage> getAbstractPages(Space var1, String var2, List<ContentStatus> var3, LimitedRequest var4);

    @Override
    @Deprecated
    default public PageResponse<AbstractPage> getAbstractPages(String title, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super AbstractPage> filter) {
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getAbstractPages(title, statuses, pageRequest), filter);
    }

    public List<AbstractPage> getAbstractPages(String var1, List<ContentStatus> var2, LimitedRequest var3);

    @Override
    @Deprecated
    default public PageResponse<AbstractPage> getAbstractPages(DateTime date, List<ContentStatus> statuses, LimitedRequest pageRequest, Predicate<? super AbstractPage> filter) {
        DateUtils.DateRange range = DateUtils.toDateRange((Calendar)date.toCalendar(Locale.getDefault()), (int)5);
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.getAbstractPages(range, statuses, pageRequest), filter);
    }

    public List<AbstractPage> getAbstractPages(DateUtils.DateRange var1, List<ContentStatus> var2, LimitedRequest var3);

    public Collection<Page> getPermissionPages(Space var1, LimitedRequest var2);

    public long getPermissionPagesCount(Space var1);
}

