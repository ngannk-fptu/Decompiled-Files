/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations$AddRemoveSets
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations$GroupShadowingType
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations$GroupsToAddUpdateReplace
 *  com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache
 *  com.atlassian.crowd.directory.synchronisation.utils.AddUpdateSets
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.util.TimedOperation
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.DirectoryCacheChangeOperations;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.directory.synchronisation.utils.AddUpdateSets;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.util.TimedOperation;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryCacheImplUsingChangeOperations
implements DirectoryCache {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryCacheImplUsingChangeOperations.class);
    private final DirectoryCacheChangeOperations dc;

    public DirectoryCacheImplUsingChangeOperations(DirectoryCacheChangeOperations dc) {
        this.dc = dc;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AddUpdateSets<UserTemplateWithCredentialAndAttributes, UserTemplate> addOrUpdateCachedUsers(Collection<? extends User> remoteUsers, Date syncStartDate) throws OperationFailedException {
        TimedOperation addOrUpdateRemoteUsersOperation = new TimedOperation();
        try {
            Set usersToUpdate;
            Set usersToAdd;
            AddUpdateSets result;
            TimedOperation findingUsersToAddAndUpdateOperation = new TimedOperation();
            try {
                result = this.dc.getUsersToAddAndUpdate(remoteUsers, syncStartDate);
                usersToAdd = result.getToAddSet();
                usersToUpdate = result.getToUpdateSet();
            }
            finally {
                logger.info(findingUsersToAddAndUpdateOperation.complete("scanned and compared [ " + remoteUsers.size() + " ] users for update in DB cache"));
            }
            this.dc.updateUsers((Collection)usersToUpdate);
            this.dc.addUsers(usersToAdd);
            AddUpdateSets addUpdateSets = result;
            return addUpdateSets;
        }
        finally {
            logger.info(addOrUpdateRemoteUsersOperation.complete("synchronised [ " + remoteUsers.size() + " ] users"));
        }
    }

    public void deleteCachedUsersByGuid(Set<String> guids) throws OperationFailedException {
        this.dc.deleteCachedUsersByGuid(guids);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addOrUpdateCachedGroups(Collection<? extends Group> remoteGroups, Date syncStartDate) throws OperationFailedException {
        logger.info("scanning [ {} ] groups to add or update", (Object)remoteGroups.size());
        TimedOperation operation = new TimedOperation();
        try {
            DirectoryCacheChangeOperations.GroupsToAddUpdateReplace addUpdateReplace = this.dc.findGroupsToUpdate(remoteGroups, syncStartDate);
            logger.debug("replacing [ {} ] groups", (Object)addUpdateReplace.groupsToReplace.size());
            this.dc.removeGroups(addUpdateReplace.groupsToReplace.keySet());
            HashSet allToAdd = new HashSet();
            allToAdd.addAll(addUpdateReplace.groupsToAdd);
            allToAdd.addAll(addUpdateReplace.groupsToReplace.values());
            this.dc.addGroups(allToAdd);
            this.dc.updateGroups((Collection)addUpdateReplace.groupsToUpdate);
        }
        finally {
            logger.info(operation.complete("synchronized [ " + remoteGroups.size() + " ] groups"));
        }
    }

    public void deleteCachedGroupsNotIn(GroupType groupType, List<? extends Group> remoteGroups, Date syncStartDate) throws OperationFailedException {
        this.dc.deleteCachedGroupsNotIn(groupType, remoteGroups, syncStartDate);
    }

    public void deleteCachedGroupsNotInByExternalId(Collection<? extends Group> remoteGroups, Date syncStartDate) throws OperationFailedException {
        this.dc.deleteCachedGroupsNotInByExternalId(remoteGroups, syncStartDate);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void syncUserMembersForGroup(Group parentGroup, Collection<String> remoteUsers) throws OperationFailedException {
        if (this.shouldSkipSyncingGroupMembers(parentGroup)) {
            return;
        }
        TimedOperation operation = new TimedOperation();
        try {
            DirectoryCacheChangeOperations.AddRemoveSets addRemove = this.dc.findUserMembershipForGroupChanges(parentGroup, remoteUsers);
            logger.debug("removing [ {} ] users from group '{}'", (Object)addRemove.toRemove.size(), (Object)parentGroup.getName());
            this.dc.removeUserMembershipsForGroup(parentGroup, addRemove.toRemove);
            logger.debug("adding [ {} ] users to group '{}'", (Object)addRemove.toAdd.size(), (Object)parentGroup.getName());
            this.dc.addUserMembershipsForGroup(parentGroup, addRemove.toAdd);
        }
        finally {
            logger.debug(operation.complete("synchronised [ " + remoteUsers.size() + " ] user members for group [ " + parentGroup.getName() + " ]"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addUserMembersForGroup(Group parentGroup, Set<String> remoteUsers) throws OperationFailedException {
        if (this.shouldSkipSyncingGroupMembers(parentGroup)) {
            return;
        }
        TimedOperation operation = new TimedOperation();
        try {
            this.dc.addUserMembershipsForGroup(parentGroup, remoteUsers);
        }
        finally {
            logger.debug(operation.complete("added [ " + remoteUsers.size() + " ] user members for group [ " + parentGroup.getName() + " ]"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteUserMembersForGroup(Group parentGroup, Set<String> remoteUsers) throws OperationFailedException {
        if (this.shouldSkipSyncingGroupMembers(parentGroup)) {
            return;
        }
        TimedOperation operation = new TimedOperation();
        try {
            this.dc.removeUserMembershipsForGroup(parentGroup, remoteUsers);
        }
        finally {
            logger.debug(operation.complete("removed [ " + remoteUsers.size() + " ] user members for group [ " + parentGroup.getName() + " ]"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void syncGroupMembersForGroup(Group parentGroup, Collection<String> remoteGroups) throws OperationFailedException {
        if (this.shouldSkipSyncingGroupMembers(parentGroup)) {
            return;
        }
        TimedOperation operation = new TimedOperation();
        try {
            DirectoryCacheChangeOperations.AddRemoveSets addRemove = this.dc.findGroupMembershipForGroupChanges(parentGroup, remoteGroups);
            logger.debug("removing [ " + addRemove.toRemove.size() + " ] group members to group [ " + parentGroup.getName() + " ]");
            this.dc.removeGroupMembershipsForGroup(parentGroup, (Collection)addRemove.toRemove);
            logger.debug("adding [ " + addRemove.toAdd.size() + " ] group members from group [ " + parentGroup.getName() + " ]");
            this.dc.addGroupMembershipsForGroup(parentGroup, (Collection)addRemove.toAdd);
        }
        finally {
            logger.debug(operation.complete("synchronised [ " + remoteGroups.size() + " ] group members for group [ " + parentGroup.getName() + " ]"));
        }
    }

    @VisibleForTesting
    boolean shouldSkipSyncingGroupMembers(Group parentGroup) throws OperationFailedException {
        DirectoryCacheChangeOperations.GroupShadowingType groupShadowingType = this.dc.isGroupShadowed(parentGroup);
        switch (groupShadowingType) {
            case SHADOWED_BY_LOCAL_GROUP: {
                logger.info("Skipping update of group '{}' due to the group being shadowed by a local group with the same name", (Object)parentGroup.getName());
                return true;
            }
            case SHADOWED_BY_ROLE: {
                logger.info("Skipping update of group '{}' due to the group being shadowed by a {} with the same name", (Object)parentGroup.getName(), (Object)GroupType.LEGACY_ROLE);
                return true;
            }
            case GROUP_REMOVED: {
                logger.info("Skipping update of group '{}' due to the group being removed from the cache in the mean time.", (Object)parentGroup.getName());
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addGroupMembersForGroup(Group parentGroup, Set<String> remoteGroups) throws OperationFailedException {
        if (this.shouldSkipSyncingGroupMembers(parentGroup)) {
            return;
        }
        TimedOperation operation = new TimedOperation();
        try {
            this.dc.addGroupMembershipsForGroup(parentGroup, remoteGroups);
        }
        finally {
            logger.debug(operation.complete("added [ " + remoteGroups.size() + " ] group members for group [ " + parentGroup.getName() + " ]"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteGroupMembersForGroup(Group parentGroup, Set<String> remoteGroups) throws OperationFailedException {
        if (this.shouldSkipSyncingGroupMembers(parentGroup)) {
            return;
        }
        TimedOperation operation = new TimedOperation();
        try {
            this.dc.removeGroupMembershipsForGroup(parentGroup, remoteGroups);
        }
        finally {
            logger.debug(operation.complete("removed [ " + remoteGroups.size() + " ] group members for group [ " + parentGroup.getName() + " ]"));
        }
    }

    public void deleteCachedGroups(Set<String> groupnames) throws OperationFailedException {
        this.dc.deleteCachedGroups(groupnames);
    }

    public void deleteCachedGroupsByGuids(Set<String> guids) throws OperationFailedException {
        this.dc.deleteCachedGroupsByGuids(guids);
    }

    public void deleteCachedUsersNotIn(Collection<? extends User> users, Date syncStartDate) throws OperationFailedException {
        this.dc.deleteCachedUsersNotIn(users, syncStartDate);
    }

    public void addOrUpdateCachedUser(User user) throws OperationFailedException {
        this.dc.addOrUpdateCachedUser(user);
    }

    public void deleteCachedUser(String username) throws OperationFailedException {
        this.dc.deleteCachedUser(username);
    }

    public void addOrUpdateCachedGroup(Group group) throws OperationFailedException {
        this.dc.addOrUpdateCachedGroup(group);
    }

    public void deleteCachedGroup(String groupName) throws OperationFailedException {
        this.dc.deleteCachedGroup(groupName);
    }

    public void addUserToGroup(String username, String groupName) throws OperationFailedException {
        this.dc.addUserToGroup(username, groupName);
    }

    public void removeUserFromGroup(String username, String groupName) throws OperationFailedException {
        this.dc.removeUserFromGroup(username, groupName);
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws OperationFailedException {
        this.dc.addGroupToGroup(childGroup, parentGroup);
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws OperationFailedException {
        this.dc.removeGroupFromGroup(childGroup, parentGroup);
    }

    public void syncGroupMembershipsForUser(String childUsername, Set<String> parentGroupNames) throws OperationFailedException {
        this.dc.syncGroupMembershipsForUser(childUsername, parentGroupNames);
    }

    public void syncGroupMembershipsAndMembersForGroup(String groupName, Set<String> parentGroupNames, Set<String> childGroupNames) throws OperationFailedException {
        this.dc.syncGroupMembershipsAndMembersForGroup(groupName, parentGroupNames, childGroupNames);
    }

    public Set<String> getAllUserGuids() throws OperationFailedException {
        return this.dc.getAllUserGuids();
    }

    public Set<String> getAllGroupGuids() throws OperationFailedException {
        return this.dc.getAllGroupGuids();
    }

    public Set<String> getAllLocalGroupNames() throws OperationFailedException {
        return this.dc.getAllLocalGroupNames();
    }

    public long getUserCount() throws OperationFailedException {
        return this.dc.getUserCount();
    }

    public long getGroupCount() throws OperationFailedException {
        return this.dc.getGroupCount();
    }

    public long getExternalCachedGroupCount() throws OperationFailedException {
        return this.dc.getExternalCachedGroupCount();
    }

    public UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException, OperationFailedException {
        return this.dc.findUserWithAttributesByName(name);
    }

    public Map<String, String> findUsersByExternalIds(Set<String> externalIds) {
        return this.dc.findUsersByExternalIds(externalIds);
    }

    public Map<String, String> findGroupsByExternalIds(Set<String> externalIds) throws OperationFailedException {
        return this.dc.findGroupsByExternalIds(externalIds);
    }

    public Map<String, String> findGroupsExternalIdsByNames(Set<String> groupNames) throws OperationFailedException {
        return this.dc.findGroupsExternalIdsByNames(groupNames);
    }

    public GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException, OperationFailedException {
        return this.dc.findGroupWithAttributesByName(name);
    }

    public void applySyncingUserAttributes(String userName, Set<String> deletedAttributes, Map<String, Set<String>> storedAttributes) throws UserNotFoundException, OperationFailedException {
        this.dc.applySyncingUserAttributes(userName, deletedAttributes, storedAttributes);
    }

    public void applySyncingGroupAttributes(String groupName, Set<String> deletedAttributes, Map<String, Set<String>> storedAttributes) throws GroupNotFoundException, OperationFailedException {
        this.dc.applySyncingGroupAttributes(groupName, deletedAttributes, storedAttributes);
    }
}

