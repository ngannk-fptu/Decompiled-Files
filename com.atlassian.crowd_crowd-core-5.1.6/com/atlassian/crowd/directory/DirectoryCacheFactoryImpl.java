/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory
 *  com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache
 *  com.atlassian.crowd.directory.synchronisation.cache.GroupActionStrategy
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.core.event.DelegatingMultiEventPublisher;
import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.crowd.directory.DbCachingRemoteChangeOperations;
import com.atlassian.crowd.directory.DirectoryCacheChangeOperations;
import com.atlassian.crowd.directory.DirectoryCacheImplUsingChangeOperations;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory;
import com.atlassian.crowd.directory.synchronisation.cache.DefaultGroupActionStrategy;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.directory.synchronisation.cache.ExternalIdCheckingGroupActionStrategy;
import com.atlassian.crowd.directory.synchronisation.cache.GroupActionStrategy;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.event.api.EventPublisher;

public class DirectoryCacheFactoryImpl
implements DirectoryCacheFactory {
    private static final String AZURE_AD_CLASS_NAME = "com.atlassian.crowd.directory.AzureAdDirectory";
    private final DirectoryDao directoryDao;
    private final SynchronisationStatusManager synchronisationStatusManager;
    private final MultiEventPublisher eventPublisher;
    private final UserDao userDao;
    private final GroupDao groupDao;
    private final CrowdDarkFeatureManager crowdDarkFeatureManager;

    public DirectoryCacheFactoryImpl(DirectoryDao directoryDao, SynchronisationStatusManager synchronisationStatusManager, EventPublisher eventPublisher, UserDao userDao, GroupDao groupDao, CrowdDarkFeatureManager crowdDarkFeatureManager) {
        this(directoryDao, synchronisationStatusManager, new DelegatingMultiEventPublisher(eventPublisher), userDao, groupDao, crowdDarkFeatureManager);
    }

    public DirectoryCacheFactoryImpl(DirectoryDao directoryDao, SynchronisationStatusManager synchronisationStatusManager, MultiEventPublisher eventPublisher, UserDao userDao, GroupDao groupDao, CrowdDarkFeatureManager crowdDarkFeatureManager) {
        this.directoryDao = directoryDao;
        this.synchronisationStatusManager = synchronisationStatusManager;
        this.eventPublisher = eventPublisher;
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.crowdDarkFeatureManager = crowdDarkFeatureManager;
    }

    DirectoryCacheChangeOperations createDirectoryCacheChangeOperations(RemoteDirectory remoteDirectory, InternalRemoteDirectory internalDirectory) {
        return new DbCachingRemoteChangeOperations(this.directoryDao, remoteDirectory, internalDirectory, this.synchronisationStatusManager, this.eventPublisher, this.userDao, this.groupDao, (GroupActionStrategy)(remoteDirectory.getClass().getName().equals(AZURE_AD_CLASS_NAME) ? new ExternalIdCheckingGroupActionStrategy() : new DefaultGroupActionStrategy()), this.crowdDarkFeatureManager);
    }

    public final DirectoryCache createDirectoryCache(RemoteDirectory remoteDirectory, InternalRemoteDirectory internalDirectory) {
        DirectoryCacheChangeOperations x = this.createDirectoryCacheChangeOperations(remoteDirectory, internalDirectory);
        return new DirectoryCacheImplUsingChangeOperations(x);
    }
}

