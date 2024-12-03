/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import java.util.Objects;

@Deprecated
public class UserDeletedEvent
extends DirectoryEvent {
    private final String username;

    public UserDeletedEvent(Object source, Directory directory, String username) {
        super(source, directory);
        this.username = username;
    }

    public UserDeletedEvent(Object source, ImmutableDirectory directory, String username) {
        super(source, (Directory)directory);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
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
        UserDeletedEvent that = (UserDeletedEvent)o;
        return Objects.equals(this.username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.username);
    }
}

