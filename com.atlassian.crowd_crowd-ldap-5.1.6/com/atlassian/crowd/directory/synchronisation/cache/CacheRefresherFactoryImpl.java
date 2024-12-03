/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.directory.RemoteCrowdDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.directory.MicrosoftActiveDirectory;
import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory;
import com.atlassian.crowd.directory.ldap.cache.EventTokenChangedCacheRefresher;
import com.atlassian.crowd.directory.ldap.cache.RemoteDirectoryCacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.UsnChangedCacheRefresher;

@ExperimentalApi
public class CacheRefresherFactoryImpl
implements CacheRefresherFactory {
    @Override
    public CacheRefresher createRefresher(RemoteDirectory remoteDirectory) {
        if (remoteDirectory instanceof MicrosoftActiveDirectory) {
            return new UsnChangedCacheRefresher((MicrosoftActiveDirectory)remoteDirectory);
        }
        if (remoteDirectory instanceof RemoteCrowdDirectory) {
            RemoteDirectoryCacheRefresher fullSyncCacheRefresher = new RemoteDirectoryCacheRefresher(remoteDirectory);
            return new EventTokenChangedCacheRefresher((RemoteCrowdDirectory)remoteDirectory, fullSyncCacheRefresher);
        }
        return new RemoteDirectoryCacheRefresher(remoteDirectory);
    }
}

