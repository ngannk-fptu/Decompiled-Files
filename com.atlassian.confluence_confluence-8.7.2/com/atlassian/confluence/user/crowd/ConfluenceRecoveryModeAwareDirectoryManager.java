/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.crowd.core.event.MultiEventPublisher
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper
 *  com.atlassian.crowd.directory.ldap.util.LdapConnectionPropertiesDiffResultMapper
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.manager.directory.BeforeGroupRemoval
 *  com.atlassian.crowd.manager.directory.DirectorySynchroniser
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerManager
 *  com.atlassian.crowd.manager.permission.PermissionManager
 *  com.atlassian.crowd.manager.recovery.RecoveryModeAwareDirectoryManager
 *  com.atlassian.crowd.manager.recovery.RecoveryModeService
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper;
import com.atlassian.crowd.directory.ldap.util.LdapConnectionPropertiesDiffResultMapper;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.manager.directory.BeforeGroupRemoval;
import com.atlassian.crowd.manager.directory.DirectorySynchroniser;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerManager;
import com.atlassian.crowd.manager.permission.PermissionManager;
import com.atlassian.crowd.manager.recovery.RecoveryModeAwareDirectoryManager;
import com.atlassian.crowd.manager.recovery.RecoveryModeService;
import java.util.Optional;

public final class ConfluenceRecoveryModeAwareDirectoryManager
extends RecoveryModeAwareDirectoryManager {
    public ConfluenceRecoveryModeAwareDirectoryManager(DirectoryDao directoryDao, ApplicationDAO applicationDAO, MultiEventPublisher eventPublisher, PermissionManager permissionManager, DirectoryInstanceLoader directoryInstanceLoader, DirectorySynchroniser directorySynchroniser, DirectoryPollerManager directoryPollerManager, ClusterLockService clusterLockService, SynchronisationStatusManager synchronisationStatusManager, BeforeGroupRemoval beforeGroupRemoval, RecoveryModeService recoveryModeService, LDAPPropertiesHelper ldapPropertiesHelper, LdapConnectionPropertiesDiffResultMapper ldapConnectionPropertiesDiffResultMapper) {
        super(directoryDao, applicationDAO, eventPublisher, permissionManager, directoryInstanceLoader, directorySynchroniser, directoryPollerManager, clusterLockService, synchronisationStatusManager, beforeGroupRemoval, recoveryModeService, Optional.empty(), ldapPropertiesHelper, ldapConnectionPropertiesDiffResultMapper);
    }
}

