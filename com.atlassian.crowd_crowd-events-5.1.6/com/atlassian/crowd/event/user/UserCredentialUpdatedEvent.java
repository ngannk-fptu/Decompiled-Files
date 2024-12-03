/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.event.DirectoryEvent;
import java.util.Objects;

public class UserCredentialUpdatedEvent
extends DirectoryEvent {
    private final String username;
    private final PasswordCredential newCredential;

    public UserCredentialUpdatedEvent(Object source, Directory directory, String username, PasswordCredential newCredential) {
        super(source, directory);
        this.username = username;
        this.newCredential = newCredential;
    }

    public String getUsername() {
        return this.username;
    }

    public PasswordCredential getNewCredential() {
        return this.newCredential;
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
        UserCredentialUpdatedEvent that = (UserCredentialUpdatedEvent)o;
        return Objects.equals(this.username, that.username) && Objects.equals(this.newCredential, that.newCredential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.username, this.newCredential);
    }
}

