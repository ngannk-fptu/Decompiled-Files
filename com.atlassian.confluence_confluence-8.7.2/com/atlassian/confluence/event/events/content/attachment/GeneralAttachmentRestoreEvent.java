/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.types.Restore;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GeneralAttachmentRestoreEvent
extends AttachmentEvent
implements Restore,
UserDriven {
    private static final long serialVersionUID = 8845915937568135289L;
    private final @Nullable ConfluenceUser originatingUser;

    public GeneralAttachmentRestoreEvent(Object source, Attachment attachment, @Nullable ConfluenceUser originatingUser, boolean suppressNotifications) {
        super(source, attachment, suppressNotifications);
        this.originatingUser = originatingUser;
    }

    @Override
    public @Nullable User getOriginatingUser() {
        return this.originatingUser;
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
        GeneralAttachmentRestoreEvent that = (GeneralAttachmentRestoreEvent)o;
        return Objects.equals(this.originatingUser, that.originatingUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.originatingUser);
    }
}

