/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.Entity
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.google.common.collect.Sets
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.extract;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.agent.service.extract.UserGroupExtractionService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.Entity;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class SpacePermissionExtractionService
implements UserGroupExtractionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpacePermissionExtractionService.class);
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionsManager;
    private final GroupManager groupManager;
    private final TransactionTemplate transactionTemplate;

    public SpacePermissionExtractionService(SpaceManager spaceManager, SpacePermissionManager spacePermissionsManager, GroupManager groupManager, TransactionTemplate transactionTemplate) {
        this.spaceManager = spaceManager;
        this.spacePermissionsManager = spacePermissionsManager;
        this.groupManager = groupManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public Set<String> getUsersFromSpaces(List<String> spaceKeys) {
        return spaceKeys.stream().flatMap(spaceKey -> {
            Set<String> usernamesFromSpace = this.getUsernamesFromSpace((String)spaceKey);
            return usernamesFromSpace.stream();
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getGroupsFromSpaces(List<String> spaceKeys) {
        return spaceKeys.stream().flatMap(spaceKey -> {
            Set<String> groups = this.getGroupNamesWithSpacePermission((String)spaceKey);
            return groups.stream();
        }).collect(Collectors.toSet());
    }

    public Set<String> getGroupNamesWithSpacePermission(String spaceKey) {
        return (Set)this.transactionTemplate.execute(() -> {
            Space space = this.spaceManager.getSpace(spaceKey);
            Collection groups = this.spacePermissionsManager.getGroupsWithPermissions(space);
            return groups.stream().map(Entity::getName).collect(Collectors.toSet());
        });
    }

    private Set<String> getUsernamesFromSpace(String spaceKey) {
        return (Set)this.transactionTemplate.execute(() -> {
            Set members = this.getGroupsWithSpacePermissions(spaceKey).stream().flatMap(group -> {
                try {
                    HashSet usersInGroup = Sets.newHashSet((Iterable)this.groupManager.getMemberNames(group));
                    return usersInGroup.stream();
                }
                catch (Exception e) {
                    log.warn("Encountered problem getting members of group: " + group.getName());
                    HashSet emptySet = new HashSet();
                    return emptySet.stream();
                }
            }).collect(Collectors.toSet());
            Collection<User> usersWithSpacePermissions = this.getUsersWithSpacePermissions(spaceKey);
            Set usernames = usersWithSpacePermissions.stream().map(Entity::getName).collect(Collectors.toSet());
            HashSet usersWithPermissions = new HashSet();
            usersWithPermissions.addAll(members);
            usersWithPermissions.addAll(usernames);
            return usersWithPermissions;
        });
    }

    private Collection<User> getUsersWithSpacePermissions(String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        return this.spacePermissionsManager.getUsersWithPermissions(space);
    }

    private Collection<Group> getGroupsWithSpacePermissions(String spaceKey) {
        return (Collection)this.transactionTemplate.execute(() -> {
            Space space = this.spaceManager.getSpace(spaceKey);
            return this.spacePermissionsManager.getGroupsWithPermissions(space);
        });
    }
}

