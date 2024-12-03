/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher
 */
package com.atlassian.crowd.directory.ldap.cache;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher;

@FunctionalInterface
public interface CacheRefresherFactory {
    public CacheRefresher createRefresher(RemoteDirectory var1);
}

