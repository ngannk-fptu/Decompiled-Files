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
import java.util.List;

public class RequestUsernamesEvent {
    private final ImmutableUser user;
    private final List<String> usernames;

    public RequestUsernamesEvent(User user, List<String> usernames) {
        this.user = ImmutableUser.from((User)user);
        this.usernames = usernames;
    }

    public User getUser() {
        return this.user;
    }

    public List<String> getUsernames() {
        return this.usernames;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestUsernamesEvent)) {
            return false;
        }
        RequestUsernamesEvent that = (RequestUsernamesEvent)o;
        if (!this.usernames.equals(that.usernames)) {
            return false;
        }
        return this.user.equals((Object)that.user);
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + this.user.hashCode();
        result = 31 * result + this.usernames.hashCode();
        return result;
    }
}

