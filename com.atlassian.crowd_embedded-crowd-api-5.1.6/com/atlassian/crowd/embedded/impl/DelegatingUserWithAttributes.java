/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.embedded.impl.AbstractDelegatingEntityWithAttributes;

public class DelegatingUserWithAttributes
extends AbstractDelegatingEntityWithAttributes
implements UserWithAttributes {
    private final User user;

    public DelegatingUserWithAttributes(User user, Attributes attributes) {
        super(attributes);
        this.user = user;
    }

    @Override
    public long getDirectoryId() {
        return this.user.getDirectoryId();
    }

    @Override
    public boolean isActive() {
        return this.user.isActive();
    }

    @Override
    public String getEmailAddress() {
        return this.user.getEmailAddress();
    }

    @Override
    public String getDisplayName() {
        return this.user.getDisplayName();
    }

    @Override
    public int compareTo(User user) {
        return this.user.compareTo(user);
    }

    @Override
    public String getName() {
        return this.user.getName();
    }

    @Override
    public boolean equals(Object o) {
        return this.user.equals(o);
    }

    @Override
    public int hashCode() {
        return this.user.hashCode();
    }
}

