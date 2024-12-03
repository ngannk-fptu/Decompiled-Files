/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.event.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DirectoryUpdatedEvent
extends DirectoryEvent {
    private final Directory oldDirectory;

    @Deprecated
    public DirectoryUpdatedEvent(Object source, Directory directory) {
        this(source, directory, null);
    }

    public DirectoryUpdatedEvent(Object source, Directory oldDirectory, @Nonnull Directory newDirectory) {
        super(source, newDirectory);
        this.oldDirectory = oldDirectory == null ? null : ImmutableDirectory.from((Directory)oldDirectory);
    }

    public Directory getOldDirectory() {
        return this.oldDirectory;
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
        DirectoryUpdatedEvent that = (DirectoryUpdatedEvent)o;
        return Objects.equals(this.oldDirectory, that.oldDirectory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.oldDirectory);
    }
}

