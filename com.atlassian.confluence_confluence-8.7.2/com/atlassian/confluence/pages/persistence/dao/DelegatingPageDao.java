/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.util.DateUtils$DateRange
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionSummary;
import com.atlassian.confluence.core.ContributionStatus;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageStatisticsDTO;
import com.atlassian.confluence.pages.persistence.dao.PageDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.util.DateUtils;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DelegatingPageDao
implements PageDaoInternal {
    private final PageDaoInternal delegateDao;

    protected DelegatingPageDao(PageDaoInternal delegateDao) {
        this.delegateDao = delegateDao;
    }

    @VisibleForTesting
    public PageDao getDelegateDao() {
        return this.delegateDao;
    }

    @Override
    public Page getPage(Space space, String pageTitle) {
        return this.delegateDao.getPage(space, pageTitle);
    }

    @Override
    public Page getPageWithComments(Space space, String pageTitle) {
        return this.delegateDao.getPageWithComments(space, pageTitle);
    }

    @Override
    public void remove(EntityObject object) {
        this.delegateDao.remove(object);
    }

    @Override
    public void removeEntity(Page objectToRemove) {
        this.delegateDao.removeEntity(objectToRemove);
    }

    @Override
    public void save(EntityObject objectToSave, EntityObject originalObject) {
        this.delegateDao.save(objectToSave, originalObject);
    }

    @Override
    public void saveEntity(Page currentObject, @Nullable Page originalObject) {
        this.delegateDao.saveEntity(currentObject, originalObject);
    }

    @Override
    public void save(EntityObject objectToSave) {
        this.delegateDao.save(objectToSave);
    }

    @Override
    public void saveEntity(Page objectToSave) {
        this.delegateDao.saveEntity(objectToSave);
    }

    @Override
    public void saveRaw(EntityObject objectToSave) {
        this.delegateDao.saveRaw(objectToSave);
    }

    @Override
    public void saveRawEntity(Page objectToSave) {
        this.delegateDao.saveRawEntity(objectToSave);
    }

    @Override
    public @NonNull List<Page> findAll() {
        return this.delegateDao.findAll();
    }

    @Override
    public @NonNull List<Page> findAllSorted(String s) {
        return this.delegateDao.findAllSorted(s);
    }

    @Override
    public @NonNull List<Page> findAllSorted(String s, boolean cached, int firstRow, int maxRows) {
        return this.delegateDao.findAllSorted(s, cached, firstRow, maxRows);
    }

    @Override
    @Deprecated
    public <T> @NonNull PageResponse<T> findByClassIds(Iterable<Long> ids, LimitedRequest limitedRequest, com.google.common.base.Predicate<? super T> filter) {
        return this.delegateDao.findByClassIds(ids, limitedRequest, filter);
    }

    @Override
    public @NonNull PageResponse<Page> findByClassIdsFiltered(Iterable<Long> ids, LimitedRequest limitedRequest, Predicate<? super Page> filter) {
        return this.delegateDao.findByClassIdsFiltered(ids, limitedRequest, filter);
    }

    @Override
    public long findLatestVersionsCount() {
        return this.delegateDao.findLatestVersionsCount();
    }

    @Override
    public Iterator<Page> findLatestVersionsIterator() {
        return this.delegateDao.findLatestVersionsIterator();
    }

    @Override
    public List<ContentEntityObject> findPreviousVersions(long originalContentId) {
        return this.delegateDao.findPreviousVersions(originalContentId);
    }

    @Override
    public List<AbstractPage> findPagesWithCurrentOrHistoricalTitleInPermittedSpace(SpacePermissionQueryBuilder permissionQueryBuilder, String pageTitle, Space space, int maxResultCount) {
        return this.delegateDao.findPagesWithCurrentOrHistoricalTitleInPermittedSpace(permissionQueryBuilder, pageTitle, space, maxResultCount);
    }

    @Override
    public List<AbstractPage> findBlogsWithCurrentOrHistoricalTitleInPermittedSpace(SpacePermissionQueryBuilder permissionQueryBuilder, String blogTitle, Space space, int maxResultCount) {
        return this.delegateDao.findBlogsWithCurrentOrHistoricalTitleInPermittedSpace(permissionQueryBuilder, blogTitle, space, maxResultCount);
    }

    @Override
    public List<AbstractPage> findBlogsWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept(SpacePermissionQueryBuilder permissionQueryBuilder, String blogTitle, Space space, int maxResultCount) {
        return this.delegateDao.findBlogsWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept(permissionQueryBuilder, blogTitle, space, maxResultCount);
    }

    @Override
    public List<AbstractPage> findPagesWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept(SpacePermissionQueryBuilder permissionQueryBuilder, String pageTitle, Space space, int maxResultCount) {
        return this.delegateDao.findPagesWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept(permissionQueryBuilder, pageTitle, space, maxResultCount);
    }

    @Override
    public List<ContentPermissionSummary> findContentPermissionSummaryByIds(Collection<Long> ids) {
        return this.delegateDao.findContentPermissionSummaryByIds(ids);
    }

    @Override
    public Iterator<SpaceContentEntityObject> getAllCurrentEntities() {
        return this.delegateDao.getAllCurrentEntities();
    }

    @Override
    public int getAuthoredPagesCountByUser(String username) {
        return this.delegateDao.getAuthoredPagesCountByUser(username);
    }

    @Override
    public Page getById(long id) {
        return (Page)this.delegateDao.getById(id);
    }

    @Override
    public List<ContentEntityObject> getContentAuthoredByUser(String username) {
        return this.delegateDao.getContentAuthoredByUser(username);
    }

    @Override
    public List<Long> getDescendantIds(Page page) {
        return this.delegateDao.getDescendantIds(page);
    }

    @Override
    public List<Long> getDescendantIds(Page page, ContentStatus ... statuses) {
        return this.delegateDao.getDescendantIds(page, statuses);
    }

    @Override
    public int countPagesInSubtree(@NonNull Page page) {
        return this.delegateDao.countPagesInSubtree(page);
    }

    @Override
    public List<Page> getDescendants(Page page) {
        return this.delegateDao.getDescendants(page);
    }

    @Override
    public List<String> getDescendantTitles(Page page) {
        return this.delegateDao.getDescendantTitles(page);
    }

    @Override
    public ContentEntityObject getFirstVersionAfter(long originalVersionContentId, int version) {
        return this.delegateDao.getFirstVersionAfter(originalVersionContentId, version);
    }

    @Override
    public ContentEntityObject getFirstVersionBefore(long originalVersionContentId, int version) {
        return this.delegateDao.getFirstVersionBefore(originalVersionContentId, version);
    }

    @Override
    public String getObjectType(long id) {
        return this.delegateDao.getObjectType(id);
    }

    @Override
    public List<Page> getOrphanedPages(String spaceKey) {
        return this.delegateDao.getOrphanedPages(spaceKey);
    }

    @Override
    public Page getPageById(long id) {
        return this.delegateDao.getPageById(id);
    }

    @Override
    public List<Page> getPagesByIds(Iterable<Long> ids) {
        return this.delegateDao.getPagesByIds(ids);
    }

    @Override
    public Page getPageByIdWithComments(long id) {
        return this.delegateDao.getPageByIdWithComments(id);
    }

    @Override
    public int getCommentCountOnPage(long id) {
        return this.delegateDao.getCommentCountOnPage(id);
    }

    @Override
    public List<Page> getPageInTrash(String spaceKey, String title) {
        return this.delegateDao.getPageInTrash(spaceKey, title);
    }

    @Override
    public Map<Long, List<Long>> getAncestorsFor(Collection<Long> ids) {
        return this.delegateDao.getAncestorsFor(ids);
    }

    @Override
    public List<Page> getPages(@Nullable Space space, boolean currentOnly) {
        return this.delegateDao.getPages(space, currentOnly);
    }

    @Override
    public List<Page> getPagesWithPermissions(Space space) {
        return this.delegateDao.getPagesWithPermissions(space);
    }

    @Override
    public List<Page> getPages(Space space, LimitedRequest pageRequest) {
        return this.delegateDao.getPages(space, pageRequest);
    }

    @Override
    public List<Page> getPages(LimitedRequest pageRequest) {
        return this.delegateDao.getPages(pageRequest);
    }

    @Override
    public List<BlogPost> getBlogPosts(Space space, LimitedRequest pageRequest) {
        return this.delegateDao.getBlogPosts(space, pageRequest);
    }

    @Override
    public List<Page> getPagesCreatedOrUpdatedSinceDate(Date previousLoginDate) {
        return this.delegateDao.getPagesCreatedOrUpdatedSinceDate(previousLoginDate);
    }

    @Override
    public List<Page> getPagesStartingWith(Space space, String s) {
        return this.delegateDao.getPagesStartingWith(space, s);
    }

    @Override
    public List<Page> getPermissionPages(Space space) {
        return this.delegateDao.getPermissionPages(space);
    }

    @Override
    public Class<Page> getPersistentClass() {
        return this.delegateDao.getPersistentClass();
    }

    @Override
    public Iterator<ContentEntityObject> getRecentlyAddedEntities(String spaceKey, int maxResults) {
        return this.delegateDao.getRecentlyAddedEntities(spaceKey, maxResults);
    }

    @Override
    public List<Page> getRecentlyAddedPages(int maxCount, String spaceKey) {
        return this.delegateDao.getRecentlyAddedPages(maxCount, spaceKey);
    }

    @Override
    public List<Page> getRecentlyAuthoredPagesByUser(String username, int maxCount) {
        return this.delegateDao.getRecentlyAuthoredPagesByUser(username, maxCount);
    }

    @Override
    public Iterator<ContentEntityObject> getRecentlyModifiedEntities(int maxResults) {
        return this.delegateDao.getRecentlyModifiedEntities(maxResults);
    }

    @Override
    public Iterator<SpaceContentEntityObject> getRecentlyModifiedEntities(String spaceKey, int maxResults) {
        return this.delegateDao.getRecentlyModifiedEntities(spaceKey, maxResults);
    }

    @Override
    public Iterator<ContentEntityObject> getRecentlyModifiedEntitiesForUser(String username) {
        return this.delegateDao.getRecentlyModifiedEntitiesForUser(username);
    }

    @Override
    public List<ContentEntityObject> getRecentlyModifiedForChangeDigest(Date fromDate) {
        return this.delegateDao.getRecentlyModifiedForChangeDigest(fromDate);
    }

    @Override
    public List<Page> getRecentlyUpdatedPages(int maxCount, String spaceKey) {
        return this.delegateDao.getRecentlyUpdatedPages(maxCount, spaceKey);
    }

    @Override
    public List<Page> getTopLevelPages(Space space) {
        return this.delegateDao.getTopLevelPages(space);
    }

    @Override
    public List<Page> getTopLevelPages(Space space, LimitedRequest limitedRequest) {
        return this.delegateDao.getTopLevelPages(space, limitedRequest);
    }

    @Override
    @Deprecated
    public List<Page> getChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        return this.delegateDao.getChildren(page, pageRequest, depth);
    }

    @Override
    public PageResponse<Page> getDraftChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        return this.delegateDao.getDraftChildren(page, pageRequest, depth);
    }

    @Override
    public PageResponse<Page> getAllChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        return this.delegateDao.getAllChildren(page, pageRequest, depth);
    }

    @Override
    public Integer getMaxSiblingPosition(Page page) {
        return this.delegateDao.getMaxSiblingPosition(page);
    }

    @Override
    public Collection<Long> getPageIds(Space space) {
        return this.delegateDao.getPageIds(space);
    }

    @Override
    public List<AbstractPage> getAbstractPagesByTitle(String title, LimitedRequest pageRequest) {
        return this.delegateDao.getAbstractPagesByTitle(title, pageRequest);
    }

    @Override
    public List<AbstractPage> getAbstractPagesByCreationDate(DateUtils.DateRange date, LimitedRequest pageRequest) {
        return this.delegateDao.getAbstractPagesByCreationDate(date, pageRequest);
    }

    @Override
    public List<AbstractPage> getAbstractPages(List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        return this.delegateDao.getAbstractPages(contentTypes, statuses, pageRequest);
    }

    @Override
    public List<AbstractPage> getAbstractPages(Space space, List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        return this.delegateDao.getAbstractPages(space, contentTypes, statuses, pageRequest);
    }

    @Override
    public List<AbstractPage> getAbstractPages(Space space, String title, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        return this.delegateDao.getAbstractPages(space, title, statuses, pageRequest);
    }

    @Override
    public List<AbstractPage> getAbstractPages(String title, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        return this.delegateDao.getAbstractPages(title, statuses, pageRequest);
    }

    @Override
    public List<AbstractPage> getAbstractPages(DateUtils.DateRange date, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        return this.delegateDao.getAbstractPages(date, statuses, pageRequest);
    }

    @Override
    public Date getOldestPageCreationDate() {
        return this.delegateDao.getOldestPageCreationDate();
    }

    @Override
    public List<ContentEntityObject> getTrashedContent(String spaceKey) {
        return this.delegateDao.getTrashedContent(spaceKey);
    }

    @Override
    public List<ContentEntityObject> getTrashedContents(String spaceKey, int offset, int limit) {
        return this.delegateDao.getTrashedContents(spaceKey, offset, limit);
    }

    @Override
    public List<Page> getUndefinedPages(String spaceKey) {
        return this.delegateDao.getUndefinedPages(spaceKey);
    }

    @Override
    public final List<OutgoingLink> getUndefinedLinks(@Nullable String spaceKey) {
        return this.delegateDao.getUndefinedLinks(spaceKey);
    }

    @Override
    public ContentEntityObject getVersion(long originalVersionContentId, int version) {
        return this.delegateDao.getVersion(originalVersionContentId, version);
    }

    @Override
    public List<VersionHistorySummary> getVersionHistorySummary(long originalContentId) {
        return this.delegateDao.getVersionHistorySummary(originalContentId);
    }

    @Override
    public PageResponse<VersionHistorySummary> getVersionHistorySummary(long originalContentId, LimitedRequest request) {
        return this.delegateDao.getVersionHistorySummary(originalContentId, request);
    }

    @Override
    public Map<Long, List<ConfluenceUser>> getVersionEditContributors(Iterable<Page> originalVersions) {
        return this.delegateDao.getVersionEditContributors(originalVersions);
    }

    @Override
    public Set<ConfluenceUser> getAllModifiers(Page page) {
        return this.delegateDao.getAllModifiers(page);
    }

    @Override
    public Map<Long, Set<ConfluenceUser>> getAllModifiers(Collection<Long> contentIds) {
        return this.delegateDao.getAllModifiers(contentIds);
    }

    @Override
    public Map<Long, ContentEntityObject> getVersionsLastEditedByUser(Collection<Long> contentIds, UserKey userKey) {
        return this.delegateDao.getVersionsLastEditedByUser(contentIds, userKey);
    }

    @Override
    public Map<Long, ContentEntityObject> getVersionsLastEditedByUserNew(Collection<Long> contentIds, UserKey userKey) {
        return this.delegateDao.getVersionsLastEditedByUserNew(contentIds, userKey);
    }

    @Override
    public Map<Long, ContributionStatus> getContributionStatusByUser(Collection<ContentId> contentIds, UserKey userKey) {
        return this.delegateDao.getContributionStatusByUser(contentIds, userKey);
    }

    @Override
    public void refresh(EntityObject objectToRefresh) {
        this.delegateDao.refresh(objectToRefresh);
    }

    @Override
    public void refreshEntity(Page objectToRefresh) {
        this.delegateDao.refreshEntity(objectToRefresh);
    }

    @Override
    public void replicate(Object objectToReplicate) {
        this.delegateDao.replicate(objectToReplicate);
    }

    @Override
    public void replicateEntity(Page objectToReplicate) {
        this.delegateDao.refreshEntity(objectToReplicate);
    }

    @Override
    public List<Page> getLastEditedVersionsOf(Page content) {
        return this.delegateDao.getLastEditedVersionsOf(content);
    }

    @Override
    public List<SpaceContentEntityObject> findContentBySpaceIdAndStatus(long spaceId, String status, int offset, int count) {
        return this.delegateDao.findContentBySpaceIdAndStatus(spaceId, status, offset, count);
    }

    @Override
    @Deprecated
    public PageResponse<SpaceContentEntityObject> findContentBySpaceIdAndStatus(long spaceId, String status, LimitedRequest limitedRequest, com.google.common.base.Predicate<? super SpaceContentEntityObject> predicate) {
        return this.delegateDao.findContentBySpaceIdAndStatusAndFilter(spaceId, status, limitedRequest, predicate != null ? arg_0 -> predicate.apply(arg_0) : null);
    }

    @Override
    public int countContentBySpaceIdAndStatus(long spaceId, String status) {
        return this.delegateDao.countContentBySpaceIdAndStatus(spaceId, status);
    }

    @Override
    public List<ContentEntityObject> findHistoricalVersionsAfterVersion(long originalContentId, int version) {
        return this.delegateDao.findHistoricalVersionsAfterVersion(originalContentId, version);
    }

    @Override
    public List<ContentEntityObject> findAllDraftsFor(long contentId) {
        return this.delegateDao.findAllDraftsFor(contentId);
    }

    @Override
    public List<Draft> findAllLegacyDraftsFor(long contentId) {
        return this.delegateDao.findAllLegacyDraftsFor(contentId);
    }

    @Override
    public ContentEntityObject findDraftFor(long contentId) {
        return this.delegateDao.findDraftFor(contentId);
    }

    @Override
    public PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUser(UserKey userKey, LimitedRequest request) {
        return this.delegateDao.getPageAndBlogPostsVersionsLastEditedByUser(userKey, request);
    }

    @Override
    public PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUserIncludingDrafts(UserKey userKey, LimitedRequest request) {
        return this.delegateDao.getPageAndBlogPostsVersionsLastEditedByUserIncludingDrafts(userKey, request);
    }

    @Override
    public List<ContentEntityObject> findUnpublishedContentWithUserContributions(String username) {
        return this.delegateDao.findUnpublishedContentWithUserContributions(username);
    }

    @Override
    public List<ContentEntityObject> findDraftsWithUnpublishedChangesForUser(String creatorName) {
        return this.delegateDao.findDraftsWithUnpublishedChangesForUser(creatorName);
    }

    @Override
    public int countAllPages() {
        return this.delegateDao.countAllPages();
    }

    @Override
    public Optional<PageStatisticsDTO> getPageStatistics() {
        return this.delegateDao.getPageStatistics();
    }

    @Override
    public int countCurrentPages() {
        return this.delegateDao.countCurrentPages();
    }

    @Override
    public Collection<Page> getPermissionPages(Space space, LimitedRequest requeste) {
        return this.delegateDao.getPermissionPages(space, requeste);
    }

    @Override
    public long getPermissionPagesCount(Space space) {
        return this.delegateDao.getPermissionPagesCount(space);
    }

    @Override
    public int countDraftPages() {
        return this.delegateDao.countDraftPages();
    }

    @Override
    public int countPagesWithUnpublishedChanges() {
        return this.delegateDao.countPagesWithUnpublishedChanges();
    }

    @Override
    public long getPageCount(@NonNull String spaceKey) {
        return this.delegateDao.getPageCount(spaceKey);
    }

    @Override
    public long getPageCount(@NonNull String spaceKey, List<ContentStatus> statuses) {
        return this.delegateDao.getPageCount(spaceKey, statuses);
    }

    @Override
    public List<SpaceContentEntityObject> getTrashedEntities(long contentIdOffset, int limit) {
        return this.delegateDao.getTrashedEntities(contentIdOffset, limit);
    }

    @Override
    public void saveRawWithoutReindex(EntityObject objectToSave) {
        this.delegateDao.saveRawWithoutReindex(objectToSave);
    }

    @Override
    public @NonNull List<Page> scanFilteredPages(List<ContentStatus> statuses, LimitedRequest pageRequest) {
        return this.delegateDao.scanFilteredPages(statuses, pageRequest);
    }

    @Override
    public @NonNull List<Page> scanFilteredPages(Space space, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        return this.delegateDao.scanFilteredPages(space, statuses, pageRequest);
    }
}

