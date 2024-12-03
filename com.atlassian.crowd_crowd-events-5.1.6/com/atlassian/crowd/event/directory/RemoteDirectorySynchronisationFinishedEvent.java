/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.event.directory;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.event.Event;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class RemoteDirectorySynchronisationFinishedEvent
extends Event {
    private final RemoteDirectory remoteDirectory;
    @Nullable
    private final DirectorySynchronisationRoundInformation lastRound;
    private final long timeTakenInMs;

    public RemoteDirectorySynchronisationFinishedEvent(Object source, RemoteDirectory remoteDirectory, @Nullable DirectorySynchronisationRoundInformation lastRound, long timeTakenInMs) {
        super(source);
        Preconditions.checkNotNull((Object)remoteDirectory);
        this.remoteDirectory = remoteDirectory;
        this.lastRound = lastRound;
        this.timeTakenInMs = timeTakenInMs;
    }

    public RemoteDirectory getRemoteDirectory() {
        return this.remoteDirectory;
    }

    public long getDirectoryId() {
        return this.remoteDirectory.getDirectoryId();
    }

    public Optional<DirectorySynchronisationRoundInformation> getLastRound() {
        return Optional.ofNullable(this.lastRound);
    }

    public long getTimeTakenInMs() {
        return this.timeTakenInMs;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RemoteDirectorySynchronisationFinishedEvent that = (RemoteDirectorySynchronisationFinishedEvent)o;
        return this.timeTakenInMs == that.timeTakenInMs && Objects.equal((Object)this.remoteDirectory, (Object)that.remoteDirectory) && Objects.equal((Object)this.lastRound, (Object)that.lastRound);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.remoteDirectory, this.lastRound, this.timeTakenInMs});
    }
}

