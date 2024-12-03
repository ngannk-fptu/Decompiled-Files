/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.BatchResult
 */
package com.atlassian.crowd.dao.membership.cache;

import com.atlassian.crowd.dao.membership.InternalMembershipDao;
import com.atlassian.crowd.dao.membership.cache.CachingMembershipDao;
import com.atlassian.crowd.dao.membership.cache.MembershipCache;
import com.atlassian.crowd.dao.membership.cache.QueryType;
import com.atlassian.crowd.model.membership.InternalMembership;
import com.atlassian.crowd.util.BatchResult;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CachingInternalMembershipDao
extends CachingMembershipDao
implements InternalMembershipDao {
    private final InternalMembershipDao delegate;

    public CachingInternalMembershipDao(InternalMembershipDao delegate, MembershipCache membershipCache) {
        super(delegate, membershipCache);
        this.delegate = delegate;
    }

    @Override
    public void removeGroupMembers(long directoryId, String groupName) {
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_PARENTS);
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_SUBGROUPS, groupName);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, groupName);
        this.delegate.removeGroupMembers(directoryId, groupName);
    }

    @Override
    public void removeGroupMemberships(long directoryId, String groupName) {
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_SUBGROUPS);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_PARENTS, groupName);
        this.delegate.removeGroupMemberships(directoryId, groupName);
    }

    @Override
    public void removeUserMemberships(long directoryId, String username) {
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS);
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, username);
        this.delegate.removeUserMemberships(directoryId, username);
    }

    @Override
    public void removeAllRelationships(long directoryId) {
        this.membershipCache.invalidateCache(directoryId);
        this.delegate.removeAllRelationships(directoryId);
    }

    @Override
    public void removeAllUserRelationships(long directoryId) {
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS);
        this.delegate.removeAllUserRelationships(directoryId);
    }

    @Override
    public void renameUserRelationships(long directoryId, String oldName, String newName) {
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS);
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, oldName);
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, newName);
        this.delegate.renameUserRelationships(directoryId, oldName, newName);
    }

    @Override
    public void renameGroupRelationships(long directoryId, String oldName, String newName) {
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_PARENTS);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_SUBGROUPS);
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, oldName);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, newName);
        this.delegate.renameGroupRelationships(directoryId, oldName, newName);
    }

    @Override
    public BatchResult<InternalMembership> addAll(Set<InternalMembership> memberships) {
        for (InternalMembership membership : memberships) {
            long directoryId = membership.getDirectory().getId();
            switch (membership.getMembershipType()) {
                case GROUP_USER: {
                    this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, membership.getLowerChildName());
                    this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, membership.getLowerParentName());
                    break;
                }
                case GROUP_GROUP: {
                    this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_PARENTS, membership.getLowerChildName());
                    this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_SUBGROUPS, membership.getLowerParentName());
                }
            }
        }
        return this.delegate.addAll(memberships);
    }

    @Override
    public List<InternalMembership> getMembershipsCreatedAfter(long directoryId, Date timestamp, int maxResults) {
        return this.delegate.getMembershipsCreatedAfter(directoryId, timestamp, maxResults);
    }
}

