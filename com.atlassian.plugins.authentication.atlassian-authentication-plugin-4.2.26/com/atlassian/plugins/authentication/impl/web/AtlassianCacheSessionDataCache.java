/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.cache.Cache;
import com.atlassian.plugins.authentication.impl.web.SessionData;
import com.atlassian.plugins.authentication.impl.web.SessionDataCache;
import javax.inject.Inject;

public class AtlassianCacheSessionDataCache
implements SessionDataCache {
    private final Cache<String, SessionData> delegate;

    @Inject
    public AtlassianCacheSessionDataCache(Cache<String, SessionData> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(String key, SessionData sessionData) {
        this.delegate.put((Object)key, (Object)sessionData);
    }

    @Override
    public SessionData get(String key) {
        return (SessionData)this.delegate.get((Object)key);
    }

    @Override
    public void remove(String key) {
        this.delegate.remove((Object)key);
    }
}

