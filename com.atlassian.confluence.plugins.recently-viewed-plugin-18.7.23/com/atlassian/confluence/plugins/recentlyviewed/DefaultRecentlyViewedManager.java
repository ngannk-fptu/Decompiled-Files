/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.SpaceStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.recentlyviewed;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.SpaceStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewed;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.confluence.plugins.recentlyviewed.dao.RecentlyViewedDao;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={RecentlyViewedManager.class})
public class DefaultRecentlyViewedManager
implements RecentlyViewedManager {
    public static final int DELETE_OLDER_THAN_DAYS = 60;
    private static final int SPACE_FETCH_FACTOR = 20;
    private static final String RECENT_SPACES_CACHE_NAME = DefaultRecentlyViewedManager.class.getName() + ".RecentSpacesCache";
    private static final int PAGE_FETCH_FACTOR = 2;
    private final RecentlyViewedDao recentlyViewedDao;
    private final ContentService contentService;
    private final SpaceService spaceService;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final Cache<String, ImmutableList<String>> cache;
    private final UserAccessor userAccessor;

    @Autowired
    public DefaultRecentlyViewedManager(RecentlyViewedDao recentlyViewedDao, @ComponentImport ContentService contentService, @ComponentImport SpaceService spaceService, @ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, @ComponentImport CacheManager cacheFactory, @ComponentImport UserAccessor userAccessor) {
        this.recentlyViewedDao = recentlyViewedDao;
        this.contentService = contentService;
        this.spaceService = spaceService;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.cache = cacheFactory.getCache(RECENT_SPACES_CACHE_NAME);
        this.userAccessor = userAccessor;
    }

    @Override
    public void savePageView(String userKey, long contentId, String spaceKey, long timestamp) {
        this.savePageView(userKey, contentId, null, spaceKey, timestamp);
    }

    @Override
    public void savePageView(String userKey, long contentId, String contentType, String spaceKey, long timestamp) {
        this.recentlyViewedDao.update(contentId, contentType, userKey, spaceKey, timestamp);
        this.updateSpaceCache(userKey, spaceKey);
    }

    @Override
    public void removePageViews(long contentId) {
        this.recentlyViewedDao.delete(contentId);
    }

    @Override
    public List<Long> getRecentlyViewedIds(String userKey) {
        return this.recentlyViewedDao.findRecentContentIds(userKey);
    }

    @Override
    public List<RecentlyViewed> getRecentlyViewed(String userKey, int limit) {
        return this.getRecentlyViewed(new UserKey(userKey), false, limit);
    }

    @Override
    public List<RecentlyViewed> getRecentlyViewed(@Nonnull UserKey userKey, boolean includeTrashedContent, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        List<RecentlyViewed> recentlyViewedItems = this.recentlyViewedDao.findRecentlyViewed(userKey, limit, 0);
        return recentlyViewedItems.stream().map(item -> {
            Content content = (Content)this.contentService.find(ExpansionsParser.parse((String)"space")).withStatus(new ContentStatus[]{ContentStatus.CURRENT, ContentStatus.DRAFT, ContentStatus.TRASHED}).withId(ContentId.of((long)item.getId())).fetchOrNull();
            if (content == null) {
                return null;
            }
            if (content.getStatus().equals((Object)ContentStatus.TRASHED) && !includeTrashedContent) {
                return null;
            }
            item.setContent(content);
            return item;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<RecentlyViewed> getRecentlyViewedPages(String userKey, boolean noTrashedContent, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        List<RecentlyViewed> recentlyViewedPages = this.recentlyViewedDao.findRecentlyViewedPages(userKey, limit * 2);
        return recentlyViewedPages.stream().map(page -> {
            Content content = (Content)this.contentService.find(ExpansionsParser.parse((String)"space")).withType(new ContentType[]{ContentType.PAGE}).withStatus(new ContentStatus[]{ContentStatus.CURRENT, ContentStatus.DRAFT, ContentStatus.TRASHED}).withId(ContentId.of((long)page.getId())).fetchOrNull();
            if (content == null) {
                return null;
            }
            if (content.getStatus().equals((Object)ContentStatus.TRASHED) && noTrashedContent) {
                return null;
            }
            page.setContent(content);
            return page;
        }).filter(Objects::nonNull).limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<RecentlyViewed> getRecentlyViewedPages(String userKey, Date after, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        return this.recentlyViewedDao.findRecentlyViewedPages(userKey, after, limit);
    }

    @Override
    @Deprecated
    public List<Space> getRecentlyViewedSpaces(String userKey, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKey));
        List lastSpaces = (List)this.cache.get((Object)userKey, () -> ImmutableList.copyOf(this.recentlyViewedDao.findRecentlyViewedSpaceKeys(userKey, limit * 20)));
        return this.spacesWithPermissions(new LinkedHashSet<String>(lastSpaces), limit, (User)user);
    }

    @Override
    public List<com.atlassian.confluence.api.model.content.Space> findRecentlyViewedSpaces(String userKey, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        List lastSpaces = (List)this.cache.get((Object)userKey, () -> ImmutableList.copyOf(this.recentlyViewedDao.findRecentlyViewedSpaceKeys(userKey, limit * 20)));
        return this.spaceService.find(new Expansion[0]).withStatus(SpaceStatus.CURRENT).withKeys(lastSpaces.toArray(new String[0])).fetchMany((PageRequest)new SimplePageRequest(0, limit)).getResults();
    }

    @Override
    public Map<Long, Collection<UserKey>> getRecentViewers(Iterable<Long> contentIds) {
        return Maps.transformValues(this.recentlyViewedDao.findRecentViewers(contentIds), x -> x.stream().map(UserKey::new).collect(Collectors.toList()));
    }

    @Override
    public List<RecentlyViewed> getRecentlyViewed(String userKey, Date after, Set<String> spaceKeys, int limit, int offset) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        return this.recentlyViewedDao.findRecentlyViewed(userKey, after, spaceKeys, limit, offset);
    }

    @Deprecated
    private List<Space> spacesWithPermissions(Set<String> spaceKeys, int limit, User user) {
        return spaceKeys.stream().map(arg_0 -> ((SpaceManager)this.spaceManager).getSpace(arg_0)).filter(space -> space != null && this.permissionManager.hasPermission(user, Permission.VIEW, space) && !space.isArchived()).limit(limit).collect(Collectors.toList());
    }

    @Override
    public void deleteOldEntries() {
        this.recentlyViewedDao.deleteOldRecentyViewed(60);
    }

    private void updateSpaceCache(String userKey, String spaceKey) {
        ImmutableList cachedSpaces = (ImmutableList)this.cache.get((Object)userKey);
        if (cachedSpaces != null && (cachedSpaces.isEmpty() || !spaceKey.equals(cachedSpaces.get(0)))) {
            this.cache.remove((Object)userKey);
        }
    }
}

