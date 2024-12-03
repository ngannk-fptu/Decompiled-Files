/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.model.user.InternalUser
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.confluence.impl.user.crowd.GroupMembershipCache;
import com.atlassian.confluence.impl.user.crowd.MembershipCache;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalMembershipDao;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.InternalUser;

public final class CachedCrowdInternalMembershipDao
implements InternalMembershipDao {
    private final InternalMembershipDao delegate;
    private final MembershipCache stringCache;
    private final GroupMembershipCache parentGroupCache;
    private final GroupMembershipCache childGroupCache;

    public CachedCrowdInternalMembershipDao(InternalMembershipDao delegate, MembershipCache stringCache, GroupMembershipCache parentGroupCache, GroupMembershipCache childGroupCache) {
        this.delegate = delegate;
        this.stringCache = stringCache;
        this.parentGroupCache = parentGroupCache;
        this.childGroupCache = childGroupCache;
    }

    @Override
    public void removeAllGroupRelationships(InternalGroup group) {
        this.stringCache.removeAllGroupMemberships(group.getDirectoryId(), group.getName());
        this.parentGroupCache.removeAllGroupMemberships((Group)group);
        this.childGroupCache.removeAllGroupMemberships((Group)group);
        this.delegate.removeAllGroupRelationships(group);
    }

    @Override
    public void removeAllUserRelationships(InternalUser user) {
        this.stringCache.removeAllUserMemberships(user.getDirectoryId(), user.getName());
        this.delegate.removeAllUserRelationships(user);
    }

    @Override
    public void removeAllRelationships(Directory directory) {
        this.stringCache.removeAllDirectoryMemberships(directory.getId());
        this.parentGroupCache.removeAllDirectoryMemberships(directory.getId());
        this.childGroupCache.removeAllDirectoryMemberships(directory.getId());
        this.delegate.removeAllRelationships(directory);
    }

    @Override
    public void rename(String oldUsername, InternalUser user) {
        this.stringCache.removeAllUserMemberships(user.getDirectoryId(), oldUsername);
        this.delegate.rename(oldUsername, user);
    }
}

