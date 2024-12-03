/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GeneralAttachmentRemoveEvent
extends AttachmentEvent
implements Removed,
UserDriven {
    private static final long serialVersionUID = -2612647347717623261L;
    private final @Nullable User remover;

    public GeneralAttachmentRemoveEvent(Object src, Attachment attachment, @Nullable User remover) {
        this(src, attachment, remover, false);
    }

    public GeneralAttachmentRemoveEvent(Object src, Attachment attachment, @Nullable User remover, boolean suppressNotifications) {
        super(src, attachment, suppressNotifications);
        this.remover = remover;
    }

    @Override
    public @Nullable User getOriginatingUser() {
        return this.remover;
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
        GeneralAttachmentRemoveEvent that = (GeneralAttachmentRemoveEvent)o;
        return Objects.equals(this.remover, that.remover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.remover);
    }
}

