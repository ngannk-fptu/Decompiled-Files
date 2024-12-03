/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.user.UserCreatedEvent;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.atlassian.crowd.model.user.User;

public class UserCreatedFromDirectorySynchronisationEvent
extends UserCreatedEvent {
    public UserCreatedFromDirectorySynchronisationEvent(Object source, Directory directory, User user) {
        super(source, directory, user);
    }

    public UserCreatedFromDirectorySynchronisationEvent(Object source, ImmutableDirectory directory, User user) {
        super(source, directory, user);
    }
}

