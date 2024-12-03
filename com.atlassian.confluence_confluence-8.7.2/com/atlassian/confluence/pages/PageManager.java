/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DeleteContext;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.BlogPostStatisticsDTO;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageStatisticsDTO;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.user.User;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public interface PageManager
extends ContentEntityManager {
    @Deprecated
    public @Nullable Page getPage(long var1);

    @Deprecated
    public @NonNull List<Page> getPages(Iterable<Long> var1);

    @Deprecated
    public @Nullable AbstractPage getAbstractPage(long var1);

    @Deprecated
    public @NonNull List<AbstractPage> getAbstractPages(Iterable<Long> var1);

    @Deprecated
    public @Nullable BlogPost getBlogPost(long var1);

    @Deprecated
    public @Nullable Page getPage(String var1, String var2);

    @Deprecated
    public @Nullable Page getPageWithComments(String var1, String var2);

    public int getCommentCountOnPage(long var1);

    public int getCommentCountOnBlog(long var1);

    @Deprecated
    public @Nullable BlogPost getBlogPost(String var1, String var2, Calendar var3);

    @Deprecated
    public @Nullable BlogPost getBlogPost(String var1, String var2, Calendar var3, boolean var4);

    public @Nullable BlogPost getNewestBlogPost(@Nullable String var1);

    public @NonNull List getRecentlyAddedBlogPosts(int var1, @Nullable String var2);

    public @NonNull List getRecentlyAddedPages(int var1, @Nullable String var2);

    public @NonNull List getRecentlyUpdatedPages(int var1, @Nullable String var2);

    @Deprecated
    public @NonNull List getOrphanedPages(@Nullable String var1);

    @Deprecated
    public @NonNull List getUndefinedPages(@Nullable String var1);

    public List<OutgoingLink> getUndefinedLinks(@Nullable String var1);

    public @NonNull List getPermissionPages(Space var1);

    public int getAuthoredPagesCountByUser(String var1);

    public boolean isPageRecentlyUpdatedForUser(Page var1, @Nullable User var2);

    public @NonNull List getPagesCreatedOrUpdatedSinceDate(Date var1);

    public @NonNull List getBlogPosts(String var1, Calendar var2, int var3);

    public @NonNull List getBlogPosts(String var1, Calendar var2, int var3, int var4, int var5);

    public @NonNull List<BlogPost> getBlogPosts(Space var1, boolean var2);

    public @NonNull Set<Date> getYearsWithBlogPosts(String var1);

    public @NonNull Set<Date> getMonthsWithBlogPosts(String var1, Calendar var2);

    public @NonNull List<Date> getBlogPostDates(String var1);

    public @NonNull List<Date> getBlogPostDates(String var1, Calendar var2, int var3);

    public @NonNull List<Page> getPages(@Nullable Space var1, boolean var2);

    public List<Page> getPagesWithPermissions(@NonNull Space var1);

    public @NonNull Collection<Long> getPageIds(Space var1);

    public @NonNull List getPagesStartingWith(Space var1, String var2);

    public @Nullable BlogPost findNextBlogPost(BlogPost var1);

    public @Nullable BlogPost findPreviousBlogPost(BlogPost var1);

    @Deprecated
    default public NotificationManager getNotificationManager() {
        return null;
    }

    @Deprecated
    default public void setNotificationManager(NotificationManager notificationManager) {
    }

    public void renamePage(AbstractPage var1, String var2);

    public void renamePageWithoutNotifications(AbstractPage var1, String var2);

    public @NonNull List<AbstractPage> getPossibleRedirectsNotInSpace(Space var1, String var2, int var3);

    public @NonNull List<AbstractPage> getPossibleRedirectsInSpace(Space var1, String var2, int var3);

    public @NonNull List<AbstractPage> getPossibleBlogRedirectsInSpace(Space var1, String var2, int var3);

    public @NonNull List<AbstractPage> getPossibleBlogRedirectsNotInSpace(Space var1, String var2, int var3);

    public @NonNull List getRecentlyAddedBlogPosts(int var1, @Nullable Date var2, String var3);

    public @NonNull List getPageInTrash(String var1, String var2);

    public @NonNull List<BlogPost> getBlogPostsInTrash(String var1, String var2);

    public boolean spaceHasBlogPosts(String var1);

    @Deprecated
    public @NonNull List getDescendents(Page var1);

    public @NonNull List<Page> getDescendants(Page var1);

    public @NonNull List<String> getDescendantTitles(Page var1);

    public PageResponse<Page> getDraftChildren(Page var1, LimitedRequest var2, Depth var3);

    public int removeStaleSharedDrafts();

    public void updatePageInAncestorCollections(Page var1, Page var2);

    public void removePageFromAncestorCollections(Page var1);

    public @NonNull Collection<Long> getDescendantIds(Page var1);

    public @NonNull List getTopLevelPages(Space var1);

    @Deprecated
    public @Nullable AbstractPage getPageByVersion(AbstractPage var1, int var2);

    public void removeAllPages(Space var1);

    public void removeAllPages(Space var1, ProgressMeter var2);

    public void removeAllBlogPosts(Space var1);

    public void removeAllBlogPosts(Space var1, ProgressMeter var2);

    public @Nullable BlogPost findPreviousBlogPost(String var1, Date var2);

    public @Nullable BlogPost findNextBlogPost(String var1, Date var2);

    @Deprecated
    public void trashPage(AbstractPage var1);

    public void trashPage(AbstractPage var1, @NonNull DeleteContext var2);

    public @NonNull ListBuilder<Page> getTopLevelPagesBuilder(Space var1);

    public void restorePage(AbstractPage var1);

    public void movePageAfter(Page var1, Page var2);

    public void movePageBefore(Page var1, Page var2);

    public void movePageAsChild(Page var1, Page var2);

    public void moveChildrenToNewParent(Page var1, Page var2);

    public void movePageToTopLevel(Page var1, Space var2);

    public void moveBlogPostToTopLevel(BlogPost var1, Space var2);

    public void setChildPageOrder(Page var1, List<Long> var2);

    public void revertChildPageOrder(Page var1);

    public @NonNull List<ContentEntityObject> getOrderedXhtmlContentFromContentId(long var1, long var3, int var5);

    public int countPagesInSubtree(Page var1);

    public int getCountOfLatestXhtmlContent(long var1);

    public long getHighestCeoId();

    public @NonNull List<ContentEntityObject> getPreviousVersionsOfPageWithTaskId(long var1, long var3, int var5);

    public long getBlogPostCount(String var1, @Nullable Calendar var2, int var3);

    public long getPageCount(@NonNull String var1);

    public int countCurrentPages();

    public int countDraftPages();

    public int countPagesWithUnpublishedChanges();

    public Optional<PageStatisticsDTO> getPageStatistics();

    public int countCurrentBlogs();

    public int countDraftBlogs();

    public int countBlogsWithUnpublishedChanges();

    public Optional<BlogPostStatisticsDTO> getBlogStatistics();

    public void refreshPage(ContentEntityObject var1);

    public void deepCopyPage(PageCopyOptions var1, Page var2, Page var3);

    public void deepDeletePage(PageDeleteOptions var1, Page var2);
}

