/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.user.UserUpdatedEvent;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.Preconditions;
import java.util.Objects;

public class UserEditedEvent
extends UserUpdatedEvent {
    private final ImmutableUser originalUser;

    public UserEditedEvent(Object source, Directory directory, User user, User originalUser) {
        super(source, directory, user);
        Preconditions.checkArgument((originalUser != user ? 1 : 0) != 0);
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
        UserEditedEvent that = (UserEditedEvent)o;
        return Objects.equals(this.originalUser, that.originalUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.originalUser);
    }
}

