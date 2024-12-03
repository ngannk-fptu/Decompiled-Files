/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.crowd.model.group;

import com.atlassian.crowd.model.group.Membership;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;

public final class ImmutableMembership
implements Membership {
    private final String groupName;
    private final Set<String> userNames;
    private final Set<String> childGroupNames;

    public ImmutableMembership(String groupName, Iterable<String> userNames, Iterable<String> childGroupNames) {
        this.groupName = (String)Preconditions.checkNotNull((Object)groupName);
        this.childGroupNames = ImmutableSet.copyOf(childGroupNames);
        this.userNames = ImmutableSet.copyOf(userNames);
    }

    public static ImmutableMembership from(Membership membership) {
        if (membership instanceof ImmutableMembership) {
            return (ImmutableMembership)membership;
        }
        return new ImmutableMembership(membership.getGroupName(), membership.getUserNames(), membership.getChildGroupNames());
    }

    @Override
    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public Set<String> getUserNames() {
        return this.userNames;
    }

    @Override
    public Set<String> getChildGroupNames() {
        return this.childGroupNames;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableMembership that = (ImmutableMembership)o;
        return Objects.equals(this.groupName, that.groupName) && Objects.equals(this.userNames, that.userNames) && Objects.equals(this.childGroupNames, that.childGroupNames);
    }

    public int hashCode() {
        return Objects.hash(this.groupName, this.userNames, this.childGroupNames);
    }

    public String toString() {
        return "ImmutableMembership{groupName='" + this.groupName + '\'' + ", userNames=" + this.userNames + ", childGroupNames=" + this.childGroupNames + '}';
    }
}

