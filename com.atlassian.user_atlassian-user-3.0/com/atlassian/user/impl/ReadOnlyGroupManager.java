/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ReadOnlyGroupManager
implements GroupManager {
    @Override
    public List<Group> getWritableGroups() {
        return Collections.emptyList();
    }

    @Override
    public Group createGroup(String groupName) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only GroupManager [" + this.getIdentifier().getKey() + "]");
    }

    @Override
    public void removeGroup(Group group) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only GroupManager [" + this.getIdentifier().getKey() + "]");
    }

    public void saveGroup(Group group) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only GroupManager [" + this.getIdentifier().getKey() + "]");
    }

    @Override
    public void addMembership(Group group, User user) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only GroupManager [" + this.getIdentifier().getKey() + "]");
    }

    @Override
    public void removeMembership(Group group, User user) throws EntityException {
        throw new UnsupportedOperationException("Cannot write to read-only GroupManager [" + this.getIdentifier().getKey() + "]");
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isReadOnly(Group group) {
        return true;
    }

    @Override
    public boolean supportsExternalMembership() throws EntityException {
        return false;
    }
}

