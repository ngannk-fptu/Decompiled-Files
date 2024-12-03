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

public class UserAttributeDeletedEvent
extends UserUpdatedEvent {
    private final String attributeName;

    public UserAttributeDeletedEvent(Object source, Directory directory, User user, String attributeName) {
        super(source, directory, user);
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
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
        UserAttributeDeletedEvent that = (UserAttributeDeletedEvent)o;
        return Objects.equals(this.attributeName, that.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.attributeName);
    }
}

