/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.event.directory;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.event.directory.RemoteDirectorySynchronisationFinishedEvent;
import javax.annotation.Nullable;

public class RemoteDirectorySynchronisedEvent
extends RemoteDirectorySynchronisationFinishedEvent {
    public RemoteDirectorySynchronisedEvent(Object source, RemoteDirectory remoteDirectory, @Nullable DirectorySynchronisationRoundInformation lastRound, long timeTakenInMs) {
        super(source, remoteDirectory, lastRound, timeTakenInMs);
    }
}

