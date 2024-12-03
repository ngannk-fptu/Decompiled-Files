/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 */
package com.atlassian.crowd.event.application;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.application.ApplicationUpdatedEvent;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.directory.ImmutableDirectory;

public class ApplicationDirectoryOrderUpdatedEvent
extends ApplicationUpdatedEvent {
    private final ImmutableDirectory directory;

    public ApplicationDirectoryOrderUpdatedEvent(Application oldApplication, Application application, Directory directory) {
        super(oldApplication, application);
        this.directory = ImmutableDirectory.from((Directory)directory);
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public Long getDirectoryId() {
        return this.directory.getId();
    }
}

