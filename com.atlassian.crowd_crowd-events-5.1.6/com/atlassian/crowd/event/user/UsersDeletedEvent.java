/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import java.util.Collection;
import java.util.Objects;

public class UsersDeletedEvent
extends DirectoryEvent {
    private Collection<String> usernames;

    public UsersDeletedEvent(Object source, Directory directory, Collection<String> usernames) {
        super(source, directory);
        this.usernames = usernames;
    }

    public Collection<String> getUsernames() {
        return this.usernames;
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
        UsersDeletedEvent that = (UsersDeletedEvent)o;
        return Objects.equals(this.usernames, that.usernames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.usernames);
    }
}

