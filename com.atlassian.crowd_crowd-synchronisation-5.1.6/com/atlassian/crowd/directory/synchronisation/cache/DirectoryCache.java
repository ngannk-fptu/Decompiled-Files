/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 */
package com.atlassian.crowd.directory.synchronisation.cache;

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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectoryCache {
    public AddUpdateSets<UserTemplateWithCredentialAndAttributes, UserTemplate> addOrUpdateCachedUsers(Collection<? extends User> var1, Date var2) throws OperationFailedException;

    public void deleteCachedUsersNotIn(Collection<? extends User> var1, Date var2) throws OperationFailedException;

    public void deleteCachedUsersByGuid(Set<String> var1) throws OperationFailedException;

    public void addOrUpdateCachedGroups(Collection<? extends Group> var1, Date var2) throws OperationFailedException;

    public void deleteCachedGroupsNotIn(GroupType var1, List<? extends Group> var2, Date var3) throws OperationFailedException;

    public void deleteCachedGroupsNotInByExternalId(Collection<? extends Group> var1, Date var2) throws OperationFailedException;

    public void deleteCachedGroups(Set<String> var1) throws OperationFailedException;

    public void deleteCachedGroupsByGuids(Set<String> var1) throws OperationFailedException;

    public void syncUserMembersForGroup(Group var1, Collection<String> var2) throws OperationFailedException;

    public void addUserMembersForGroup(Group var1, Set<String> var2) throws OperationFailedException;

    public void deleteUserMembersForGroup(Group var1, Set<String> var2) throws OperationFailedException;

    public void syncGroupMembersForGroup(Group var1, Collection<String> var2) throws OperationFailedException;

    public void addGroupMembersForGroup(Group var1, Set<String> var2) throws OperationFailedException;

    public void deleteGroupMembersForGroup(Group var1, Set<String> var2) throws OperationFailedException;

    public void addOrUpdateCachedUser(User var1) throws OperationFailedException;

    public void deleteCachedUser(String var1) throws OperationFailedException;

    public void addOrUpdateCachedGroup(Group var1) throws OperationFailedException;

    public void deleteCachedGroup(String var1) throws OperationFailedException;

    public void addUserToGroup(String var1, String var2) throws OperationFailedException;

    public void removeUserFromGroup(String var1, String var2) throws OperationFailedException;

    public void addGroupToGroup(String var1, String var2) throws OperationFailedException;

    public void removeGroupFromGroup(String var1, String var2) throws OperationFailedException;

    public void syncGroupMembershipsForUser(String var1, Set<String> var2) throws OperationFailedException;

    public void syncGroupMembershipsAndMembersForGroup(String var1, Set<String> var2, Set<String> var3) throws OperationFailedException;

    public Set<String> getAllUserGuids() throws OperationFailedException;

    public long getUserCount() throws OperationFailedException;

    public UserWithAttributes findUserWithAttributesByName(String var1) throws UserNotFoundException, OperationFailedException;

    public Map<String, String> findUsersByExternalIds(Set<String> var1);

    public Set<String> getAllGroupGuids() throws OperationFailedException;

    public Set<String> getAllLocalGroupNames() throws OperationFailedException;

    public long getGroupCount() throws OperationFailedException;

    public long getExternalCachedGroupCount() throws OperationFailedException;

    public Map<String, String> findGroupsByExternalIds(Set<String> var1) throws OperationFailedException;

    public Map<String, String> findGroupsExternalIdsByNames(Set<String> var1) throws OperationFailedException;

    public GroupWithAttributes findGroupWithAttributesByName(String var1) throws GroupNotFoundException, OperationFailedException;

    public void applySyncingUserAttributes(String var1, Set<String> var2, Map<String, Set<String>> var3) throws UserNotFoundException, OperationFailedException;

    public void applySyncingGroupAttributes(String var1, Set<String> var2, Map<String, Set<String>> var3) throws GroupNotFoundException, OperationFailedException;
}

