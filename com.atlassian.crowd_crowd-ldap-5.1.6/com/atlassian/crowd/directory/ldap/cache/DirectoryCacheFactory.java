/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache
 */
package com.atlassian.crowd.directory.ldap.cache;

import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;

public interface DirectoryCacheFactory {
    public DirectoryCache createDirectoryCache(RemoteDirectory var1, InternalRemoteDirectory var2);
}

