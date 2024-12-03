/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.user.UserUpdatedEvent;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;
import java.util.Objects;

public class AutoUserUpdatedEvent
extends UserUpdatedEvent {
    private final ImmutableUser originalUser;

    public AutoUserUpdatedEvent(Object source, Directory directory, User user, User originalUser) {
        super(source, directory, user);
        this.originalUser = ImmutableUser.from((User)originalUser);
    }

    public User getOriginalUser() {
        return this.originalUser;
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
        AutoUserUpdatedEvent that = (AutoUserUpdatedEvent)o;
        return Objects.equals(this.originalUser, that.originalUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.originalUser);
    }
}

