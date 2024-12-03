/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.DirectoryEntities
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.Groups
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.group.Membership$MembershipIterationException
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterators
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.DirectoryEntities;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.Groups;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DirectoryMembershipsIterable
implements Iterable<Membership> {
    private final RemoteDirectory remoteDirectory;
    private final Iterable<String> groupNames;
    private final Function<String, Membership> lookUpMembers = new Function<String, Membership>(){

        public Membership apply(String from) {
            try {
                return DirectoryMembershipsIterable.this.get(from);
            }
            catch (OperationFailedException ofe) {
                throw new Membership.MembershipIterationException((Throwable)ofe);
            }
        }
    };
    @Deprecated
    public static final Function<Group, String> GROUPS_TO_NAMES = Groups.NAME_FUNCTION;

    public DirectoryMembershipsIterable(RemoteDirectory remoteDirectory, Iterable<String> groupNames) {
        Preconditions.checkNotNull((Object)remoteDirectory);
        Preconditions.checkNotNull(groupNames);
        this.remoteDirectory = remoteDirectory;
        this.groupNames = groupNames;
    }

    public DirectoryMembershipsIterable(RemoteDirectory remoteDirectory) throws OperationFailedException {
        Preconditions.checkNotNull((Object)remoteDirectory);
        List<Group> groups = remoteDirectory.searchGroups(QueryBuilder.queryFor(Group.class, EntityDescriptor.group(GroupType.GROUP)).returningAtMost(-1));
        this.remoteDirectory = remoteDirectory;
        this.groupNames = DirectoryEntities.namesOf(groups);
    }

    @Override
    public Iterator<Membership> iterator() {
        return Iterators.transform(this.groupNames.iterator(), this.lookUpMembers);
    }

    private Membership get(final String groupName) throws OperationFailedException {
        ImmutableSet childGroupNamesSet;
        List<String> userNames = this.remoteDirectory.searchGroupRelationships(QueryBuilder.queryFor(String.class, EntityDescriptor.user()).childrenOf(EntityDescriptor.group()).withName(groupName).returningAtMost(-1));
        if (this.remoteDirectory.supportsNestedGroups()) {
            List<String> childGroupNames = this.remoteDirectory.searchGroupRelationships(QueryBuilder.queryFor(String.class, EntityDescriptor.group()).childrenOf(EntityDescriptor.group()).withName(groupName).returningAtMost(-1));
            childGroupNamesSet = ImmutableSet.copyOf(childGroupNames);
        } else {
            childGroupNamesSet = Collections.emptySet();
        }
        ImmutableSet userNamesSet = ImmutableSet.copyOf(userNames);
        return new Membership((Set)userNamesSet, (Set)childGroupNamesSet){
            final /* synthetic */ Set val$userNamesSet;
            final /* synthetic */ Set val$childGroupNamesSet;
            {
                this.val$userNamesSet = set;
                this.val$childGroupNamesSet = set2;
            }

            public String getGroupName() {
                return groupName;
            }

            public Set<String> getUserNames() {
                return this.val$userNamesSet;
            }

            public Set<String> getChildGroupNames() {
                return this.val$childGroupNamesSet;
            }
        };
    }
}

