/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import java.util.List;
import java.util.function.Supplier;

public interface GroupMembershipCache {
    @Deprecated
    public List<InternalDirectoryGroup> getGroupsForGroup(long var1, String var3);

    public List<InternalDirectoryGroup> getGroupsForGroup(long var1, String var3, Supplier<List<InternalDirectoryGroup>> var4);

    public void removeGroupGroupMemberships(long var1, String var3);

    public void removeAllGroupMemberships(Group var1);

    public void removeAllDirectoryMemberships(long var1);
}

