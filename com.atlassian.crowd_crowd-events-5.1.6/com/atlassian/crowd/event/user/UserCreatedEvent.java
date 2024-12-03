/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;
import java.util.Objects;

public class UserCreatedEvent
extends DirectoryEvent {
    private final ImmutableUser user;

    public UserCreatedEvent(Object source, Directory directory, User user) {
        super(source, directory);
        this.user = ImmutableUser.from((User)user);
    }

    public UserCreatedEvent(Object source, ImmutableDirectory directory, User user) {
        super(source, (Directory)directory);
        this.user = ImmutableUser.from((User)user);
    }

    public User getUser() {
        return this.user;
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
        UserCreatedEvent that = (UserCreatedEvent)o;
        return Objects.equals(this.user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.user);
    }
}

