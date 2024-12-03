/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.impl.web.SessionData;
import com.atlassian.plugins.authentication.impl.web.SessionDataCache;
import com.google.common.cache.Cache;

public class GuavaSessionDataCache
implements SessionDataCache {
    private final Cache<String, SessionData> delegate;

    public GuavaSessionDataCache(Cache<String, SessionData> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(String key, SessionData sessionData) {
        this.delegate.put((Object)key, (Object)sessionData);
    }

    @Override
    public SessionData get(String key) {
        return (SessionData)this.delegate.getIfPresent((Object)key);
    }

    @Override
    public void remove(String key) {
        this.delegate.invalidate((Object)key);
    }
}

