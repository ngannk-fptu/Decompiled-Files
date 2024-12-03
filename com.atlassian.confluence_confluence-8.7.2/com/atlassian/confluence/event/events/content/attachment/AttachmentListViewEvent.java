/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AttachmentListViewEvent
extends SpaceEvent
implements Viewed {
    private static final long serialVersionUID = 4345331121626097460L;
    private final @Nullable String fileExtension;

    public AttachmentListViewEvent(Object src, Space space, @Nullable String fileExtension) {
        super(src, space);
        this.fileExtension = fileExtension;
    }

    public @Nullable String getFileExtension() {
        return this.fileExtension;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AttachmentListViewEvent that = (AttachmentListViewEvent)o;
        return Objects.equals(this.fileExtension, that.fileExtension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.fileExtension);
    }
}

