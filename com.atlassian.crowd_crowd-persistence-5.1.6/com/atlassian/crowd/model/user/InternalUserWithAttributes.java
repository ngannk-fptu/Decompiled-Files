/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Multimap
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.model.EntityWithAttributes;
import com.atlassian.crowd.model.InternalEntityAttribute;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Multimap;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class InternalUserWithAttributes
extends EntityWithAttributes
implements UserWithAttributes,
TimestampedUser {
    private final InternalUser user;

    public InternalUserWithAttributes(InternalUser user) {
        super((Multimap<String, String>)InternalEntityAttribute.toMap(user.getAttributes()));
        this.user = user;
    }

    public InternalUserWithAttributes(InternalUser user, Map<String, Set<String>> attributes) {
        super(attributes);
        this.user = user;
    }

    public long getDirectoryId() {
        return this.user.getDirectoryId();
    }

    public String getName() {
        return this.user.getName();
    }

    public boolean isActive() {
        return this.user.isActive();
    }

    public String getEmailAddress() {
        return this.user.getEmailAddress();
    }

    public String getFirstName() {
        return this.user.getFirstName();
    }

    public String getLastName() {
        return this.user.getLastName();
    }

    public String getDisplayName() {
        return this.user.getDisplayName();
    }

    @VisibleForTesting
    public InternalUser getInternalUser() {
        return this.user;
    }

    public PasswordCredential getCredential() {
        return this.user.getCredential();
    }

    public String getExternalId() {
        return this.user.getExternalId();
    }

    public Date getCreatedDate() {
        return this.user.getCreatedDate();
    }

    public Date getUpdatedDate() {
        return this.user.getUpdatedDate();
    }

    public boolean equals(Object o) {
        return UserComparator.equalsObject((User)this, (Object)o);
    }

    public int hashCode() {
        return UserComparator.hashCode((User)this);
    }

    public int compareTo(User o) {
        return UserComparator.compareTo((User)this, (User)o);
    }
}

