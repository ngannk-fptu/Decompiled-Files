/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.GroupComparator
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.GroupComparator;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.Nonnull;

public class ImmutableGroup
implements Group,
Serializable {
    private static final long serialVersionUID = -8981033575230430514L;
    private final String name;

    public ImmutableGroup(@Nonnull String name) {
        this.name = (String)Preconditions.checkNotNull((Object)name);
    }

    public String getName() {
        return this.name;
    }

    public int compareTo(Group other) {
        return GroupComparator.compareTo((Group)this, (Group)other);
    }

    public boolean equals(Object o) {
        return GroupComparator.equalsObject((Group)this, (Object)o);
    }

    public int hashCode() {
        return GroupComparator.hashCode((Group)this);
    }
}

