/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.cache.Cache
 *  org.springframework.cache.Cache$ValueWrapper
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.userdetails.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class SpringCacheBasedUserCache
implements UserCache {
    private static final Log logger = LogFactory.getLog(SpringCacheBasedUserCache.class);
    private final Cache cache;

    public SpringCacheBasedUserCache(Cache cache) {
        Assert.notNull((Object)cache, (String)"cache mandatory");
        this.cache = cache;
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        Cache.ValueWrapper element = username != null ? this.cache.get((Object)username) : null;
        logger.debug((Object)LogMessage.of(() -> "Cache hit: " + (element != null) + "; username: " + username));
        return element != null ? (UserDetails)element.get() : null;
    }

    @Override
    public void putUserInCache(UserDetails user) {
        logger.debug((Object)LogMessage.of(() -> "Cache put: " + user.getUsername()));
        this.cache.put((Object)user.getUsername(), (Object)user);
    }

    public void removeUserFromCache(UserDetails user) {
        logger.debug((Object)LogMessage.of(() -> "Cache remove: " + user.getUsername()));
        this.removeUserFromCache(user.getUsername());
    }

    @Override
    public void removeUserFromCache(String username) {
        this.cache.evict((Object)username);
    }
}

