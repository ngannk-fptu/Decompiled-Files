/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 */
package com.atlassian.crowd.event;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.event.Event;
import com.atlassian.crowd.model.directory.ImmutableDirectory;

public abstract class DirectoryEvent
extends Event {
    private final ImmutableDirectory directory;

    public DirectoryEvent(Object source, Directory directory) {
        super(source);
        this.directory = ImmutableDirectory.from((Directory)directory);
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public Long getDirectoryId() {
        return this.directory.getId();
    }

    public DirectoryType getDirectoryType() {
        return this.directory.getType();
    }

    public String getDirectoryImplementationClass() {
        return this.directory.getImplementationClass();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryEvent that = (DirectoryEvent)o;
        return this.directory.equals((Object)that.directory);
    }

    public int hashCode() {
        return this.directory != null ? this.directory.hashCode() : 0;
    }
}

