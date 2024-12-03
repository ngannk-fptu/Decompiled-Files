/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.crowd.model.config;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class AzureGroupFiltersConfiguration {
    private final boolean enabled;
    private final Set<String> groupsNames;

    public AzureGroupFiltersConfiguration(boolean enabled, Iterable<String> groupsNames) {
        this.enabled = enabled;
        this.groupsNames = groupsNames != null ? ImmutableSet.copyOf(groupsNames) : Collections.emptySet();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Set<String> getGroupsNames() {
        return this.groupsNames;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AzureGroupFiltersConfiguration that = (AzureGroupFiltersConfiguration)o;
        return Objects.equals(this.isEnabled(), that.isEnabled()) && Objects.equals(this.getGroupsNames(), that.getGroupsNames());
    }

    public int hashCode() {
        return Objects.hash(this.isEnabled(), this.getGroupsNames());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("enabled", this.isEnabled()).add("groupsNames", this.getGroupsNames()).toString();
    }
}

