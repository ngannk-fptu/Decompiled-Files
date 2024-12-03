/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Predicate
 *  org.apache.commons.lang3.NotImplementedException
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.core.persistence;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContributionStatus;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.core.persistence.VersionedObjectDao;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface ContentEntityObjectDao<T extends ContentEntityObject>
extends VersionedObjectDao<T> {
    public T getById(long var1);

    public List<ContentEntityObject> getContentAuthoredByUser(String var1);

    public Iterator<SpaceContentEntityObject> getAllCurrentEntities();

    public Iterator<ContentEntityObject> getRecentlyAddedEntities(String var1, int var2);

    public Iterator<ContentEntityObject> getRecentlyModifiedEntities(int var1);

    public Iterator<SpaceContentEntityObject> getRecentlyModifiedEntities(String var1, int var2);

    public List<ContentEntityObject> getRecentlyModifiedForChangeDigest(Date var1);

    public Iterator<ContentEntityObject> getRecentlyModifiedEntitiesForUser(String var1);

    @Deprecated
    public List<ContentEntityObject> getTrashedContent(String var1);

    default public List<ContentEntityObject> getTrashedContents(String spaceKey, int offset, int limit) {
        return this.getTrashedContent(spaceKey);
    }

    default public PageResponse<ContentEntityObject> getTrashedContents(String spaceKey, LimitedRequest pageRequest, @Nullable Predicate<? super ContentEntityObject> filter) {
        throw new NotImplementedException("Method ContentEntityObjectDao.getTrashedContents is not implemented");
    }

    public String getObjectType(long var1);

    public ContentEntityObject getFirstVersionBefore(long var1, int var3);

    public ContentEntityObject getFirstVersionAfter(long var1, int var3);

    public ContentEntityObject getVersion(long var1, int var3);

    public List<VersionHistorySummary> getVersionHistorySummary(long var1);

    public PageResponse<VersionHistorySummary> getVersionHistorySummary(long var1, LimitedRequest var3);

    public Map<Long, List<ConfluenceUser>> getVersionEditContributors(Iterable<T> var1);

    @Deprecated
    default public Set<ConfluenceUser> getAllModifiers(T ceo) {
        Long contentId = ceo.getId();
        return this.getAllModifiers((Collection<Long>)Collections.singleton(contentId)).getOrDefault(contentId, new HashSet());
    }

    public Map<Long, Set<ConfluenceUser>> getAllModifiers(Collection<Long> var1);

    public Map<Long, ContentEntityObject> getVersionsLastEditedByUser(Collection<Long> var1, UserKey var2);

    @Deprecated
    public Map<Long, ContentEntityObject> getVersionsLastEditedByUserNew(Collection<Long> var1, UserKey var2);

    public Map<Long, ContributionStatus> getContributionStatusByUser(Collection<ContentId> var1, UserKey var2);

    public PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUser(@Nullable UserKey var1, LimitedRequest var2);

    public PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUserIncludingDrafts(@Nullable UserKey var1, LimitedRequest var2);

    public List<ContentEntityObject> findPreviousVersions(long var1);

    public List<ContentEntityObject> findHistoricalVersionsAfterVersion(long var1, int var3);

    public List<T> getLastEditedVersionsOf(T var1);

    public List<SpaceContentEntityObject> findContentBySpaceIdAndStatus(long var1, String var3, int var4, int var5);

    @Deprecated
    public PageResponse<SpaceContentEntityObject> findContentBySpaceIdAndStatus(long var1, String var3, LimitedRequest var4, com.google.common.base.Predicate<? super SpaceContentEntityObject> var5);

    public int countContentBySpaceIdAndStatus(long var1, String var3);

    public Date getOldestPageCreationDate();

    public List<ContentEntityObject> findAllDraftsFor(long var1);

    default public List<Draft> findAllLegacyDraftsFor(long contentId) {
        return Collections.emptyList();
    }

    public ContentEntityObject findDraftFor(long var1);

    public List<ContentEntityObject> findUnpublishedContentWithUserContributions(String var1);

    default public List<ContentEntityObject> findDraftsWithUnpublishedChangesForUser(String creatorName) {
        return Collections.emptyList();
    }
}

