/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.model.user.InternalUser;
import com.google.common.base.MoreObjects;
import java.time.Instant;
import java.util.Objects;

public class InternalUserWithPasswordLastChanged {
    private InternalUser user;
    private Instant passwordLastChanged;

    public InternalUserWithPasswordLastChanged(InternalUser user, Instant passwordLastChanged) {
        this.user = user;
        this.passwordLastChanged = passwordLastChanged;
    }

    public InternalUserWithPasswordLastChanged(InternalUser user, long passwordLastChangedInMillis) {
        this.user = user;
        this.passwordLastChanged = Instant.ofEpochMilli(passwordLastChangedInMillis);
    }

    public String getDisplayName() {
        return this.user.getDisplayName();
    }

    public String getEmailAddress() {
        return this.user.getEmailAddress();
    }

    public InternalUser getUser() {
        return this.user;
    }

    public Instant getPasswordLastChanged() {
        return this.passwordLastChanged;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InternalUserWithPasswordLastChanged that = (InternalUserWithPasswordLastChanged)o;
        return Objects.equals((Object)this.user, (Object)that.user) && Objects.equals(this.passwordLastChanged, that.passwordLastChanged);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.user, this.passwordLastChanged});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("user", (Object)this.user).add("passwordLastChanged", (Object)this.passwordLastChanged).toString();
    }
}

