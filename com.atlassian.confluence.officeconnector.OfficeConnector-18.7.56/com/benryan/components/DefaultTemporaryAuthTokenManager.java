/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.components;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import com.benryan.components.TemporaryAuthTokenManager;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="temporaryAuthTokenManager")
public class DefaultTemporaryAuthTokenManager
implements TemporaryAuthTokenManager {
    private static final Duration EXPIRY_TIME = Duration.ofMinutes(10L);
    private static final String CACHE_NAME = DefaultTemporaryAuthTokenManager.class.getName() + ".temporary.tokens";
    private final UserAccessor userAccessor;
    private final Supplier<Cache<String, String>> tokenCacheRef = Lazy.supplier(() -> cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().remote().expireAfterWrite(EXPIRY_TIME.toMillis(), TimeUnit.MILLISECONDS).build()));

    @Autowired
    public DefaultTemporaryAuthTokenManager(@ComponentImport CacheManager cacheFactory, @ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Override
    public String createToken(User user) {
        UUID randomUUID = UUID.randomUUID();
        String token = randomUUID.toString();
        ((Cache)this.tokenCacheRef.get()).put((Object)token, (Object)user.getName());
        return token;
    }

    @Override
    public ConfluenceUser getUser(String token) throws EntityException {
        return Optional.ofNullable((String)((Cache)this.tokenCacheRef.get()).get((Object)token)).map(arg_0 -> ((UserAccessor)this.userAccessor).getUserByName(arg_0)).orElse(null);
    }

    @Override
    @Deprecated
    public void cleanExpiredTokens() {
    }
}

