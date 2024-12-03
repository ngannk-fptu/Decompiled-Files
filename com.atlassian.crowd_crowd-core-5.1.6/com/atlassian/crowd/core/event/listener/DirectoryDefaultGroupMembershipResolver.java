/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.base.Splitter
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
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryDefaultGroupMembershipResolver
implements DefaultGroupMembershipResolver {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryDefaultGroupMembershipResolver.class);
    public static final String AUTO_GROUPS_ADDED = "autoGroupsAdded";
    private final DirectoryManager directoryManager;

    public DirectoryDefaultGroupMembershipResolver(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    @Override
    public Collection<String> getDefaultGroupNames(Application application, Directory directory, UserWithAttributes user) {
        if (Boolean.parseBoolean(user.getValue(AUTO_GROUPS_ADDED))) {
            return Collections.emptySet();
        }
        List<String> groups = this.getDirectoryGroups(directory);
        if (!groups.isEmpty()) {
            logger.info("User '{}' will be added to default groups of directory '{}': {}", new Object[]{user.getName(), directory.getName(), groups});
        }
        return groups;
    }

    private List<String> getDirectoryGroups(Directory directory) {
        String attributeValue = directory.getValue("autoAddGroups");
        if (attributeValue != null) {
            return Splitter.on((char)'|').splitToList((CharSequence)attributeValue);
        }
        return ImmutableList.of();
    }

    @Override
    public void onDefaultGroupsAdded(Application application, Directory directory, UserWithAttributes user) throws OperationFailedException {
        try {
            this.directoryManager.storeUserAttributes(directory.getId().longValue(), user.getName(), (Map)ImmutableMap.of((Object)AUTO_GROUPS_ADDED, Collections.singleton(Boolean.TRUE.toString())));
        }
        catch (DirectoryNotFoundException | UserNotFoundException | DirectoryPermissionException e) {
            throw new OperationFailedException(e);
        }
    }
}

