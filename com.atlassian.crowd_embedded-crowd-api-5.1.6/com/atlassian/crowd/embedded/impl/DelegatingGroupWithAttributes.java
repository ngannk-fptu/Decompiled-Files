/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.GroupWithAttributes;
import com.atlassian.crowd.embedded.impl.AbstractDelegatingEntityWithAttributes;

public class DelegatingGroupWithAttributes
extends AbstractDelegatingEntityWithAttributes
implements GroupWithAttributes {
    private final Group group;

    public DelegatingGroupWithAttributes(Group group, Attributes attributes) {
        super(attributes);
        this.group = group;
    }

    @Override
    public String getName() {
        return this.group.getName();
    }

    @Override
    public int compareTo(Group group) {
        return this.group.compareTo(group);
    }

    @Override
    public boolean equals(Object o) {
        return this.group.equals(o);
    }

    @Override
    public int hashCode() {
        return this.group.hashCode();
    }
}

