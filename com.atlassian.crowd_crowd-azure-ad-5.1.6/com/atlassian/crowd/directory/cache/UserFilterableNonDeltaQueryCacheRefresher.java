/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.ldap.cache.RemoteDirectoryCacheRefresher
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsIterator
 *  com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProvider
 *  com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProviderBuilder
 *  com.atlassian.crowd.model.DirectoryEntities
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.ImmutableMembership
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.crowd.directory.cache;

import com.atlassian.crowd.directory.AzureAdDirectory;
import com.atlassian.crowd.directory.AzureMembershipHelper;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.ldap.cache.RemoteDirectoryCacheRefresher;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsIterator;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProvider;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProviderBuilder;
import com.atlassian.crowd.model.DirectoryEntities;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.ImmutableMembership;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class UserFilterableNonDeltaQueryCacheRefresher
extends RemoteDirectoryCacheRefresher {
    private final AzureAdDirectory azureAdDirectory;
    private SyncData syncData;

    public UserFilterableNonDeltaQueryCacheRefresher(AzureAdDirectory azureAdDirectory) {
        super((RemoteDirectory)azureAdDirectory);
        this.azureAdDirectory = azureAdDirectory;
    }

    protected List<GroupWithAttributes> findAllRemoteGroups(boolean withAttributes) throws OperationFailedException {
        return ImmutableList.copyOf(this.getSyncData().getGroups());
    }

    protected List<UserWithAttributes> findAllRemoteUsers(boolean withAttributes) throws OperationFailedException {
        return ImmutableList.copyOf(this.getSyncData().getUsers());
    }

    protected Iterable<Membership> getMemberships(Collection groups, boolean isFullSync) throws OperationFailedException {
        return ImmutableList.copyOf(this.getSyncData().getMemberships());
    }

    private synchronized SyncData getSyncData() throws OperationFailedException {
        if (this.syncData == null) {
            this.syncData = this.computeFullSyncData();
        }
        return this.syncData;
    }

    private SyncData computeFullSyncData() throws OperationFailedException {
        List<GroupWithAttributes> filteredGroups = this.azureAdDirectory.getFilteredGroups();
        SyncData syncData = new SyncData();
        syncData.addGroups(filteredGroups);
        AzureMembershipHelper membershipHelper = this.azureAdDirectory.createMembershipHelper();
        NestedGroupsProvider provider = NestedGroupsProviderBuilder.create().useExternalId().setSingleGroupProvider(id -> {
            Pair<List<UserWithAttributes>, List<GroupWithAttributes>> children = membershipHelper.getDirectChildren(id);
            syncData.addMemberships(id, (List)children.getLeft(), (List)children.getRight());
            return ImmutableList.copyOf((Collection)((Collection)children.getRight()));
        }).build();
        List externalIds = filteredGroups.stream().map(Group::getExternalId).collect(Collectors.toList());
        NestedGroupsIterator.namesIterator(externalIds, (boolean)true, (NestedGroupsProvider)provider).visitAll();
        return syncData;
    }

    private static final class SyncData {
        final Set<UserWithAttributes> users = new HashSet<UserWithAttributes>();
        final Set<GroupWithAttributes> groups = new HashSet<GroupWithAttributes>();
        final List<Membership> memberships = new ArrayList<Membership>();
        final Map<String, String> groupIdToName = new HashMap<String, String>();

        private SyncData() {
        }

        public void addGroups(List<GroupWithAttributes> groups) {
            groups.forEach(group -> this.groupIdToName.put(group.getExternalId(), group.getName()));
            this.groups.addAll(groups);
        }

        public void addMemberships(String groupId, List<UserWithAttributes> users, List<GroupWithAttributes> groups) {
            this.addGroups(groups);
            this.users.addAll(users);
            this.memberships.add((Membership)new ImmutableMembership(this.groupIdToName.get(groupId), DirectoryEntities.namesOf(users), DirectoryEntities.namesOf(groups)));
        }

        public Set<UserWithAttributes> getUsers() {
            return this.users;
        }

        public Set<GroupWithAttributes> getGroups() {
            return this.groups;
        }

        public List<Membership> getMemberships() {
            return this.memberships;
        }
    }
}

