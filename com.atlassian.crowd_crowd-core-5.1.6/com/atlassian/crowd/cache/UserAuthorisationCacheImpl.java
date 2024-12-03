/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.user.User
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.ObjectWriter
 */
package com.atlassian.crowd.cache;

import com.atlassian.cache.Cache;
import com.atlassian.crowd.cache.UserAuthorisationCache;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.user.User;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class UserAuthorisationCacheImpl
implements UserAuthorisationCache {
    private static final ObjectWriter jsonWriter = new ObjectMapper().writer();
    private final Cache<String, Boolean> cache;

    public UserAuthorisationCacheImpl(Cache<String, Boolean> cache) {
        this.cache = cache;
    }

    @Override
    public void setPermitted(User user, String applicationName, boolean permitted) {
        this.cache.put((Object)UserAuthorisationCacheImpl.getCacheKey(user, applicationName), (Object)permitted);
    }

    @Override
    public Boolean isPermitted(User user, String applicationName) {
        return (Boolean)this.cache.get((Object)UserAuthorisationCacheImpl.getCacheKey(user, applicationName));
    }

    @Override
    public void clear() {
        this.cache.removeAll();
    }

    static String getCacheKey(User user, String applicationName) {
        try {
            List<Serializable> cacheKeyComponents = Arrays.asList(IdentifierUtils.toLowerCase((String)user.getName()), user.getDirectoryId(), user.getExternalId(), IdentifierUtils.toLowerCase((String)applicationName));
            return jsonWriter.writeValueAsString(cacheKeyComponents);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

