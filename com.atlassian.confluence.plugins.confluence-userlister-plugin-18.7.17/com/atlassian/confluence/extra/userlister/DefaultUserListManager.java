/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.userlister;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.extra.userlister.UserListManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserListManager
implements UserListManager {
    private final BandanaManager bandanaManager;
    private final CacheManager cacheManager;

    @Autowired
    public DefaultUserListManager(@ComponentImport BandanaManager bandanaManager, @ComponentImport CacheManager cacheManager) {
        this.bandanaManager = bandanaManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public Set<String> getGroupBlackList() {
        Set blackList = (Set)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "com.atlassian.confluence.extra.userlister.blacklist");
        return null == blackList ? Collections.emptySet() : blackList;
    }

    @Override
    public void saveGroupBlackList(Set<String> deniedGroups) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "com.atlassian.confluence.extra.userlister.blacklist", deniedGroups);
    }

    @Override
    public boolean isGroupPermitted(String groupName) {
        return StringUtils.isBlank((CharSequence)groupName) || !this.getGroupBlackList().contains(groupName);
    }

    private Cache<String, Set<String>> getLoggedInUsersCache() {
        return this.cacheManager.getCache(this.getClass().getName());
    }

    @Override
    public Set<String> getLoggedInUsers() {
        return new HashSet<String>(this.getLoggedInUsersCache().getKeys());
    }

    private Set<String> getLoggedInUserSessionIds(String userName) {
        Set sessionIds = (Set)this.getLoggedInUsersCache().get((Object)userName);
        return null == sessionIds ? new HashSet() : sessionIds;
    }

    private void cacheLoggedInUser(String userName, Set<String> sessionIds) {
        this.getLoggedInUsersCache().put((Object)userName, sessionIds);
    }

    @Override
    public void registerLoggedInUser(String userName, String sessionId) {
        Set<String> sessionIds = this.getLoggedInUserSessionIds(userName);
        sessionIds.add(sessionId);
        this.cacheLoggedInUser(userName, sessionIds);
    }

    @Override
    public void unregisterLoggedInUser(String userName, String sessionId) {
        Set<String> sessionIds = this.getLoggedInUserSessionIds(userName);
        sessionIds.remove(sessionId);
        if (sessionIds.isEmpty()) {
            this.getLoggedInUsersCache().remove((Object)userName);
        } else {
            this.cacheLoggedInUser(userName, sessionIds);
        }
    }
}

