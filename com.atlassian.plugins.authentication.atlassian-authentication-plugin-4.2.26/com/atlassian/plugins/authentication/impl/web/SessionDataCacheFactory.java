/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.impl.web.SessionDataCache;
import com.atlassian.plugins.authentication.impl.web.SessionDataCacheConfiguration;

public interface SessionDataCacheFactory {
    public SessionDataCache createSessionDataCache(SessionDataCacheConfiguration var1);
}

