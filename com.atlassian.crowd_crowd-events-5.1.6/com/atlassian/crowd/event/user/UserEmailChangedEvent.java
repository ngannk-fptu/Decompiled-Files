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

public class UserEmailChangedEvent
extends UserUpdatedEvent {
    private final String originalEmail;

    public UserEmailChangedEvent(Object source, Directory directory, User user, String originalEmail) {
        super(source, directory, user);
        this.originalEmail = originalEmail;
    }

    public String getOriginalEmail() {
        return this.originalEmail;
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
        UserEmailChangedEvent that = (UserEmailChangedEvent)o;
        return Objects.equals(this.originalEmail, that.originalEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.originalEmail);
    }
}

