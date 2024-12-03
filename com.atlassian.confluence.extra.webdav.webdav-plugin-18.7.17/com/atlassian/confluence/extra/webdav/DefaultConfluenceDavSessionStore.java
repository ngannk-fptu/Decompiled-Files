/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionStore;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionTask;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collection;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
@ParametersAreNonnullByDefault
public class DefaultConfluenceDavSessionStore
implements ConfluenceDavSessionStore {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConfluenceDavSessionStore.class);
    private static final String CACHE_KEY = "com.atlassian.confluence.extra.webdav.sessions";
    private final CacheManager cacheManager;
    private final long sessionTimeoutInMillis;
    private static final long DEFAULT_SESSION_TIMEOUT = 1800000L;

    public DefaultConfluenceDavSessionStore(@ComponentImport CacheManager cacheManager, long sessionTimeoutInMillis) {
        this.cacheManager = cacheManager;
        this.sessionTimeoutInMillis = sessionTimeoutInMillis;
    }

    public DefaultConfluenceDavSessionStore(CacheManager cacheManager) {
        this(cacheManager, 1800000L);
    }

    private Cache<String, ConfluenceDavSession> getSessionCache() {
        return this.cacheManager.getCache(CACHE_KEY);
    }

    private void cacheSession(String userName, ConfluenceDavSession confluenceDavSession) {
        Cache<String, ConfluenceDavSession> sessionCache = this.getSessionCache();
        sessionCache.put((Object)userName, (Object)confluenceDavSession);
    }

    private ConfluenceDavSession getCachedSession(String userName) {
        return this.getCachedSession(this.getSessionCache(), userName);
    }

    private ConfluenceDavSession getCachedSession(Cache<String, ConfluenceDavSession> sessionCache, String userName) {
        try {
            return (ConfluenceDavSession)sessionCache.get((Object)userName);
        }
        catch (ClassCastException cce) {
            logger.warn("Unable to cast the cached session of user " + userName + " to a ConfluenceDavSession. It will be purged from the cache.", (Throwable)cce);
            sessionCache.remove((Object)userName);
            return null;
        }
    }

    @Override
    public void mapSession(ConfluenceDavSession davSession, String userName) {
        this.cacheSession(userName, davSession);
    }

    @Override
    public ConfluenceDavSession getSession(String userName) {
        return this.getCachedSession(userName);
    }

    private boolean isSessionExpired(ConfluenceDavSession confluenceDavSession) {
        return System.currentTimeMillis() - confluenceDavSession.getLastActivityTimestamp() > this.sessionTimeoutInMillis && !confluenceDavSession.isCurrentlyBeingUsed();
    }

    @Override
    public void invalidateExpiredSessions() {
        Cache<String, ConfluenceDavSession> sessionCache = this.getSessionCache();
        Collection uniqueSessionKeys = sessionCache.getKeys();
        int sessionSize = uniqueSessionKeys.size();
        logger.debug("Number of sessions before invalidation: " + sessionSize);
        if (sessionSize > 100) {
            logger.warn("There are " + sessionSize + " active WebDAV sessions just before invalidation. Just thought of telling you that because there seems to be an unusual number of users using the WebDAV plugin.");
        }
        for (String sessionKey : uniqueSessionKeys) {
            ConfluenceDavSession confluenceDavSession = this.getCachedSession(sessionCache, sessionKey);
            if (null == confluenceDavSession || !this.isSessionExpired(confluenceDavSession)) continue;
            sessionCache.remove((Object)sessionKey);
        }
        logger.debug("Number of sessions after invalidation: " + sessionCache.getKeys().size());
    }

    @Override
    public void executeTaskOnSessions(ConfluenceDavSessionTask confluenceDavSessionTask) {
        Cache<String, ConfluenceDavSession> sessionCache = this.getSessionCache();
        Collection uniqueSessionKeys = sessionCache.getKeys();
        for (String sessionKey : uniqueSessionKeys) {
            ConfluenceDavSession confluenceDavSession = (ConfluenceDavSession)sessionCache.get((Object)sessionKey);
            confluenceDavSessionTask.execute(confluenceDavSession);
            if (confluenceDavSession == null) continue;
            this.mapSession(confluenceDavSession, confluenceDavSession.getUserName());
        }
    }
}

