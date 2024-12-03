/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.login;

import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;

public class RequestResetPasswordEvent {
    private final ImmutableUser user;
    private final String resetLink;

    public RequestResetPasswordEvent(User user, String resetLink) {
        this.user = ImmutableUser.from((User)user);
        this.resetLink = resetLink;
    }

    public User getUser() {
        return this.user;
    }

    public String getResetLink() {
        return this.resetLink;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestResetPasswordEvent)) {
            return false;
        }
        RequestResetPasswordEvent that = (RequestResetPasswordEvent)o;
        if (!this.resetLink.equals(that.resetLink)) {
            return false;
        }
        return this.user.equals((Object)that.user);
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + this.user.hashCode();
        result = 31 * result + this.resetLink.hashCode();
        return result;
    }
}

