/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.user.UserUpdatedEvent;
import com.atlassian.crowd.model.user.User;
import java.util.Objects;

public class UserRenamedEvent
extends UserUpdatedEvent {
    private final String oldUsername;

    public UserRenamedEvent(Object source, Directory directory, User user, String oldUsername) {
        super(source, directory, user);
        this.oldUsername = oldUsername;
    }

    public String getOldUsername() {
        return this.oldUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UserRenamedEvent that = (UserRenamedEvent)o;
        return Objects.equals(this.oldUsername, that.oldUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.oldUsername);
    }
}

