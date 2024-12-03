/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper
 *  com.atlassian.crowd.directory.ldap.util.LdapConnectionPropertiesDiffResultMapper
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.directory.BeforeGroupRemoval
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.crowd.manager.permission.PermissionManager
 *  com.atlassian.crowd.search.query.DirectoryQueries
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.manager.recovery;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper;
import com.atlassian.crowd.directory.ldap.util.LdapConnectionPropertiesDiffResultMapper;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.BeforeGroupRemoval;
import com.atlassian.crowd.manager.directory.DirectoryManagerGeneric;
import com.atlassian.crowd.manager.directory.DirectorySynchroniser;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerManager;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsCacheProvider;
import com.atlassian.crowd.manager.permission.PermissionManager;
import com.atlassian.crowd.manager.recovery.RecoveryModeService;
import com.atlassian.crowd.search.query.DirectoryQueries;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;

public class RecoveryModeAwareDirectoryManager
extends DirectoryManagerGeneric {
    private final RecoveryModeService recoveryModeService;

    public RecoveryModeAwareDirectoryManager(DirectoryDao directoryDao, ApplicationDAO applicationDAO, MultiEventPublisher eventPublisher, PermissionManager permissionManager, DirectoryInstanceLoader directoryInstanceLoader, DirectorySynchroniser directorySynchroniser, DirectoryPollerManager directoryPollerManager, ClusterLockService clusterLockService, SynchronisationStatusManager synchronisationStatusManager, BeforeGroupRemoval beforeGroupRemoval, RecoveryModeService recoveryModeService, Optional<NestedGroupsCacheProvider> nestedGroupsCacheProvider, LDAPPropertiesHelper ldapPropertiesHelper, LdapConnectionPropertiesDiffResultMapper ldapConnectionPropertiesDiffResultMapper) {
        super(directoryDao, applicationDAO, eventPublisher, permissionManager, directoryInstanceLoader, directorySynchroniser, directoryPollerManager, clusterLockService, synchronisationStatusManager, beforeGroupRemoval, nestedGroupsCacheProvider, ldapPropertiesHelper, ldapConnectionPropertiesDiffResultMapper);
        this.recoveryModeService = (RecoveryModeService)Preconditions.checkNotNull((Object)recoveryModeService, (Object)"recoveryModeService");
    }

    @Override
    public Directory findDirectoryById(long directoryId) throws DirectoryNotFoundException {
        if (this.recoveryModeService.isRecoveryModeOn() && this.recoveryModeService.getRecoveryDirectory().getId().equals(directoryId)) {
            return this.recoveryModeService.getRecoveryDirectory();
        }
        return super.findDirectoryById(directoryId);
    }

    @Override
    public List<Directory> findAllDirectories() {
        List<Directory> directories = super.findAllDirectories();
        return this.addRecoveryDirectoryIfNeeded(directories);
    }

    @Override
    public List<Directory> searchDirectories(EntityQuery<Directory> query) {
        List<Directory> directories = super.searchDirectories(query);
        if (query.equals((Object)DirectoryQueries.allDirectories())) {
            return this.addRecoveryDirectoryIfNeeded(directories);
        }
        return directories;
    }

    private List<Directory> addRecoveryDirectoryIfNeeded(List<Directory> directories) {
        if (this.recoveryModeService.isRecoveryModeOn()) {
            return ImmutableList.builder().add((Object)this.recoveryModeService.getRecoveryDirectory()).addAll(directories).build();
        }
        return directories;
    }

    @Override
    public Directory findDirectoryByName(String name) throws DirectoryNotFoundException {
        if (this.recoveryModeService.isRecoveryModeOn() && this.recoveryModeService.getRecoveryDirectory().getName().equalsIgnoreCase(name)) {
            return this.recoveryModeService.getRecoveryDirectory();
        }
        return super.findDirectoryByName(name);
    }
}

