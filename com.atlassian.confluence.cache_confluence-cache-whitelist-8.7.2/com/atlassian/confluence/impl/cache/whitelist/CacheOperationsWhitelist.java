/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.ConfluenceCache
 */
package com.atlassian.confluence.impl.cache.whitelist;

import com.atlassian.confluence.cache.ConfluenceCache;

interface CacheOperationsWhitelist {
    public void assertPermitted(Operation var1, ConfluenceCache<?, ?> var2);

    public static enum Operation {
        PUT,
        REMOVE_CONDITIONAL,
        REPLACE,
        LISTENER;

    }
}

