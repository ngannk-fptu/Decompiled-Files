/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 */
package com.atlassian.crowd.event.directory;

import com.atlassian.crowd.directory.RemoteDirectory;

public class RemoteDirectorySynchronisationStartedEvent {
    private RemoteDirectory remoteDirectory;

    public RemoteDirectorySynchronisationStartedEvent(RemoteDirectory remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public RemoteDirectory getRemoteDirectory() {
        return this.remoteDirectory;
    }
}

