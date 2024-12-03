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
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UserAttributeStoredEvent
extends UserUpdatedEvent {
    private final Map<String, Set<String>> attributes;

    public UserAttributeStoredEvent(Object source, Directory directory, User user, Map<String, Set<String>> attributes) {
        super(source, directory, user);
        this.attributes = attributes;
    }

    public Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(this.attributes.keySet());
    }

    public Set<String> getAttributeValues(String key) {
        return Collections.unmodifiableSet(this.attributes.get(key));
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
        UserAttributeStoredEvent that = (UserAttributeStoredEvent)o;
        return Objects.equals(this.attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.attributes);
    }
}

