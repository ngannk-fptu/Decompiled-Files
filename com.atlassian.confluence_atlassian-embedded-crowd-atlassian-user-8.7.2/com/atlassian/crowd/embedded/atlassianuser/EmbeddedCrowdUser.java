/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Deprecated
public class EmbeddedCrowdUser
implements com.atlassian.user.User,
User {
    private final boolean active;
    private final String name;
    private final long directoryId;
    private final String emailAddress;
    private final String displayName;

    public EmbeddedCrowdUser(long directoryId, String name, String displayName, String emailAddress, boolean isActive) {
        this.directoryId = directoryId;
        this.name = (String)Preconditions.checkNotNull((Object)name, (Object)"Username must not be null");
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.active = isActive;
    }

    public EmbeddedCrowdUser(long directoryId, String name, String displayName, String emailAddress) {
        this(directoryId, name, displayName, emailAddress, true);
    }

    public EmbeddedCrowdUser(User crowdUser) {
        this(((User)Preconditions.checkNotNull((Object)crowdUser, (Object)"User must not be null")).getDirectoryId(), crowdUser.getName(), crowdUser.getDisplayName(), crowdUser.getEmailAddress(), crowdUser.isActive());
    }

    public String getFullName() {
        return this.getDisplayName();
    }

    public String getEmail() {
        return this.getEmailAddress();
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

    public int compareTo(User user) {
        return this.getName().compareToIgnoreCase(user.getName());
    }

    public final long getDirectoryId() {
        return this.directoryId;
    }

    public final String getName() {
        return this.name;
    }

    private String getLowerName() {
        return IdentifierUtils.toLowerCase((String)this.name);
    }

    public String toString() {
        return String.format("EmbeddedCrowdUser{name='%s', displayName='%s', directoryId=%d}", this.name, this.displayName, this.directoryId);
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EmbeddedCrowdUser that = (EmbeddedCrowdUser)o;
        return this.directoryId == that.directoryId && this.getLowerName().equals(that.getLowerName());
    }

    public final int hashCode() {
        return new HashCodeBuilder(1, 31).append(this.directoryId).append((Object)this.getLowerName()).toHashCode();
    }
}

