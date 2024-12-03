/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.user.crowd;

import java.util.List;
import java.util.function.Supplier;

public interface MembershipCache {
    public boolean isUserDirectMember(long var1, String var3, String var4, Supplier<Iterable<String>> var5);

    public boolean isGroupDirectMember(long var1, String var3, String var4, Supplier<Iterable<String>> var5);

    public List<String> getGroupsForUser(long var1, String var3, Supplier<List<String>> var4);

    @Deprecated
    public List<String> getGroupsForGroup(long var1, String var3);

    public List<String> getGroupsForGroup(long var1, String var3, Supplier<List<String>> var4);

    public void removeUserGroupMemberships(long var1, String var3);

    public void removeGroupGroupMemberships(long var1, String var3);

    public void removeAllUserMemberships(long var1, String var3);

    public void removeAllGroupMemberships(long var1, String var3);

    public void removeAllDirectoryMemberships(long var1);
}

