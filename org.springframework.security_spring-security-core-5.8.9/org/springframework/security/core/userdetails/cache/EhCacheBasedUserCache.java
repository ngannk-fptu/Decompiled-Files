/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.userdetails.cache;

import java.io.Serializable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@Deprecated
public class EhCacheBasedUserCache
implements UserCache,
InitializingBean {
    private static final Log logger = LogFactory.getLog(EhCacheBasedUserCache.class);
    private Ehcache cache;

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.cache, (String)"cache mandatory");
    }

    public Ehcache getCache() {
        return this.cache;
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        Element element = this.cache.get((Serializable)((Object)username));
        logger.debug((Object)LogMessage.of(() -> "Cache hit: " + (element != null) + "; username: " + username));
        return element != null ? (UserDetails)element.getValue() : null;
    }

    @Override
    public void putUserInCache(UserDetails user) {
        Element element = new Element((Serializable)((Object)user.getUsername()), (Serializable)user);
        logger.debug((Object)LogMessage.of(() -> "Cache put: " + element.getKey()));
        this.cache.put(element);
    }

    public void removeUserFromCache(UserDetails user) {
        logger.debug((Object)LogMessage.of(() -> "Cache remove: " + user.getUsername()));
        this.removeUserFromCache(user.getUsername());
    }

    @Override
    public void removeUserFromCache(String username) {
        this.cache.remove((Serializable)((Object)username));
    }

    public void setCache(Ehcache cache) {
        this.cache = cache;
    }
}

