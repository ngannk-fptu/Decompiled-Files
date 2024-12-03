/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.embedded.impl.DelegatingUserWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;

public class DelegatingUserWithAttributes
extends com.atlassian.crowd.embedded.impl.DelegatingUserWithAttributes
implements UserWithAttributes {
    private final User user;

    public DelegatingUserWithAttributes(User user, Attributes attributes) {
        super((com.atlassian.crowd.embedded.api.User)user, attributes);
        this.user = user;
    }

    public String getFirstName() {
        return this.user.getFirstName();
    }

    public String getLastName() {
        return this.user.getLastName();
    }

    public boolean equals(Object o) {
        return this.user.equals(o);
    }

    public int hashCode() {
        return this.user.hashCode();
    }

    public int compareTo(com.atlassian.crowd.embedded.api.User other) {
        return UserComparator.compareTo((com.atlassian.crowd.embedded.api.User)this, (com.atlassian.crowd.embedded.api.User)other);
    }

    public String getExternalId() {
        return this.user.getExternalId();
    }
}

