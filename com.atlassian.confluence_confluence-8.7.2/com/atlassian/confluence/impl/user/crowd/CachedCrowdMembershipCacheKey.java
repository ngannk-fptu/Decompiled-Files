/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.confluence.user.crowd.NameUtils;
import com.atlassian.crowd.model.group.Group;
import java.io.Serializable;
import java.util.Objects;

public class CachedCrowdMembershipCacheKey
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long directoryId;
    private final MemberType type;
    private final String name;

    public static CachedCrowdMembershipCacheKey forUser(long directoryId, String userName) {
        return new CachedCrowdMembershipCacheKey(directoryId, MemberType.GROUPS_FOR_USER, userName);
    }

    public static CachedCrowdMembershipCacheKey forGroup(long directoryId, String groupName) {
        return new CachedCrowdMembershipCacheKey(directoryId, MemberType.GROUPS_FOR_GROUP, groupName);
    }

    public static CachedCrowdMembershipCacheKey forGroup(Group group) {
        return CachedCrowdMembershipCacheKey.forGroup(group.getDirectoryId(), group.getName());
    }

    CachedCrowdMembershipCacheKey(long directoryId, MemberType type, String name) {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        this.directoryId = directoryId;
        this.type = type;
        this.name = NameUtils.getCanonicalName(name);
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public MemberType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public boolean matches(Group group) {
        return this.directoryId == group.getDirectoryId() && this.name.equals(group.getName()) && this.type == MemberType.GROUPS_FOR_GROUP;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CachedCrowdMembershipCacheKey that = (CachedCrowdMembershipCacheKey)o;
        return this.directoryId == that.directoryId && this.name.equals(that.name) && this.type == that.type;
    }

    public int hashCode() {
        int result = (int)(this.directoryId ^ this.directoryId >>> 32);
        result = 31 * result + this.type.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }

    public String toString() {
        return "MembershipCacheKey{directoryId=" + this.directoryId + ", type=" + this.type + ", name='" + this.name + "'}";
    }

    static enum MemberType {
        GROUPS_FOR_USER,
        GROUPS_FOR_GROUP;

    }
}

