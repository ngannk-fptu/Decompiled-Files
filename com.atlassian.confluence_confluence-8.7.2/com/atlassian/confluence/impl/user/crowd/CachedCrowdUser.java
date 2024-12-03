/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.Date;

@Immutable
public class CachedCrowdUser
implements TimestampedUser,
Serializable {
    private final long directoryId;
    private final boolean active;
    private final String emailAddress;
    private final String displayName;
    private final Date createdDate;
    private final Date updatedDate;
    private final String firstName;
    private final String lastName;
    private final String name;
    private final String externalId;

    public CachedCrowdUser(TimestampedUser user) {
        this.directoryId = user.getDirectoryId();
        this.active = user.isActive();
        this.emailAddress = user.getEmailAddress();
        this.displayName = user.getDisplayName();
        this.createdDate = user.getCreatedDate() == null ? null : new Date(user.getCreatedDate().getTime());
        this.updatedDate = user.getUpdatedDate() == null ? null : new Date(user.getUpdatedDate().getTime());
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.name = user.getName();
        this.externalId = user.getExternalId();
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Date getCreatedDate() {
        if (this.createdDate == null) {
            return null;
        }
        return new Date(this.createdDate.getTime());
    }

    public Date getUpdatedDate() {
        if (this.updatedDate == null) {
            return null;
        }
        return new Date(this.updatedDate.getTime());
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getName() {
        return this.name;
    }

    public int compareTo(User other) {
        return UserComparator.compareTo((User)this, (User)other);
    }
}

