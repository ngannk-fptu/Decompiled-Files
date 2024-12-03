/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.application.DefaultGroupMembershipService
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.core.event.listener;

import com.atlassian.crowd.core.event.listener.DefaultGroupMembershipResolver;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.application.DefaultGroupMembershipService;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationDefaultGroupMembershipResolver
implements DefaultGroupMembershipResolver {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationDefaultGroupMembershipResolver.class);
    private final DefaultGroupMembershipService defaultGroupMembershipService;
    private final DirectoryManager directoryManager;

    public ApplicationDefaultGroupMembershipResolver(DefaultGroupMembershipService defaultGroupMembershipService, DirectoryManager directoryManager) {
        this.defaultGroupMembershipService = defaultGroupMembershipService;
        this.directoryManager = directoryManager;
    }

    @Override
    public Collection<String> getDefaultGroupNames(Application application, Directory directory, UserWithAttributes user) {
        try {
            ImmutableList groups;
            if (this.userHasAttributeForApplicationAlreadySet(application, user)) {
                logger.debug("User '{}' won't be added to default groups for application '{}', because the user has been already added to default groups for that application", (Object)user.getName(), (Object)application.getName());
                return Collections.emptySet();
            }
            ApplicationDirectoryMapping mapping = application.getApplicationDirectoryMapping(directory.getId().longValue());
            Object object = groups = mapping == null ? ImmutableList.of() : this.defaultGroupMembershipService.listAll(application, mapping);
            if (!groups.isEmpty()) {
                logger.info("User '{}' will be added to default groups of application '{}': {}", new Object[]{user.getName(), application.getName(), groups});
            }
            return groups;
        }
        catch (OperationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDefaultGroupsAdded(Application application, Directory directory, UserWithAttributes user) throws OperationFailedException {
        try {
            this.directoryManager.storeUserAttributes(directory.getId().longValue(), user.getName(), (Map)ImmutableMap.of((Object)ApplicationDefaultGroupMembershipResolver.autoGroupsAddedForApplicationAttributeName(application), Collections.singleton(Boolean.TRUE.toString())));
        }
        catch (DirectoryNotFoundException | UserNotFoundException | DirectoryPermissionException e) {
            throw new OperationFailedException(e);
        }
    }

    private boolean userHasAttributeForApplicationAlreadySet(Application application, UserWithAttributes user) {
        return Boolean.parseBoolean(user.getValue(ApplicationDefaultGroupMembershipResolver.autoGroupsAddedForApplicationAttributeName(application)));
    }

    @VisibleForTesting
    static String autoGroupsAddedForApplicationAttributeName(Application application) {
        Preconditions.checkNotNull((Object)application.getId());
        return "autoGroupsAdded.app." + application.getId();
    }
}

