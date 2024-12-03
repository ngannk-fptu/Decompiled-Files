/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang.builder.EqualsBuilder
 *  org.apache.commons.lang.builder.HashCodeBuilder
 *  org.apache.commons.lang.builder.ToStringBuilder
 */
package com.atlassian.plugin.notifications.api.medium.recipient;

import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class UserKeyRoleRecipient
implements RoleRecipient {
    public static final UserKeyRoleRecipient UNKNOWN = new UserKeyRoleRecipient(null, null);
    private final UserRole role;
    private final UserKey userKey;
    private final boolean overrideSendingOwnNotification;

    public UserKeyRoleRecipient(UserRole role, UserKey userKey, boolean overrideSendingOwnNotification) {
        this.role = role;
        this.userKey = userKey;
        this.overrideSendingOwnNotification = overrideSendingOwnNotification;
    }

    public UserKeyRoleRecipient(UserRole role, UserKey userKey) {
        this(role, userKey, false);
    }

    @Override
    public UserRole getRole() {
        return this.role;
    }

    @Override
    public UserKey getUserKey() {
        return this.userKey;
    }

    @Override
    public boolean shouldOverrideSendingOwnEventNotifications() {
        return this.overrideSendingOwnNotification;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        UserKeyRoleRecipient rhs = (UserKeyRoleRecipient)obj;
        return new EqualsBuilder().append((Object)this.role, (Object)rhs.role).append((Object)this.userKey, (Object)rhs.userKey).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(37, 17).append((Object)this.role).append((Object)this.userKey).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("role", (Object)this.role).append("userKey", (Object)this.userKey).toString();
    }
}

